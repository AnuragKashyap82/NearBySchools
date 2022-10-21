package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityAddUniqueIdBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddUniqueIdActivity extends AppCompatActivity {

    private ActivityAddUniqueIdBinding binding;
    private String uniqueId, phone;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUniqueIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.addUniqueIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        uniqueId = binding.uniqueIdEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();

        if (TextUtils.isEmpty(uniqueId)) {
            Toast.makeText(this, "Enter Unique Id Of the Student....!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter Phone No. Of the Student....!", Toast.LENGTH_SHORT).show();
        } else {
            addUniqueId();
        }
    }

    private void addUniqueId() {
        progressDialog.setMessage("Uploading Unique_Id");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uniqueId",""+uniqueId);
        hashMap.put("phone",""+phone);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UniqueId");
        ref.child(uniqueId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        clearText();
                        Toast.makeText(AddUniqueIdActivity.this, "Unique_Id Successfully Uploaded....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddUniqueIdActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearText() {
        binding.uniqueIdEt.setText("");
        binding.phoneEt.setText("");
    }
}