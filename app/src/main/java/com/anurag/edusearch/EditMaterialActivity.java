package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAddMaterialBinding;
import com.anurag.edusearch.databinding.ActivityEditMaterialBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditMaterialActivity extends AppCompatActivity {

    ActivityEditMaterialBinding binding;
    private String schoolId, materialId;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri pdfUri = null;

    private String subject, topic, semester, branch;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditMaterialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        schoolId = getIntent().getStringExtra("schoolId");
        materialId = getIntent().getStringExtra("materialId");
        branch = getIntent().getStringExtra("branch");
        semester = getIntent().getStringExtra("semester");

        loadMaterial();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.materialPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
        binding.updateMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void loadMaterial() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Material").child(branch).child(semester).child(materialId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         String materialUrl = ""+snapshot.child("url").getValue();
                         branch = ""+snapshot.child("branch").getValue();
                         subject = ""+snapshot.child("subject").getValue();
                         topic = ""+snapshot.child("topic").getValue();
                         semester = ""+snapshot.child("semester").getValue();

                        binding.subjectNameEt.setText(subject);
                        binding.topicEt.setText(topic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void validateData() {

        subject = binding.subjectNameEt.getText().toString().trim();
        topic = binding.topicEt.getText().toString().trim();


        if (TextUtils.isEmpty(subject)) {
            Toast.makeText(this, "Enter Subject Name....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(topic)) {
            Toast.makeText(this, "Enter Topic....!", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pick Material Pdf", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToStorage();
        }
    }
    private void uploadPdfToStorage() {
        progressDialog.setMessage("Uploading Material");
        progressDialog.show();

        String filePathAndName = "Material/" + materialId;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedMaterialUrl = ""+uriTask.getResult();

                        uploadPdfInfoToDb(uploadedMaterialUrl, materialId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditMaterialActivity.this, "Material pdf upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfoToDb(String uploadedMaterialUrl, String materialId) {

        progressDialog.setMessage("Uploading Material Pdf into....");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("materialId", ""+materialId);
        hashMap.put("subject", ""+subject);
        hashMap.put("topic", ""+topic);
        hashMap.put("branch", ""+branch);
        hashMap.put("semester", ""+semester);
        hashMap.put("schoolId", ""+schoolId);
        hashMap.put("timestamp", ""+materialId);
        hashMap.put("url", ""+uploadedMaterialUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Material").child(branch).child(semester).child(materialId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(EditMaterialActivity.this, "Material Successfully Updated....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditMaterialActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void pdfPickIntent() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == PDF_PICK_CODE){

                pdfUri = data.getData();

            }
        }
        else{
            Toast.makeText(this, "Cancelled picking pdf", Toast.LENGTH_SHORT).show();

        }
    }
}
