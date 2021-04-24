package com.example.weather.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.TTAdNative;
import com.example.weather.R;
import com.example.weather.config.TTAdManagerHolder;
import com.example.weather.util.BannerUtil;
import com.example.weather.util.StaticClass;

import static com.example.weather.MainActivity.list;
import static com.example.weather.MainActivity.map1;
import static com.example.weather.MainActivity.weather_date;
import static com.example.weather.util.OutputUtil.writeObjectIntoLocal;

public class City_choiceActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView return_button;
    ListView lv_city;
    //底部添加城市布局
    LinearLayout lin_add;
    //删除城市按钮
    ImageView city_delete;
    //记录删除键的显示隐藏
    boolean istrue = false;
    //修改城市设置取消按钮
    ImageView cancel_button;
    //修改城市设置确认按钮
    ImageView confirm_button;
    //定位图标
    ImageView location;
    //Banner广告布局
    private FrameLayout mBannerContainer;
    //
    TTAdNative mTTAdNative;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_choice);

        //保持屏幕竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //初始化
        initDate();
        initview();

        mBannerContainer=findViewById(R.id.banner_container);
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        BannerUtil.loadBannerAd(StaticClass.BANNERID,mTTAdNative, City_choiceActivity.this,mBannerContainer);


    }

    private void initview() {
        return_button.setOnClickListener(this);
        lv_city.setAdapter(new Myadapter());
        lv_city.setDivider(null);
        lin_add.setOnClickListener(this);
        confirm_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        //对listview进行长按监听
        lv_city.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                istrue = true;
                lv_city.setAdapter(new Myadapter());
                confirm_button.setVisibility(View.VISIBLE);
                lin_add.setVisibility(View.GONE);
                return true;
            }
        });
    }

    private void initDate() {

        confirm_button = findViewById(R.id.confirm_button);
        cancel_button = findViewById(R.id.cancel_button);
        lin_add = findViewById(R.id.lin_add);
        return_button = findViewById(R.id.return_button);
        lv_city = findViewById(R.id.lv_city);
    }

    protected void onStart() {
        super.onStart();
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();

        }
    }

    //点击事件处理
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_button:
                finish();
                break;
            case R.id.lin_add:
                startActivity(new Intent(City_choiceActivity.this, AddcityActivity.class));
                //修改城市确认按钮
            case R.id.confirm_button:
                Toast.makeText(City_choiceActivity.this, "修改数据成功", Toast.LENGTH_SHORT).show();
                istrue = false;
                lv_city.setAdapter(new Myadapter());
                confirm_button.setVisibility(View.GONE);
                lin_add.setVisibility(View.VISIBLE);
                break;
            //修改城市取消按钮
            case R.id.cancel_button:

                break;
        }
    }

    //自己的Adapter
    private class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return weather_date.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.lv_city_item, null);
            } else {
                view = convertView;
            }
            TextView city = view.findViewById(R.id.city);
            TextView temperature = view.findViewById(R.id.temperature);

            city_delete = view.findViewById(R.id.city_delete);
            location = view.findViewById(R.id.location);
            if (position == 0) {
                location.setImageResource(R.mipmap.location);
            }

            city_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //finalView.setVisibility(View.GONE);
                    if (weather_date.size()==1) {
                        Toast.makeText(City_choiceActivity.this, "请至少保留一个定位或者关注城市哦", Toast.LENGTH_SHORT).show();
                    } else {
                        weather_date.remove(list.get(position));
                        list.remove(position);
                        writeObjectIntoLocal(getApplicationContext(),map1,weather_date);
                        notifyDataSetChanged();
                    }

                }
            });
            if (istrue) {
                city_delete.setVisibility(View.VISIBLE);
            } else {
                city_delete.setVisibility(View.GONE);
            }

            city.setText((CharSequence) list.get(position));
            temperature.setText((CharSequence) weather_date.get(list.get(position)).getWendu() + "°");
            return view;
        }


    }
}