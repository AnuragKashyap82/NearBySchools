package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityFacultyDetailsBinding;
import com.anurag.edusearch.databinding.ActivityFacultyDetailsUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FacultyDetailsUserActivity extends AppCompatActivity {

    ActivityFacultyDetailsUserBinding binding;
    String schoolId, facultyId;
    private FirebaseAuth firebaseAuth;

    private String facultyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyDetailsUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        schoolId = getIntent().getStringExtra("schoolId");
        facultyId = getIntent().getStringExtra("facultyId");
        loadFacultyDetails();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadFacultyDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Faculty").child(facultyId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String facultyId = "" + snapshot.child("facultyId").getValue();
                        facultyName = "" + snapshot.child("facultyName").getValue();
                        String facultyDescription = "" + snapshot.child("facultyDescription").getValue();
                        String schoolId = "" + snapshot.child("schoolId").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String facultyImage = "" + snapshot.child("facultyImage").getValue();
                        String address = "" + snapshot.child("address").getValue();
                        String phone = "" + snapshot.child("phoneNumber").getValue();
                        String email = "" + snapshot.child("email").getValue();
                        String subject = "" + snapshot.child("subject").getValue();
                        String qualification = "" + snapshot.child("qualification").getValue();
                        String dob = "" + snapshot.child("dob").getValue();
                        String age = "" + snapshot.child("age").getValue();
                        String linkedln = "" + snapshot.child("linkedlnId").getValue();
                        String about = "" + snapshot.child("aboutFaculty").getValue();
                        String experience = "" + snapshot.child("experience").getValue();

                        binding.facultyNameTv.setText(facultyName);
                        binding.facultyNameHeadingTv.setText(facultyName);
                        binding.facultyDescriptionTv.setText(facultyDescription);
                        binding.designationTv.setText(facultyDescription);
                        binding.addressTv.setText(address);
                        binding.phoneTv.setText(phone);
                        binding.emailTv.setText(email);
                        binding.subjectTv.setText(subject);
                        binding.qualificationTv.setText(qualification);
                        binding.dobTv.setText(dob);
                        binding.ageTv.setText(age);
                        binding.linkedlnTv.setText(linkedln);
                        binding.moreAboutFacultyTv.setText(about);
                        binding.experienceTv.setText(experience);

                        try {
                            Picasso.get().load(facultyImage).placeholder(R.drawable.ic_person_gray).into(binding.facultyIv);

                        } catch (Exception e) {
                            binding.facultyIv.setImageResource(R.drawable.ic_person_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}