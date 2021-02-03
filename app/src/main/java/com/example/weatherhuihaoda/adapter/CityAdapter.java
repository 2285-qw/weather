package com.example.weatherhuihaoda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.weatherhuihaoda.R;

import java.util.List;

public class CityAdapter extends ArrayAdapter<String> {
    private int resource;
    public CityAdapter(Context context, int resource, List<String> list) {
        super(context, resource,list);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String city = getItem(position);
        View view = View.inflate(getContext(),resource,null);
        TextView tv_city = view.findViewById(R.id.item_city);
        tv_city.setText(city);
        return view;
    }
}
