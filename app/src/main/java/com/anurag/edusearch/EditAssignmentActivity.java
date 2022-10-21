package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAddAssignmentBinding;
import com.anurag.edusearch.databinding.ActivityEditAssignmentBinding;
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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditAssignmentActivity extends AppCompatActivity {

    ActivityEditAssignmentBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri pdfUri = null;
    private String assignmentId, branch, year, semester, schoolId, fullMarks;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        schoolId = getIntent().getStringExtra("schoolId");
        assignmentId = getIntent().getStringExtra("assignmentId");
        branch = getIntent().getStringExtra("branch");
        year = getIntent().getStringExtra("year");
        semester = getIntent().getStringExtra("semester");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAssignmentInfo();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.assignmentPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
        binding.updateAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.fullMarksTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditAssignmentActivity.this);
                builder.setTitle("Select Full Marks:")
                        .setItems(Constants.marksCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedMarks = Constants.marksCategories[i];
                                binding.fullMarksTv.setText(selectedMarks);
                            }
                        }).show();
            }
        });
        binding.dueDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickDialog();
            }
        });
    }

    private void loadAssignmentInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String assignmentUrl = ""+snapshot.child("url").getValue();
                        branch = ""+snapshot.child("branch").getValue();
                        year = ""+snapshot.child("year").getValue();
                        semester = ""+snapshot.child("semester").getValue();
                        fullMarks = ""+snapshot.child("fullMarks").getValue();
                        assignmentName = ""+snapshot.child("assignmentName").getValue();
                        dueDate = ""+snapshot.child("dueDate").getValue();
                        assignedBy = ""+snapshot.child("assignedBy").getValue();

                        binding.assignmentNameEt.setText(assignmentName);
                        binding.branchTv.setText(branch);
                        binding.semesterTv.setText(semester);
                        binding.fullMarksTv.setText(fullMarks);
                        binding.yearTv.setText(year);
                        binding.dueDateTv.setText(dueDate);
                        binding.assignedBy.setText(assignedBy);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void datePickDialog() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                DecimalFormat mFormat = new DecimalFormat("00");
                String pDay  =mFormat.format(dayOfMonth);
                String pMonth  =mFormat.format(monthOfYear + 1);
                String pYear  = ""+year;
                String pDate = pDay +"/"+pMonth+"/"+pYear;


                binding.dueDateTv.setText(pDate);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
        datePickerDialog.getDatePicker();
    }

    private String assignmentName, dueDate, assignedBy;
    private void validateData() {

        assignmentName = binding.assignmentNameEt.getText().toString().trim();
        dueDate = binding.dueDateTv.getText().toString().trim();
        assignedBy = binding.assignedBy.getText().toString().trim();
        fullMarks = binding.fullMarksTv.getText().toString().trim();

        if (TextUtils.isEmpty(semester)) {
            Toast.makeText(this, "Select Semester....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(assignmentName)) {
            Toast.makeText(this, "Enter Assignment Name....!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(fullMarks)) {
            Toast.makeText(this, "Enter Full Marks....!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(branch)) {
            Toast.makeText(this, "Select Branch....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Select Year....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(dueDate)) {
            Toast.makeText(this, "Select Due Date....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(assignedBy)) {
            Toast.makeText(this, "Enter Your Signature....!", Toast.LENGTH_SHORT).show();
        }else if (pdfUri == null) {
            Toast.makeText(this, "Pick Result Pdf", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToStorage();
        }
    }
    private void uploadPdfToStorage() {
        progressDialog.setMessage("Updating Assignment");
        progressDialog.show();

        String filePathAndName = "Assignment/" + assignmentId;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedAssignmentUrl = ""+uriTask.getResult();

                        updateAssignmentInfoToDb(uploadedAssignmentUrl, assignmentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditAssignmentActivity.this, "Assignment pdf upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateAssignmentInfoToDb(String uploadedAssignmentUrl, String assignmentId) {

        progressDialog.setMessage("Updating Assignment into....");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("assignmentId", ""+assignmentId);
        hashMap.put("branch", ""+branch);
        hashMap.put("assignmentName", ""+assignmentName);
        hashMap.put("semester", ""+semester);
        hashMap.put("fullMarks", ""+fullMarks);
        hashMap.put("year", ""+year);
        hashMap.put("schoolId", ""+schoolId);
        hashMap.put("dueDate", ""+dueDate);
        hashMap.put("assignedBy", ""+assignedBy);
        hashMap.put("viewsCount", "0");
        hashMap.put("downloadsCount", "0");
        hashMap.put("url", ""+uploadedAssignmentUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(EditAssignmentActivity.this, "Assignment Successfully Updated....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditAssignmentActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

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
