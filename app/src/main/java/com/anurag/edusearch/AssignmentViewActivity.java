package com.anurag.edusearch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAssignmentViewBinding;
import com.anurag.edusearch.databinding.ActivityMaterialVievBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import static com.anurag.edusearch.Constants.MAX_BYTES_PDF;

public class AssignmentViewActivity extends AppCompatActivity {

    ActivityAssignmentViewBinding binding;
    private FirebaseAuth firebaseAuth;
    private String assignmentId, schoolId, assignmentUrl, semester, branch, year, dueDate, fullMarks, marksObtained;
    private Uri pdfUri = null;
    boolean isCompletedAssignment = false;
    private ProgressDialog progressDialog;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityAssignmentViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        schoolId = getIntent().getStringExtra("schoolId");
        assignmentUrl = getIntent().getStringExtra("assignmentUrl");
        assignmentId = getIntent().getStringExtra("assignmentId");
        semester = getIntent().getStringExtra("semester");
        branch = getIntent().getStringExtra("branch");
        dueDate = getIntent().getStringExtra("dueDate");
        year = getIntent().getStringExtra("year");
        fullMarks = getIntent().getStringExtra("fullMarks");

        checkUser();
        loadAssignment();

        binding.deleteAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentViewActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete Assignment: " + assignmentId + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteAssignment();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.editAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentViewActivity.this, EditAssignmentActivity.class);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("branch", branch);
                intent.putExtra("year", year);
                intent.putExtra("semester", semester);
                startActivity(intent);
                Toast.makeText(AssignmentViewActivity.this, "Edit Material Btn Clicked....!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.downloadAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentViewActivity.this);
                builder.setTitle("Download")
                        .setMessage("Do you want to Download Notice: " + assignmentId+ " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ContextCompat.checkSelfPermission(AssignmentViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    downloadAssignment(AssignmentViewActivity.this, "" + schoolId, "" + assignmentId, ""+branch, ""+assignmentUrl);

                                } else {

                                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
        binding.addAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsBottomSheet(dueDate, fullMarks);
            }
        });
    }
    private void detailsBottomSheet(String dueDate, String fullMarks) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AssignmentViewActivity.this);

        View view = LayoutInflater.from(AssignmentViewActivity.this).inflate(R.layout.bs_add_assignment, null);
        bottomSheetDialog.setContentView(view);

        TextView dueDateTv = view.findViewById(R.id.dueDateTv);
        Button submitAssignmentBtn = view.findViewById(R.id.submitAssignmentBtn);
        Button submittedAssignmentBtn = view.findViewById(R.id.submittedAssignmentBtn);
        Button assWorkViewBtn = view.findViewById(R.id.assWorkViewBtn);
        ImageButton pdfPickBtn = view.findViewById(R.id.pdfPickBtn);
        TextView completedTv = view.findViewById(R.id.completedTv);
        TextView notCompletedTv = view.findViewById(R.id.notCompletedTv);
        TextView fullMarksTv = view.findViewById(R.id.fullMarksTv);
        TextView obtainedMarksTv = view.findViewById(R.id.obtainedMarksTv);

        dueDateTv.setText(dueDate);
        bottomSheetDialog.show();
        fullMarksTv.setText(fullMarks);

        loadMarks(obtainedMarksTv);

        completedTv.setVisibility(View.GONE);
        checkCompleted(completedTv, notCompletedTv, submitAssignmentBtn, assWorkViewBtn);
        checkUsers(submittedAssignmentBtn);

        pdfPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        submitAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
                bottomSheetDialog.dismiss();
            }
        });
        assWorkViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentViewActivity.this, YourAssWorkActivity.class);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("branch", branch);
                intent.putExtra("year", year);
                intent.putExtra("semester", semester);
                intent.putExtra("assignmentUrl", assignmentUrl);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });
        submittedAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentViewActivity.this, SubmittedAssignmentActivity.class);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("branch", branch);
                intent.putExtra("year", year);
                intent.putExtra("semester", semester);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void checkUsers(Button submittedAssignmentBtn) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String  userType = ""+snapshot.child("userType").getValue();

                        if (userType.equals("user")){
                            submittedAssignmentBtn.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            submittedAssignmentBtn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkCompleted(TextView completedTv, TextView notCompletedTv, Button submitAssignmentBtn, Button assWorkViewBtn) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isCompletedAssignment = snapshot.exists();
                        if (isCompletedAssignment){
                            completedTv.setVisibility(View.VISIBLE);
                            notCompletedTv.setVisibility(View.GONE);
                            submitAssignmentBtn.setText("Replace Your Work");
                            assWorkViewBtn.setVisibility(View.VISIBLE);
                        }
                        else{
                            completedTv.setVisibility(View.GONE);
                            notCompletedTv.setVisibility(View.VISIBLE);
                            submitAssignmentBtn.setVisibility(View.VISIBLE);
                            assWorkViewBtn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
    private void validateData() {
        if (pdfUri == null) {
            Toast.makeText(this, "Pick Your Assignment....!", Toast.LENGTH_SHORT).show();
        }else {
            uploadAssignmentToStorage();
        }
    }
    private void uploadAssignmentToStorage() {
        progressDialog.setMessage("Uploading Assignment");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Assignment/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedAssignmentUrl = ""+uriTask.getResult();

                        uploadAssignmentWorkInfoToDb(uploadedAssignmentUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AssignmentViewActivity.this, "Assignment Work upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void uploadAssignmentWorkInfoToDb(String uploadedAssignmentUrl, long timestamp) {

        progressDialog.setMessage("Uploading Assignment....!");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("assignmentId", ""+assignmentId);
        hashMap.put("branch", ""+branch);
        hashMap.put("semester", ""+semester);
        hashMap.put("year", ""+year);
        hashMap.put("schoolId", ""+schoolId);
        hashMap.put("dueDate", ""+dueDate);
        hashMap.put("fullMarks", ""+fullMarks);
        hashMap.put("marksObtained", "");
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("url", ""+uploadedAssignmentUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission").child(firebaseAuth.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(AssignmentViewActivity.this, "Work Successfully uploaded....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AssignmentViewActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void deleteAssignment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AssignmentViewActivity.this, "Assignment Deleted...!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AssignmentViewActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadAssignment() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String assignmentUrl = ""+snapshot.child("url").getValue();
                        String assignmentName = ""+snapshot.child("assignmentName").getValue();
                        dueDate = ""+snapshot.child("dueDate").getValue();

                        binding.assignmentNameTv.setText(assignmentName);
                        loadMaterialFromUrl(assignmentUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadMaterialFromUrl(String assignmentUrl) {

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(assignmentUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        binding.assignmentPdfView.fromBytes(bytes)
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
                                        Toast.makeText(AssignmentViewActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(AssignmentViewActivity.this, "Error on page"+page+" "+t.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void loadMarks(TextView obtainedMarksTv) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission").child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String marksObtained = ""+snapshot.child("marksObtained").getValue();

                        obtainedMarksTv.setText(marksObtained);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    downloadAssignment(AssignmentViewActivity.this, "" + schoolId, "" + assignmentId, ""+branch, ""+assignmentUrl);
                } else {
                    Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
                }
            });
    public static void downloadAssignment(Context context, String schoolId, String assignmentId, String branch, String assignmentUrl) {

        String nameWithExtension = branch + ".pdf";

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Downloading" + nameWithExtension + "....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(assignmentUrl);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        saveDownloadedBook(context, progressDialog, bytes, nameWithExtension, assignmentId, branch, schoolId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to download due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private static void saveDownloadedBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String assignmentId, String branch, String schoolId) {
        try {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdir();

            String filePath = downloadsFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        } catch (Exception e) {
            Toast.makeText(context, "Failed saving to download folder due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String  userType = ""+snapshot.child("userType").getValue();

                        if (userType.equals("user")){
                            binding.fb.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.fb.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}