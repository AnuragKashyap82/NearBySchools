package com.anurag.edusearch.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.anurag.edusearch.FacultyDetailsUserActivity;
import com.anurag.edusearch.Filters.FilterFacultyUser;
import com.anurag.edusearch.Models.ModelFacultyUser;
import com.anurag.edusearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterFacultyUser extends RecyclerView.Adapter<AdapterFacultyUser.HolderFacultyUser> implements Filterable {

    private Context context;
    public ArrayList<ModelFacultyUser> facultyList, filterList;
    private FilterFacultyUser filter;

    public AdapterFacultyUser(Context context, ArrayList<ModelFacultyUser> facultyList) {
        this.context = context;
        this.facultyList = facultyList;
        this.filterList = facultyList;
    }

    @NonNull
    @Override
    public HolderFacultyUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_faculty_user, parent, false);
        return new HolderFacultyUser(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderFacultyUser holder, int position) {

        final  ModelFacultyUser modelFaculty = facultyList.get(position);
        String facultyName = modelFaculty.getFacultyName();
        String facultyDescription = modelFaculty.getFacultyDescription();
        String facultyId = modelFaculty.getFacultyId();
        String schoolId = modelFaculty.getSchoolId();
        String facultyImage = modelFaculty.getFacultyImage();

        holder.facultyName.setText(facultyName);
        holder.descriptionTv.setText(facultyDescription);


        try {
            Picasso.get().load(facultyImage).placeholder(R.drawable.ic_person_gray).into(holder.facultyIv);
        }
        catch (Exception e){
            holder.facultyIv.setImageResource(R.drawable.ic_person_gray);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              detailsBottomSheet(modelFaculty);
                Intent intent =new Intent(context, FacultyDetailsUserActivity.class);
                intent.putExtra("facultyId", facultyId);
                intent.putExtra("schoolId", schoolId);
                context.startActivity(intent);

            }
        });

    }
    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterFacultyUser(this, filterList);

        }
        return filter;
    }

    class HolderFacultyUser extends RecyclerView.ViewHolder{

        private ImageView facultyIv;
        private TextView facultyName, descriptionTv;

        public HolderFacultyUser(@NonNull View itemView) {
            super(itemView);

            facultyIv = itemView.findViewById(R.id.profileIv);
            facultyName = itemView.findViewById(R.id.facultyNameEt);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);

        }
    }

}
