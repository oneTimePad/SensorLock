package com.example.lie.sensorlock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {




    Activity act;
    Context ctx;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ctx = this;
        act = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    protected void onStart(){
        super.onStart();
        isMyServiceRunning(LockService.class);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.togglebutton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    startService(new Intent(MainActivity.this, LockService.class));

                    // The toggle is enabled
                } else {
                    stopService(new Intent(MainActivity.this, LockService.class));
                    // The toggle is disabled
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.togglebutton);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                toggle.setChecked(true);
                return true;

            }
        }
        return false;
    }

}

