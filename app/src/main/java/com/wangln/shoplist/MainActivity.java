package com.wangln.shoplist;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HttpCallback{
    List<ShopBean> list = new ArrayList<>();
    MyAdapter adapter;
    BestRefreshView brv;
    TextView textView;
    TextView textView1;
    ImageView imageView;
    ViewPager viewPager;
    AnimationDrawable animationDrawable;
    int count = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            brv.endRefresh();
        }
    };
    RelativeLayout relativeLayout;
    LinearLayout linearLayout;
    TextView title_tv;
    TextView title_tv_clone;
    boolean isAdd = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = (LinearLayout) findViewById(R.id.ll);
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv_clone = (TextView) LayoutInflater.from(this).inflate(R.layout.title_tv,null);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
        brv = (BestRefreshView) findViewById(R.id.brv);
        GridView gv = (GridView) findViewById(R.id.gv);
        View view = LayoutInflater.from(this).inflate(R.layout.footer,null);
        View view1 = LayoutInflater.from(this).inflate(R.layout.header,null);
        textView = (TextView) view.findViewById(R.id.tv);
        imageView = (ImageView) view1.findViewById(R.id.iv_header);
        viewPager = (ViewPager) findViewById(R.id.vp);
        ScrollView scrollView = (ScrollView) findViewById(R.id.sv);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int top = viewPager.getTop();
                float y = viewPager.getY();
                Log.d("wang","top " + top + " y " + y + " scrollY " + scrollY);
                if(scrollY >= y && !isAdd) {
                    relativeLayout.addView(title_tv_clone,title_tv.getLayoutParams());
                    isAdd = true;
                } else if(scrollY < y && isAdd){
                    relativeLayout.removeView(title_tv_clone);
                    isAdd = false;
                }
            }
        });
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        brv.setFooterLayout(view);
        brv.setHeaderLayout(view1);
        brv.setOnRefreshLisetner(new OnRefreshListner() {
            @Override
            public void onPull(float value, int mode) {
                Log.d("wang","onPull " + value);
                if(value <= 1) {
                    imageView.setScaleX(value);
                    imageView.setScaleY(value);
                }
            }

            @Override
            public void onPullToRefresh(float value, int mode) {

            }

            @Override
            public void onRefresh(int mode) {
                Log.d("wang","onRefresh");
                if(mode==BestRefreshView.PULL_UP) {
                    textView.setText("加载中...");
                    MyTask task = new MyTask(MainActivity.this);
                    count++;
                    task.execute("http://114.215.46.63/Test/first/homepage", "page=" + count + "&rows=10");
                }
                if(mode==BestRefreshView.PULL_DOWN) {
                    animationDrawable.start();
                    handler.sendEmptyMessageDelayed(123,4000);
                }
            }

            @Override
            public void onRelease(float value, int mode) {
                textView.setText("加载更多");
                if(mode == BestRefreshView.PULL_DOWN) {
                    animationDrawable.stop();
                }
            }
        });
        adapter = new MyAdapter(this,list);
        gv.setAdapter(adapter);
        MyTask task = new MyTask(this);
        task.execute("http://114.215.46.63/Test/first/homepage","page=0&rows=10");
    }
    @Override
    public void onSuccess(Object result) {
        List<ShopBean> list1 = parseJson((String) result);
        list.addAll(list1);
        brv.endRefresh();
//        adapter.setList(list1);
        adapter.notifyDataSetChanged();
        Log.d("wang",list.toString());
    }
    private List<ShopBean> parseJson(String json){
        List<ShopBean> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray array = data.getJSONArray("recommendationList");
            for (int i = 0;i < array.length();i++){
                JSONObject child = array.getJSONObject(i);
                int shopid = child.getInt("shopid");
                String shoptitle = child.getString("shoptitle");
                double shopprice = child.getDouble("shopprice");
                int shoptype = child.getInt("shoptype");
                String shoppictureurl = child.getString("shoppictureurl");
                ShopBean shopBean = new ShopBean();
                shopBean.setShopid(shopid);
                shopBean.setShoppictureurl(shoppictureurl);
                shopBean.setShopprice(shopprice);
                shopBean.setShoptitle(shoptitle);
                shopBean.setShoptype(shoptype);
                list.add(shopBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
