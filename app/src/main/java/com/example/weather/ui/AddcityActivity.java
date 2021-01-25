package com.example.weather.ui;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.application.BaseApplication;
import com.example.weather.binding.Bind;
import com.example.weather.binding.ViewBinder;
import com.example.weather.cb.impl.WeatherCallback;
import com.example.weather.entity.TodayWeather;
import com.example.weather.model.CityEntity;
import com.example.weather.service.LocationService;
import com.example.weather.service.Utils;
import com.example.weather.util.JsonReadUtil;
import com.example.weather.util.ScreenUtils;
import com.example.weather.util.ToastUtils;
import com.example.weather.view.LetterListView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.example.weather.MainActivity.list;
import static com.example.weather.MainActivity.map1;
import static com.example.weather.MainActivity.weather_date;
import static com.example.weather.util.OutputUtil.fileIsExists;
import static com.example.weather.util.OutputUtil.readObjectFromLocal;
import static com.example.weather.util.OutputUtil.writeObjectIntoLocal;

public class AddcityActivity extends AppCompatActivity implements AbsListView.OnScrollListener, View.OnClickListener {

    //文件名称
    private final static String CityFileName = "allcity.json";

    @Bind(R.id.tool_bar_fl)
    private FrameLayout mToolbar;
    @Bind(R.id.search_locate_content_et)
    private EditText searchContentEt;
    @Bind(R.id.total_city_lv)
    private ListView totalCityLv;
    @Bind(R.id.total_city_letters_lv)
    private LetterListView lettersLv;
    @Bind(R.id.search_city_lv)
    private ListView searchCityLv;
    @Bind(R.id.no_search_result_tv)
    private TextView noSearchDataTv;
    @Bind(R.id.return_button1)
    private ImageView return_button;
    @Bind(R.id.yisi)
    private TextView yisi;

    private Handler handler;
    private TextView overlay; // 对话框首字母TextView
    private OverlayThread overlayThread; // 显示首字母对话框
    private boolean mReady = false;
    private boolean isScroll = false;

    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置

    protected List<CityEntity> hotCityList = new ArrayList<>();
    protected List<CityEntity> totalCityList = new ArrayList<>();
    protected List<CityEntity> curCityList = new ArrayList<>();
    protected List<CityEntity> searchCityList = new ArrayList<>();
    protected CityListAdapter cityListAdapter;
    protected SearchCityListAdapter searchCityListAdapter;

    private String locationCity, curSelCity;

    //位置获取后台
    private LocationService locationService;
    //记录市的字符串
    private  String location1="定位";

    TextView curCityNameTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 默认软键盘不弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //setSystemBarTransparent();


        setContentView(R.layout.activity_addcity);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        ViewBinder.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = ScreenUtils.getSystemBarHeight();
            mToolbar.setPadding(0, top, 0, 0);
        }

        handler = new Handler();
        overlayThread = new OverlayThread();
        searchCityListAdapter = new SearchCityListAdapter(this, searchCityList);
        searchCityLv.setAdapter(searchCityListAdapter);
        locationCity = "定位";
        curSelCity = locationCity;

        yisi.setOnClickListener(this);
    }

    private void initData() {
        initTotalCityList();
        cityListAdapter = new CityListAdapter(this, totalCityList, hotCityList);
        totalCityLv.setAdapter(cityListAdapter);
        totalCityLv.setOnScrollListener(this);
        totalCityLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 1) {
                    CityEntity cityEntity = totalCityList.get(position);
                    showSetCityDialog(cityEntity.getName(), cityEntity.getCityCode());
                }

            }
        });
        lettersLv.setOnTouchingLetterChangedListener(new LetterListViewListener());
        initOverlay();

        return_button.setOnClickListener(this);
    }

    private void initListener() {

        searchCityLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityEntity cityEntity = searchCityList.get(position);
                showSetCityDialog(cityEntity.getName(), cityEntity.getCityCode());
            }
        });
        searchContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = searchContentEt.getText().toString().trim().toLowerCase();
                setSearchCityList(content);
            }
        });

        searchContentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftInput(searchContentEt.getWindowToken());
                    String content = searchContentEt.getText().toString().trim().toLowerCase();
                    setSearchCityList(content);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 设置搜索数据展示
     */
    private void setSearchCityList(String content) {
        searchCityList.clear();
        if (TextUtils.isEmpty(content)) {
            totalCityLv.setVisibility(View.VISIBLE);
            lettersLv.setVisibility(View.VISIBLE);
            searchCityLv.setVisibility(View.GONE);
            noSearchDataTv.setVisibility(View.GONE);
        } else {
            totalCityLv.setVisibility(View.GONE);
            lettersLv.setVisibility(View.GONE);
            for (int i = 0; i < curCityList.size(); i++) {
                CityEntity cityEntity = curCityList.get(i);
                if (cityEntity.getName().contains(content) || cityEntity.getPinyin().contains(content)
                        || cityEntity.getFirst().contains(content)) {
                    searchCityList.add(cityEntity);
                }
            }

            if (searchCityList.size() != 0) {
                noSearchDataTv.setVisibility(View.GONE);
                searchCityLv.setVisibility(View.VISIBLE);
            } else {
                noSearchDataTv.setVisibility(View.VISIBLE);
                searchCityLv.setVisibility(View.GONE);
            }

            searchCityListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化全部城市列表
     */
    public void initTotalCityList() {
        hotCityList.clear();
        totalCityList.clear();
        curCityList.clear();

        String cityListJson = JsonReadUtil.getJsonStr(this, CityFileName);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(cityListJson);
            JSONArray array = jsonObject.getJSONArray("City");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                String key = object.getString("key");
                String pinyin = object.getString("full");
                String first = object.getString("first");
                String cityCode = object.getString("code");

                CityEntity cityEntity = new CityEntity();
                cityEntity.setName(name);
                cityEntity.setKey(key);
                cityEntity.setPinyin(pinyin);
                cityEntity.setFirst(first);
                cityEntity.setCityCode(cityCode);

                if (key.equals("热门")) {
                    hotCityList.add(cityEntity);
                } else {
                    if (!cityEntity.getKey().equals("0") && !cityEntity.getKey().equals("1")) {
                        curCityList.add(cityEntity);
                    }
                    totalCityList.add(cityEntity);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL
                || scrollState == SCROLL_STATE_FLING) {
            isScroll = true;
        } else {
            isScroll = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (!isScroll) {
            return;
        }

        if (mReady) {
            String key = getAlpha(totalCityList.get(firstVisibleItem).getKey());
            overlay.setText(key);
            overlay.setVisibility(View.VISIBLE);
            handler.removeCallbacks(overlayThread);
            // 延迟让overlay为不可见
            handler.postDelayed(overlayThread, 700);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_button1:
                startActivity(new Intent(AddcityActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.yisi:
                startActivity(new Intent(AddcityActivity.this, yisiActivity.class));
        }
    }

    /**
     * 总城市适配器
     */
    private class CityListAdapter extends BaseAdapter {
        private Context context;

        private List<CityEntity> totalCityList;
        private List<CityEntity> hotCityList;
        private LayoutInflater inflater;
        final int VIEW_TYPE = 3;

        CityListAdapter(Context context,
                        List<CityEntity> totalCityList,
                        List<CityEntity> hotCityList) {
            this.context = context;
            this.totalCityList = totalCityList;
            this.hotCityList = hotCityList;
            inflater = LayoutInflater.from(context);

            alphaIndexer = new HashMap<>();

            for (int i = 0; i < totalCityList.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = totalCityList.get(i).getKey();

                String previewStr = (i - 1) >= 0 ? totalCityList.get(i - 1).getKey() : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = getAlpha(currentStr);
                    alphaIndexer.put(name, i);
                }
            }
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE;
        }

        @Override
        public int getItemViewType(int position) {
            return position < 2 ? position : 2;
        }

        @Override
        public int getCount() {
            return totalCityList == null ? 0 : totalCityList.size();
        }

        @Override
        public Object getItem(int position) {
            return totalCityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int viewType = getItemViewType(position);
            if (viewType == 0) { // 定位
                convertView = inflater.inflate(R.layout.select_city_location_item, null);

                LinearLayout noLocationLl = convertView.findViewById(R.id.cur_city_no_data_ll);
                TextView getLocationTv = convertView.findViewById(R.id.cur_city_re_get_location_tv);
                curCityNameTv = convertView.findViewById(R.id.cur_city_name_tv);

                if (TextUtils.isEmpty(locationCity)) {
                    noLocationLl.setVisibility(View.VISIBLE);
                    curCityNameTv.setVisibility(View.GONE);
                    getLocationTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //获取定位

                        }
                    });
                } else {
                    noLocationLl.setVisibility(View.GONE);
                    curCityNameTv.setVisibility(View.VISIBLE);

                    curCityNameTv.setText(locationCity);
                    curCityNameTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!locationCity.equals(curSelCity)) {
                                //设置城市代码
                                String cityCode = "";
                                for (CityEntity cityEntity : curCityList) {
                                    if (cityEntity.getName().equals(locationCity)) {
                                        cityCode = cityEntity.getCityCode();
                                        break;
                                    }
                                }
                                showSetCityDialog(locationCity, cityCode);
                            } else {
                                if (location1.isEmpty()){
                                    locationService.start();// 定位SDK
                                    curCityNameTv.setText(location1);
                                }else{
                                    //定位成功后的操作
                                    showSetCityDialog(location1,null);
                                }
                            }
                        }
                    });
                }
            } else if (viewType == 1) { //热门城市
                convertView = inflater.inflate(R.layout.recent_city_item, null);
                GridView hotCityGv = convertView.findViewById(R.id.recent_city_gv);
                hotCityGv.setAdapter(new HotCityListAdapter(context, this.hotCityList));
                hotCityGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        CityEntity cityEntity = hotCityList.get(position);
                        showSetCityDialog(cityEntity.getName(), cityEntity.getCityCode());
                    }
                });
            } else {
                if (null == convertView) {
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.city_list_item_layout, null);
                    ViewBinder.bind(holder, convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                CityEntity cityEntity = totalCityList.get(position);
                holder.cityKeyTv.setVisibility(View.VISIBLE);
                holder.cityKeyTv.setText(getAlpha(cityEntity.getKey()));
                holder.cityNameTv.setText(cityEntity.getName());

                if (position >= 1) {
                    CityEntity preCity = totalCityList.get(position - 1);
                    if (preCity.getKey().equals(cityEntity.getKey())) {
                        holder.cityKeyTv.setVisibility(View.GONE);
                    } else {
                        holder.cityKeyTv.setVisibility(View.VISIBLE);
                    }
                }
            }

            return convertView;
        }

        private class ViewHolder {
            @Bind(R.id.city_name_tv)
            TextView cityNameTv;
            @Bind(R.id.city_key_tv)
            TextView cityKeyTv;
        }
    }

    /**
     * 热门城市适配器
     */
    private class HotCityListAdapter extends BaseAdapter {

        private List<CityEntity> cityEntities;
        private LayoutInflater inflater;

        HotCityListAdapter(Context mContext, List<CityEntity> cityEntities) {
            this.cityEntities = cityEntities;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return cityEntities == null ? 0 : cityEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return cityEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.city_list_grid_item_layout, null);
                ViewBinder.bind(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CityEntity cityEntity = cityEntities.get(position);
            holder.cityNameTv.setText(cityEntity.getName());

            return convertView;
        }

        private class ViewHolder {
            @Bind(R.id.city_list_grid_item_name_tv)
            TextView cityNameTv;
        }
    }

    /**
     * 搜索城市列表适配器
     */
    private class SearchCityListAdapter extends BaseAdapter {

        private List<CityEntity> cityEntities;
        private LayoutInflater inflater;

        SearchCityListAdapter(Context mContext, List<CityEntity> cityEntities) {
            this.cityEntities = cityEntities;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return cityEntities == null ? 0 : cityEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return cityEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.city_list_item_layout, null);
                ViewBinder.bind(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CityEntity cityEntity = cityEntities.get(position);
            holder.cityKeyTv.setVisibility(View.GONE);
            holder.cityNameTv.setText(cityEntity.getName());

            return convertView;
        }


        private class ViewHolder {
            @Bind(R.id.city_name_tv)
            TextView cityNameTv;
            @Bind(R.id.city_key_tv)
            TextView cityKeyTv;
        }
    }

    /**
     * 获得首字母
     */
    private String getAlpha(String key) {
        if (key.equals("0")) {
            return "定位";
        } else if (key.equals("1")) {
            return "热门";
        } else {
            return key;
        }
    }


    /**
     * 初始化汉语拼音首字母弹出提示框
     */
    private void initOverlay() {
        mReady = true;
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }


    private class LetterListViewListener implements
            LetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            isScroll = false;
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                totalCityLv.setSelection(position);
                overlay.setText(s);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟让overlay为不可见
                handler.postDelayed(overlayThread, 700);
            }
        }
    }

    /**
     * 设置overlay不可见
     */
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    /**
     * 展示设置城市对话框
     */
    private void showSetCityDialog(final String curCity, final String cityCode) {
        if (curCity.equals(curSelCity)) {
            ToastUtils.show("当前定位城市" + curCity);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否添加" + curCity + "的天气预报？"); //设置内容

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //选中之后做你的方法
                if ( weather_date.containsKey(curCity)){
                    Toast.makeText(AddcityActivity.this, "城市"+curCity+"已经存在", Toast.LENGTH_SHORT).show();
                }else {
                    getWeather(curCity);
                }


            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * 隐藏软件盘
     */
    public void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 设置沉浸式状态栏
     */
    private void setSystemBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0 LOLLIPOP解决方案
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4 KITKAT解决方案
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        locationService = ((BaseApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.start();
        }
        //开启定位
        locationService.start();// 定位SDK
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
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
                            if (todayWeather.getWendu() == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddcityActivity.this, "当前城市暂时查询不到信息", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }else {
                                if (fileIsExists(map1)){
                                    weather_date=readObjectFromLocal(getApplicationContext(),map1);
                                }else {
                                    weather_date=new HashMap<>();
                                    list=new ArrayList();
                                }
                                weather_date.put(city,todayWeather);
                                list.add(city);
                                Log.d("weather_date",weather_date+"");
                                writeObjectIntoLocal(getApplicationContext(),map1,weather_date);
                                startActivity(new Intent(AddcityActivity.this,MainActivity.class));
                                finish();
                            }
                        }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddcityActivity.this, "请检查您的网络连接是否正常", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).start();
    }



    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {

            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                int tag = 1;
                StringBuffer sb = new StringBuffer(256);

                if (!(location.getCity()==null)){
                    location1=location.getCity();
                }

                if (!location1.isEmpty()){
                    //startLocation.setText(location1);
                    curCityNameTv.setText(location1);
                    locationService.stop();
                }else {
                    Toast.makeText(AddcityActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    //sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    //sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    //sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                logMsg(sb.toString(), tag);
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            super.onConnectHotSpotMessage(s, i);
        }

        /**
         * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
         * @param locType 当前定位类型
         * @param diagnosticType 诊断类型（1~9）
         * @param diagnosticMessage 具体的诊断信息释义
         */
        @Override
        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
            super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
            int tag = 2;
            StringBuffer sb = new StringBuffer(256);
            //sb.append("诊断结果: ");
            if (locType == BDLocation.TypeNetWorkLocation) {
                if (diagnosticType == 1) {
                    sb.append("网络定位成功，没有开启GPS，建议打开GPS会更好");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 2) {
                    sb.append("网络定位成功，没有开启Wi-Fi，建议打开Wi-Fi会更好");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeOffLineLocationFail) {
                if (diagnosticType == 3) {
                    sb.append("定位失败，请您检查您的网络状态");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeCriteriaException) {
                if (diagnosticType == 4) {
                    sb.append("定位失败，无法获取任何有效定位依据");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 5) {
                    sb.append("定位失败，无法获取有效定位依据，请检查运营商网络或者Wi-Fi网络是否正常开启，尝试重新请求定位");
                    sb.append(diagnosticMessage);
                } else if (diagnosticType == 6) {
                    sb.append("定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开Wi-Fi重试");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 7) {
                    sb.append("定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 9) {
                    sb.append("定位失败，无法获取任何有效定位依据");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeServerError) {
                if (diagnosticType == 8) {
                    sb.append("定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限");
                    sb.append("\n" + diagnosticMessage);
                }
            }
            logMsg(sb.toString(), tag);
        }
    };

    /**
     * 显示请求字符串
     *
     * @param str
     */
    public void logMsg(final String str, final int tag) {

        try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tag == Utils.RECEIVE_TAG) {
                                    Toast.makeText(AddcityActivity.this, str, Toast.LENGTH_SHORT).show();
                                    Log.d("tag",str);
                                } else if (tag == Utils.DIAGNOSTIC_TAG) {
                                    //LocationDiagnostic.setText(str);
                                    Toast.makeText(AddcityActivity.this, str, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                            }


                }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
