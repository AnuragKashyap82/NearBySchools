package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.anurag.edusearch.Adapters.AdapterAssignment;
import com.anurag.edusearch.Adapters.AdapterMaterial;
import com.anurag.edusearch.Models.ModelAssignment;
import com.anurag.edusearch.Models.ModelMaterial;
import com.anurag.edusearch.databinding.ActivityAllAssignmentBinding;
import com.anurag.edusearch.databinding.ActivityAllStudyMaterialBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllAssignmentActivity extends AppCompatActivity {

    ActivityAllAssignmentBinding binding;
    private String schoolId, branch, semester, year;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelAssignment> assignmentList;
    private AdapterAssignment adapterAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");
        branch = getIntent().getStringExtra("branch");
        semester = getIntent().getStringExtra("semester");
        year = getIntent().getStringExtra("year");

        binding.semesterTv.setText(semester);
        binding.branchTv.setText(branch);

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllAssignment();

        binding.searchAssignmentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterAssignment.getFilter().filter(charSequence);
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

    private void loadAllAssignment() {
        assignmentList =new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        assignmentList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelAssignment modelAssignment = ds.getValue(ModelAssignment.class);
                            assignmentList.add(modelAssignment);
                        }
                        Collections.sort(assignmentList, new Comparator<ModelAssignment>() {
                            @Override
                            public int compare(ModelAssignment t1, ModelAssignment t2) {
                                return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                            }
                        });
                        Collections.reverse(assignmentList);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(AllAssignmentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binding.assignmentRv.setLayoutManager(layoutManager);

                        binding.assignmentRv.setLayoutManager(new LinearLayoutManager(AllAssignmentActivity.this));

                        adapterAssignment = new AdapterAssignment(AllAssignmentActivity.this, assignmentList);
                        binding.assignmentRv.setAdapter(adapterAssignment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}