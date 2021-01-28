package com.example.weather.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.weather.R;
import com.example.weather.entity.Weather;
import com.example.weather.util.UtilTools;

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
        //TextView type = view.findViewById(R.id.tv_type);
        TextView low = view.findViewById(R.id.tv_low);
        TextView high = view.findViewById(R.id.tv_high);
        ImageView weather_icon=view.findViewById(R.id.weather_icon);
        //设置字体
        UtilTools.setFont(getContext(),date,"fonts/DIN-Medium.otf");
        UtilTools.setFont(getContext(),low,"fonts/DIN-Medium.otf");
        UtilTools.setFont(getContext(),high,"fonts/DIN-Medium.otf");

        Log.d("eee",weather.getType());
        switch (weather.getType()){
            case "多云":
                weather_icon.setImageResource(R.mipmap.duoyun);
                break;
            case "小雨":
            case "雨":
            case "大雨":
            case "暴雨":
                weather_icon.setImageResource(R.mipmap.xiayu);
                break;
            case "阴":
                weather_icon.setImageResource(R.mipmap.yingtian);
                break;

        }
        if(position == 0){
            date.setText("今天");
        }else{
            if (weather.getDate().length()==5){
                date.setText(weather.getDate().substring(2,5));
            }else {
                Log.d("YY",weather.getDate().substring(0,weather.getDate().length()));
                date.setText(weather.getDate().substring(3,6));
            }

        }
        //type.setText(weather.getType());
        low.setText(weather.getLow().substring(3,weather.getLow().length()));
        high.setText(weather.getHigh().substring(3,weather.getHigh().length()));
        return view;
    }
}
