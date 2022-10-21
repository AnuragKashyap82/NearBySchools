package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterFacultyAdmin;
import com.anurag.edusearch.Adapters.AdapterGalleryCategory;
import com.anurag.edusearch.Models.ModelCategory;
import com.anurag.edusearch.Models.ModelGalleryCategory;
import com.anurag.edusearch.databinding.ActivityGalleryCategoryBinding;

import java.util.ArrayList;

public class FilterGalleryCategory extends Filter {

    private AdapterGalleryCategory adapter;
    private ArrayList<ModelGalleryCategory> filterList;

    public FilterGalleryCategory(AdapterGalleryCategory adapter, ArrayList<ModelGalleryCategory> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelGalleryCategory> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getGalleryName().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getGalleryName().toUpperCase().contains(charSequence)) {
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        adapter.categoryList = (ArrayList<ModelGalleryCategory>) filterResults.values;

        adapter.notifyDataSetChanged();
    }
}
