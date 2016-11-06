package com.uncgcapstone.android.seniorcapstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uncgcapstone.android.seniorcapstone.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preference_key), MODE_PRIVATE);
        if((mSharedPreferences.contains("uid"))){
            if(mSharedPreferences.getString("uid", "").equals("")){
                Intent i = new Intent(StartActivity.this, LogInActivity.class);
                startActivity(i);
            }
            else{
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("query", "0");
                editor.putString("imageurl", "");
                editor.commit();
                Intent i = new Intent(StartActivity.this, MainActivity.class);
                startActivity(i);
            }
        }
        else{
            Intent i = new Intent(StartActivity.this, LogInActivity.class);
            startActivity(i);
        }
    }
}
