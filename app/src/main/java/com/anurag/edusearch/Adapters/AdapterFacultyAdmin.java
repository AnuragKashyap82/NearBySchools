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

import com.anurag.edusearch.FacultyDetailsActivity;
import com.anurag.edusearch.Filters.FilterCategory;
import com.anurag.edusearch.Models.ModelCategory;
import com.anurag.edusearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterFacultyAdmin extends RecyclerView.Adapter<AdapterFacultyAdmin.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelCategory> facultyList, filterList;
    private FilterCategory filter;

    public AdapterFacultyAdmin(Context context, ArrayList<ModelCategory> facultyList) {
        this.context = context;
        this.facultyList = facultyList;
        this.filterList = facultyList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_faculty_admin, parent, false);
        return new HolderProductSeller(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {

        final ModelCategory modelCategory = facultyList.get(position);
        String facultyName = modelCategory.getFacultyName();
        String facultyDescription = modelCategory.getFacultyDescription();
        String facultyId = modelCategory.getFacultyId();
        String schoolId = modelCategory.getSchoolId();
        String facultyImage = modelCategory.getFacultyImage();

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
                Intent intent =new Intent(context, FacultyDetailsActivity.class);
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
            filter = new FilterCategory(this, filterList);

        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{

        private ImageView facultyIv;
        private TextView facultyName, descriptionTv;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            facultyIv = itemView.findViewById(R.id.profileIv);
            facultyName = itemView.findViewById(R.id.facultyNameEt);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);

        }
    }

}
