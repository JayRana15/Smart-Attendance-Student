package com.example.de_2bstudentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView logoTV,logoTV2;
    Animation anime;

    private static int SPLASH_SCREEN = 1250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.light_blue));

        logoTV = findViewById(R.id.logoTV);
        logoTV2 = findViewById(R.id.logoTV2);

        anime = AnimationUtils.loadAnimation(this,R.anim.anime);

        logoTV.setAnimation(anime);
        logoTV2.setAnimation(anime);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String temp = sp.getString("Enrollment_Number","0");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!temp.equals("0")) {
                    startActivity(new Intent(MainActivity.this,scanActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this,logInActivity.class));
                    finish();
                }
            }
        },SPLASH_SCREEN);


    }
}