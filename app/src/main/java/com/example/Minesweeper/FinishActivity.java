package com.example.Minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class FinishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Intent intent = getIntent();
        if(intent != null){

            setContentView(R.layout.activity_finish);
            TextView tv = (TextView)findViewById(R.id.textView);

            int gameResult = intent.getIntExtra(MainActivity.ACTIVITY_RESULT_KEY, 0);
            switch (gameResult){
                case 1:
                    tv.setText("You lost");
                    break;
                case 2:
                    tv.setText("You Won");
                    break;
                default:
                    break;
            }
        }
    }
}
