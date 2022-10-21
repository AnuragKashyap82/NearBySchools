package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.anurag.edusearch.Adapters.AdapterAssignment;
import com.anurag.edusearch.Adapters.AdapterSubmittedAss;
import com.anurag.edusearch.Models.ModelAssignment;
import com.anurag.edusearch.Models.ModelSubmittedAss;
import com.anurag.edusearch.databinding.ActivityAllAssignmentBinding;
import com.anurag.edusearch.databinding.ActivitySubmittedAssignmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SubmittedAssignmentActivity extends AppCompatActivity {

    ActivitySubmittedAssignmentBinding binding;
    private String schoolId, branch, semester, year, assignmentId;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelSubmittedAss> assignmentList;
    private AdapterSubmittedAss adapterSubmittedAss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmittedAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");
        branch = getIntent().getStringExtra("branch");
        semester = getIntent().getStringExtra("semester");
        year = getIntent().getStringExtra("year");
        assignmentId = getIntent().getStringExtra("assignmentId");

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllSubmittedAss(schoolId, year, branch, semester, assignmentId);
        countNoOfStudentSubmitted(schoolId, year, branch, semester, assignmentId);

    }

    private void countNoOfStudentSubmitted(String schoolId, String year, String branch, String semester, String assignmentId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long noOfStudent = snapshot.getChildrenCount();
                        float noOfStudents = noOfStudent/1;

                        binding.noOfStudentsTv.setText(String.format("%.0f", noOfStudents));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllSubmittedAss(String schoolId, String year, String branch, String semester, String assignmentId) {
        assignmentList =new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        assignmentList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelSubmittedAss modelSubmittedAss = ds.getValue(ModelSubmittedAss.class);
                            assignmentList.add(modelSubmittedAss);
                        }
                        Collections.sort(assignmentList, new Comparator<ModelSubmittedAss>() {
                            @Override
                            public int compare(ModelSubmittedAss t1, ModelSubmittedAss t2) {
                                return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                            }
                        });
                        Collections.reverse(assignmentList);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(SubmittedAssignmentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binding.submittedAssignmentRv.setLayoutManager(layoutManager);

                        binding.submittedAssignmentRv.setLayoutManager(new LinearLayoutManager(SubmittedAssignmentActivity.this));

                        adapterSubmittedAss = new AdapterSubmittedAss(SubmittedAssignmentActivity.this, assignmentList);
                        binding.submittedAssignmentRv.setAdapter(adapterSubmittedAss);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}