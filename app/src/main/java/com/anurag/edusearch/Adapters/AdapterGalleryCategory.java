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

import com.anurag.edusearch.Filters.FilterCategory;
import com.anurag.edusearch.Filters.FilterGalleryCategory;
import com.anurag.edusearch.GalleryActivity;
import com.anurag.edusearch.Models.ModelCategory;
import com.anurag.edusearch.Models.ModelGalleryCategory;
import com.anurag.edusearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterGalleryCategory extends RecyclerView.Adapter<AdapterGalleryCategory.HolderGalleryCategory> implements Filterable {

    private Context context;
    public ArrayList<ModelGalleryCategory> categoryList, filterList;
    private FilterGalleryCategory filter;

    public AdapterGalleryCategory(Context context, ArrayList<ModelGalleryCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.filterList = categoryList;
    }

    @NonNull
    @Override
    public HolderGalleryCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_gallery_category, parent, false);
        return new HolderGalleryCategory(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderGalleryCategory holder, int position) {

        final ModelGalleryCategory modelGalleryCategory = categoryList.get(position);
        String galleryName = modelGalleryCategory.getGalleryName();
        String galleryImage = modelGalleryCategory.getGalleryImage();
        String galleryId = modelGalleryCategory.getGalleryId();
        String schoolId = modelGalleryCategory.getSchoolId();
        String uid = modelGalleryCategory.getUid();

        holder.categoryNameTv.setText(galleryName);


        try {
            Picasso.get().load(galleryImage).placeholder(R.drawable.ic_gallery_primary).into(holder.categoryIv);
        } catch (Exception e) {
            holder.categoryIv.setImageResource(R.drawable.ic_gallery_primary);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, GalleryActivity.class);
                intent.putExtra("galleryId", galleryId);
                intent.putExtra("galleryName", galleryName);
                intent.putExtra("schoolId", schoolId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterGalleryCategory(this, filterList);

        }
        return filter;
    }

    class HolderGalleryCategory extends RecyclerView.ViewHolder {

        private ImageView categoryIv;
        private TextView categoryNameTv;

        public HolderGalleryCategory(@NonNull View itemView) {
            super(itemView);

            categoryIv = itemView.findViewById(R.id.categoryIv);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);

        }
    }

}
