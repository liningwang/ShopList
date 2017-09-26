package com.wangln.shoplist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2017/9/25 0025.
 */

public class MyAdapter extends BaseAdapter {
    Context context;
    List<ShopBean> list;
    public MyAdapter(Context context, List<ShopBean> list){
        this.context = context;
        this.list = list;
    }
    public void setList(List<ShopBean> list) {
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
//        ImageView shop = (ImageView) view.findViewById(R.id.specialShop);
        TextView title = (TextView) view.findViewById(R.id.title1);
        TextView price = (TextView) view.findViewById(R.id.price);
        title.setText(list.get(position).getShoptitle());
        price.setText(list.get(position).getShopprice()+"");
        if(list.get(position).getShoptype() == 1) {
//            shop.setImageResource(R.drawable.teshushangpin_big);
//            shop.setVisibility(View.GONE);
            DynamicDrawableSpan drawableSpan =
                    new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BOTTOM) {
                        @Override
                        public Drawable getDrawable() {
                            Drawable d = context.getResources().getDrawable(R.drawable.teshushangpin_small);
                            d.setBounds(0, 0, 85, 32);
                            return d;
                        }
                    };
//            ImageSpan imageSpan = new ImageSpan(context,R.drawable.teshushangpin_small,DynamicDrawableSpan.ALIGN_BOTTOM);
            SpannableString spannableString = new SpannableString("a");
            spannableString.setSpan(drawableSpan,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(spannableString);
            title.append(list.get(position).getShoptitle());
        } else {
//            shop.setVisibility(View.GONE);
            title.setText(list.get(position).getShoptitle());
        }
        displayPic(list.get(position).getShoppictureurl(),iv);
        return view;
    }
    private void displayPic(String url,ImageView view){
        PicTask picTask = new PicTask(view);
        picTask.execute(url);
    }
    class PicTask extends AsyncTask<String,Integer,Bitmap> {
        ImageView view;
        public PicTask(ImageView view){
            this.view = view;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap pic = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream inputStream = con.getInputStream();
                pic = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pic;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            view.setImageBitmap(bitmap);
        }
    }
}
