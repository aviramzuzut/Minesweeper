package com.example.Minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.Minesweeper.Logic.RecordsFragment;
import com.example.Minesweeper.Logic.Tile;
import com.example.Minesweeper.R;

public class MainActivity extends AppCompatActivity  implements  SensorServiceListener {

    //for services

    SensorsService.SensorServiceBinder mBinder;
    boolean isBound = false;

    //needed to be implemented
    public void alarmStateChanged(ALARM_STATE state) {
        Log.d("ACTIVITY", "STATE: " + state);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("Service Connection", "bound to service");
            mBinder = (SensorsService.SensorServiceBinder) service;
            mBinder.registerListener(MainActivity.this);
            Log.d("Service Connection", "registered as listener");
            isBound = true;
            mBinder.startSensors();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            isBound = false;
        }
    };



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("touch", "touch event recieved");
        Log.e("touch", event.getRawX() + " " + event.getRawY());




        Log.e("touch", "event type:" + event);

        return super.onTouchEvent(event);

    }

    //till here saggies code
    //orientation from stackoverflow
    public void lockDeviceRotation(boolean value) {
        if (value) {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        }
        Log.d(TAG, "orientation lock is on!");

    }



    //before changes - delete till here
    int savedDiff;

    // Easy
    public static final int BOMB_NUMBER_EASY = 10;
    public static final int WIDTH_EASY = 8;
    public static final int HEIGHT_EASY = 8;

    // Medium
    public static final int BOMB_NUMBER_MEDIUM = 40;
    public static final int WIDTH_MEDIUM = 16;
    public static final int HEIGHT_MEDIUM = 16;

    // Hard
    public static final int BOMB_NUMBER_HARD = 75;
    public static final int WIDTH_HARD = 16;
    public static final int HEIGHT_HARD = 23;

    private static final String TAG = "MainActivity";
    public static final String ACTIVITY_RESULT_KEY = "Activity_result_key";

    public enum difficulty{
        Easy, Medium, Hard;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate started");
        getLastDifficultyChosen();

        if(savedDiff == 1 ){
            findViewById(R.id.button1).setBackgroundColor(Color.GREEN);
        }
        if(savedDiff == 2 ){
            findViewById(R.id.button2).setBackgroundColor(Color.GREEN);
        }
        if(savedDiff == 3 ){
            findViewById(R.id.button3).setBackgroundColor(Color.GREEN);
        }


        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty diff = difficulty.Easy;
                Game game = Game.getInstance();
                game.setWidth(WIDTH_EASY);
                game.setHeight(HEIGHT_EASY);
                game.setBombNumber(BOMB_NUMBER_EASY);
                game.MinesweeperGrid = new Tile[WIDTH_EASY][HEIGHT_EASY];
                startGameActivity(1);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty diff = difficulty.Medium;
                Game game = Game.getInstance();
                game.setHeight(HEIGHT_MEDIUM);
                game.setWidth(WIDTH_MEDIUM);
                game.setBombNumber(BOMB_NUMBER_MEDIUM);
                game.MinesweeperGrid = new Tile[WIDTH_MEDIUM][HEIGHT_MEDIUM];
                startGameActivity(2);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty diff = difficulty.Hard;
                Game game = Game.getInstance();
                game.setWidth(WIDTH_HARD);
                game.setHeight(HEIGHT_HARD);
                game.setBombNumber(BOMB_NUMBER_HARD);
                game.MinesweeperGrid = new Tile[WIDTH_HARD][HEIGHT_HARD];
                // game.createGrid(game.getBaseContext());
                startGameActivity(3);
            }
        });


        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.fragment_records, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

        //for finish activity
        if (getIntent().getBooleanExtra("EXIT", false)) {

            finish();
        }
/*
        //maybe will be deleted - popup message
        Button b1 = (Button)findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBound) {

                    Log.d("button listener", "getting random number...");


                } else {
                    Toast.makeText(MainActivity.this, "Service not bound yet!", Toast.LENGTH_LONG).show();
                }

            }
        });

 */
    }

    private void startGameActivity(final int diff) {
        //save chosen difficulty
        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_mins_difficulty), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_difficulty_key), diff);
        editor.commit();
         //start the game
        Dialog mProgressDialog = ProgressDialog.show(this, "Please wait", "Long operation starts...", true);
        new Thread() {
            @Override
            public void run() {

                // doLongOperation();


                try {

                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent =new Intent(getBaseContext(), Game.class);
                            intent.putExtra(MainActivity.ACTIVITY_RESULT_KEY, diff);
                            startActivity(intent);
                        }
                    });
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread");
                }
            }
        }.start();
        mProgressDialog.dismiss();
    }

    protected int getLastDifficultyChosen() {

        Context context = getBaseContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_mins_difficulty) ,Context.MODE_PRIVATE);
        savedDiff = sharedPref.getInt(getString(R.string.saved_difficulty_key),4);

        return savedDiff;
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart started");
        // my reaction to getting to onStart State
//for services
        Intent intent = new Intent(this, SensorsService.class);
        Log.d("On start", "binding to service...");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart started");
 //i added this
        if(isBound) {
            mBinder.startSensors();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume started");
//for services
        if(isBound) {
            mBinder.startSensors();
           // SensorManager.getOrientation(R.id.button1,0.0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause started");
        if(isBound) {
            mBinder.stopSensors();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop started");
        if (isBound) {
            unbindService(mConnection);
            isBound = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory started");

    }
}
