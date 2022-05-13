package com.test.looperhandlerandhandlerthread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

// Handler Thread Example 2
public class MyHandler extends Handler {

    String TAG = "MyHandler";
    MainActivity activity;

    public MyHandler(@NonNull Looper looper) {
        super(looper);
    }

    public void setActivity(MainActivity activity){
        this.activity = activity;
    }

    public MyHandler(){
        super();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Bundle data = msg.getData();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String name = data.getString("name");
                int age = data.getInt("age");
                Log.d(TAG, "name:" + name);
                Log.d(TAG, "age:" + age);

                if(activity != null){
                    activity.updateNameAndAge(name, age);
                }
            }
        });
    }
}
