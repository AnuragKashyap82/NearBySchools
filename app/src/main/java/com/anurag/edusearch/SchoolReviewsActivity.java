package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterReview;
import com.anurag.edusearch.Models.ModelReview;
import com.anurag.edusearch.Models.ModelSchoolUser;
import com.anurag.edusearch.databinding.ActivitySchoolDetailsUserBinding;
import com.anurag.edusearch.databinding.ActivitySchoolReviewsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SchoolReviewsActivity extends AppCompatActivity {

    private ActivitySchoolReviewsBinding binding;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelReview> reviewArrayList;
    private AdapterReview adapterReview;

    private String schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadSchoolDetails();
        loadReviews();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private float ratingSum = 0;
    private void loadReviews() {
        reviewArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewArrayList.clear();
                        ratingSum = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                            ModelReview modelReview = ds.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);
                        }
                        Collections.sort(reviewArrayList, new Comparator<ModelReview>() {
                            @Override
                            public int compare(ModelReview t1, ModelReview t2) {
                                return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                            }
                        });
                        Collections.reverse(reviewArrayList);

                        adapterReview = new AdapterReview(SchoolReviewsActivity.this, reviewArrayList);
                        binding.reviewsRv.setAdapter(adapterReview);

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        binding.ratingTv.setText(String.format("%.1f", avgRating) + "[" + numberOfReviews+ "]");
                        binding.ratingBar.setRating(avgRating);

                        updateAvgRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateAvgRating(float avgRating) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("avgRating", "" + avgRating);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SchoolReviewsActivity.this, "Avg Rating Updated Successfully...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SchoolReviewsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadSchoolDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String schoolName = ""+snapshot.child("schoolName").getValue();
                        String schoolImage = ""+snapshot.child("schoolImage").getValue();

                        binding.schoolNameTv.setText(schoolName);
                        try {
                            Picasso.get().load(schoolImage).placeholder(R.drawable.ic_school_primary).into(binding.profileIv);
                        }
                        catch (Exception e){
                            binding.profileIv.setImageResource(R.drawable.ic_school_primary);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}