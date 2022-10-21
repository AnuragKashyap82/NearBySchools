package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityVerifyUniqueIdBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifyUniqueIdActivity extends AppCompatActivity {

    private ActivityVerifyUniqueIdBinding binding;
    private ProgressDialog progressDialog;
    private boolean isUniqueId;
    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyUniqueIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.phoneEt.setEnabled(false);
        binding.sendOtpBtn.setEnabled(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyUniqueIdActivity.this, NeedHelpActivity.class));
            }
        });
    }
    String uniqueId;
    private void validateData() {
        uniqueId = binding.uniqueIdEt.getText().toString().trim();

        if (TextUtils.isEmpty(uniqueId)) {
            Toast.makeText(this, "Enter Your Unique Id....!", Toast.LENGTH_SHORT).show();
        } else {
            checkExistingUniqueId();
        }
    }

    private void checkExistingUniqueId() {
        progressDialog.setMessage("Verifying Unique_Id");
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("RegisteredUniqueId");
        ref.child(uniqueId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isRegistered = snapshot.exists();
                        if (isRegistered) {
                            progressDialog.dismiss();
                            Toast.makeText(VerifyUniqueIdActivity.this, "This Unique_Id is already Registered...!!", Toast.LENGTH_SHORT).show();
                        }else {
                            verifyUniqueId();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void verifyUniqueId() {
        progressDialog.setMessage("Verifying Unique_Id");
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UniqueId");
        ref.child(uniqueId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isUniqueId = snapshot.exists();
                        if (isUniqueId){
//                            Intent intent = new Intent(VerifyUniqueIdActivity.this, RegisterActivity.class);
//                            intent.putExtra("uniqueId", uniqueId);
//                            startActivity(intent);
                            progressDialog.dismiss();
                            verifyPhoneNumber();
                            binding.sendOtpBtn.setEnabled(true);
                            Toast.makeText(VerifyUniqueIdActivity.this, "Unique-Id Verified....!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(VerifyUniqueIdActivity.this, "Unique-Id Not Found....!", Toast.LENGTH_SHORT).show();
                        }
                        String  phone = ""+snapshot.child("phone").getValue();
                        binding.phoneEt.setText(phone);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void verifyPhoneNumber() {
        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePhoneNumber();
            }
        });
    }
    private String phoneNumber;
    private void validatePhoneNumber() {

        phoneNumber = binding.phoneEt.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Enter Your Phone Number....", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Intent intent = new Intent(VerifyUniqueIdActivity.this, OtpActivity.class);
            intent.putExtra("phoneNumber", binding.phoneEt.getText().toString());
            intent.putExtra("uniqueId", binding.uniqueIdEt.getText().toString());
            startActivity(intent);
        }
    }
}
