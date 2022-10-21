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
import com.anurag.edusearch.Models.ModelDistrict;
import com.anurag.edusearch.SchoolsAdminActivity;
import com.anurag.edusearch.databinding.RowDistrictBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterDistrict extends RecyclerView.Adapter<AdapterDistrict.HolderDistrict> implements Filterable {

    private Context context;
    public ArrayList<ModelDistrict> districtArrayList, filterList;

    private RowDistrictBinding binding;
    private FilterDistrict filter;

    public AdapterDistrict(Context context, ArrayList<ModelDistrict> districtArrayList) {
        this.context = context;
        this.districtArrayList = districtArrayList;
        this.filterList = districtArrayList;
    }

    @NonNull
    @Override
    public HolderDistrict onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowDistrictBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderDistrict(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDistrict holder, int position) {

        ModelDistrict model = districtArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        String timestamp = model.getTimestamp();

        holder.categoryTv.setText(category);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to delete this category?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "Deleting....", Toast.LENGTH_SHORT).show();
                                deleteCategory(model, holder);
                                
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();


            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SchoolsAdminActivity.class);
                intent.putExtra("districtId", id);
                intent.putExtra("districtTitle", category);
                context.startActivity(intent);
            }
        });


    }

    private void deleteCategory(ModelDistrict model, HolderDistrict holder) {
        
        String id = model.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Districts");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Successfully deleted....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show(); 
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
            filter = new FilterDistrict(filterList, this);

        }
        return filter;
    }

    class HolderDistrict extends RecyclerView.ViewHolder{

        TextView categoryTv;
        ImageButton deleteBtn;

        public HolderDistrict(@NonNull View itemView) {
            super(itemView);

            categoryTv = binding.districtTv;
            deleteBtn = binding.deleteBtn;

        }
    }
}
