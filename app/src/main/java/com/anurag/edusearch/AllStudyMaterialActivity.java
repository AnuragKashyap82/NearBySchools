package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterMaterial;
import com.anurag.edusearch.Adapters.AdapterNotice;
import com.anurag.edusearch.Models.ModelMaterial;
import com.anurag.edusearch.Models.ModelNotice;
import com.anurag.edusearch.databinding.ActivityAllStudyMaterialBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllStudyMaterialActivity extends AppCompatActivity {

    ActivityAllStudyMaterialBinding binding;
    private String schoolId, branch, semester;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelMaterial> materialList;
    private AdapterMaterial adapterMaterial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllStudyMaterialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");
        branch = getIntent().getStringExtra("branch");
        semester = getIntent().getStringExtra("semester");

        binding.semesterTv.setText(semester);
        binding.branchTv.setText(branch);

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllMaterial();

        binding.searchMaterialEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterMaterial.getFilter().filter(charSequence);
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

    private void loadAllMaterial() {
        materialList =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Material").child(branch).child(semester)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        materialList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelMaterial modelMaterial = ds.getValue(ModelMaterial.class);
                            materialList.add(modelMaterial);
                        }
                        Collections.sort(materialList, new Comparator<ModelMaterial>() {
                            @Override
                            public int compare(ModelMaterial t1, ModelMaterial t2) {
                                return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                            }
                        });
                        Collections.reverse(materialList);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(AllStudyMaterialActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binding.materialRv.setLayoutManager(layoutManager);

                        binding.materialRv.setLayoutManager(new LinearLayoutManager(AllStudyMaterialActivity.this));

                        adapterMaterial = new AdapterMaterial(AllStudyMaterialActivity.this, materialList);
                        binding.materialRv.setAdapter(adapterMaterial);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}