package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterFacultyAdmin;
import com.anurag.edusearch.Adapters.AdapterNotice;
import com.anurag.edusearch.Models.ModelCategory;
import com.anurag.edusearch.Models.ModelNotice;

import java.util.ArrayList;

public class FilterNotice extends Filter {

    private AdapterNotice adapter;
    private ArrayList<ModelNotice> filterList;

    public FilterNotice(AdapterNotice adapter, ArrayList<ModelNotice> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelNotice> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getTitle().toUpperCase().contains(charSequence)) {
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

        adapter.noticeList = (ArrayList<ModelNotice>) filterResults.values;

        adapter.notifyDataSetChanged();
    }
}
