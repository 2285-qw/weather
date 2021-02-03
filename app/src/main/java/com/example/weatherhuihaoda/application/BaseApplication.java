package com.example.weatherhuihaoda.application;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.weatherhuihaoda.config.TTAdManagerHolder;
import com.example.weatherhuihaoda.service.LocationService;
import com.example.weatherhuihaoda.util.ScreenUtils;
import com.example.weatherhuihaoda.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

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

        //调试 log
        //UMConfigure.setLogEnabled(true);
        //初始化sdk
        UMConfigure.init(this,"601a17fcf1eb4f3f9b84e6ee", "yunhao", UMConfigure.DEVICE_TYPE_PHONE,null);
        //页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(this);

    }




    private void init() {
        ToastUtils.init(this);
        ScreenUtils.init(this);
    }
}
