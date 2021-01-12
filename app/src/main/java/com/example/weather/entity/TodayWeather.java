package com.example.weather.entity;

import java.util.ArrayList;
import java.util.List;

public class TodayWeather {
    private String wendu;
    private String city;
    private String note;
    private int aqi;

    public List<Weather> getList() {
        return list;
    }

    public void setList(List<Weather> list) {
        this.list = list;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private List<Weather> list=new ArrayList<>();

    public void addWeather(Weather weather) {
        list.add(weather);
    }

    public String getWendu() {
        return wendu;
    }



    public void setNote(String note) {
        this.note = note;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getCity() {
        return city;
    }


    public String getNote() {
        return note;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public Weather get(int i) {
        return list.get(i);
    }
}
