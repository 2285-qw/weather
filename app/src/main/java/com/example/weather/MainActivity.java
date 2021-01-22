package com.example.weather;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.adapter.WeatherAdapter;
import com.example.weather.cb.impl.WeatherCallback;
import com.example.weather.entity.TodayWeather;
import com.example.weather.ui.AddcityActivity;
import com.example.weather.ui.BaseActivity;
import com.example.weather.ui.City_choiceActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.example.weather.util.OutputUtil.fileIsExists;
import static com.example.weather.util.OutputUtil.readObjectFromLocal;
import static com.example.weather.util.OutputUtil.writeObjectIntoLocal;

public class MainActivity extends BaseActivity {

    ViewPager mviewPager;
    List views;
    TextView ed_wendu;
    ListView listView;
    TextView city;
    public static List list;
    List<TodayWeather> list1;
    //什么天气——————阴天  晴天
    TextView type;
    //主页背景图片
    ImageView main_beijin;
    //数据集合
    public static Map<String, TodayWeather> weather_date;
    //添加城市按钮
    ImageView add_city;
    //定位图标
    ImageView location;
    TodayWeather info;
    //月日
    String Time;
    //天气数据本地储存名
    public static String map1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*weather_date = new HashMap();
        weather_date.put("天津", null);
        weather_date.put("长沙", null);
        weather_date.put("北京", null);*/
        map1 = "map11";
    }

    @Override
    protected void onResume() {
        setContentView(R.layout.activity_main);
        list = new ArrayList();
        list1 = new ArrayList<>();
        //从本地读取map天气集合
        weather_date = readObjectFromLocal(getApplicationContext(), map1);
        for (String key : weather_date.keySet()) {
            list.add(key);
            System.out.println(key);
        }

        fileIsExists(map1);
        Log.d("xxx", fileIsExists(map1) + "");

        initView();
        super.onResume();
    }


    private void initView() {
        //weather_date=readObjectFromLocal(getApplicationContext(),map1);
        Log.d("weather_date", weather_date + "");
        mviewPager = findViewById(R.id.viewPager);
        mviewPager.setOffscreenPageLimit(3);
        views = new ArrayList();
        //获取当前时间
        gettime();
        //读取本地文件失败
        if (weather_date == null) {
            weather_date.put("北京", null);
        }
        //循环加载viewpager

        for (int i = 0; i < weather_date.size(); i++) {
            getWeather((String) list.get(i));
            views.add(getLayoutInflater().inflate(R.layout.mian, null));
        }

        if (weather_date.get(list.get(0)) != null) {
            mviewPager.setAdapter(new MyPagerAdapter());
        }


    }


    //加载天气
    public void getWeather(String city) {

        if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, "请输入城市", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = getString(R.string.weatherurl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url + city)
                        .get()
                        .build();
                Call call = okHttpClient.newCall(request);
                System.out.println(call + "------");
                System.out.println(request);
                call.enqueue(new WeatherCallback() {
                    @Override
                    public void onComplete(TodayWeather todayWeather) {
                        //Log.d("tag", String.valueOf(todayWeather));

                        info = todayWeather;
                        list1.add(info);
                        weather_date.put(city, todayWeather);
                        Log.d("EEE", list1.size() + "list1.size()");
                        Log.d("EEE", weather_date.size() + "weather_date.size()");
                        if (list1.size() == weather_date.size()) {
                            if (todayWeather != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //网络加载数据完成设置viewpager的Adapter
                                        mviewPager.setAdapter(new MyPagerAdapter());

                                        boolean op = writeObjectIntoLocal(getApplicationContext(), map1, weather_date);
                                        Log.d("aaa", "op" + op);

                                        Map setmap = readObjectFromLocal(getApplicationContext(), map1);
                                        Log.d("map", weather_date + "---" + setmap);
                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = "更新天气失败，请检查网络连接是否正常";
                        handler.sendMessage(msg);
                    }
                });
            }
        }).start();
    }


    private void updateUI(TodayWeather info) {
        if (info.getWendu() == null) {
            Toast.makeText(MainActivity.this, "请输入正确的城市名称！", Toast.LENGTH_SHORT).show();
            return;
        }

        type.setText(info.getList().get(1).getType());
        switch (info.getList().get(1).getType()) {
            case "小雨":
            case "雨":
            case "大雨":
                main_beijin.setImageResource(R.mipmap.beijin_xiayu);
                break;
            case "多云":
            case "阴":
                main_beijin.setImageResource(R.mipmap.yin);
                break;
        }
        city.setText(info.getCity());
        ed_wendu.setText(info.getWendu() + "°");
        WeatherAdapter weatherAdapter = new WeatherAdapter(this, R.layout.weather_item, info.getList());
        listView.setAdapter(weatherAdapter);


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    TodayWeather info = (TodayWeather) msg.obj;
                    if (info == null) {
                        info.getCity();
                        Toast.makeText(MainActivity.this, "请输入正确的城市名称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case 0:
                    String str = (String) msg.obj;
                    Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    public void gettime() {
        //获取单前时间
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow();
        int m = t.month;
        int r = t.monthDay;
        Time = m + 1 + "/" + r;
        Log.d("www", m + 1 + "/" + r);
    }

    class MyPagerAdapter extends PagerAdapter implements View.OnClickListener {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.d("xxxx", "instantiateItem: position = " + position);
            container.addView((View) views.get(position));
            TextView time = ((View) views.get(position)).findViewById(R.id.time);
            location = ((View) views.get(position)).findViewById(R.id.location);
            ed_wendu = ((View) views.get(position)).findViewById(R.id.ed_wendu);
            listView = ((View) views.get(position)).findViewById(R.id.list_weather);
            city = ((View) views.get(position)).findViewById(R.id.city);
            type = ((View) views.get(position)).findViewById(R.id.weather);
            main_beijin = ((View) views.get(position)).findViewById(R.id.main_beijin);
            add_city = ((View) views.get(position)).findViewById(R.id.add_city);

            city.setText((CharSequence) list.get(position));

            time.setText(Time + "");

            city.setOnClickListener(this);
            add_city.setOnClickListener(this);

            if (position == 0) {
                location.setImageResource(R.mipmap.location);
            }

            if (weather_date.get(list.get(position)) != null) {
                updateUI(weather_date.get(list.get(position)));
                //updateUI(list1.get(position));
            }
            return views.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) views.get(position));
            //super.destroyItem(container, position, object);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.city:
                    startActivity(new Intent(MainActivity.this, City_choiceActivity.class));
                    break;
                case R.id.add_city:
                    startActivity(new Intent(MainActivity.this, AddcityActivity.class));
                    break;
            }
        }
    }

}