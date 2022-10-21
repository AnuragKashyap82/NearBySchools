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
import com.anurag.edusearch.databinding.ActivityAddResultBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddResultActivity extends AppCompatActivity {

    ActivityAddResultBinding binding;
    private String schoolId;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddResultBinding.inflate(getLayoutInflater());
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

        binding.resultPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
        binding.uploadResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.branchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddResultActivity.this);
                builder.setTitle("Select Branch:")
                        .setItems(Constants.branchCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedBranch = Constants.branchCategories[i];
                                binding.branchTv.setText(selectedBranch);
                            }
                        }).show();
            }
        });
        binding.semesterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddResultActivity.this);
                builder.setTitle("Select Semester:")
                        .setItems(Constants.semesterCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedSemester = Constants.semesterCategories[i];
                                binding.semesterTv.setText(selectedSemester);
                            }
                        }).show();
            }
        });
        binding.yearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddResultActivity.this);
                builder.setTitle("Select Year:")
                        .setItems(Constants.yearCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedYear = Constants.yearCategories[i];
                                binding.yearTv.setText(selectedYear);
                            }
                        }).show();
            }
        });
    }

    private String semester, branch, year;
    private void validateData() {

        semester = binding.semesterTv.getText().toString().trim();
        branch = binding.branchTv.getText().toString().trim();
        year = binding.yearTv.getText().toString().trim();

        if (TextUtils.isEmpty(semester)) {
            Toast.makeText(this, "Select Semester....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(branch)) {
            Toast.makeText(this, "Select Branch....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Select Year....!", Toast.LENGTH_SHORT).show();
        }else if (pdfUri == null) {
            Toast.makeText(this, "Pick Result Pdf", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToStorage();
        }
    }
    private void uploadPdfToStorage() {
        progressDialog.setMessage("Uploading Result");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Result/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedResultUrl = ""+uriTask.getResult();

                        uploadResultInfoToDb(uploadedResultUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddResultActivity.this, "Result pdf upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void uploadResultInfoToDb(String uploadedResultUrl, long timestamp) {

        progressDialog.setMessage("Uploading Result Pdf into....");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("resultId", ""+timestamp);
        hashMap.put("branch", ""+branch);
        hashMap.put("semester", ""+semester);
        hashMap.put("year", ""+year);
        hashMap.put("schoolId", ""+schoolId);
        hashMap.put("viewsCount", "0");
        hashMap.put("downloadsCount", "0");
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("url", ""+uploadedResultUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Results").child(year).child(branch).child(semester)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(AddResultActivity.this, "Result Successfully uploaded....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddResultActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

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
