package com.anurag.edusearch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterImage;
import com.anurag.edusearch.databinding.ActivityFacultyDetailsBinding;
import com.anurag.edusearch.databinding.ActivityImageViewBinding;
import com.anurag.edusearch.databinding.DialogCommentAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import static com.anurag.edusearch.Constants.MAX_BYTES_PDF;

public class ImageViewActivity extends AppCompatActivity {

    ActivityImageViewBinding binding;
    String schoolId, imageId, galleryId, image;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    boolean isLiked = false;
    private String comment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        schoolId = getIntent().getStringExtra("schoolId");
        imageId = getIntent().getStringExtra("imageId");
        galleryId = getIntent().getStringExtra("galleryId");
        image = getIntent().getStringExtra("image");
        loadImage();
        checkUser();
        checkIsLiked(schoolId, imageId, galleryId);
        countLikes();
        countComments();
        countDownloads();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.deleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to delete This Image" + " ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteImage(imageId, schoolId, galleryId);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
        binding.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLiked) {
                    MyApplication.removeLikedImage(ImageViewActivity.this, "" + schoolId, "" + galleryId, "" + imageId);
                } else {
                    MyApplication.likeImage(ImageViewActivity.this, "" + schoolId, "" + galleryId, "" + imageId);
                }
            }
        });
        binding.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(ImageViewActivity.this));

                AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewActivity.this, R.style.CustomDialog);
                builder.setView(commentAddBinding.getRoot());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        comment = commentAddBinding.commentEt.getText().toString().trim();
                        if (TextUtils.isEmpty(comment)) {
                            Toast.makeText(ImageViewActivity.this, "Enter your comment....", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            addComment();
                        }
                    }
                });
            }
        });
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                boolean isDownloadImageEnabled = mFirebaseRemoteConfig.getBoolean("isDownloadImageEnabled");
                if(isDownloadImageEnabled) {
                    binding.downloadImageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewActivity.this);
                            builder.setTitle("Download")
                                    .setMessage("Do you want to Download Image: " + imageId+ " ?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (ContextCompat.checkSelfPermission(ImageViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                                downloadImage();

                                            } else {
                                                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    binding.downloadImageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(ImageViewActivity.this, "This Features is Temporarily disabled...!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void addComment() {
        progressDialog.setMessage("Adding comment");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", "" + comment);
        hashMap.put("commentId", "" + firebaseAuth.getUid());
        hashMap.put("imageId", "" + imageId);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("galleryId", "" + galleryId);
        hashMap.put("schoolId", "" + schoolId);
        hashMap.put("uid", "" + firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Comments").child(firebaseAuth.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ImageViewActivity.this, "Comment Added....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ImageViewActivity.this, "Failed to add comment due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String image = "" + snapshot.child("image").getValue();

                        try {
                            Picasso.get().load(image).placeholder(R.drawable.ic_person_gray).into(binding.imageIv);

                        } catch (Exception e) {
                            binding.imageIv.setImageResource(R.drawable.ic_person_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void deleteImage(String imageId, String schoolId, String galleryId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        startActivity(new Intent(ImageViewActivity.this, SchoolFacultyAdminActivity.class));
                        Toast.makeText(ImageViewActivity.this, "Image Deleted...!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImageViewActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String  userType = ""+snapshot.child("userType").getValue();

                        if (userType.equals("user")){
                            binding.deleteImageBtn.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.deleteImageBtn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void checkIsLiked(String schoolId, String imageId, String galleryId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Likes").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isLiked = snapshot.exists();
                        if (isLiked) {
                            binding.likeBtn.setImageResource(R.drawable.ic_like_primary);
                        } else {
                            binding.likeBtn.setImageResource(R.drawable.ic_like_outline);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void countLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Likes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long numberOfLikes = snapshot.getChildrenCount();
                        float numberLikes = numberOfLikes/1;

                        binding.noLike.setText(String.format("%.0f", numberLikes));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void countComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long numberOfComments = snapshot.getChildrenCount();
                        float numberComments = numberOfComments/1;

                        binding.noComment.setText(String.format("%.0f", numberComments));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    downloadImage();
                } else {
                    Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
                }
            });
    public  void downloadImage() {

        String nameWithExtension = imageId + ".png";

        ProgressDialog progressDialog = new ProgressDialog(ImageViewActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Downloading" + nameWithExtension + "....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        saveDownloadedBook(ImageViewActivity.this, progressDialog, bytes, nameWithExtension);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ImageViewActivity.this, "Failed to download due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private  void saveDownloadedBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension) {
        try {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdir();

            String filePath = downloadsFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            incrementBookDownloadCount();

        } catch (Exception e) {
            Toast.makeText(context, "Failed saving to download folder due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private  void incrementBookDownloadCount() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();

                        if (downloadsCount.equals("") || downloadsCount.equals("null")) {
                            downloadsCount = "0";
                        }

                        long newDownloadsCount = Long.parseLong(downloadsCount) + 1;

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("downloadsCount", newDownloadsCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
                        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void countDownloads() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();

                        binding.noDownload.setText(downloadsCount);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}