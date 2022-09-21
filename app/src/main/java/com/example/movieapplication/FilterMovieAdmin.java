package com.example.movieapplication;


import android.widget.Filter;

import java.util.ArrayList;

public class FilterMovieAdmin extends Filter {

    ArrayList<ModelMovie> filterList;

    AdapterMovieAdmin adapterMovieAdmin;

    public FilterMovieAdmin (ArrayList<ModelMovie> filterList, AdapterMovieAdmin adapterMovieAdmin) {

        this.filterList = filterList;
        this.adapterMovieAdmin = adapterMovieAdmin;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint !=null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelMovie> filteredModels = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++) {

                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)){
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
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterMovieAdmin.moviesArrayList = (ArrayList<ModelMovie>)results.values;

        adapterMovieAdmin.notifyDataSetChanged();
    }
}