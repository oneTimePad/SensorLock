package com.example.lie.sensorlock;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;


/**
 * Created by lie on 12/28/15.
 */
public class Settings extends AppCompatActivity {


    public static Activity thisActivity;



    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        thisActivity= this;

        setContentView(R.layout.activity_settings);

        Toolbar settingsToolbar = (Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);






    }

    public void onResume(){
        super.onResume();


        CheckBox camera = (CheckBox) findViewById(R.id.camera);
        CheckBox mic = (CheckBox) findViewById(R.id.mic);
        CheckBox geo = (CheckBox) findViewById(R.id.geo);
        Intent intent = getIntent();




        camera.setChecked((boolean)intent.getExtras().get("cam"));
        mic.setChecked((boolean) intent.getExtras().get("mic"));
        geo.setChecked((boolean) intent.getExtras().get("geo"));


    }





    public void onSave(View view){
        CheckBox camera = (CheckBox) findViewById(R.id.camera);
        CheckBox mic = (CheckBox) findViewById(R.id.mic);
        CheckBox geo = (CheckBox) findViewById(R.id.geo);

        Intent toMain =  new Intent(Settings.this,MainActivity.class);



        toMain.putExtra("Camera",camera.isChecked());
        toMain.putExtra("Mic", mic.isChecked());
        toMain.putExtra("Geo", geo.isChecked());
        startActivity(toMain);
    }






}
