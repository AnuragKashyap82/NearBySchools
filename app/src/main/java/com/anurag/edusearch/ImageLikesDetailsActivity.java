package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.edusearch.Adapters.AdapterImageComment;
import com.anurag.edusearch.Adapters.AdapterImageLikes;
import com.anurag.edusearch.Models.ModelImageComment;
import com.anurag.edusearch.Models.ModelImageLikes;
import com.anurag.edusearch.databinding.ActivityImageCommentDetailsBinding;
import com.anurag.edusearch.databinding.ActivityImageLikesDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageLikesDetailsActivity extends AppCompatActivity {

    ActivityImageLikesDetailsBinding binding;
    private String galleryId, imageId, schoolId;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private ArrayList<ModelImageLikes> likesArrayList;
    private AdapterImageLikes adapterImageLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageLikesDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        galleryId = intent.getStringExtra("galleryId");
        imageId = intent.getStringExtra("imageId");
        schoolId = intent.getStringExtra("schoolId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadLikes();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void loadLikes() {
        likesArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Likes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        likesArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelImageLikes model = ds.getValue(ModelImageLikes.class);
                            likesArrayList.add(model);
                        }
                        adapterImageLikes = new AdapterImageLikes(ImageLikesDetailsActivity.this, likesArrayList);
                        binding.likesRv.setAdapter(adapterImageLikes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}