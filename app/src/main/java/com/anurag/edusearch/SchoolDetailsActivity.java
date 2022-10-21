package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivitySchoolDetailsBinding;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SchoolDetailsActivity extends AppCompatActivity {

    private ActivitySchoolDetailsBinding binding;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String schoolId;

    private String myLatitude, myLongitude;
    private String phoneNumber, schoolLatitude, schoolLongitude;
    ImageSlider mainSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schoolId = getIntent().getStringExtra("schoolId");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        mainSlider = (ImageSlider) findViewById(R.id.slider);

        loadSliderImages();

        loadSchoolDetails();
        loadMyInfo();
        loadReviews();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhone();
            }
        });
        binding.mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });
        binding.aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, AboutSchoolDetails.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, SchoolReviewsActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, WriteReviewActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, GalleryCategoryActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.admissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, AdmissionActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
                Toast.makeText(SchoolDetailsActivity.this, "Admission Btn Clicked...!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.facultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, SchoolFacultyAdminActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.noticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, NoticeActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.materialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, StudyMaterialActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.resultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, ResultActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.assignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, AssignmentActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.hostelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, HostelActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.addSliderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolDetailsActivity.this, AddSliderActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
            }
        });
        binding.deleteSliderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SchoolDetailsActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete Slider")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteSlider();
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
    }
    private void deleteSlider() {
        progressDialog.setMessage("Deleting Slider");
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Slider")
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(SchoolDetailsActivity.this, "Slider Deleted...!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SchoolDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    final List<SlideModel> schoolBanner = new ArrayList<>();

    private void loadSliderImages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Slider")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot data : snapshot.getChildren())
                            schoolBanner.add(new SlideModel(data.child("url").getValue().toString(), data.child("title").getValue().toString(),  ScaleTypes.FIT));

                        mainSlider.setImageList(schoolBanner, ScaleTypes.FIT);
                        mainSlider.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int i) {
//                                String  title = schoolBanner.get(i).getTitle().toString();
//                                String  url = schoolBanner.get(i).getImageUrl().toString();
//
//                                Intent intent = new Intent(SchoolDetailsActivity.this, SliderEditActivity.class);
//                                intent.putExtra("title", title);
//                                intent.putExtra("url", url);
//                                intent.putExtra("schoolId", schoolId);
//                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), schoolBanner.get(i).getTitle().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadSchoolDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String schoolName = "" + snapshot.child("schoolName").getValue();
                        String board = "" + snapshot.child("board").getValue();
                        String address = "" + snapshot.child("address").getValue();
                        schoolLatitude = "" + snapshot.child("latitude").getValue();
                        schoolLongitude = "" + snapshot.child("longitude").getValue();
                        String schoolId = "" + snapshot.child("schoolId").getValue();
                        String schoolImage = "" + snapshot.child("schoolImage").getValue();
                        String email = "" + snapshot.child("email").getValue();
                        phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                        String website = "" + snapshot.child("website").getValue();
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

                        binding.schoolNameTv.setText(schoolName);
                        binding.boardTv.setText(board);
                        binding.addressTv.setText(address);
                        binding.emailTv.setText(email);
                        binding.phoneTv.setText(phoneNumber);
                        binding.schoolWebsiteTv.setText(website);
                        binding.districtTv.setText(district);
                        binding.schoolDetailsTv.setText(aboutSchool);
                        binding.areaTv.setText(area + " Acre");
                        binding.facultyTv.setText(noFaculty + "+");
                        binding.principalTv.setText(principal);
                        binding.vicePrincipalTv.setText(vicePrincipal);
                        binding.schoolCodeTv.setText(code);
                        binding.schoolType.setText(schoolType);
                        binding.founderTv.setText(founder);
                        binding.highestDegreeTv.setText(highestDegree);
                        binding.rankingTv.setText(ranking);
                        binding.timingTv.setText(timing);
                        binding.nurseryTv.setText(nursery);
                        binding.oneToFourTv.setText(oneFour);
                        binding.fiveToSevenTv.setText(fiveSeven);
                        binding.eightToTenTv.setText(eightTen);
                        binding.elevenToTwelveTv.setText(elevenTwelve);

                        try {
                            Picasso.get().load(schoolImage).into(binding.profileIv);
                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            myLatitude = "" + ds.child("latitude").getValue();
                            myLongitude = "" + ds.child("longitude").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private float ratingSum = 0;

    private void loadReviews() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                        }
                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum / numberOfReviews;

                        binding.ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void openMap() {
        String address = "https://maps.google.com/maps?saddr=" + myLatitude + "," + myLongitude + "&daddr=" + schoolLatitude + "," + schoolLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phoneNumber))));
        Toast.makeText(this, "" + phoneNumber, Toast.LENGTH_SHORT).show();
    }
}