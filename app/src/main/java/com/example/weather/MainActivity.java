package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.adapter.WeatherAdapter;
import com.example.weather.cb.impl.WeatherCallback;
import com.example.weather.entity.TodayWeather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    ViewPager mviewPager;
    List views;
    TextView ed_wendu;
    TextView ed_note;
    ListView listView;
    List list;
    List<TodayWeather> list1;

    Button button;

    TodayWeather info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=new ArrayList();
        list1=new ArrayList<>();

        list.add("北京");
        list.add("长沙");
        initView();
    }

    class MyPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.d("xxxx", "instantiateItem: position = "+position);
            container.addView((View) views.get(position));
            initviews((View) views.get(position));
            ed_wendu = ((View) views.get(position)).findViewById(R.id.ed_wendu);
            ed_note = ((View) views.get(position)).findViewById(R.id.ed_note);
            listView = ((View) views.get(position)).findViewById(R.id.list_weather);

            button=((View) views.get(position)).findViewById(R.id.button);
            button.setText((CharSequence) list.get(position));
            updateUI(list1.get(position));
//                if (list1!=null){
//                    updateUI((TodayWeather)list1.get(position));
//                }


            return views.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            //super.destroyItem(container, position, object);
            container.removeView((View) views.get(position));
        }
    }

    private void initView() {
        mviewPager =findViewById(R.id.viewPager);

        views=new ArrayList();

        //循环加载viewpager
        for (int i=0;i<2;i++ ){
            getWeather((String) list.get(i));
            views.add(getLayoutInflater().inflate(R.layout.mian,null));
        }
    }

    private void initviews(View view) {

    }

    //加载天气
    public void getWeather(String city){

        if(TextUtils.isEmpty(city)){
            Toast.makeText(this,"请输入城市",Toast.LENGTH_SHORT).show();
            return;
        }
        String url = getString(R.string.weatherurl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url+city)
                        .get()
                        .build();
                Call call = okHttpClient.newCall(request);
                System.out.println(call+"------");
                System.out.println(request);
                call.enqueue(new WeatherCallback(){
                    @Override
                    public void onComplete(TodayWeather todayWeather) {

                        System.out.println(todayWeather);
                        Log.d("tag", String.valueOf(todayWeather));
                        if(todayWeather != null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mviewPager.setOffscreenPageLimit(3);
                                    mviewPager.setAdapter(new MyPagerAdapter());
                                    //updateUI(todayWeather);
                                    //useSp(todayWeather.getCity());
                                }
                            });
                            info=todayWeather;
                            list1.add(info);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = "获取天气失败";
                        handler.sendMessage(msg);
                    }
                });
            }
        }).start();
    }



    private void updateUI(TodayWeather info) {
//        if(info.getWendu() == null){
//            Toast.makeText(MainActivity.this,"请输入正确的城市名称！", Toast.LENGTH_SHORT).show();
//            return;
//        }
        //info.getCity();
        ed_wendu.setText(info.getWendu()+"℃");
        ed_note.setText(info.getNote());
        WeatherAdapter weatherAdapter = new WeatherAdapter(this,R.layout.weather_item,info.getList());
        listView.setAdapter(weatherAdapter);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    TodayWeather info = (TodayWeather) msg.obj;
                    if(info == null){

                        info.getCity();
                        Toast.makeText(MainActivity.this,"请输入正确的城市名称" , Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //list1.add(info);
                    updateUI(info);
                    useSp(info.getCity());
                    break;
                case 0:
                    String str = (String) msg.obj;
                    Toast.makeText(MainActivity.this,str , Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    public void useSp(String city){
        SharedPreferences sp = getSharedPreferences("citys", Activity.MODE_PRIVATE);//创建sp对象,如果有key为"SP_PEOPLE"的sp就取出
        String citys = sp.getString("citys","");  //取出key为"KEY_PEOPLE_DATA"的值，如果值为空，则将第二个参数作为默认值赋值
        List<String> citylist = new ArrayList<>();
        String json = "";
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sp.edit() ;
        if(citys!="")  //防空判断
        {
            citylist = gson.fromJson(citys, new TypeToken<List<String>>() {}.getType()); //将json字符串转换成List集合
            if(citylist.contains(city)){
                return;
            }else{
                citylist.add(city);
                json = gson.toJson(citylist);
                editor.putString("citys", json) ; //存入json串
                editor.commit() ;
            }
        }else{
            citylist.add(city);
            json = gson.toJson(citylist);
            editor.putString("citys", json);
            editor.commit() ;
        }
    }
}