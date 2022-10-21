package com.anurag.edusearch.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.Filters.FilterSchoolAdmin;
import com.anurag.edusearch.Models.ModelSchool;
import com.anurag.edusearch.Models.ModelSchoolUser;
import com.anurag.edusearch.R;
import com.anurag.edusearch.SchoolDetailsActivity;
import com.anurag.edusearch.SchoolEditActivity;
import com.anurag.edusearch.databinding.RowSchoolAdminBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterSchoolAdmin extends RecyclerView.Adapter<AdapterSchoolAdmin.HolderSchoolAdmin> implements Filterable {

    private Context context;

    public ArrayList<ModelSchool> schoolArrayList, filterList;

    private RowSchoolAdminBinding binding;

    private FilterSchoolAdmin filter;

    private ProgressDialog progressDialog;

    public AdapterSchoolAdmin(Context context, ArrayList<ModelSchool> schoolArrayList) {
        this.context = context;
        this.schoolArrayList = schoolArrayList;
        this.filterList = schoolArrayList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderSchoolAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowSchoolAdminBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderSchoolAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSchoolAdmin holder, int position) {

        ModelSchool model = schoolArrayList.get(position);
        String schoolId = model.getSchoolId();
        String schoolName = model.getSchoolName();
        String avgRating = model.getAvgRating();
        String board = model.getBoard();
        String schoolImage = model.getSchoolImage();
        String email = model.getEmail();
        String phoneNumber = model.getPhoneNumber();
        String districtId = model.getDistrictId();
        String district = model.getDistrict();
        String address = model.getAddress();
        String uid = model.getUid();

        loadReviews(model, holder);

        holder.schoolNameTv.setText(schoolName);
        holder.boardTv.setText(board);
        holder.emailTv.setText(email);
        holder.phoneTv.setText(phoneNumber);
        holder.districtTv.setText(district);

        try {
            Picasso.get().load(schoolImage).placeholder(R.drawable.ic_school_primary).into(holder.profileIv);
        } catch (Exception e) {
            holder.profileIv.setImageResource(R.drawable.ic_school_primary);
        }


        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model, holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SchoolDetailsActivity.class);
                intent.putExtra("schoolId", schoolId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptionsDialog(ModelSchool model, HolderSchoolAdmin holder) {

        String schoolId = model.getSchoolId();
        String schoolImage = model.getSchoolImage();
        String schoolName = model.getSchoolName();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent(context, SchoolEditActivity.class);
                            intent.putExtra("schoolId", schoolId);
                            context.startActivity(intent);
                            Toast.makeText(context, "EDIT Schools", Toast.LENGTH_SHORT).show();
                        } else if (i == 1) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Delete")
                                    .setMessage("Are you sure want to delete this School?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(context, "Deleting....", Toast.LENGTH_SHORT).show();
                                            deleteBook(context, schoolId, schoolName);
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
                    }
                })
                .show();
    }

    private void deleteBook(Context context, String schoolId, String schoolName) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Deleting"+schoolName+" ...");
        progressDialog.show();


                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
                        reference.child(schoolId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "School Deleted Successfully....", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        progressDialog.dismiss();
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

    private float ratingSum = 0;
    private void loadReviews(ModelSchool model, AdapterSchoolAdmin.HolderSchoolAdmin holder) {
        String schoolId = model.getSchoolId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum = ratingSum + rating;
                        }
                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        holder.ratingBar.setRating(avgRating);
                        holder.ratingTv.setText(String.format("%.1f", avgRating) + "[" + numberOfReviews+ "]");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return schoolArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterSchoolAdmin(filterList, this);
        }
        return filter;
    }

    class HolderSchoolAdmin extends RecyclerView.ViewHolder {

        ImageView profileIv;
        TextView schoolNameTv, emailTv, phoneTv, districtTv, ratingTv, boardTv;
        ImageButton moreButton;
        RatingBar ratingBar;


        public HolderSchoolAdmin(@NonNull View itemView) {
            super(itemView);

            profileIv = binding.profileIv;
            ratingBar = binding.ratingBar;
            ratingTv = binding.ratingTv;
            schoolNameTv = binding.schoolNameTv;
            boardTv = binding.boardTv;
            emailTv = binding.emailTv;
            phoneTv = binding.phoneTv;
            districtTv = binding.districtTv;
            moreButton = binding.moreButton;


        }
    }
}