package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.edusearch.databinding.ActivityAdmissionBinding;
import com.anurag.edusearch.databinding.ActivityHostelBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HostelActivity extends AppCompatActivity {

    ActivityHostelBinding binding;
    private String schoolId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHostelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");
        firebaseAuth = FirebaseAuth.getInstance();

        loadSchoolDetails();
        loadReviews();
        loadHostelDetails();
        checkUser();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.hostelDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostelActivity.this, HostelEditActivity.class);
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
    private void loadHostelDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hostels");
        ref.child(schoolId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String allotmentProcedure = "" + snapshot.child("allotmentProcedure").getValue();
                        String hostelImage = "" + snapshot.child("hostelImage").getValue();
                        String googleFormLink = "" + snapshot.child("googleFormLink").getValue();
                        String hostelId = "" + snapshot.child("hostelId").getValue();
                        String schoolId = "" + snapshot.child("schoolId").getValue();


                        binding.allotmentProcedure.setText(allotmentProcedure);
                        binding.googleFormLink.setText(googleFormLink);

                        try {
                            Picasso.get().load(hostelImage).placeholder(R.drawable.ic_school_primary).into(binding.hostelIv);

                        } catch (Exception e) {
                            binding.hostelIv.setImageResource(R.drawable.ic_school_primary);
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
                            binding.hostelDetailBtn.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.hostelDetailBtn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}