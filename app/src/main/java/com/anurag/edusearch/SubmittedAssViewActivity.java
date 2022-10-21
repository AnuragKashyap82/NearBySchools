package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.app.AlertDialog;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivitySubmittedAssViewBinding;
import com.anurag.edusearch.databinding.DialogMarksObtainedBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import static com.anurag.edusearch.Constants.MAX_BYTES_PDF;

public class SubmittedAssViewActivity extends AppCompatActivity {

    ActivitySubmittedAssViewBinding binding;
    private FirebaseAuth firebaseAuth;
    private String submittedAssUrl, schoolId, assignmentId, branch, semester, dueDate, fullMarks, year, uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivitySubmittedAssViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        schoolId = getIntent().getStringExtra("schoolId");
        submittedAssUrl = getIntent().getStringExtra("submittedAssUrl");
        assignmentId = getIntent().getStringExtra("assignmentId");
        branch = getIntent().getStringExtra("branch");
        semester = getIntent().getStringExtra("semester");
        dueDate = getIntent().getStringExtra("dueDate");
        year = getIntent().getStringExtra("year");
        uid = getIntent().getStringExtra("uid");

        loadSubmittedAssFromUrl();
        checkUser();
        loadMarks();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.addMarksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogMarksObtainedBinding marksAddBinding = DialogMarksObtainedBinding.inflate(LayoutInflater.from(SubmittedAssViewActivity.this));

                AlertDialog.Builder builder = new AlertDialog.Builder(SubmittedAssViewActivity.this, R.style.CustomDialog);
                builder.setView(marksAddBinding.getRoot());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                marksAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                marksAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String marksObtained = marksAddBinding.marksEt.getText().toString().trim();
                        if (TextUtils.isEmpty(marksObtained)) {
                            Toast.makeText(SubmittedAssViewActivity.this, "Enter His Marks Obtained....!", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            uploadObtainedMarks(marksObtained);
                        }
                    }
                });
            }
        });
    }

    private void uploadObtainedMarks(String marksObtained) {

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("marksObtained", ""+marksObtained);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission").child(uid)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SubmittedAssViewActivity.this, "Marks Uploaded....!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SubmittedAssViewActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadSubmittedAssFromUrl() {
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(submittedAssUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        binding.submittedAssPdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page + 1);
                                        binding.toolbarSubtitleTv.setText(currentPage + "/"+pageCount);

                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(SubmittedAssViewActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(SubmittedAssViewActivity.this, "Error on page"+page+" "+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
    private void loadMarks() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String fullMarks = ""+snapshot.child("fullMarks").getValue();
                        String marksObtained = ""+snapshot.child("marksObtained").getValue();

                        binding.fullMarksTv.setText(fullMarks);
                        binding.obtainedMarksTv.setText(marksObtained);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String  userType = ""+snapshot.child("userType").getValue();
                        String  name = ""+snapshot.child("name").getValue();

                        if (userType.equals("user")){
                            binding.nameTv.setText(name);
                        }
                        else if (userType.equals("admin")){
                            binding.nameTv.setText(name);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
