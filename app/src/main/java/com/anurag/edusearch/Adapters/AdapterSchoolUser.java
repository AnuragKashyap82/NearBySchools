package com.anurag.edusearch.Adapters;

import android.content.Context;
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

import com.anurag.edusearch.Filters.FilterSchoolUser;
import com.anurag.edusearch.Models.ModelSchoolUser;
import com.anurag.edusearch.R;
import com.anurag.edusearch.SchoolDetailsActivity;
import com.anurag.edusearch.SchoolDetailsUserActivity;
import com.anurag.edusearch.databinding.RowSchoolUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterSchoolUser extends RecyclerView.Adapter<AdapterSchoolUser.HolderSchoolUser> implements Filterable {

    private Context context;
    public ArrayList<ModelSchoolUser> schoolUserArrayList, filterList;
    private FilterSchoolUser filter;

    private RowSchoolUserBinding binding;
    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterSchoolUser(Context context, ArrayList<ModelSchoolUser> schoolUserArrayList) {
        this.context = context;
        this.schoolUserArrayList = schoolUserArrayList;
        this.filterList = schoolUserArrayList;
    }


    @Override
    public HolderSchoolUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowSchoolUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderSchoolUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSchoolUser holder, int position) {
        ModelSchoolUser model = schoolUserArrayList.get(position);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SchoolDetailsUserActivity.class);
                intent.putExtra("schoolId", schoolId);
                context.startActivity(intent);
            }
        });

    }

    private float ratingSum = 0;
    private void loadReviews(ModelSchoolUser model, HolderSchoolUser holder) {
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
        return schoolUserArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterSchoolUser(filterList, this);
        }
        return filter;
    }

    class HolderSchoolUser extends RecyclerView.ViewHolder {

        ImageView profileIv;
        TextView schoolNameTv, boardTv, emailTv, phoneTv, districtTv, ratingTv;
        RatingBar ratingBar;

        public HolderSchoolUser(@NonNull View itemView) {
            super(itemView);


            profileIv = binding.profileIv;
            ratingBar = binding.ratingBar;
            ratingTv = binding.ratingTv;
            schoolNameTv = binding.schoolNameTv;
            boardTv = binding.boardTv;
            emailTv = binding.emailTv;
            phoneTv = binding.phoneTv;
            districtTv = binding.districtTv;


        }
    }
}
