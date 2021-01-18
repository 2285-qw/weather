package com.example.weather.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.R;

import static com.example.weather.MainActivity.list;
import static com.example.weather.MainActivity.weather_date;

public class City_choiceActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView return_button;
    ListView lv_city;
    LinearLayout lin_add;
    ImageView city_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_choice);
        initDate();
        initview();
    }

    private void initview() {
        return_button.setOnClickListener(this);
        lv_city.setAdapter(new Myadapter());
        lv_city.setDivider(null);
        lin_add.setOnClickListener(this);
        lv_city.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(City_choiceActivity.this, "长按", Toast.LENGTH_SHORT).show();

                ImageView city_delete = view.findViewById(R.id.city_delete);
                city_delete.setVisibility(View.VISIBLE);

                return true;
            }
        });
    }

    private void initDate() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_button:
                finish();
                break;
            case R.id.lin_add:
                startActivity(new Intent(City_choiceActivity.this, AddcityActivity.class));
        }
    }

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
            city_delete=view.findViewById(R.id.city_delete);

            city.setText((CharSequence) list.get(position));
            temperature.setText((CharSequence) weather_date.get(list.get(position)).getWendu() + "°");
            return view;
        }
    }
}