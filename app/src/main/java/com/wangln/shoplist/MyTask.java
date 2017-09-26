package com.wangln.shoplist;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/9/25 0025.
 */

public class MyTask extends AsyncTask<String,Integer,String> {
    HttpCallback callback;
    public MyTask(HttpCallback callback){
        this.callback = callback;
    }
    @Override
    protected String doInBackground(String... params) {
        String result="";
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if(params[1] != null) {
                con.setDoOutput(true);
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(params[1].getBytes());
            }
            InputStream inputStream = con.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String content = "";
            while ((content = bufferedReader.readLine()) != null) {
                result += content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("wang",s);
        callback.onSuccess(s);
    }
}
