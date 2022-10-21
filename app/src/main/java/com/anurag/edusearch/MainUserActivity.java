package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterDistrict;
import com.anurag.edusearch.Adapters.AdapterDistrictUser;
import com.anurag.edusearch.Models.ModelDistrict;
import com.anurag.edusearch.Models.ModelDistrictUser;
import com.anurag.edusearch.databinding.ActivityMainUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private ActivityMainUserBinding binding;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<ModelDistrictUser> districtArrayList;
    private AdapterDistrictUser adapterDistrictUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        loadMyInfo();
        loadDistrict();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    adapterDistrictUser.getFilter().filter(charSequence);
                } catch (Exception e) {

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, ProfileActivity.class));
            }
        });
        PopupMenu popupMenu = new PopupMenu(MainUserActivity.this, binding.moreBtn);
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Change Password");
        popupMenu.getMenu().add("Change Phone No");
        popupMenu.getMenu().add("About Us");
        popupMenu.getMenu().add("Logout");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Profile") {
                    startActivity(new Intent(MainUserActivity.this, ProfileActivity.class));
                }else  if (menuItem.getTitle() == "Change Password") {
                    startActivity(new Intent(MainUserActivity.this, ChangePasswordActivity.class));
                }else  if (menuItem.getTitle() == "Change Phone No") {
//                    startActivity(new Intent(MainAdminActivity.this, ChangePasswordActivity.class));
                    Toast.makeText(MainUserActivity.this, "To be Integrated...!!!!", Toast.LENGTH_SHORT).show();
                }else  if (menuItem.getTitle() == "About Us") {
                    startActivity(new Intent(MainUserActivity.this, AboutUsActivity.class));
                } else if (menuItem.getTitle() == "Logout") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainUserActivity.this);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure want to Logout")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    makeMeOffline();
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
                return true;
            }
        });

        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
    }

    private void makeMeOffline() {
        progressDialog.setMessage("Logging out...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.setMessage("Logging Out...!");
                        progressDialog.show();
                        firebaseAuth.signOut();
                        startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
                        finish();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

//                            String name = ""+ds.child("name").getValue();
//                            String accountType = ""+ds.child("accountType").getValue();
//                            String email = ""+ds.child("email").getValue();
//                            String shopName = ""+ds.child("shopName").getValue();

                            String profileImage = "" + ds.child("profileImage").getValue();


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

    private void loadDistrict() {
        districtArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Districts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                districtArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ModelDistrictUser model = dataSnapshot.getValue(ModelDistrictUser.class);
                    districtArrayList.add(model);

                }
                Collections.sort(districtArrayList, new Comparator<ModelDistrictUser>() {
                    @Override
                    public int compare(ModelDistrictUser t1, ModelDistrictUser t2) {
                        return t1.getCategory().compareToIgnoreCase(t2.getCategory());
                    }
                });
//                Collections.reverse(districtArrayList);
                binding.progressBar.setVisibility(View.GONE);
                adapterDistrictUser = new AdapterDistrictUser(MainUserActivity.this, districtArrayList);
                binding.districtRv.setAdapter(adapterDistrictUser);
                adapterDistrictUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}