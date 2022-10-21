package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.anurag.edusearch.Adapters.AdapterFacultyUser;
import com.anurag.edusearch.Models.ModelFacultyUser;
import com.anurag.edusearch.databinding.ActivitySchoolFacultyUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolFacultyUserActivity extends AppCompatActivity {

    ActivitySchoolFacultyUserBinding binding;

    private FirebaseAuth firebaseAuth;
    private String schoolId;
    private ProgressDialog progressDialog;

    private ArrayList<ModelFacultyUser> facultyList;
    private AdapterFacultyUser adapterFacultyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolFacultyUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        schoolId = getIntent().getStringExtra("schoolId");

        loadAllFaculty();

        binding.searchFacultyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterFacultyUser.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadAllFaculty() {
        facultyList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Faculty")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        facultyList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelFacultyUser modelProduct = ds.getValue(ModelFacultyUser.class);
                            facultyList.add(modelProduct);
                        }
//                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainSellerActivity.this, LinearLayoutManager.HORIZONTAL, false);
//                        productsRv.setLayoutManager(layoutManager);

                        // productsRv.setLayoutManager(new LinearLayoutManager(MainSellerActivity.this));

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(SchoolFacultyUserActivity.this, 3);
                        binding.facultyRv.setLayoutManager(gridLayoutManager);

                        adapterFacultyUser = new AdapterFacultyUser(SchoolFacultyUserActivity.this, facultyList);
                        binding.facultyRv.setAdapter(adapterFacultyUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}