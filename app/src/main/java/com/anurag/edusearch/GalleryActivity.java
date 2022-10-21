package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterGalleryCategory;
import com.anurag.edusearch.Adapters.AdapterImage;
import com.anurag.edusearch.Models.ModeImage;
import com.anurag.edusearch.Models.ModelGalleryCategory;
import com.anurag.edusearch.databinding.ActivityGalleryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    ActivityGalleryBinding binding;
    String galleryId, galleryName, schoolId;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModeImage> imagesList;
    private AdapterImage adapterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        schoolId = getIntent().getStringExtra("schoolId");
        galleryName = getIntent().getStringExtra("galleryName");
        galleryId = getIntent().getStringExtra("galleryId");

        binding.galleryNameTv.setText(galleryName);
        checkUser();
        loadAllImages();

        binding.addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GalleryActivity.this, AddImageActivity.class);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("galleryId", galleryId);
                startActivity(intent);
                Toast.makeText(GalleryActivity.this, "Add Image Btn Clicked....!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.editGalleryCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(GalleryActivity.this, EditGalleryCategoryActivity.class);
                intent.putExtra("galleryId", galleryId);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
                Toast.makeText(GalleryActivity.this, "Edit Gallery Btn Clicked...!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.deleteGalleryCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to delete Category " + galleryName+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteCategory(galleryId, schoolId);
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
    }
    private void loadAllImages() {
        imagesList =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        imagesList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModeImage modeImage = ds.getValue(ModeImage.class);
                            imagesList.add(modeImage);
                        }
                        LinearLayoutManager layoutManager = new LinearLayoutManager(GalleryActivity.this, LinearLayoutManager.VERTICAL, false);
                        binding.galleryImageRv.setLayoutManager(layoutManager);

                        adapterImage = new AdapterImage(GalleryActivity.this, imagesList);
                        binding.galleryImageRv.setAdapter(adapterImage);

//                        GridLayoutManager gridLayoutManager = new GridLayoutManager(GalleryActivity.this, 3);
//                        binding.galleryImageRv.setLayoutManager(gridLayoutManager);
//
//                        adapterImage = new AdapterImage(GalleryActivity.this, imagesList);
//                        binding.galleryImageRv.setAdapter(adapterImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void deleteCategory(String galleryId, String schoolId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Gallery").child(galleryId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        startActivity(new Intent(GalleryActivity.this, GalleryCategoryActivity.class));
                        Toast.makeText(GalleryActivity.this, "Category Deleted...!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GalleryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            binding.deleteGalleryCategory.setVisibility(View.GONE);
                            binding.editGalleryCategory.setVisibility(View.GONE);
                            binding.addImageBtn.setVisibility(View.GONE);
                            binding.fb.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.deleteGalleryCategory.setVisibility(View.VISIBLE);
                            binding.editGalleryCategory.setVisibility(View.VISIBLE);
                            binding.addImageBtn.setVisibility(View.VISIBLE);
                            binding.fb.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}