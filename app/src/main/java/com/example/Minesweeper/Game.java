package com.example.Minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

        private Tile[][] MinesweeperGrid;

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

            int[][] GeneratedGrid = Generator.generate(this.getBombNumber(), this.getWidth(), this.getHeight());
            print(GeneratedGrid, this.getWidth(), this.getHeight());
            setGrid(context, GeneratedGrid, this.getWidth(), this.getHeight());
        }

        private void setGrid(final Context context, final int[][] grid, int WIDTH, int HEIGHT) {
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (MinesweeperGrid[x][y] == null) {
                        MinesweeperGrid[x][y] = new Tile(context, x, y);
                    }
                    MinesweeperGrid[x][y].setValue(grid[x][y]);
                    MinesweeperGrid[x][y].invalidate();
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

                new Thread() {
                    @Override
                    public void run() {

                        // doLongOperation();


                        try {

                            // code runs in a thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent =new Intent(getBaseContext(), FinishActivity.class);
                                    intent.putExtra(ACTIVITY_RESULT_KEY, 1);
                                    startActivity(intent);
                                }
                            });
                        } catch (final Exception ex) {
                            Log.i("---","Exception in thread");
                        }
                    }
                }.start();
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


            new Thread() {
                @Override
                public void run() {

                    // doLongOperation();


                    try {

                        // code runs in a thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent =new Intent(getBaseContext(), FinishActivity.class);
                                intent.putExtra(ACTIVITY_RESULT_KEY, 2);
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
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Game onCreate!!!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if(intent != null){
            int diffChoosen = intent.getIntExtra(MainActivity.ACTIVITY_RESULT_KEY, 0);
            Game game = Game.getInstance();
            switch (diffChoosen)
            {
                case 1:

                    game.setWidth(WIDTH_EASY);
                    game.setHeight(HEIGHT_EASY);
                    game.setBombNumber(BOMB_NUMBER_EASY);
                    game.MinesweeperGrid = new Tile[WIDTH_EASY][HEIGHT_EASY];
                    game.createGrid(this);
                    break;
                case 2:

                    game.setWidth(WIDTH_MEDIUM);
                    game.setHeight(HEIGHT_MEDIUM);
                    game.setBombNumber(BOMB_NUMBER_MEDIUM);
                    game.MinesweeperGrid = new Tile[WIDTH_MEDIUM][HEIGHT_MEDIUM];
                    game.createGrid(this);
                    break;
                case 3:

                    game.setWidth(WIDTH_HARD);
                    game.setHeight(HEIGHT_HARD);
                    game.setBombNumber(BOMB_NUMBER_HARD);
                    game.MinesweeperGrid = new Tile[WIDTH_HARD][HEIGHT_HARD];
                    game.createGrid(this);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + diffChoosen);
            }

        }
    }
}
