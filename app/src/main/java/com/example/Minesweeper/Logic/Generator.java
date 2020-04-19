package com.example.Minesweeper.Logic;

import android.content.Intent;
import android.util.Log;

import com.example.Minesweeper.Game;
import com.example.Minesweeper.MainActivity;

import java.util.Random;

public class Generator {

    public static int[][] generate( int bombNumber , final int width , final int height){
        // Random for generating numbers
        Random r = new Random();

        final int[][][] grid = {new int[width][height]};
        for( int x = 0 ; x< width ;x++ ){
            grid[0][x] = new int[height];
        }

        while( bombNumber > 0 ){
            int x = r.nextInt(width);
            int y = r.nextInt(height);

            // -1 is the bomb
            if( grid[0][x][y] != -1 ){
                grid[0][x][y] = -1;
                bombNumber--;
            }
        }

        new Thread() {
            @Override
            public void run() {

                // doLongOperation();
                grid[0] = calculateNeighbours(grid[0],width,height);


                try {
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread");
                }
            }
        }.start();


        return grid[0];
    }

    private static int[][] calculateNeighbours( int[][] grid , final int width , final int height){
        for( int x = 0 ; x < width ; x++){
            for( int y = 0 ; y < height ; y++){
                grid[x][y] = getNeighbourNumber(grid,x,y,width,height);
            }
        }

        return grid;
    }

    private static int getNeighbourNumber( final int grid[][] , final int x , final int y , final int width , final int height){
        if( grid[x][y] == -1 ){
            return -1;
        }

        int count = 0;

        if( isMineAt(grid,x - 1 ,y + 1,width,height)) count++; // top-left
        if( isMineAt(grid,x     ,y + 1,width,height)) count++; // top
        if( isMineAt(grid,x + 1 ,y + 1,width,height)) count++; // top-right
        if( isMineAt(grid,x - 1 ,y    ,width,height)) count++; // left
        if( isMineAt(grid,x + 1 ,y    ,width,height)) count++; // right
        if( isMineAt(grid,x - 1 ,y - 1,width,height)) count++; // bottom-left
        if( isMineAt(grid,x     ,y - 1,width,height)) count++; // bottom
        if( isMineAt(grid,x + 1 ,y - 1,width,height)) count++; // bottom-right

        return count;
    }

    private static boolean isMineAt( final int [][] grid, final int x , final int y , final int width , final int height){
        if( x >= 0 && y >= 0 && x < width && y < height ){
            if( grid[x][y] == -1 ){
                return true;
            }
        }
        return false;
    }

}
