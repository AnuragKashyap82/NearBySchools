package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.edusearch.Adapters.AdapterComment;
import com.anurag.edusearch.Adapters.AdapterImageComment;
import com.anurag.edusearch.Models.ModelComment;
import com.anurag.edusearch.Models.ModelImageComment;
import com.anurag.edusearch.databinding.ActivityImageCommentDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageCommentDetailsActivity extends AppCompatActivity {

    ActivityImageCommentDetailsBinding binding;
    private String galleryId, imageId, schoolId;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private ArrayList<ModelImageComment> commentArrayList;
    private AdapterImageComment adapterImageComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageCommentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        galleryId = intent.getStringExtra("galleryId");
        imageId = intent.getStringExtra("imageId");
        schoolId = intent.getStringExtra("schoolId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadComments();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void loadComments() {
        commentArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelImageComment model = ds.getValue(ModelImageComment.class);
                            commentArrayList.add(model);
                        }
                        adapterImageComment = new AdapterImageComment(ImageCommentDetailsActivity.this, commentArrayList);
                        binding.commentsRv.setAdapter(adapterImageComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}