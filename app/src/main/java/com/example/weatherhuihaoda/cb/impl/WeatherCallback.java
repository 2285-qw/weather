package com.example.weatherhuihaoda.cb.impl;

import android.util.Log;

import com.example.weatherhuihaoda.cb.IConvertCallback;
import com.example.weatherhuihaoda.entity.TodayWeather;
import com.example.weatherhuihaoda.entity.Weather;
import com.example.weatherhuihaoda.util.JsonParser;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherCallback extends IConvertCallback<TodayWeather> {
    @Override
    public void onSuccess(String responce) {
        Log.d("mylog","onResponse: " + responce);
        List<Weather> list = new ArrayList<>();

        TodayWeather todayWeather = new TodayWeather();
        list = JsonParser.WeatherParser(responce);
        todayWeather.setList(list);
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String json = jsonObject.getString("data");
            JSONObject jo = new JSONObject(json);

            todayWeather.setNote(jo.getString("ganmao"));
            todayWeather.setWendu(jo.getString("wendu"));
            todayWeather.setCity(jo.getString("city"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onComplete(todayWeather);
    }

    @Override
    public void onComplete(String data) {

    }

    @Override
    public void onComplete(TodayWeather weather) {

    }
}
