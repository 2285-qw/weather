package com.example.weather.entity;

import java.io.Serializable;

public class Weather implements Serializable {
    private String date;    //"date":"25日星期五",
    private String high;    //"high":"高温 9℃",
    private String fengli;  //"fengli":"&lt;![CDATA[2级]]&gt;",
    private String low;     //"low":"低温 -1℃",
    private String fengxiang;//"fengxiang":"南风",
    private String type;    // "type":"霾"

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
