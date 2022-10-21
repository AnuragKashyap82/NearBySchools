package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityMaterialVievBinding;
import com.anurag.edusearch.databinding.ActivityNoticeViewBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
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

import java.util.HashMap;

public class MaterialVievActivity extends AppCompatActivity {

    ActivityMaterialVievBinding binding;
    private FirebaseAuth firebaseAuth;
    private String materialId, schoolId, materialUrl, topic, semester, branch;
    private FirebaseRemoteConfig FirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityMaterialVievBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                boolean isDeleteMaterialEnabled = mFirebaseRemoteConfig.getBoolean("isDeleteMaterialEnabled");
                if(isDeleteMaterialEnabled) {
                    binding.deleteMaterialBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MaterialVievActivity.this);
                            builder.setTitle("Delete")
                                    .setMessage("Are you sure want to Delete Material: " + topic+ " ?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            deleteMaterial();
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
                    binding.deleteMaterialBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MaterialVievActivity.this, "This Features is Temporarily disabled...!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        schoolId = getIntent().getStringExtra("schoolId");
        materialId = getIntent().getStringExtra("materialId");
        semester = getIntent().getStringExtra("semester");
        branch = getIntent().getStringExtra("branch");
        topic = getIntent().getStringExtra("topic");

        checkUser();
        loadMaterial();
        incrementMaterialViewCount(materialId);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.editMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaterialVievActivity.this, EditMaterialActivity.class);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("materialId", materialId);
                intent.putExtra("branch", branch);
                intent.putExtra("semester", semester);
                startActivity(intent);
                Toast.makeText(MaterialVievActivity.this, "Edit Material Btn Clicked....!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteMaterial() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Material").child(branch).child(semester).child(materialId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MaterialVievActivity.this, "Material Deleted...!", Toast.LENGTH_SHORT).show();
                        deleteFromFavorites();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MaterialVievActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteFromFavorites() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(FirebaseAuth.getInstance().getUid()).child("Favorites").child(materialId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MaterialVievActivity.this, "Deleted from Favorites also....!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void loadMaterial() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Material").child(branch).child(semester).child(materialId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String materialUrl = ""+snapshot.child("url").getValue();
                        String topic = ""+snapshot.child("topic").getValue();

                        binding.topicTv.setText(topic);
                        loadMaterialFromUrl(materialUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadMaterialFromUrl(String materialUrl) {

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(materialUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        binding.materialPdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page + 1);
                                        binding.toolbarSubtitleTv.setText(currentPage + "/"+pageCount);

                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(MaterialVievActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(MaterialVievActivity.this, "Error on page"+page+" "+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        binding.progressBar.setVisibility(View.GONE);
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
                            binding.fb.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.fb.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void incrementMaterialViewCount(String materialId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Material").child(branch).child(semester).child(materialId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        if (viewsCount.equals("") || viewsCount.equals("null")) {
                            viewsCount = "0";
                        }
                        long newViewsCount = Long.parseLong(viewsCount) + 1;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("viewsCount", newViewsCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
                        reference.child(schoolId).child("Material").child(branch).child(semester).child(materialId)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}