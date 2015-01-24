package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

public class MainThread extends Thread {
    private boolean _run = false;
    public String selection = "";

    public MainThread() {
        MainActivity.g_level.exit = true;
    }

    public void setRunning(boolean run) {
        _run = run;
    }

    @SuppressLint("WrongCall") @Override
    public void run() {
        while(MainActivity.g_level.exit) {
            while (_run) {
                MainActivity.updatePhysics();
                MainActivity.draw();
            }
            if (selection.equals("restart")) {
                MainActivity.restartLevel();
                selection = "none";
            } else if (selection.equals("next")) {
                MainActivity.nextLevel();
                selection = "none";
            }
            try {
                Thread.sleep(50);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}