package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

public class MainThread extends Thread {
    private boolean _run = false;
    public String selection = "";
    Level m_level;
    MainActivity m_mainActivity;

    public MainThread(Level level, MainActivity mainActivity) {
        m_level = level;
        m_level.exit = true;
        m_mainActivity = mainActivity;
    }

    public void setRunning(boolean run) {
        _run = run;
    }

    public void run() {
        while(m_level.exit) {
            while (_run) {
                m_mainActivity.updatePhysics();
                m_mainActivity.draw();
            }
            if (selection.equals("restart")) {
                m_mainActivity.restartLevel();
                selection = "none";
            } else if (selection.equals("next")) {
                m_mainActivity.nextLevel();
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