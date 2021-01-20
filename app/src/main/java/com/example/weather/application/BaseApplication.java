package com.example.weather.application;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.weather.service.LocationService;
import com.example.weather.util.ScreenUtils;
import com.example.weather.util.ToastUtils;

/**
 * Time:         2021/1/12
 * Author:       C
 * Description:  BaseApplication
 * on:
 */
public class BaseApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;
    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        init();
    }




    private void init() {
        ToastUtils.init(this);
        ScreenUtils.init(this);
    }
}
