package com.feicuiedu.coolweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.feicuiedu.coolweather.R;
import com.feicuiedu.coolweather.service.Gobla;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/1/2.
 */

public class IndexActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView list_index;
    private List<Map<String,String>> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        list_index = (ListView) findViewById(R.id.list_index);
        list = getData();
        list_index.setAdapter(baseAdapter);
    }


    private List<Map<String,String>> getData() {
        final List<Map<String,String>> tmp = new ArrayList<Map<String,String>>();
        RequestParams requestParams = new RequestParams(Gobla.SERVER_URL);
        requestParams.addParameter("location",Gobla.LOCATION);
        requestParams.addParameter("output",Gobla.OUTPUT);
        requestParams.addParameter("ak",Gobla.AK);
        Callback.Cancelable cancelable = x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    JSONObject resutObj=jsonArray.getJSONObject(0);
                    JSONArray indexArray = resutObj.getJSONArray("index");
                    for (int i = 0;i < 6;i++){
                        JSONObject indexObj = indexArray.getJSONObject(i);
                        String title = indexObj.getString("title");
                        String zs = indexObj.getString("zs");
                        String tipt = indexObj.getString("tipt");
                        String des = indexObj.getString("des");

                        Map<String,String> map = new HashMap<String,String>();
                        map.put("title",title);
                        map.put("zs",zs);
                        map.put("tipt",title);
                        map.put("des",des);
                        tmp.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });

        return tmp;
    }

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

            String title = tmpMap.get("title");
            String zs = tmpMap.get("zs");
            String tipt = tmpMap.get("tipt");
            String des = tmpMap.get("des");


            View view = getLayoutInflater().inflate(R.layout.layout_index,null);

            TextView tvtitle = (TextView) view.findViewById(R.id.title);
            TextView tvzs= (TextView) view.findViewById(R.id.zs);
            TextView tvtipt = (TextView) view.findViewById(R.id.tipt);
            TextView tvdes = (TextView) view.findViewById(R.id.des);

            tvtitle.setText(title);
            tvzs.setText(zs);
            tvtipt.setText(tipt);
            tvdes.setText(des);

            return view;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.city_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.city_cancel:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
