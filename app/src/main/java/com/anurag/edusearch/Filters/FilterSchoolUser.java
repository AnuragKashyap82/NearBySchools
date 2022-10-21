package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterSchoolUser;
import com.anurag.edusearch.Models.ModelSchoolUser;

import java.util.ArrayList;

public class FilterSchoolUser extends Filter{

    ArrayList<ModelSchoolUser> filterList;

    AdapterSchoolUser adapterSchoolUser;

    public FilterSchoolUser(ArrayList<ModelSchoolUser> filterList, AdapterSchoolUser adapterSchoolUser) {
        this.filterList = filterList;
        this.adapterSchoolUser = adapterSchoolUser;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence charSequence) {
        Filter.FilterResults results = new Filter.FilterResults();
        if (charSequence!=null || charSequence.length() > 0){

            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelSchoolUser> filterModels = new ArrayList<>();

            for (int i=0; i< filterList.size(); i++){
                if (filterList.get(i).getSchoolName().toUpperCase().contains(charSequence)){
                    filterModels.add(filterList.get(i));

                }
            }
            results.count = filterModels.size();
            results.values = filterModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, Filter.FilterResults results) {
        adapterSchoolUser.schoolUserArrayList = (ArrayList<ModelSchoolUser>)results.values;

        adapterSchoolUser.notifyDataSetChanged();
    }
}
