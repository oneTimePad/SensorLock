package com.example.lie.sensorlock;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class LockService extends Service {

    GeoLock gLock;
    Camera mfCamera;
    CameraLock cLock;
    MicLock mLock;

    SensorTracker mSensor;
    MediaRecorder mRecorder;
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    public void onCreate(){
        gLock = new GeoLock();
        cLock = new CameraLock();
        mLock = new MicLock();

    }
    @Override
    public void onStart(Intent intent, int startid){

        gLock.lock();
        cLock.lock();
        mLock.lock();
    }

    @Override
    public void onDestroy(){
        gLock.unlock();
        cLock.unlock();
        mLock.unlock();

    }

    private class MicLock extends HandlerThread {
        Handler mHandler;


        MicLock(){
            super("MicLock");
            start();
            mHandler = new Handler(getLooper());



        }

        public void lock(){
            mHandler.post(new Runnable() {
                public void run() {

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

                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;


                }
            });
        }


    }


    private class GeoLock extends HandlerThread{

        Handler mHandler;


        GeoLock(){
            super("CameraLock");
            start();
            mHandler = new Handler(getLooper());



        }

        public void lock(){
            mHandler.post(new Runnable() {
                public void run() {

                    mSensor = new SensorTracker(getApplicationContext());
                    mSensor.startSensors();

                }
            });

        }
        public void unlock(){
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    mSensor.stopSensors();
                    mSensor = null;

                }
            });
        }
    }


    private class CameraLock extends HandlerThread{
        Handler mHandler;

        CameraLock(){
            super("CameraLock");
            start();
            mHandler = new Handler(getLooper());
        }

        public void lock(){
            mHandler.post(new Runnable(){
                public void run(){

                    mfCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                }
            });

        }
        public void unlock(){
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    mfCamera.release();
                    mfCamera = null;
                }
            });
        }
    }
}


