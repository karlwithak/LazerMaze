package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class Dialogues {
    static void skipLevelDialog() {
        new AlertDialog.Builder(MainActivity.context)
                .setMessage("Are you sure you want to skip this level for " + MainActivity.level.skipCost + " points?")
                .setTitle("Skip Level")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.level.skip();
                        MainActivity._thread.setRunning(false);
                        MainActivity._thread.selection = "next";
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    static void restartDialog() {
        new AlertDialog.Builder(MainActivity.context)
                .setMessage("Are you sure you want to restart at level 1?")
                .setTitle("New Game")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.level.reset();
                        MainActivity.powerup = Powerups.NONE;
                        MainActivity._thread.setRunning(false);
                        MainActivity._thread.selection = "next";
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    static void newGameDialog() {
        new AlertDialog.Builder(MainActivity.context)
                .setMessage("Drag your finger from the green launcher in the direction you want to shoot " +
                        "the lazer then release!")
                .setTitle("How To Play")
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    static void endGameDialog(int score) {
        int oldScore = MainActivity.sharedPrefs.getInt("highScore", 0);
        if (oldScore < score) {
            oldScore = score;
            SharedPreferences.Editor e = MainActivity.sharedPrefs.edit();
            e.putInt("highScore", score);
            e.commit();
        }
        new AlertDialog.Builder(MainActivity.context)
                .setMessage("The score reached 0\nYou made it to level: " + score + "\nYour highscore is: " + oldScore)
                .setTitle("Game Over")
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }
}
