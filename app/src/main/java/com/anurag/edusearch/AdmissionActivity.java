package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.edusearch.databinding.ActivityAdmissionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdmissionActivity extends AppCompatActivity {

    ActivityAdmissionBinding binding;
    private String schoolId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdmissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");
        firebaseAuth = FirebaseAuth.getInstance();

        loadSchoolDetails();
        loadReviews();
        loadAdmissionDetails();
        checkUser();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.admissionDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdmissionActivity.this, AdmissionDetailsEditActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
    }

    private void loadSchoolDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String schoolName = "" + snapshot.child("schoolName").getValue();
                        String address = "" + snapshot.child("address").getValue();
                        String schoolId = "" + snapshot.child("schoolId").getValue();
                        String schoolImage = "" + snapshot.child("schoolImage").getValue();
                        String email = "" + snapshot.child("email").getValue();
                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                        String website = ""+snapshot.child("website").getValue();
                        String districtId = "" + snapshot.child("districtId").getValue();
                        String district = "" + snapshot.child("district").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String aboutSchool = "" + snapshot.child("aboutSchool").getValue();


                        binding.schoolNameTv.setText(schoolName);
                        binding.addressTv.setText(address);
                        binding.emailTv.setText(email);
                        binding.phoneTv.setText(phoneNumber);
                        binding.schoolWebsiteTv.setText(website);
                        binding.districtTv.setText(district);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private float ratingSum = 0;
    private void loadReviews() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                        }
                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum / numberOfReviews;

                        binding.ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadAdmissionDetails() {
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Admission");
        ref.child(schoolId)
            .addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String admissionProcedure = "" + snapshot.child("admissionProcedure").getValue();
            String admissionImage = "" + snapshot.child("admissionImage").getValue();
            String googleFormLink = "" + snapshot.child("googleFormLink").getValue();
            String admissionId = "" + snapshot.child("admissionId").getValue();
            String schoolId = "" + snapshot.child("schoolId").getValue();


            binding.admissionProcedure.setText(admissionProcedure);
            binding.googleFormLink.setText(googleFormLink);

            try {
                Picasso.get().load(admissionImage).placeholder(R.drawable.ic_school_primary).into(binding.admissionIv);

            } catch (Exception e) {
                binding.admissionIv.setImageResource(R.drawable.ic_school_primary);
            }

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
                            binding.admissionDetailBtn.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.admissionDetailBtn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}