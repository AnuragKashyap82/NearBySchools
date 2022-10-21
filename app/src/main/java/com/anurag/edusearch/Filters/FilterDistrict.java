package com.anurag.edusearch.Filters;

import android.widget.Filter;

import com.anurag.edusearch.Adapters.AdapterDistrict;
import com.anurag.edusearch.Models.ModelDistrict;

import java.util.ArrayList;

public class FilterDistrict extends Filter {

    ArrayList<ModelDistrict> filterList;
    AdapterDistrict adapterDistrict;

    public FilterDistrict(ArrayList<ModelDistrict> filterList, AdapterDistrict adapterDistrict) {
        this.filterList = filterList;
        this.adapterDistrict = adapterDistrict;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelDistrict> filteredModels = new ArrayList<>();
            for (int i=0; i< filterList.size(); i++){
                if (filterList.get(i).getCategory().toUpperCase().contains(charSequence)){
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
            adapterDistrict.districtArrayList = (ArrayList<ModelDistrict>)filterResults.values;

            adapterDistrict.notifyDataSetChanged();
    }
}