package com.example.de_2bstudentapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import Controller.controller;
import ResponseModel.responseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class logInActivity extends AppCompatActivity {

    GoogleSignInClient gsc;
    GoogleSignInOptions gso;

    String email,name,enrollmentNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        Toolbar toolbar1 = findViewById(R.id.toolbar0);
        setSupportActionBar(toolbar1);
        setTitle("Sign with Google");
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    void signIn() {
        Intent signInIntent =  gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 ){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);

                validate();
            } catch (ApiException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validate() {
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        if (acc != null ) {
            email = acc.getEmail();
            name = acc.getDisplayName();
            if (!email.endsWith("scet.ac.in")) {
                Toast.makeText(this, "Sign In with College Email-ID only.", Toast.LENGTH_LONG).show();
                gsc.signOut();
            } else {
                String[] temp = email.split("\\.", 5);
                enrollmentNo = temp[0];
                addStudent();

            }
        }
    }

    private void addStudent() {
        Call<responseModel> call = controller.getInstance().getAPI().addUser(enrollmentNo);
        call.enqueue(new Callback<responseModel>() {
            @Override
            public void onResponse(Call<responseModel> call, Response<responseModel> response) {
                responseModel obj = response.body();
                String output = obj.getMessage();
                int temp = Integer.parseInt(output);

                if (temp > 100){
                    Toast.makeText(logInActivity.this, "Student created", Toast.LENGTH_SHORT).show();

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(logInActivity.this);
                    sp.edit().putString("Enrollment_Number", enrollmentNo).apply();

                    startActivity(new Intent(logInActivity.this,scanActivity.class));
                    finish();
                }
                if (temp == 100){
//                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(logInActivity.this);
//                    sp.edit().putString("Enrollment_Number", enrollmentNo).apply();

                    Toast.makeText(logInActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

//                    startActivity(new Intent(logInActivity.this,scanActivity.class));
//                    finish();
                }
            }

            @Override
            public void onFailure(Call<responseModel> call, Throwable t) {
                Log.d("try1",t.getMessage());
                Toast.makeText(logInActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}