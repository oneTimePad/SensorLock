package com.example.lie.sensorlock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class MainActivity extends AppCompatActivity {





    LockService mService = null;
    boolean mBound = false;

    boolean cam = true;
    boolean mic = true;
    boolean geo = true;


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LockService.LockBinder binder = (LockService.LockBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(tbar);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.togglebutton);

        if(isMyServiceRunning(LockService.class)){
            toggle.setChecked(true);
            Log.e("Set","set conected");

            Intent findService = new Intent(MainActivity.this,LockService.class);
            getApplicationContext().bindService(findService,mConnection,0);
            cam = mService.cLock.isLocked();
            mic = mService.mLock.isLocked();
            geo= mService.gLock.isLocked();


        }
        else{

            toggle.setChecked(false);
        }



    }


    public void onResume(){
        super.onResume();
        ToggleButton toggle = (ToggleButton) findViewById(R.id.togglebutton);

        if(isMyServiceRunning(LockService.class)){
            toggle.setChecked(true);
        }
        else{
            toggle.setChecked(false);
        }
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Intent toLockService = new Intent(MainActivity.this, LockService.class);
                    toLockService.putExtra("Camera", cam);
                    toLockService.putExtra("Mic", mic);
                    toLockService.putExtra("Geo", geo);

                    startService(toLockService);
                    Toast.makeText(MainActivity.this, "Your download has resumed.", Toast.LENGTH_LONG).show();


                    // The toggle is enabled
                } else {
                    stopService(new Intent(MainActivity.this, LockService.class));
                    // The toggle is disabled
                }
            }
        });

    }





    protected  void onNewIntent(Intent intent){

        super.onNewIntent(intent);
        setIntent(intent);
        setContentView(R.layout.activity_main);
        Toolbar tbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(tbar);


        Intent toSettings = getIntent();
        try {
            boolean lcam = (boolean) toSettings.getExtras().get("Camera");
            boolean lmic = (boolean) toSettings.getExtras().get("Mic");
            boolean lgeo = (boolean) toSettings.getExtras().get("Geo");

            cam = lcam;
            mic = lmic;
            geo = lgeo;

            if(isMyServiceRunning(LockService.class)){
                stopService(new Intent(MainActivity.this, LockService.class));
                Log.e("STOPPEd","STOP");
            }
            else{
                return;
            }

            Intent toLockService = new Intent(MainActivity.this,LockService.class);
            toLockService.putExtra("Camera",cam);
            toLockService.putExtra("Mic",mic);
            toLockService.putExtra("Geo", geo);
            startService(toLockService);
            Log.e("STARTED","STARTED");

        }
        catch(Exception e){
            Log.e("Excep","3");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI..
                // .
                setContentView(R.layout.activity_settings);
                Intent toSettings = new Intent(MainActivity.this, Settings.class);
                toSettings.putExtra("cam",cam);
                toSettings.putExtra("mic",mic);
                toSettings.putExtra("geo",geo);
                startActivity(toSettings);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }






    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.togglebutton);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;

            }
        }
        return false;
    }

    public void onStop(){
        super.onStop();
        if(mBound) {
          unbindService(mConnection);

        }

    }
}


