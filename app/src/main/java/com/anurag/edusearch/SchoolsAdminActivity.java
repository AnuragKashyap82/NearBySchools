package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.anurag.edusearch.Adapters.AdapterSchoolAdmin;
import com.anurag.edusearch.Models.ModelSchool;
import com.anurag.edusearch.Models.ModelSchoolUser;
import com.anurag.edusearch.databinding.ActivitySchoolsAdminActivityBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.ContentValues.TAG;

public class SchoolsAdminActivity extends AppCompatActivity {

    private ActivitySchoolsAdminActivityBinding binding;

    private ArrayList<ModelSchool> schoolArrayList;
    private AdapterSchoolAdmin adapterSchoolAdmin;

    private String districtId, districtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolsAdminActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        districtId = intent.getStringExtra("districtId");
        districtTitle = intent.getStringExtra("districtTitle");

        binding.districtTv.setText(districtTitle);

        loadSchoolsList();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    adapterSchoolAdmin.getFilter().filter(charSequence);
                } catch (Exception e) {
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    private void loadSchoolsList() {

        schoolArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.orderByChild("districtId").equalTo(districtId)
                  .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        schoolArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelSchool model = ds.getValue(ModelSchool.class);
                            schoolArrayList.add(model);
                        }
                        Collections.sort(schoolArrayList, new Comparator<ModelSchool>() {
                            @Override
                            public int compare(ModelSchool t1, ModelSchool t2) {
                                return t1.getAvgRating().compareToIgnoreCase(t2.getAvgRating());
                            }
                        });
                        Collections.reverse(schoolArrayList);

                        binding.progressBar.setVisibility(View.GONE);
                        adapterSchoolAdmin = new AdapterSchoolAdmin(SchoolsAdminActivity.this, schoolArrayList);
                        binding.schoolsRv.setAdapter(adapterSchoolAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

