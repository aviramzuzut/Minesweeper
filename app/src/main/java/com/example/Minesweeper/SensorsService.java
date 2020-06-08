package com.example.Minesweeper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

interface SensorServiceListener {

    enum ALARM_STATE {
        ON,OFF
    }

    void alarmStateChanged(ALARM_STATE state);
}

public class SensorsService extends Service implements SensorEventListener {

    private final static String TAG = "SENSOR_SERVICE";

    private final double THRESHOLD = 1;

    private SensorServiceListener.ALARM_STATE currentAlarmState = SensorServiceListener.ALARM_STATE.OFF;

    // Binder given to clients
    private final IBinder mBinder = new SensorServiceBinder();

    SensorServiceListener mListener;

    SensorManager mSensorManager;
    Sensor mAccel;
    Sensor mOrientation;
    SensorEvent mFirstEvent;

    public class SensorServiceBinder extends Binder {



        void registerListener(SensorServiceListener listener) {
            Log.d("Binder", "registering...");
            mListener = listener;

        }

        void startSensors() {
            mSensorManager.registerListener(SensorsService.this,mAccel,SensorManager.SENSOR_DELAY_NORMAL);
          //i added
            mSensorManager.registerListener(SensorsService.this,mOrientation,SensorManager.SENSOR_DELAY_NORMAL);
        }

        void stopSensors() {
            mSensorManager.unregisterListener(SensorsService.this);
        }

    }




    public SensorsService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if(mAccel != null) {
            Log.d("Sensors ouput" , "Accelerometer avilable");
        } else {
            Log.d("Sensors ouput" , "Accelerometer NOT Availible");
        }

        if(mOrientation != null) {
            Log.d("Sensors ouput" , "Orientation avilable");
        } else {
            Log.d("Sensors ouput" , "Orientation NOT Availible");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // Sensor event listener callbacks


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(mFirstEvent == null) {
            mFirstEvent = event;
        } else {

            double absDiffX = Math.abs(mFirstEvent.values[0] - event.values[0]);
            double absDiffY = Math.abs(mFirstEvent.values[1] - event.values[1]);
            double absDiffZ = Math.abs(mFirstEvent.values[2] - event.values[2]);


            Log.d("DIFFS", "" + absDiffX + " " + absDiffY + " " + absDiffZ);

            SensorServiceListener.ALARM_STATE previousState = currentAlarmState;
            if (absDiffX > THRESHOLD || absDiffY > THRESHOLD || absDiffZ > THRESHOLD ) {
                this.currentAlarmState = SensorServiceListener.ALARM_STATE.ON;
            } else {
                this.currentAlarmState = SensorServiceListener.ALARM_STATE.OFF;
            }

            if(previousState != currentAlarmState) {
                mListener.alarmStateChanged(currentAlarmState);
            }

        }

        Log.d(TAG,event.values[0] + " " + event.values[1] + " " + event.values[2]);

    }
}
