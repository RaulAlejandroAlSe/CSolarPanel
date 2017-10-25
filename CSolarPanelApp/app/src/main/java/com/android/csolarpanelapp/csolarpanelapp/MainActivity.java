package com.android.csolarpanelapp.csolarpanelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void act_manufacter(View view){
        Intent intent = new Intent(this, ManufacterData.class);
        startActivity(intent);
    }
}
