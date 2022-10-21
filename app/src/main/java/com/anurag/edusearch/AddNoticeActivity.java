package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAddNoticeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNoticeActivity extends AppCompatActivity {

    ActivityAddNoticeBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String schoolId;
    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        schoolId = getIntent().getStringExtra("schoolId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


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

        binding.uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
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
            uploadPdfToStorage();
        }
    }
    private void uploadPdfToStorage() {
        progressDialog.setMessage("Uploading Notice");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Notice/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedPdfUrl = ""+uriTask.getResult();

                        uploadPdfInfoToDb(uploadedPdfUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddNoticeActivity.this, "Notice pdf upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timestamp) {

        progressDialog.setMessage("Uploading Notice Pdf into....");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("NoticeId", ""+timestamp);
        hashMap.put("title", ""+title);
        hashMap.put("number", ""+number);
        hashMap.put("schoolId", ""+schoolId);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("url", ""+uploadedPdfUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Notice").child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all", "New Notice Uploaded", title, getApplicationContext(), AddNoticeActivity.this);
                        notificationsSender.SendNotifications();

                        Toast.makeText(AddNoticeActivity.this, "Notice Successfully uploaded....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddNoticeActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

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

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PDF_PICK_CODE) {

                pdfUri = data.getData();

            }
        } else {
            Toast.makeText(this, "Cancelled picking pdf", Toast.LENGTH_SHORT).show();

        }
    }
}