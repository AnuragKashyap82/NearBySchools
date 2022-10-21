package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterFacultyUser;
import com.anurag.edusearch.Models.ModelFacultyUser;

import java.util.ArrayList;

public class FilterFacultyUser extends Filter {

    private AdapterFacultyUser adapter;
    private ArrayList<ModelFacultyUser> filterList;

    public FilterFacultyUser(AdapterFacultyUser adapter, ArrayList<ModelFacultyUser> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelFacultyUser> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getFacultyName().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getFacultyName().toUpperCase().contains(charSequence)) {
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

        adapter.facultyList = (ArrayList<ModelFacultyUser>) filterResults.values;

        adapter.notifyDataSetChanged();
    }
}
