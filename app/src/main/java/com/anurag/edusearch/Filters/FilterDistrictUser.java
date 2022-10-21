package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterDistrictUser;
import com.anurag.edusearch.Models.ModelDistrictUser;

import java.util.ArrayList;

public class FilterDistrictUser extends Filter{

    ArrayList<ModelDistrictUser> filterListUser;
    AdapterDistrictUser adapterDistrictUser;

    public FilterDistrictUser(ArrayList<ModelDistrictUser> filterListUser, AdapterDistrictUser adapterDistrictUser) {
        this.filterListUser = filterListUser;
        this.adapterDistrictUser = adapterDistrictUser;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence charSequence) {
        Filter.FilterResults results = new Filter.FilterResults();
        if (charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelDistrictUser> filteredModels = new ArrayList<>();
            for (int i = 0; i< filterListUser.size(); i++){
                if (filterListUser.get(i).getCategory().toUpperCase().contains(charSequence)){
                    filteredModels.add(filterListUser.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterListUser.size();
            results.values = filterListUser;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
        adapterDistrictUser.districtArrayList = (ArrayList<ModelDistrictUser>)filterResults.values;

        adapterDistrictUser.notifyDataSetChanged();
    }

}
