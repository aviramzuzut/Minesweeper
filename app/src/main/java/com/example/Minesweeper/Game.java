package com.example.Minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import com.example.Minesweeper.Logic.Generator;
import com.example.Minesweeper.Logic.Tile;
import com.example.Minesweeper.R;

import static com.example.Minesweeper.MainActivity.BOMB_NUMBER_EASY;
import static com.example.Minesweeper.MainActivity.BOMB_NUMBER_HARD;
import static com.example.Minesweeper.MainActivity.BOMB_NUMBER_MEDIUM;
import static com.example.Minesweeper.MainActivity.HEIGHT_EASY;
import static com.example.Minesweeper.MainActivity.HEIGHT_HARD;
import static com.example.Minesweeper.MainActivity.HEIGHT_MEDIUM;
import static com.example.Minesweeper.MainActivity.WIDTH_EASY;
import static com.example.Minesweeper.MainActivity.WIDTH_HARD;
import static com.example.Minesweeper.MainActivity.WIDTH_MEDIUM;

public class Game extends AppCompatActivity {

        public static final String ACTIVITY_RESULT_KEY = "Activity_result_key";
        private static final String TAG = "GameActivity";
        public static Game instance;
        public int bombNumber;
        public int width;
        private TextView txtMineCount;
        private TextView txtTimer;


        public int getBombNumber() {
            return bombNumber;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return height;
        }


        public void setHeight(int height) {
            this.height = height;
        }

        public int height;
        private Context context;

        public Tile[][] MinesweeperGrid;

        public static Game getInstance() {
            if (instance == null) {
                instance = new Game();
            }
            return instance;
        }

        public Game() {
        }

        public void setBombNumber(int bombNumber){
            this.bombNumber = bombNumber;
        }

        public void setWidth(int width){
            this.width = width;
        }


        public void createGrid(Context context) {
            Log.e("GameEngine", "createGrid is working");
            this.context = context;

            // create the grid and store it

            int[][] GeneratedGrid = Generator.generate(Game.getInstance().getBombNumber(), Game.getInstance().getWidth(), Game.getInstance().getHeight());
            print(GeneratedGrid, Game.getInstance().getWidth(), Game.getInstance().getHeight());
            setGrid(context, GeneratedGrid, Game.getInstance().getWidth(), Game.getInstance().getHeight());
        }

        private void setGrid(final Context context, final int[][] grid, int WIDTH, int HEIGHT) {
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (Game.getInstance().MinesweeperGrid[x][y] == null) {
                        Game.getInstance().MinesweeperGrid[x][y] = new Tile(context, x, y);
                    }
                    Game.getInstance().MinesweeperGrid[x][y].setValue(grid[x][y]);
                    Game.getInstance().MinesweeperGrid[x][y].invalidate();
                }
            }
        }

        public Tile getCellAt(int position) {
            int x = position % this.getWidth();
            int y = position / this.getWidth();

            return MinesweeperGrid[x][y];
        }

        public Tile getCellAt(int x, int y) {
            return MinesweeperGrid[x][y];
        }

        public void click(int x, int y) {
            if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight() && !getCellAt(x, y).isClicked()) {
                getCellAt(x, y).setClicked();

                if (getCellAt(x, y).getValue() == 0) {
                    for (int xt = -1; xt <= 1; xt++) {
                        for (int yt = -1; yt <= 1; yt++) {
                            if (xt != yt) {
                                click(x + xt, y + yt);
                            }
                        }
                    }
                }

                if (getCellAt(x, y).isBomb()) {
                    onGameLost();
                }
            }

            checkEnd();
        }

        private boolean checkEnd() {
            int bombNotFound = this.getBombNumber();
            int notRevealed = this.getWidth() * this.getHeight();
            for (int x = 0; x < this.getWidth(); x++) {
                for (int y = 0; y < this.getHeight(); y++) {
                    if (getCellAt(x, y).isRevealed() || getCellAt(x, y).isFlagged()) {
                        notRevealed--;
                    }

                    if (getCellAt(x, y).isFlagged() && getCellAt(x, y).isBomb()) {
                        bombNotFound--;
                    }
                }
            }

            if (bombNotFound == 0 && notRevealed == 0) {
                Toast.makeText(context, "Game won", Toast.LENGTH_SHORT).show();

                Intent intent =new Intent(this, FinishActivity.class);
                intent.putExtra(ACTIVITY_RESULT_KEY, 2);
                startActivity(intent);
            }
            return false;
        }

        public void flag(int x, int y) {
            boolean isFlagged = getCellAt(x, y).isFlagged();
            getCellAt(x, y).setFlagged(!isFlagged);
            getCellAt(x, y).invalidate();
        }


        public static void print(final int[][] grid, final int width, final int height) {
            for (int x = 0; x < width; x++) {
                String printedText = "| ";
                for (int y = 0; y < height; y++) {
                    printedText += String.valueOf(grid[x][y]).replace("-1", "B") + " | ";
                }
                Log.e("", printedText);
            }
        }

        private void onGameLost() {
            // handle lost game
            Toast.makeText(context, "Game lost", Toast.LENGTH_SHORT).show();

            for (int x = 0; x < this.getWidth(); x++) {
                for (int y = 0; y < this.getHeight(); y++) {
                    getCellAt(x, y).setRevealed();
                }
            }


            Intent intent =new Intent(Game.getInstance().context, FinishActivity.class);
            intent.putExtra(ACTIVITY_RESULT_KEY, 1);
            Game.getInstance().context.startActivity(intent);

        }

        int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Game onCreate!!!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //creating Timer
        //txtMineCount = (TextView) findViewById(R.id.MineCount);
        txtTimer = (TextView) findViewById(R.id.Timer);

        // set font style for timer and mine count to LCD style
        Typeface lcdFont = Typeface.createFromAsset(getAssets(),
                "fonts/lcd2mono.ttf");
        //txtMineCount.setTypeface(lcdFont);
        txtTimer.setTypeface(lcdFont);

        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                          public void run() {
                              //Called each time when 1000 milliseconds (1 second) (the period parameter)
                              //We must use this function in order to change the text view text
                              runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                    //  TextView tv = (TextView) findViewById(R.id.Timer);
                                      txtTimer.setText(String.valueOf(time));
                                      time += 1;
                                  }
                              });
                          }
                      },
                // how long before to start calling the TimerTask (in milliseconds)
                0,
                  //Set the amount of time between each execution (in milliseconds)
                1000);


        Intent intent = getIntent();
        if(intent != null){
            int diffChoosen = intent.getIntExtra(MainActivity.ACTIVITY_RESULT_KEY, 0);
            Game game = Game.getInstance();
            game.createGrid(this);

        }
    }
}
