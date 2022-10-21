package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivitySchoolDetailsBinding;
import com.anurag.edusearch.databinding.ActivityWriteReviewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {

    private ActivityWriteReviewBinding binding;

    private String schoolId;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        schoolId = getIntent().getStringExtra("schoolId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadSchoolInfo();
        loadMyReview();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        if (user.isEmailVerified()) {
            binding.submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputData();
                }
            });
            }
        else {
            Toast.makeText(WriteReviewActivity.this, "Verify Your Email first....!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSchoolInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String schoolName = "" + snapshot.child("schoolName").getValue();
                        String schoolId = "" + snapshot.child("schoolId").getValue();
                        String schoolImage = "" + snapshot.child("schoolImage").getValue();

                        binding.schoolNameTv.setText(schoolName);

                        try {
                            Picasso.get().load(schoolImage).placeholder(R.drawable.ic_school_primary).into(binding.profileIv);
                        } catch (Exception e) {
                            binding.profileIv.setImageResource(R.drawable.ic_school_primary);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyReview() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String uid = ""+ snapshot.child("uid").getValue();
                            String review = ""+ snapshot.child("review").getValue();
                            String timestamp = ""+ snapshot.child("timestamp").getValue();
                            float ratings = Float.parseFloat("" + snapshot.child("ratings").getValue());

                            binding.reviewEt.setText(review);
                            binding.ratingBar.setRating(ratings);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputData() {
        String ratings = "" + binding.ratingBar.getRating();
        String review = binding.reviewEt.getText().toString().trim();

        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("ratings", "" + ratings);
        hashMap.put("review", "" + review);
        hashMap.put("timestamp", "" + timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Ratings").child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(WriteReviewActivity.this, "Review published successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WriteReviewActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
