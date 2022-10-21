package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterFacultyAdmin;
import com.anurag.edusearch.Adapters.AdapterGalleryCategory;
import com.anurag.edusearch.Models.ModelCategory;
import com.anurag.edusearch.Models.ModelGalleryCategory;
import com.anurag.edusearch.databinding.ActivityGalleryCategoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryCategoryActivity extends AppCompatActivity {

    ActivityGalleryCategoryBinding binding;

    private FirebaseAuth firebaseAuth;
    private String schoolId;
    private ProgressDialog progressDialog;

    private ArrayList<ModelGalleryCategory> categoryList;
    private AdapterGalleryCategory adapterGalleryCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        schoolId = getIntent().getStringExtra("schoolId");

        checkUser();
        loadAllGalleryCategory();

        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GalleryCategoryActivity.this, AddGalleryCategoryActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
                Toast.makeText(GalleryCategoryActivity.this, "Add Gallery Category Btn Clicked....!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.searchCategoryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterGalleryCategory.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void loadAllGalleryCategory() {
        categoryList =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        categoryList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelGalleryCategory modelGalleryCategory = ds.getValue(ModelGalleryCategory.class);
                            categoryList.add(modelGalleryCategory);
                        }
//                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainSellerActivity.this, LinearLayoutManager.HORIZONTAL, false);
//                        productsRv.setLayoutManager(layoutManager);

                        // productsRv.setLayoutManager(new LinearLayoutManager(MainSellerActivity.this));

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(GalleryCategoryActivity.this, 2);
                        binding.galleryCategoryRv.setLayoutManager(gridLayoutManager);

                        adapterGalleryCategory = new AdapterGalleryCategory(GalleryCategoryActivity.this, categoryList);
                        binding.galleryCategoryRv.setAdapter(adapterGalleryCategory);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                            binding.addCategoryBtn.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.addCategoryBtn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}