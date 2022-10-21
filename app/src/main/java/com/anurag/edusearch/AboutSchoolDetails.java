package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAboutSchoolDetailsBinding;
import com.anurag.edusearch.databinding.ActivitySchoolDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AboutSchoolDetails extends AppCompatActivity {

    private ActivityAboutSchoolDetailsBinding binding;

    private String schoolId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutSchoolDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");
        loadAboutDetails();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAboutSchool();
            }
        });
    }

    private void loadAboutDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String aboutSchool = "" + snapshot.child("aboutSchool").getValue();
                        String schoolName = "" + snapshot.child("schoolName").getValue();

                        binding.schoolDetailsTv.setText(aboutSchool);
                        binding.schoolNameTv.setText(schoolName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void updateAboutSchool() {
        progressDialog.setMessage("Updating About School...!");
        progressDialog.show();

        String aboutSchool;
        aboutSchool = binding.schoolDetailsTv.getText().toString().trim();

        if (aboutSchool != null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("aboutSchool", "" + aboutSchool);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
            ref.child(schoolId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(AboutSchoolDetails.this, "About School Updated...!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AboutSchoolDetails.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

}