package com.example.de_2bstudentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class scanActivity extends AppCompatActivity {

    TextView enrollmentNoTV,nameTV;
    Button scanBtn;
    ImageView dpIV;

    GoogleSignInClient gsc;
    GoogleSignInOptions gso;

    String email_id,enroll_num,name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        enrollmentNoTV = findViewById(R.id.enrollmentTV);
        nameTV = findViewById(R.id.name);
        scanBtn = findViewById(R.id.scanButton);
        dpIV = findViewById(R.id.dpIV);

        Toolbar toolbar1 = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        setTitle("Your attendances");
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        Uri photo = null;
        if (acc != null) {
            email_id = acc.getEmail();
            String[] temp = email_id.split("\\.", 5);
            enroll_num = temp[0];
            photo = acc.getPhotoUrl();
            name = acc.getDisplayName();
        }

        Glide.with(this).load(photo).apply(RequestOptions.circleCropTransform()).into(dpIV);
        nameTV.setText(name);
        enrollmentNoTV.setText(enroll_num);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(scanActivity.this, scannerView.class));
            }
        });



    }
}