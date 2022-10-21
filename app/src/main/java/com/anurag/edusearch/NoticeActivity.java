package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterFacultyAdmin;
import com.anurag.edusearch.Adapters.AdapterNotice;
import com.anurag.edusearch.Models.ModelCategory;
import com.anurag.edusearch.Models.ModelNotice;
import com.anurag.edusearch.Models.ModelReview;
import com.anurag.edusearch.databinding.ActivityNoticeBinding;
import com.anurag.edusearch.databinding.ActivitySchoolFacultyAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NoticeActivity extends AppCompatActivity {

    ActivityNoticeBinding binding;

    private FirebaseAuth firebaseAuth;
    private String schoolId;

    private ArrayList<ModelNotice> noticeList;
    private AdapterNotice adapterNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        schoolId = getIntent().getStringExtra("schoolId");

        checkUser();
        loadAllNotice();

        binding.addNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoticeActivity.this, AddNoticeActivity.class);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
                Toast.makeText(NoticeActivity.this, "Add Notice Btn Clicked....!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.searchNoticeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterNotice.getFilter().filter(charSequence);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadAllNotice() {
        noticeList =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Notice")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        noticeList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelNotice modelNotice = ds.getValue(ModelNotice.class);
                            noticeList.add(modelNotice);
                        }
                        Collections.sort(noticeList, new Comparator<ModelNotice>() {
                            @Override
                            public int compare(ModelNotice t1, ModelNotice t2) {
                                return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                            }
                        });
                        Collections.reverse(noticeList);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(NoticeActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binding.noticeRv.setLayoutManager(layoutManager);

                         binding.noticeRv.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));

                        adapterNotice = new AdapterNotice(NoticeActivity.this, noticeList);
                        binding.noticeRv.setAdapter(adapterNotice);
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
                            binding.addNoticeBtn.setVisibility(View.GONE);
                        }
                        else if (userType.equals("admin")){
                            binding.addNoticeBtn.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}