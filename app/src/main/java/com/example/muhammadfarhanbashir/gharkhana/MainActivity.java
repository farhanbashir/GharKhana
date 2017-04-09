package com.example.muhammadfarhanbashir.gharkhana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoOrderNow(View view)
    {
        SharedPreference.getInstance().save(this,"guest","true");
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void gotoSignup(View view)
    {
        //Intent intent = new Intent(this, SignupActivity.class);
        //startActivity(intent);
    }

    public void gotoLogin(View view)
    {
        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
    }
}
