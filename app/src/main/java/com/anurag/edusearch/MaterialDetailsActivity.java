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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterComment;
import com.anurag.edusearch.Models.ModelComment;
import com.anurag.edusearch.databinding.ActivityMaterialDetailsBinding;
import com.anurag.edusearch.databinding.DialogCommentAddBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.anurag.edusearch.Constants.MAX_BYTES_PDF;

public class MaterialDetailsActivity extends AppCompatActivity {

    private ActivityMaterialDetailsBinding binding;

    String materialId, schoolId, materialUrl, branch, semester, topic;
    boolean isInMyFavorite = false;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private ArrayList<ModelComment> commentArrayList;
    private AdapterComment adapterComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaterialDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        materialId = intent.getStringExtra("materialId");
        schoolId = intent.getStringExtra("schoolId");
        materialUrl = intent.getStringExtra("materialUrl");
        branch = intent.getStringExtra("branch");
        semester = intent.getStringExtra("semester");
        topic = intent.getStringExtra("topic");

        binding.downloadMaterialBtn.setVisibility(View.GONE);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            checkIsFavorite();
        }

        loadMaterialDetails();
        loadComments();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.readMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaterialDetailsActivity.this, MaterialVievActivity.class);
                intent.putExtra("materialId", materialId);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("materialUrl", materialUrl);
                intent.putExtra("branch", branch);
                intent.putExtra("semester", semester);
                intent.putExtra("topic", topic);
                startActivity(intent);
            }
        });
        binding.downloadMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(MaterialDetailsActivity.this);
                builder.setTitle("Download")
                        .setMessage("Do you want to Download Material: " + topic+ " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ContextCompat.checkSelfPermission(MaterialDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    downloadBook(MaterialDetailsActivity.this, "" + materialId, "" + topic, "" + materialUrl, "" + branch, "" + semester, "" + schoolId);

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

        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(MaterialDetailsActivity.this, "You're not logged In", Toast.LENGTH_SHORT).show();
                } else {
                    if (isInMyFavorite) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        if (firebaseAuth.getCurrentUser() == null){

                            Toast.makeText(MaterialDetailsActivity.this, "You're not logged In", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(firebaseAuth.getUid()).child("Favorites").child(materialId)
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(MaterialDetailsActivity.this, "Removed from  your favorites list....", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure( Exception e) {
                                            Toast.makeText(MaterialDetailsActivity.this, "Failed to remove from favorites due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            if (firebaseAuth.getCurrentUser() == null){

                                Toast.makeText(MaterialDetailsActivity.this, "You're not logged In", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                long timestamp = System.currentTimeMillis();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("materialId", ""+materialId);
                                hashMap.put("branch", ""+branch);
                                hashMap.put("semester", ""+semester);
                                hashMap.put("schoolId", ""+schoolId);
                                hashMap.put("topic", ""+topic);
                                hashMap.put("materialUrl", ""+materialUrl);
                                hashMap.put("timestamp", ""+timestamp);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).child("Favorites").child(materialId)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(MaterialDetailsActivity.this, "Added to your favorites list....", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MaterialDetailsActivity.this, "Failed to add to favorites due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    }
                }
        });

        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    addCommentDialog();
            }
        });

    }

    private void loadComments() {
        commentArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Material").child(branch).child(semester).child(materialId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelComment model = ds.getValue(ModelComment.class);
                            commentArrayList.add(model);
                        }
                        adapterComment = new AdapterComment(MaterialDetailsActivity.this, commentArrayList);
                        binding.commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String comment = "";

    private void addCommentDialog() {
        DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
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
                    Toast.makeText(MaterialDetailsActivity.this, "Enter your comment....", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });
    }

    private void addComment() {
        progressDialog.setMessage("Adding comment");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("commentId", "" + firebaseAuth.getUid());
        hashMap.put("materialId", "" + materialId);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("comment", "" + comment);
        hashMap.put("uid", "" + firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Material").child(branch).child(semester).child(materialId).child("Comments").child(firebaseAuth.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MaterialDetailsActivity.this, "Comment Added....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MaterialDetailsActivity.this, "Failed to add comment due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    downloadBook(MaterialDetailsActivity.this, "" + materialId, "" + topic, "" + materialUrl, "" + branch, "" + semester, "" + schoolId);
                } else {
                    Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
                }
            });

    private void loadMaterialDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Material").child(branch).child(semester).child(materialId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        materialId = "" + snapshot.child("materialId").getValue();
                        String subject = "" + snapshot.child("subject").getValue();
                        String topic = "" + snapshot.child("topic").getValue();
                        String branch = "" + snapshot.child("branch").getValue();
                        String semester = "" + snapshot.child("semester").getValue();
                        schoolId = "" + snapshot.child("schoolId").getValue();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String url = "" + snapshot.child("url").getValue();

                        binding.downloadMaterialBtn.setVisibility(View.VISIBLE);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(timestamp));
                        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();


                        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(materialUrl);
                        ref.getMetadata()
                                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                    @Override
                                    public void onSuccess(StorageMetadata storageMetadata) {
                                        double bytes = storageMetadata.getSizeBytes();

                                        double kb = bytes / 1024;
                                        double mb = kb / 1024;

                                        if (mb >= 1) {
                                            binding.sizeTv.setText(String.format("%.2f", mb) + " MB");
                                        } else if (kb >= 1) {
                                            binding.sizeTv.setText(String.format("%.2f", kb) + " KB");
                                        } else {
                                            binding.sizeTv.setText(String.format("%.2f", bytes) + " bytes");
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                        binding.subjectTv.setText(subject);
                        binding.topicTv.setText(topic);
                        binding.viewsTv.setText(viewsCount.replace("null","N/A"));
                        binding.downloadsTv.setText(downloadsCount.replace("null","N/A"));
                        binding.dateTv.setText(dateFormat);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });
    }

//    private void checkIsFavorite() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.child(firebaseAuth.getUid()).child("Favorites").child(materialId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        isInMyFavorite = snapshot.exists();
//                        if (isInMyFavorite) {
//                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_white, 0, 0);
//                            binding.favoriteBtn.setText("Remove Favorite");
//                        } else {
//                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_border_white, 0, 0);
//                            binding.favoriteBtn.setText("Add Favorite");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    public static void downloadBook(Context context, String materialId, String topic, String materialUrl, String branch, String semester, String schoolId) {

        String nameWithExtension = topic + ".pdf";

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Downloading" + nameWithExtension + "....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(materialUrl);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        saveDownloadedBook(context, progressDialog, bytes, nameWithExtension, materialId, branch, semester, schoolId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to download due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void saveDownloadedBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String materialId, String branch, String semester, String schoolId) {
        try {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdir();

            String filePath = downloadsFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            incrementBookDownloadCount(materialId, branch, semester, schoolId);

        } catch (Exception e) {
            Toast.makeText(context, "Failed saving to download folder due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private static void incrementBookDownloadCount(String materialId, String branch, String semester, String schoolId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Material").child(branch).child(semester).child(materialId)
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
                        reference.child(schoolId).child("Material").child(branch).child(semester).child(materialId).updateChildren(hashMap)
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
    private void checkIsFavorite(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Favorites").child(materialId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                        if (isInMyFavorite){
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_white, 0, 0);
                            binding.favoriteBtn.setText("Remove Favorite");
                        }
                        else{
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_border_white, 0, 0);
                            binding.favoriteBtn.setText("Add Favorite");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
