package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import nkhrynui.ca.uwaterloo.csclub.LazerMaze.PowerupManager.Powerup;

public class Dialogues {
    Context    m_context;
    Level      m_level;
    MainThread m_thread;
    PowerupManager m_powerupMan;
    SharedPreferences m_sharedPrefs;
    public Dialogues(Context context, Level level, PowerupManager powerupMan,
                     SharedPreferences sharedPrefs) {
        m_context = context;
        m_level = level;
        m_powerupMan = powerupMan;
        m_sharedPrefs= sharedPrefs;
    }

    public void setup(MainThread mainThread) {
        m_thread = mainThread;
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

    public void restartDialog(final int level) {
        new AlertDialog.Builder(m_context)
                .setMessage("Are you sure you want to restart at level 1?")
                .setTitle("New Game")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateHighscore(level);
                        m_level.reset();
                        m_powerupMan.set(Powerup.NONE);
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

    public void endGameDialog(int level) {
        int best = updateHighscore(level);
        new AlertDialog.Builder(m_context)
                .setMessage("The score reached 0\nYou made it to level: " + level + "\nYour highscore is: " + best)
                .setTitle("Game Over")
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
        m_powerupMan.set(Powerup.NONE);
    }

    private int updateHighscore(int level) {
        int oldScore = m_sharedPrefs.getInt("highScore", 0);
            if (oldScore < level) {
                oldScore = level;
                SharedPreferences.Editor e = m_sharedPrefs.edit();
                e.putInt("highScore", level);
                e.apply();
        }
        return oldScore;
    }
}
