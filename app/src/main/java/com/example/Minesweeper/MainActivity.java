package com.example.Minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.Minesweeper.R;

public class MainActivity extends AppCompatActivity {

    // Easy
    public static final int BOMB_NUMBER_EASY = 10;
    public static final int WIDTH_EASY = 8;
    public static final int HEIGHT_EASY = 8;

    // Medium
    public static final int BOMB_NUMBER_MEDIUM = 40;
    public static final int WIDTH_MEDIUM = 16;
    public static final int HEIGHT_MEDIUM = 16;

    // Hard
    public static final int BOMB_NUMBER_HARD = 99;
    public static final int WIDTH_HARD = 30;
    public static final int HEIGHT_HARD = 16;

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

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty diff = difficulty.Easy;
                startGameActivity(1);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty diff = difficulty.Medium;
                startGameActivity(2);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty diff = difficulty.Hard;
                startGameActivity(1);
            }
        });
    }

    private void startGameActivity(final int diff) {

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart started");
        // my reaction to getting to onStart State
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume started");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop started");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory started");
    }
}