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
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivitySchoolEditBinding;
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

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class SchoolEditActivity extends AppCompatActivity {

    private ActivitySchoolEditBinding binding;

    private String schoolId;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri image_uri = null;

    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadDistrictCategories();
        loadSchoolInfo();

        binding.districtTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                districtPickDialog();
            }
        });
        binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.updateSchoolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.boardEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SchoolEditActivity.this);
                builder.setTitle("Choose Board:")
                        .setItems(Constants.boardCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedBoard = Constants.boardCategories[i];
                                binding.boardEt.setText(selectedBoard);
                            }
                        }).show();
            }
        });
    }
    private String name, board, email,  phone, website, address, latitude, longitude, area,
            facultyNo, principal, vicePrincipal, code, schoolType, founder, degree, ranking, timing,
            nursery, oneToFour, fiveToSeven, eightToTen, elevenToTwelve;
    private void validateData() {

        name = binding.schoolNameEt.getText().toString().trim();
        board = binding.boardEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
        website = binding.schoolWebsiteEt.getText().toString().trim();
        address = binding.addressEt.getText().toString().trim();
        latitude = binding.latitudeEt.getText().toString().trim();
        longitude = binding.longitudeEt.getText().toString().trim();
        area = binding.areaEt.getText().toString().trim();
        facultyNo = binding.facultyNoEt.getText().toString().trim();
        principal = binding.principalEt.getText().toString().trim();
        vicePrincipal = binding.vicePrinEt.getText().toString().trim();
        code = binding.schoolCodeEt.getText().toString().trim();
        schoolType = binding.schoolType.getText().toString().trim();
        founder = binding.founderEt.getText().toString().trim();
        degree = binding.highestDegreeEt.getText().toString().trim();
        ranking = binding.rankingEt.getText().toString().trim();
        timing = binding.timingEt.getText().toString().trim();
        nursery = binding.nurseryEt.getText().toString().trim();
        oneToFour = binding.oneToFourEt.getText().toString().trim();
        fiveToSeven = binding.fiveToSevenEt.getText().toString().trim();
        eightToTen = binding.eightToTenEt.getText().toString().trim();
        elevenToTwelve = binding.elevenToTwelveEt.getText().toString().trim();


        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter School Name...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(board)) {
            Toast.makeText(this, "Select School Board...!", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter Email....", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Choose District...!", Toast.LENGTH_SHORT).show();
        }else if (image_uri == null) {
            Toast.makeText(this, "Choose Image...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter Phone No...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(website)) {
            Toast.makeText(this, "Add website of the School...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Enter Address of School...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(latitude)) {
            Toast.makeText(this, "Enter latitude of School...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(longitude)) {
            Toast.makeText(this, "Enter longitude of School...!", Toast.LENGTH_SHORT).show();
        }
        else {
            updateSchool();
        }
    }
    private void updateSchool() {
        progressDialog.setMessage("Updating School");
        progressDialog.show();


        if (image_uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("schoolName", "" + name);
            hashMap.put("board", "" + board);
            hashMap.put("email", "" + email);
            hashMap.put("phoneNumber", "" + phone );
            hashMap.put("website", "" + website );
            hashMap.put("districtId", "" + selectedCategoryId);
            hashMap.put("district", "" + selectedCategoryTitle);
            hashMap.put("schoolImage", "");
            hashMap.put("address", "" + address);
            hashMap.put("latitude", "" + latitude);
            hashMap.put("longitude", "" + longitude);
            hashMap.put("area", "" + area);
            hashMap.put("numberFaculty", "" + facultyNo);
            hashMap.put("principal", "" + principal);
            hashMap.put("vicePrincipal", "" + vicePrincipal);
            hashMap.put("code", "" + code);
            hashMap.put("schoolType", "" + schoolType);
            hashMap.put("founder", "" + founder);
            hashMap.put("highestDegree", "" + degree);
            hashMap.put("ranking", "" + ranking);
            hashMap.put("timing", "" + timing);
            hashMap.put("nurseryUkg", "" + nursery);
            hashMap.put("oneFour", "" + oneToFour);
            hashMap.put("fiveSeven", "" + fiveToSeven);
            hashMap.put("eightTen", "" + eightToTen);
            hashMap.put("elevenTwelve", "" + elevenToTwelve);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
            ref.child(schoolId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(SchoolEditActivity.this, "School Updated Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SchoolEditActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {

            String filePathAndName = "school_images/" + "" + schoolId;
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
                                hashMap.put("schoolName", "" + name);
                                hashMap.put("board", "" + board);
                                hashMap.put("email", "" + email);
                                hashMap.put("phoneNumber", "" + phone );
                                hashMap.put("website", "" + website );
                                hashMap.put("districtId", "" + selectedCategoryId);
                                hashMap.put("district", "" + selectedCategoryTitle);
                                hashMap.put("schoolImage", ""+ downloadImageUri);
                                hashMap.put("address", "" + address);
                                hashMap.put("latitude", "" + latitude);
                                hashMap.put("longitude", "" + longitude);
                                hashMap.put("area", "" + area);
                                hashMap.put("numberFaculty", "" + facultyNo);
                                hashMap.put("principal", "" + principal);
                                hashMap.put("vicePrincipal", "" + vicePrincipal);
                                hashMap.put("code", "" + code);
                                hashMap.put("schoolType", "" + schoolType);
                                hashMap.put("founder", "" + founder);
                                hashMap.put("highestDegree", "" + degree);
                                hashMap.put("ranking", "" + ranking);
                                hashMap.put("timing", "" + timing);
                                hashMap.put("nurseryUkg", "" + nursery);
                                hashMap.put("oneFour", "" + oneToFour);
                                hashMap.put("fiveSeven", "" + fiveToSeven);
                                hashMap.put("eightTen", "" + eightToTen);
                                hashMap.put("elevenTwelve", "" + elevenToTwelve);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
                                ref.child(schoolId)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SchoolEditActivity.this, "School Updated Successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SchoolEditActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SchoolEditActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
    private void loadSchoolInfo() {
        DatabaseReference refBook = FirebaseDatabase.getInstance().getReference("Schools");
        refBook.child(schoolId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String schoolName = "" + snapshot.child("schoolName").getValue();
                        String board = "" + snapshot.child("board").getValue();
                        String address = "" + snapshot.child("address").getValue();
                        String schoolLatitude = "" + snapshot.child("latitude").getValue();
                        String schoolLongitude = "" + snapshot.child("longitude").getValue();
                        String schoolId = "" + snapshot.child("schoolId").getValue();
                        String schoolImage = "" + snapshot.child("schoolImage").getValue();
                        String email = "" + snapshot.child("email").getValue();
                        String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                        String website = ""+snapshot.child("website").getValue();
                        String districtId = "" + snapshot.child("districtId").getValue();
                        String district = "" + snapshot.child("district").getValue();
                        String uid = "" + snapshot.child("uid").getValue();
                        String aboutSchool = "" + snapshot.child("aboutSchool").getValue();
                        String area = "" + snapshot.child("area").getValue();
                        String noFaculty = "" + snapshot.child("numberFaculty").getValue();
                        String principal = "" + snapshot.child("principal").getValue();
                        String vicePrincipal = "" + snapshot.child("vicePrincipal").getValue();
                        String code = "" + snapshot.child("code").getValue();
                        String schoolType = "" + snapshot.child("schoolType").getValue();
                        String founder = "" + snapshot.child("founder").getValue();
                        String highestDegree = "" + snapshot.child("highestDegree").getValue();
                        String ranking = "" + snapshot.child("ranking").getValue();
                        String timing = "" + snapshot.child("timing").getValue();
                        String nursery = "" + snapshot.child("nurseryUkg").getValue();
                        String oneFour = "" + snapshot.child("oneFour").getValue();
                        String fiveSeven = "" + snapshot.child("fiveSeven").getValue();
                        String eightTen = "" + snapshot.child("eightTen").getValue();
                        String elevenTwelve = "" + snapshot.child("elevenTwelve").getValue();


                        binding.schoolNameEt.setText(schoolName);
                        binding.boardEt.setText(board);
                        binding.addressEt.setText(address);
                        binding.latitudeEt.setText(schoolLatitude);
                        binding.longitudeEt.setText(schoolLongitude);
                        binding.emailEt.setText(email);
                        binding.phoneEt.setText(phoneNumber);
                        binding.schoolWebsiteEt.setText(website);
                        binding.districtTv.setText(district);
                        binding.areaEt.setText(area);
                        binding.facultyNoEt.setText(noFaculty);
                        binding.principalEt.setText(principal);
                        binding.vicePrinEt.setText(vicePrincipal);
                        binding.schoolCodeEt.setText(code);
                        binding.schoolType.setText(schoolType);
                        binding.founderEt.setText(founder);
                        binding.highestDegreeEt.setText(highestDegree);
                        binding.rankingEt.setText(ranking);
                        binding.timingEt.setText(timing);
                        binding.nurseryEt.setText(nursery);
                        binding.oneToFourEt.setText(oneFour);
                        binding.fiveToSevenEt.setText(fiveSeven);
                        binding.eightToTenEt.setText(eightTen);
                        binding.elevenToTwelveEt.setText(elevenTwelve);

                        try {
                            Picasso.get().load(schoolImage).placeholder(R.drawable.ic_school_primary).into(binding.profileIv);
                        }
                        catch (Exception e){
                            binding.profileIv.setImageResource(R.drawable.ic_school_primary);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadDistrictCategories() {
        Log.d(TAG, "loadDistrictCategories: Loading District categories....");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Districts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    String categoryId = ""+ds.child("id").getValue();
                    String categoryTitle= ""+ds.child("category").getValue();


                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private  String selectedCategoryId, selectedCategoryTitle;
    private void districtPickDialog() {
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick District")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        selectedCategoryTitle = categoryTitleArrayList.get(i);
                        selectedCategoryId = categoryIdArrayList.get(i);
                        binding.districtTv.setText(selectedCategoryTitle);
                    }
                })
                .show();
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

                        binding.profileIv.setImageURI(image_uri);
                    } else {
                        Toast.makeText(SchoolEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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

                        binding.profileIv.setImageURI(image_uri);
                    } else {
                        Toast.makeText(SchoolEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickImageCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera permission are necessary...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>1){
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){

                        pickImageGallery();
                    }
                    else {
                        Toast.makeText(this, "Storage permission is necessary...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


}