package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

public class MainThread extends Thread {
    private MainActivity _mainActivity;
    private MainActivity.Panel _panel;
    private boolean _run = false;
    private Canvas c;
    public String selection = "";

    public MainThread(MainActivity mainActivity, MainActivity.Panel panel) {
        MainActivity.g_level.exit = true;
        _panel = panel;
        _mainActivity = mainActivity;
    }

    public void setRunning(boolean run) {
        _run = run;
    }

    @SuppressLint("WrongCall") @Override
    public void run() {
        while(MainActivity.g_level.exit) {
            while (_run) {
                c = null;
                c = _panel.getCanvas();
                synchronized (_panel.getHolder()) {
                    _mainActivity.updatePhysics(c);
                    _mainActivity.draw(c);
                }
                _panel.postCanvas(c);
            }
            if (selection.equals("restart")) {
                _mainActivity.restartLevel();
                selection = "none";
            } else if (selection.equals("next")) {
                _mainActivity.nextLevel();
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