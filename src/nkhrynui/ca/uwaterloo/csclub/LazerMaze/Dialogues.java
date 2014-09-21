package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class Dialogues {

    static MainActivity mainActivity = null;

    Dialogues(MainActivity mainActivityIn){
        mainActivity = mainActivityIn;
    }

    void skipLevelDialog() {
        new AlertDialog.Builder(mainActivity)
                .setMessage("Are you sure you want to skip this level for " + mainActivity.level.skipCost + " points?")
                .setTitle("Skip Level")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.level.skip();
                        mainActivity._thread.setRunning(false);
                        mainActivity._thread.selection = "next";
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    void restartDialog() {
        new AlertDialog.Builder(mainActivity)
                .setMessage("Are you sure you want to restart at level 1?")
                .setTitle("New Game")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.level.reset();
                        mainActivity._thread.setRunning(false);
                        mainActivity._thread.selection = "next";
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    void newGameDialog() {
        new AlertDialog.Builder(mainActivity)
                .setMessage("Drag your finger from the green launcher in the direction you want to shoot " +
                        "the lazer then release!")
                .setTitle("How To Play")
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    void endGameDialog(int score) {
        int oldScore = mainActivity.sharedPrefs.getInt("highScore", 0);
        if (oldScore < score) {
            oldScore = score;
            SharedPreferences.Editor e = mainActivity.sharedPrefs.edit();
            e.putInt("highScore", score);
            e.commit();
        }
        new AlertDialog.Builder(mainActivity)
                .setMessage("The score reached 0\nYou made it to level: " + score + "\nYour highscore is: " + oldScore)
                .setTitle("Game Over")
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }
}
