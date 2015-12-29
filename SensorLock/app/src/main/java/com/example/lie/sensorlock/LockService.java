package com.example.lie.sensorlock;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.locks.Lock;

public class LockService extends Service  {

    GeoLock gLock;
    Camera mfCamera;
    CameraLock cLock;
    MicLock mLock;

    SensorTracker mSensor;
    MediaRecorder mRecorder;
    Intent lockIntent;



    public class LockBinder extends Binder {
        public LockService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LockService.this;
        }
    }



    @Override
    public IBinder onBind(Intent intent){
        if(cLock!=null && mLock!= null && gLock!=null) {
            intent.putExtra("CAM", cLock.isLocked());
            intent.putExtra("MIC",mLock.isLocked());
            intent.putExtra("GEO",gLock.isLocked());


        }

        return null;
    }



    public void onCreate(){


        gLock = new GeoLock();
        cLock = new CameraLock();
        mLock = new MicLock();



    }



    @Override
    public void onStart(Intent intent, int startid){


        boolean cam = (boolean) intent.getExtras().get("Camera");
        boolean mic = (boolean) intent.getExtras().get("Mic");
        boolean geo = (boolean) intent.getExtras().get("Geo");


        if(cam){
            if(!cLock.isLocked()) {
                cLock.lock();
            }

        }
        else{
            if(cLock.isLocked()){
                cLock.unlock();
            }
        }

        if(mic){
            if(!mLock.isLocked()) {
                mLock.lock();
            }
        }
        else{
            if(mLock.isLocked()){
                mLock.unlock();
            }
        }

        if(geo){
            if(!gLock.isLocked()) {
                gLock.lock();
            }
        }
        else{
            if(gLock.isLocked()){
                gLock.unlock();
            }
        }



    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        gLock.unlock();
        cLock.unlock();
        mLock.unlock();

    }

    protected class MicLock extends HandlerThread {
        Handler mHandler;
        boolean Locked= false;

        MicLock(){
            super("MicLock");
            start();
            mHandler = new Handler(getLooper());



        }

        public synchronized boolean isLocked(){

            return Locked;
        }

        public void lock(){
            mHandler.post(new Runnable() {
                public void run() {

                    Locked=true;

                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    mRecorder.setOutputFile("/sdcard/" + "MicLock output.3gp");


                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e("On Mic Start", "prepare() failed");
                    }

                    mRecorder.start();

                }
            });

        }
        public void unlock(){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Locked=false;

                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;


                }
            });
        }


    }


    protected class GeoLock extends HandlerThread{

        Handler mHandler;
        boolean Locked=false;

        GeoLock(){
            super("CameraLock");
            start();
            mHandler = new Handler(getLooper());



        }

        public synchronized boolean isLocked(){

            return Locked;
        }

        public void lock(){
            mHandler.post(new Runnable() {
                public void run() {
                    Locked=true;
                    mSensor = new SensorTracker(getApplicationContext());
                    mSensor.startSensors();

                }
            });

        }
        public void unlock(){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Locked=false;
                    mSensor.stopSensors();
                    mSensor = null;

                }
            });
        }
    }


    protected class CameraLock extends HandlerThread{
        Handler mHandler;
        boolean Locked = false;

        CameraLock(){
            super("CameraLock");
            start();
            mHandler = new Handler(getLooper());
        }

        public synchronized boolean isLocked(){

            return Locked;
        }

        public void lock(){
            mHandler.post(new Runnable(){
                public void run(){
                        Locked=true;

                        mfCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);


                }
            });

        }
        public void unlock(){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Locked=false;
                    mfCamera.release();
                    mfCamera = null;
                }
            });
        }
    }
}


