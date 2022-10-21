package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAddNoticeBinding;
import com.anurag.edusearch.databinding.ActivityEditNoticeBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
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

public class EditNoticeActivity extends AppCompatActivity {

    ActivityEditNoticeBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String schoolId, noticeId;
    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        schoolId = getIntent().getStringExtra("schoolId");
        noticeId = getIntent().getStringExtra("noticeId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadNoticeDetails();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        binding.updateNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void loadNoticeDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Notice").child(noticeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String noticeUrl = ""+snapshot.child("url").getValue();
                        String noticeTitle = ""+snapshot.child("title").getValue();
                        String noticeNumber = ""+snapshot.child("number").getValue();

                        binding.noticeTitleEt.setText(noticeTitle);
                        binding.noticeNoEt.setText(noticeNumber);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String title = "", number = "";
    private void validateData() {

        title = binding.noticeTitleEt.getText().toString().trim();
        number = binding.noticeNoEt.getText().toString().trim();


        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Notice Title....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Enter Notice Number....", Toast.LENGTH_SHORT).show();
        }else if (pdfUri == null) {
            Toast.makeText(this, "Pick Notice Pdf", Toast.LENGTH_SHORT).show();
        } else {
            updatePdfToStorage();
        }
    }
    private void updatePdfToStorage() {
        progressDialog.setMessage("Uploading Pdf");
        progressDialog.show();


        String filePathAndName = "Notice/" + noticeId;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedPdfUrl = ""+uriTask.getResult();

                        uploadPdfInfoToDb(uploadedPdfUrl, noticeId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditNoticeActivity.this, "Notice pdf upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, String noticeId) {

        progressDialog.setMessage("Updating Notice Pdf into....");

        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("NoticeId", ""+noticeId);
        hashMap.put("title", ""+title);
        hashMap.put("number", ""+number);
        hashMap.put("schoolId", ""+schoolId);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("url", ""+uploadedPdfUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Notice").child(noticeId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(EditNoticeActivity.this, "Notice Successfully Updated....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditNoticeActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

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
