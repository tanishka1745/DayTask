package com.example.daynotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.daynotes.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread= new Thread()
        {
            public void run()
            {
                try{
                    sleep(3000);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}