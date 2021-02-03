package com.example.weatherhuihaoda.util;

import com.example.weatherhuihaoda.entity.TodayWeather;
import com.example.weatherhuihaoda.entity.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static List<Weather> WeatherParser(String json) {
        List<Weather> list = new ArrayList<>();
        try {
            JSONObject jo = new JSONObject(json);
            JSONObject data = jo.getJSONObject("data");
            String forecast = data.getString("forecast");
            Util.log(forecast);
            Gson gson = new Gson();
            list = gson.fromJson(forecast, new TypeToken<List<Weather>>(){}.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
    public static TodayWeather getTodayWeather(){
        TodayWeather todayWeather = new TodayWeather();

        return todayWeather;
    }
}
