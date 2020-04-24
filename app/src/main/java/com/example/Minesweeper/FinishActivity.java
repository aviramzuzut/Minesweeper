package com.example.Minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
                    tv.setText("Sorry, You lost!");
                    break;
                case 2:
                    tv.setText("You Won!");
                    break;
                default:
                    break;
            }
        }
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //back to main activity to start a new game
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", false);
                startActivity(intent);                       }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back to main activity to start a new game
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                // android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(1);
                }
        });


    }
}
