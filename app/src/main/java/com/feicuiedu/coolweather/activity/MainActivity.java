package com.feicuiedu.coolweather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.feicuiedu.coolweather.R;
import com.feicuiedu.coolweather.service.Gobla;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.limxing.xlistview.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.feicuiedu.coolweather.R.drawable.weather;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //标题栏
    private Toolbar toolbar;

    //图标指示器滑动
    private ViewPager viewPager;
    private ImageView[] icons = new ImageView[4];

    //从网络获取未来四日的早晚天气图片，创建view
    private ImageView[] imageView = new ImageView[8];
    private View view0;
    private View view1;
    private View view2;
    private View view3;
    private List<View> viewList = new ArrayList<View>();

    private ListView list_weatherdata;
    private List<Map<String,String>> list;

    private LineChart chart;

    private XListView listView;
    private List<String> xlist;

    private TextView tvCityName;
    Date sysDate = new Date();

    int currentItem;
    private ScheduledExecutorService executor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**标题栏设置*/
        toolBarSetUp();

        /**初始化*/
        initId();

        initViewPager();

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(pageChangeListener);

        list = requestNet();
        list_weatherdata.setAdapter(baseAdapter);

        chartSet();

        getData();

        listView.setXListViewListener(listViewListener);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(true);

        listView.setAdapter(ba);
    }

    private List<String> getData() {

        xlist = new ArrayList<String>();

        for (int i = 0; i < 10; i++) {
            xlist.add("item");
        }
        return xlist;
    }

    /**
     * 下拉数据处理
     *
     * @return
     */
    private List<String> getXiaLaData() {

        List<String> downList = new ArrayList<String>();
        for (int i = 0; i < 4; i++) {
            downList.add("下拉增加的数据");
        }
        downList.addAll(xlist);

        if (downList.size() >10) {

            downList = downList.subList(0, 9);

        }

        return downList;
    }

    /**
     * 上推数据处理
     *
     * @return
     */
    private List<String> getShangTuiData() {

        List<String> upList = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            upList.add("上推增加的数据");
        }
        xlist.addAll(upList);

        if (xlist.size() >10) {

            xlist = xlist.subList(xlist.size()-10, xlist.size()-1);

        }
        return xlist;
    }

    private static class ViewHolder {
        TextView tv;
    }

    private BaseAdapter ba = new BaseAdapter() {
        ViewHolder vh = null;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main_item, null);

                vh = new ViewHolder();
                vh.tv = (TextView) convertView.findViewById(R.id.item_tv1);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            vh.tv.setText(getItemId(position) + ":" + getItem(position));

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }
    };

    private XListView.IXListViewListener listViewListener = new XListView.IXListViewListener() {

        // 下拉方法
        @Override
        public void onRefresh() {
            // 加载数据。。。。。。。。。。。。。。。。。。。
            Log.d("refresh", "11111111111111111");
            xlist = getXiaLaData();
            ba.notifyDataSetChanged();
            // 加载完毕
            listView.stopLoadMore();
            listView.stopRefresh(true);
            listView.setRefreshTime(dateToString(sysDate));
        }

        // 上推方法
        @Override
        public void onLoadMore() {
            // 加载数据。。。。。。。。。。。。。。。。。。。
            Log.d("onLoadMore", "2222222222222");
            xlist = getShangTuiData();
            ba.notifyDataSetChanged();
            listView.stopLoadMore();
            listView.stopRefresh(true);
        }
    };



    private void chartSet(){
        setChart(chart);
        // 制作4个数据点。
        setData(chart,4);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        XAxis xAxis = chart.getXAxis();
        // 将X坐标轴的标尺刻度移动底部。
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // X轴之间数值的间隔
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        YAxis leftAxis = chart.getAxisLeft();
        setYAxisLeft(leftAxis);
    }

    private void setChart(LineChart mChart) {
        mChart.setNoDataTextDescription("如果传递的数值是空，那么你将看到这段文字。");
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.LTGRAY);
        mChart.animateX(3000);
    }

    private void setYAxisLeft(YAxis leftAxis) {
        // 在左侧的Y轴上标出4个刻度值
        leftAxis.setLabelCount(5, true);

        // Y坐标轴轴线的颜色
        leftAxis.setGridColor(Color.RED);

        // Y轴坐标轴上坐标刻度值的颜色
        leftAxis.setTextColor(Color.RED);

        // Y坐标轴最大值
        leftAxis.setAxisMaxValue(10);

        // Y坐标轴最小值
        leftAxis.setAxisMinValue(0);

        leftAxis.setStartAtZero(false);

        leftAxis.setDrawLabels(true);
    }

    private void setData(LineChart mChart, int count) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(""+i+"");
        }

        ArrayList<Entry> yHigh = new ArrayList<Entry>();
        LineDataSet high = new LineDataSet(yHigh, "平均温度");
        setTemperature(high, yHigh, count);


        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(high);

        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueTextSize(10f);
        mChart.setData(data);
    }

    private void setTemperature(LineDataSet high, ArrayList<Entry> yVals, int count) {

        for (int i = 0; i < count; i++) {
            float val = (float) Math.random() + 5;
            yVals.add(new Entry(val, i));
        }

        // 以左边的Y坐标轴为准
        high.setAxisDependency(YAxis.AxisDependency.LEFT);

        high.setLineWidth(5f);
        high.setColor(Color.RED);
        high.setCircleSize(8f);
        high.setCircleColor(Color.YELLOW);
        high.setCircleColorHole(Color.DKGRAY);
        high.setDrawCircleHole(true);

        // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
        high.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                DecimalFormat decimalFormat = new DecimalFormat(".0");
                String s = "平均温度" + decimalFormat.format(v);
                return s;
            }

        });

    }



    /**
     * ToolBar属性设置
     */
    private void toolBarSetUp(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /**设置logo*/
        toolbar.setLogo(weather);
        /**设置日期为子标题*/
        toolbar.setSubtitle(dateToString(sysDate));
    }

    /***
     * id初始化
     */
    private void initId(){
        icons[0] = (ImageView) findViewById(R.id.icon1);
        icons[1] = (ImageView) findViewById(R.id.icon2);
        icons[2] = (ImageView) findViewById(R.id.icon3);
        icons[3] = (ImageView) findViewById(R.id.icon4);
        icons[0].setImageResource(R.drawable.adware_style_selected);

        list_weatherdata = (ListView) findViewById(R.id.lv_weatherdata);
        chart = (LineChart) findViewById(R.id.chart);

        listView = (XListView) findViewById(R.id.lv1);
        tvCityName = (TextView) findViewById(R.id.tv_city);
        tvCityName.setText(Gobla.LOCATION);
    }

    /**
     * 初始化天气图片，添加到view中
     */
    private void initViewPager(){

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater = getLayoutInflater();

        view0 = inflater.inflate(R.layout.layout_icon_vp0,null);
        imageView[0] = (ImageView) view0.findViewById(R.id.iv1);
        imageView[1] = (ImageView) view0.findViewById(R.id.iv2);
        view1 = inflater.inflate(R.layout.layout_icon_vp1,null);
        imageView[2] = (ImageView) view1.findViewById(R.id.iv3);
        imageView[3] = (ImageView) view1.findViewById(R.id.iv4);
        view2 = inflater.inflate(R.layout.layout_icon_vp2,null);
        imageView[4] = (ImageView) view2.findViewById(R.id.iv5);
        imageView[5] = (ImageView) view2.findViewById(R.id.iv6);
        view3 = inflater.inflate(R.layout.layout_icon_vp3,null);
        imageView[6] = (ImageView) view3.findViewById(R.id.iv7);
        imageView[7] = (ImageView) view3.findViewById(R.id.iv8);

        requestNet();

        viewList.add(view0);
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

    }

    private List<Map<String,String>> requestNet(){
        final List<Map<String,String>> tmpw = new ArrayList<Map<String,String>>();
        RequestParams requestParams = new RequestParams(Gobla.SERVER_URL);
        requestParams.addParameter("location",Gobla.LOCATION);
        requestParams.addParameter("output",Gobla.OUTPUT);
        requestParams.addParameter("ak",Gobla.AK);
        Log.d(TAG, "requestNet: "+requestParams);
        Callback.Cancelable cancelable = x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: result="+result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    JSONObject resutObj=jsonArray.getJSONObject(0);
                    tvCityName.setText(resutObj.getString("currentCity"));
                    JSONArray weatherArray = resutObj.getJSONArray("weather_data");
                    for (int i = 0;i < 4;i++){
                        JSONObject weatherObj = weatherArray.getJSONObject(i);
                        String date = weatherObj.getString("date");
                        String weather = weatherObj.getString("weather");
                        String wind = weatherObj.getString("wind");
                        String temperature = weatherObj.getString("temperature");
                        String dayPictureUrl = weatherObj.getString("dayPictureUrl");
                        String nightPictureUrl = weatherObj.getString("nightPictureUrl");
                        Log.d(TAG, "dayPictureUrl: "+dayPictureUrl);

                        x.image().bind(imageView[2*i+0],dayPictureUrl);
                        x.image().bind(imageView[2*i+1],nightPictureUrl);

                        Map<String,String> map = new HashMap<String,String>();
                        map.put("date",date);
                        map.put("weather",weather);
                        map.put("wind",wind);
                        map.put("temperature",temperature);
                        tmpw.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError: result="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "onCancelled: result="+cex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished: ");
            }
        });
        return tmpw;
    }

    private PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position % viewList.size()));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position % viewList.size()),0);
            return viewList.get(position % viewList.size());
        }
    };

    /**
     * 指示标创监听
     */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            startAutoScroll();
            setImageBackground(position % viewList.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 图标显示图片
     * @param selectItems
     */
    private void setImageBackground(int selectItems){
        for (int i = 0; i < icons.length; i++) {
            if (i == selectItems){
                icons[i].setImageResource(R.drawable.adware_style_selected);
            }else{
                icons[i].setImageResource(R.drawable.adware_style_default);}
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        startAutoScroll(); // activity激活时候自动播放
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopAutoScroll(); // activity暂停时候停止自动播放
    }

    private void startAutoScroll() {
        stopAutoScroll();

        executor = Executors.newSingleThreadScheduledExecutor();
        Runnable command = new Runnable() {
            @Override
            public void run() {
                selectNextItem();
            }

            private void selectNextItem() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        viewPager.setCurrentItem(++currentItem);

                        if (currentItem >= 3) {
                            currentItem = -1;
                        }
                    }
                });
            }
        };
        int delay = 2;
        int period = 2;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        executor.scheduleAtFixedRate(command, delay, period, timeUnit);
    }

    private void stopAutoScroll() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    /**
     * weatherdata的listview展示
     */
    private BaseAdapter baseAdapter = new BaseAdapter() {

        // 定义数据的个数
        @Override
        public int getCount() {
            return list.size();
        }

        // 定义每行数据的对象
        @Override
        public Map<String,String> getItem(int position) {
            return list.get(position);
        }

        // 定义每行的id值
        @Override
        public long getItemId(int position) {
            return position;
        }

        // 定义每行都 是什么样的布局,如何展示数据
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Map<String,String> tmpMap = getItem(position);

            String date = tmpMap.get("date");
            String weather = tmpMap.get("weather");
            String wind = tmpMap.get("wind");
            String temperature = tmpMap.get("temperature");


            View view = getLayoutInflater().inflate(R.layout.layout_weatherdata,null);

            TextView tvdate = (TextView) view.findViewById(R.id.date);
            TextView tvweather= (TextView) view.findViewById(R.id.weather);
            TextView tvwind = (TextView) view.findViewById(R.id.wind);
            TextView tvtemperature = (TextView) view.findViewById(R.id.temperature);

            tvdate.setText(date);
            tvweather.setText(weather);
            tvwind.setText(wind);
            tvtemperature.setText(temperature);

            return view;
        }
    };

    /**
     * 时间转换为字符串
     */
    public static String dateToString(Date date) {
       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str = format.format(date);
        Log.d(TAG, "dateToString: "+str);
        return str;
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    /**
     * 定义菜单响应事件
     * 分别跳转到choosecityActivity，IndexActivity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.choosecity:
                Intent cityIntent = new Intent(this,ChoosecityActivity.class);
                startActivity(cityIntent);
                break;
            case R.id.airindex:
                Intent indexIntent = new Intent(this,IndexActivity.class);
                startActivity(indexIntent);
                break;
            default:
        }
        return true;
    }



}
