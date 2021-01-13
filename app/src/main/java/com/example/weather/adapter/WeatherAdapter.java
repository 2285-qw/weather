package com.example.weather.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.weather.R;
import com.example.weather.entity.Weather;

import java.util.List;

public class WeatherAdapter extends ArrayAdapter<Weather> {
    private int resource;
    public WeatherAdapter(Context context, int resource, List<Weather> list) {
        super(context, resource,list);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Weather weather = getItem(position);
        View view = View.inflate(getContext(),resource,null);
        TextView date = view.findViewById(R.id.tv_date);
        TextView type = view.findViewById(R.id.tv_type);
        TextView low = view.findViewById(R.id.tv_low);
        TextView high = view.findViewById(R.id.tv_high);
        if(position == 0){
            date.setText("今天");
        }else{
            date.setText(weather.getDate().substring(0,5));
        }
        type.setText(weather.getType());
        low.setText(weather.getLow());
        high.setText(weather.getHigh());
        return view;
    }
}