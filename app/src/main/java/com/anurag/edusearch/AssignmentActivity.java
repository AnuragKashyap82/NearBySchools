package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAssignmentBinding;
import com.anurag.edusearch.databinding.ActivityResultBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AssignmentActivity extends AppCompatActivity {

    ActivityAssignmentBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        checkUser();
        schoolId = getIntent().getStringExtra("schoolId");

        binding.branchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentActivity.this);
                builder.setTitle("Select Branch:")
                        .setItems(Constants.branchCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedBranch = Constants.branchCategories[i];
                                binding.branchTv.setText(selectedBranch);
                            }
                        }).show();
            }
        });
        binding.semesterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentActivity.this);
                builder.setTitle("Select Semester:")
                        .setItems(Constants.semesterCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedSemester = Constants.semesterCategories[i];
                                binding.semesterTv.setText(selectedSemester);
                            }
                        }).show();
            }
        });
        binding.yearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentActivity.this);
                builder.setTitle("Select Year:")
                        .setItems(Constants.yearCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedYear = Constants.yearCategories[i];
                                binding.yearTv.setText(selectedYear);
                            }
                        }).show();
            }
        });
        binding.searchAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.addAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentActivity.this, AddAssignmentActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
    }

    private String branch, semester, year;

    private void validateData() {

        branch = binding.branchTv.getText().toString().trim();
        semester = binding.semesterTv.getText().toString().trim();
        year = binding.yearTv.getText().toString().trim();

        if (TextUtils.isEmpty(branch)) {
            Toast.makeText(this, "Please Select Your Branch...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(semester)) {
            Toast.makeText(this, "Please Select Your Semester...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Please Select Year...!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(AssignmentActivity.this, AllAssignmentActivity.class);
            intent.putExtra("schoolId", schoolId);
            intent.putExtra("branch", branch);
            intent.putExtra("semester", semester);
            intent.putExtra("year", year);
            startActivity(intent);
        }
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String userType = "" + snapshot.child("userType").getValue();

                        if (userType.equals("user")) {

                            binding.addAssignmentBtn.setVisibility(View.GONE);
                        } else if (userType.equals("admin")) {
                            binding.addAssignmentBtn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
