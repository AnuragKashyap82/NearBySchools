package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterImage;
import com.anurag.edusearch.Models.ModeImage;

import java.util.ArrayList;

public class FilterImage extends Filter {
    private AdapterImage adapter;
    private ArrayList<ModeImage> filterList;

    public FilterImage(AdapterImage adapter, ArrayList<ModeImage> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModeImage> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getGalleryId().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getGalleryId().toUpperCase().contains(charSequence)) {
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

        adapter.imagesList = (ArrayList<ModeImage>) filterResults.values;

        adapter.notifyDataSetChanged();
    }
}
