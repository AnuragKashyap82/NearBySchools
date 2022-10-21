package com.anurag.edusearch.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.Filters.FilterDistrict;
import com.anurag.edusearch.Filters.FilterDistrictUser;
import com.anurag.edusearch.Models.ModelDistrict;
import com.anurag.edusearch.Models.ModelDistrictUser;
import com.anurag.edusearch.SchoolsAdminActivity;
import com.anurag.edusearch.SchoolsUserActivity;
import com.anurag.edusearch.databinding.RowDistrictBinding;
import com.anurag.edusearch.databinding.RowDistrictUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterDistrictUser extends RecyclerView.Adapter<AdapterDistrictUser.HolderDistrict> implements Filterable {

    private Context context;
    public ArrayList<ModelDistrictUser> districtArrayList, filterListUser;

    private RowDistrictUserBinding binding;
    private FilterDistrictUser filter;

    public AdapterDistrictUser(Context context, ArrayList<ModelDistrictUser> districtArrayList) {
        this.context = context;
        this.districtArrayList = districtArrayList;
        this.filterListUser = districtArrayList;
    }

    @NonNull
    @Override
    public HolderDistrict onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowDistrictUserBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderDistrict(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDistrict holder, int position) {

        ModelDistrictUser model = districtArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        String timestamp = model.getTimestamp();

        holder.categoryTv.setText(category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SchoolsUserActivity.class);
                intent.putExtra("districtId", id);
                intent.putExtra("districtTitle", category);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return districtArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterDistrictUser(filterListUser, this);

        }
        return filter;
    }

    class HolderDistrict extends RecyclerView.ViewHolder{

        TextView categoryTv;

        public HolderDistrict(@NonNull View itemView) {
            super(itemView);

            categoryTv = binding.districtTv;
        }
    }
}
