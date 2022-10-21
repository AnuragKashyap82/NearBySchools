package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterSchoolAdmin;
import com.anurag.edusearch.Models.ModelSchool;

import java.util.ArrayList;

public class FilterSchoolAdmin extends Filter {

    ArrayList<ModelSchool> filterList;
    AdapterSchoolAdmin adapterSchoolAdmin;

    public FilterSchoolAdmin(ArrayList<ModelSchool> filterList, AdapterSchoolAdmin adapterSchoolAdmin) {
        this.filterList = filterList;
        this.adapterSchoolAdmin = adapterSchoolAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelSchool> filteredModels = new ArrayList<>();
            for (int i=0; i< filterList.size(); i++){
                if (filterList.get(i).getSchoolName().toUpperCase().contains(charSequence)){
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapterSchoolAdmin.schoolArrayList = (ArrayList<ModelSchool>)filterResults.values;

            adapterSchoolAdmin.notifyDataSetChanged();
    }
}