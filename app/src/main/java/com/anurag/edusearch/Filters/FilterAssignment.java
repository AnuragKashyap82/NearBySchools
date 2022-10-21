package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterAssignment;
import com.anurag.edusearch.Adapters.AdapterMaterial;
import com.anurag.edusearch.Models.ModelAssignment;
import com.anurag.edusearch.Models.ModelMaterial;

import java.util.ArrayList;

public class FilterAssignment extends Filter {

    private AdapterAssignment adapter;
    private ArrayList<ModelAssignment> filterList;

    public FilterAssignment(AdapterAssignment adapter, ArrayList<ModelAssignment> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelAssignment> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getAssignedBy().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getAssignedBy().toUpperCase().contains(charSequence)) {
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

        adapter.assignmentList = (ArrayList<ModelAssignment>) filterResults.values;

        adapter.notifyDataSetChanged();
    }
}
