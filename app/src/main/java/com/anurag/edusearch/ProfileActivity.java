package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.edusearch.Adapters.AdapterMaterialFavorite;
import com.anurag.edusearch.Models.ModelMaterialFav;
import com.anurag.edusearch.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelMaterialFav> materialFavArrayList;
    private AdapterMaterialFavorite adapterMaterialFavorite;
    private ActivityProfileBinding holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadFavoriteMaterial();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();
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

                            String userType = "" + ds.child("userType").getValue();
                            String address = "" + ds.child("address").getValue();
                            String city = "" + ds.child("city").getValue();
                            String state = "" + ds.child("state").getValue();
                            String country = "" + ds.child("country").getValue();
                            String email = "" + ds.child("email").getValue();
                            String name = "" + ds.child("name").getValue();
                            String uniqueId = "" + ds.child("uniqueId").getValue();
                            Double latitude = Double.parseDouble("" + ds.child("latitude").getValue());
                            Double longitude = Double.parseDouble("" + ds.child("longitude").getValue());
                            String online = "" + ds.child("online").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String uid = "" + ds.child("uid").getValue();

                            binding.nameTv.setText(name);
                            binding.phoneTv.setText("+91"+phone);
                            binding.emailTv.setText(email);
                            binding.countryTv.setText(country);
                            binding.stateTv.setText(state);
                            binding.cityTv.setText(city);
                            binding.addressTv.setText(address);
                            binding.uniqueIdTv.setText(uniqueId);

                            if (userType.equals("user")){
                                binding.adminVector.setVisibility(View.GONE);
                            }
                            else if (userType.equals("admin")){
                                binding.adminVector.setVisibility(View.VISIBLE);
                            }

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(binding.profileIv);
                            } catch (Exception e) {
                                binding.profileIv.setImageResource(R.drawable.ic_person_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                            startActivity(new Intent(ProfileActivity.this, ProfileEditUserActivity.class));
                        }
                        else if (userType.equals("admin")){
                            startActivity(new Intent(ProfileActivity.this, ProfileEditAdminActivity.class));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadFavoriteMaterial() {
        materialFavArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        materialFavArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String materialId = "" + ds.child("materialId").getValue();
                            String branch = "" + ds.child("branch").getValue();
                            String semester = "" + ds.child("semester").getValue();
                            String schoolId = "" + ds.child("schoolId").getValue();
                            String topic = "" + ds.child("topic").getValue();

                            ModelMaterialFav modelMaterialFav = new ModelMaterialFav();
                            modelMaterialFav.setMaterialId(materialId);
                            modelMaterialFav.setSchoolId(schoolId);
                            modelMaterialFav.setBranch(branch);
                            modelMaterialFav.setSemester(semester);
                            modelMaterialFav.setMaterialId(materialId);
                            modelMaterialFav.setTopic(topic);

                            materialFavArrayList.add(modelMaterialFav);
                        }

                        binding.favoriteMaterialCountTv.setText("" + materialFavArrayList.size());
                        adapterMaterialFavorite = new AdapterMaterialFavorite(ProfileActivity.this, materialFavArrayList);
                        binding.FabMaterialsRv.setAdapter(adapterMaterialFavorite);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}