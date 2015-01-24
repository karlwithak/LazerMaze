package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class Dialogues {
    Context    m_context;
    Level      m_level;
    MainThread m_thread;
    Powerups   m_powerup;
    SharedPreferences m_sharedPrefs;
    public Dialogues(Context context, Panel panel, SharedPreferences sharedPrefs) {
        m_context = context;
        m_level = panel.m_level;
        m_thread = panel.m_mainThread;
        m_powerup = panel.m_powerup;
        m_sharedPrefs= sharedPrefs;
    }
    public void skipLevelDialog() {
        new AlertDialog.Builder(m_context)
                .setMessage("Are you sure you want to skip this level for " + m_level.skipCost + " points?")
                .setTitle("Skip Level")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        m_level.skip();
                        m_thread.setRunning(false);
                        m_thread.selection = "next";
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void restartDialog() {
        new AlertDialog.Builder(m_context)
                .setMessage("Are you sure you want to restart at level 1?")
                .setTitle("New Game")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        m_level.reset();
                        m_powerup = Powerups.NONE;
                        m_thread.setRunning(false);
                        m_thread.selection = "next";
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    public void newGameDialog() {
        new AlertDialog.Builder(m_context)
                .setMessage("Drag your finger from the green launcher in the direction you want to shoot " +
                        "the lazer then release!")
                .setTitle("How To Play")
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    public void endGameDialog() {
        int score = m_level.score;
        int oldScore = m_sharedPrefs.getInt("highScore", 0);
        if (oldScore < score) {
            oldScore = score;
            SharedPreferences.Editor e = m_sharedPrefs.edit();
            e.putInt("highScore", score);
            e.commit();
        }
        new AlertDialog.Builder(m_context)
                .setMessage("The score reached 0\nYou made it to level: " + score + "\nYour highscore is: " + oldScore)
                .setTitle("Game Over")
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }
}
