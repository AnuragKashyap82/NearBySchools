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
import com.anurag.edusearch.Models.ModelCategory;
import com.anurag.edusearch.databinding.ActivitySchoolFacultyAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolFacultyAdminActivity extends AppCompatActivity {

    ActivitySchoolFacultyAdminBinding binding;

    private FirebaseAuth firebaseAuth;
    private String schoolId;
    private ProgressDialog progressDialog;

    private ArrayList<ModelCategory> facultyList;
    private AdapterFacultyAdmin adapterFacultyAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolFacultyAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        schoolId = getIntent().getStringExtra("schoolId");

        loadAllFaculty();

        binding.addFacultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolFacultyAdminActivity.this, AddFacultyActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
                Toast.makeText(SchoolFacultyAdminActivity.this, "Add Faculty Btn Clicked....!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.searchFacultyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterFacultyAdmin.getFilter().filter(charSequence);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadAllFaculty() {
        facultyList =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Faculty")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        facultyList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelCategory modelProduct = ds.getValue(ModelCategory.class);
                            facultyList.add(modelProduct);
                        }
//                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainSellerActivity.this, LinearLayoutManager.HORIZONTAL, false);
//                        productsRv.setLayoutManager(layoutManager);

                        // productsRv.setLayoutManager(new LinearLayoutManager(MainSellerActivity.this));

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(SchoolFacultyAdminActivity.this, 3);
                        binding.facultyRv.setLayoutManager(gridLayoutManager);

                        adapterFacultyAdmin = new AdapterFacultyAdmin(SchoolFacultyAdminActivity.this, facultyList);
                        binding.facultyRv.setAdapter(adapterFacultyAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}