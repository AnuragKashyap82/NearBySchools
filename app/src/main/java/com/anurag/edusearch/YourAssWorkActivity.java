package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAssignmentViewBinding;
import com.anurag.edusearch.databinding.ActivityYourAssWorkBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class YourAssWorkActivity extends AppCompatActivity {

    ActivityYourAssWorkBinding binding;
    private FirebaseAuth firebaseAuth;
    private String assignmentId, schoolId, semester, branch, year;
    private Uri pdfUri = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityYourAssWorkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        schoolId = getIntent().getStringExtra("schoolId");
        assignmentId = getIntent().getStringExtra("assignmentId");
        semester = getIntent().getStringExtra("semester");
        branch = getIntent().getStringExtra("branch");
        year = getIntent().getStringExtra("year");

        loadMyAssignment();
    }
    private void loadMyAssignment() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission").child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String myAssignmentUrl = ""+snapshot.child("url").getValue();

//                        binding.assignmentNameTv.setText(assignmentName);
                        loadMaterialFromUrl(myAssignmentUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadMaterialFromUrl(String myAssignmentUrl) {

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(myAssignmentUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        binding.myAssPdfView.fromBytes(bytes)
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
                                        Toast.makeText(YourAssWorkActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(YourAssWorkActivity.this, "Error on page"+page+" "+t.getMessage(), Toast.LENGTH_SHORT).show();
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
}