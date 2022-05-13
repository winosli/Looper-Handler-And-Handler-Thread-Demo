package com.test.looperhandlerandhandlerthread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/*
You would use HandlerThread in case that you want to perform background tasks one at a time and you want that those tasks will run at the order of execution.

        For example if you want to make several network background operations one by one.

        the HandlerThread has it's own looper and handlers could be created and post it, (so it would not block the main thread).

        * If whatever you are doing is "heavy" you should be doing it in a Thread.
*/
public class MainActivity extends AppCompatActivity {

    String myKey = "myKey";
    TextView tvExample1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        handlerThreadExample1();

        // Here we are using "MyHandler" class (Example 2)
        HandlerThread t = new HandlerThread("handler_thread");
        t.start();

        MyHandler myHandler = new MyHandler(t.getLooper());
        myHandler.setActivity(MainActivity.this);
        Message msg = myHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt("age", 30);
        bundle.putString("name", "John");
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private void initializeViews() {
        tvExample1 = findViewById(R.id.tvHandlerThreadExample1);
    }

    Handler handler;
    int counter = 0;

    private void handlerThreadExample1() {
        // Define our thread (HanderThread class inherits from the Thread class) - HandlerThread is good to avoid "ThreadLeak" exceptions because it has "TimeOut" feature
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();

        // We can create many handlers for each HandlerThread but for each HandlerThread (or any other thread) we have 1 looper only
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String msg1 = (String) msg.obj; //Extract the string from the Message here
                // If we want to communicate with the UI thread we can use "handler.post()" but with the Looper of the Main Thread! (Looper.getMainLooper()), option B can be "RunOnUIThread"
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (msg1 != null) {
                            tvExample1.setText(msg1 + "");
                        }
                    }
                });
            }
        };

        // Send messages: there are many different ways to send messages, here is some example:
        // We are using the timer to send message each 1 second
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Get date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault());
                String currentDateAndTime = sdf.format(new Date());
                String message = "Date And Time = " + currentDateAndTime;

                Message msg = Message.obtain(); // Creates an new Message instance
                msg.obj = message; // Put the string into Message, into "obj" field.
                msg.setTarget(handler); // Set the Handler
                msg.sendToTarget(); //Send the message

                counter++;
                if (counter >= 20) {
                    timer.cancel();
                }
            }
        }, 0, 1000);

        // There are more ways to send messages. for example:
        /*
        Message msg = new Message()
        msg.what = xxx;
        msg.arg1  = xxx;
        msg.arg2  = xxx;
        handler.sendMessage(msg);
         */
    }

    public void updateNameAndAge(String name, int age) {
        TextView tvHandlerThreadExample2 = findViewById(R.id.tvHandlerThreadExample2);
        tvHandlerThreadExample2.setText("Name: " + name + ", age: " + age);
    }
}