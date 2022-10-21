package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityStudyMaterialBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudyMaterialActivity extends AppCompatActivity {

    ActivityStudyMaterialBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudyMaterialBinding.inflate(getLayoutInflater());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(StudyMaterialActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(StudyMaterialActivity.this);
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
        binding.searchMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.addMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyMaterialActivity.this, AddMaterialActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
    }
    private String branch, semester;

    private void validateData() {

        branch = binding.branchTv.getText().toString().trim();
        semester = binding.semesterTv.getText().toString().trim();

        if (TextUtils.isEmpty(branch)) {
            Toast.makeText(this, "Please Select Your Branch...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(semester)) {
            Toast.makeText(this, "Please Select Your Semester...!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(StudyMaterialActivity.this, AllStudyMaterialActivity.class);
            intent.putExtra("schoolId", schoolId);
            intent.putExtra("branch", branch);
            intent.putExtra("semester", semester);
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

                        String  userType = ""+snapshot.child("userType").getValue();

                        if (userType.equals("user")){

                            binding.addMaterialBtn.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.addMaterialBtn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}