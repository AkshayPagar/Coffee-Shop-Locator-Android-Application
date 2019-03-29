package com.example.student.coffeeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView textView;
    Typeface myfont;
    private static int SPLASH_TIMEOUT=3000;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.text1);
        myfont= Typeface.createFromAsset(getAssets(), "m.ttf");
        textView.setTypeface(myfont);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent maps= new Intent(MainActivity.this, Nav_bar.class);
                startActivity(maps);
                finish();
            }
        },SPLASH_TIMEOUT);
    }
}
