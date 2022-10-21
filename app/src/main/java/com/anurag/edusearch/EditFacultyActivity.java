package com.anurag.edusearch;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityEditFacultyBinding;
import com.anurag.edusearch.databinding.ActivityFacultyDetailsBinding;
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
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class EditFacultyActivity extends AppCompatActivity {

    ActivityEditFacultyBinding binding;
    String schoolId, facultyId;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri image_uri = null;

    private String facultyName, facultyDescription, address, phone, email, subject, qualification, dob, age, linkedln, about, experience;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditFacultyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        schoolId = getIntent().getStringExtra("schoolId");
        facultyId = getIntent().getStringExtra("facultyId");
        loadFacultyDetails();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.facultyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });
        binding.updateFacultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });
        binding.dobEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickDialog();
            }
        });
    }


    private void inputData() {
        facultyName = binding.facultyNameEt.getText().toString().trim();
        facultyDescription = binding.facultyDescriptionEt.getText().toString().trim();
        address = binding.addressEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        subject = binding.subjectEt.getText().toString().trim();
        qualification = binding.qualificationEt.getText().toString().trim();
        dob = binding.dobEt.getText().toString().trim();
        age = binding.ageEt.getText().toString().trim();
        linkedln = binding.linkedlnIdEt.getText().toString().trim();
        about = binding.moreAboutFacultyEt.getText().toString().trim();
        experience = binding.experienceEt.getText().toString().trim();

        if (TextUtils.isEmpty(facultyName)) {
            Toast.makeText(this, "Faculty Name is required....!", Toast.LENGTH_SHORT).show();
            return;
        }else  if (image_uri == null) {
            Toast.makeText(this, "Choose Image...!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(facultyDescription)) {
            Toast.makeText(this, "Faculty Description is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Faculty Address is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Faculty Phone No. is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Faculty Email is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(subject)) {
            Toast.makeText(this, "Faculty Subject Taught is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(qualification)) {
            Toast.makeText(this, "Faculty Qualification is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(dob)) {
            Toast.makeText(this, "Faculty DOB is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(age)) {
            Toast.makeText(this, "Faculty Age is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(experience)) {
            Toast.makeText(this, "Faculty Experience is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        assert user != null;
//        if (user.isEmailVerified()) {
//            addFaculty();
//        }
//        else {
//            Toast.makeText(AddFacultyActivity.this, "Verify your Email first then only you can add product to your Shop", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(AddFacultyActivity.this, ProfileEditAdminActivity.class));
//        }
        updateFaculty();
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


                binding.dobEt.setText(pDate);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
        datePickerDialog.getDatePicker();
    }
    private void updateFaculty() {
        progressDialog.setMessage("Updating Faculty");
        progressDialog.show();

        if (image_uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("facultyName", "" + facultyName);
            hashMap.put("facultyImage", "");
            hashMap.put("facultyDescription", "" + facultyDescription);
            hashMap.put("address", "" + address);
            hashMap.put("phoneNumber", "" + phone);
            hashMap.put("email", "" + email);
            hashMap.put("subject", "" + subject);
            hashMap.put("qualification", "" + qualification);
            hashMap.put("dob", "" + dob);
            hashMap.put("age", "" + age);
            hashMap.put("experience", "" + experience);
            hashMap.put("linkedlnId", "" + linkedln);
            hashMap.put("aboutFaculty", "" + about);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
            reference.child(schoolId).child("Faculty").child(facultyId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(EditFacultyActivity.this, "Faculty Updated Successfully...", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditFacultyActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {

            String filePathAndName = "faculty_images/" + "" + facultyId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("facultyName", "" + facultyName);
                                hashMap.put("facultyDescription", "" + facultyDescription);
                                hashMap.put("facultyImage", "" + downloadImageUri);
                                hashMap.put("address", "" + address);
                                hashMap.put("phoneNumber", "" + phone);
                                hashMap.put("email", "" + email);
                                hashMap.put("subject", "" + subject);
                                hashMap.put("qualification", "" + qualification);
                                hashMap.put("dob", "" + dob);
                                hashMap.put("age", "" + age);
                                hashMap.put("experience", "" + experience);
                                hashMap.put("linkedlnId", "" + linkedln);
                                hashMap.put("aboutFaculty", "" + about);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
                                reference.child(schoolId).child("Faculty").child(facultyId)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditFacultyActivity.this, "Faculty Updated Successfully...", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditFacultyActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditFacultyActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraActivityResultLauncher.launch(intent);

    }

    private void pickImageGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri);
                        Intent data = result.getData();

                        binding.facultyIv.setImageURI(image_uri);
                    } else {
                        Toast.makeText(EditFacultyActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri);
                        Intent data = result.getData();
                        image_uri = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri);

                        binding.facultyIv.setImageURI(image_uri);
                    } else {
                        Toast.makeText(EditFacultyActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,

                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera();
                    } else {
                        Toast.makeText(this, "Camera permission are necessary...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickImageGallery();
                    } else {
                        Toast.makeText(this, "Storage permission is necessary...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadFacultyDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Faculty").child(facultyId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        facultyId = "" + snapshot.child("facultyId").getValue();
                        facultyName = "" + snapshot.child("facultyName").getValue();
                        facultyDescription = "" + snapshot.child("facultyDescription").getValue();
                        schoolId = "" + snapshot.child("schoolId").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String facultyImage = "" + snapshot.child("facultyImage").getValue();
                        address = "" + snapshot.child("address").getValue();
                        phone = "" + snapshot.child("phoneNumber").getValue();
                        email = "" + snapshot.child("email").getValue();
                        subject = "" + snapshot.child("subject").getValue();
                        qualification = "" + snapshot.child("qualification").getValue();
                        dob = "" + snapshot.child("dob").getValue();
                        age = "" + snapshot.child("age").getValue();
                        experience = "" + snapshot.child("experience").getValue();
                        linkedln = "" + snapshot.child("linkedlnId").getValue();
                        about = "" + snapshot.child("aboutFaculty").getValue();

                        binding.facultyNameEt.setText(facultyName);
                        binding.facultyDescriptionEt.setText(facultyDescription);
                        binding.addressEt.setText(address);
                        binding.phoneEt.setText(phone);
                        binding.emailEt.setText(email);
                        binding.subjectEt.setText(subject);
                        binding.qualificationEt.setText(qualification);
                        binding.dobEt.setText(dob);
                        binding.ageEt.setText(age);
                        binding.linkedlnIdEt.setText(linkedln);
                        binding.moreAboutFacultyEt.setText(about);
                        binding.experienceEt.setText(experience);

                        try {
                            Picasso.get().load(facultyImage).placeholder(R.drawable.ic_person_gray).into(binding.facultyIv);

                        } catch (Exception e) {
                            binding.facultyIv.setImageResource(R.drawable.ic_person_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}