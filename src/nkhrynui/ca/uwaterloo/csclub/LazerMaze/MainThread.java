package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.util.Log;

public class MainThread extends Thread {
    private boolean _run = false;
    public String selection = "";
    private Level m_level;
    private MainActivity m_mainActivity;
    private Physics m_physics;

    public MainThread(Level level, MainActivity mainActivity, Physics physics) {
        m_level = level;
        m_level.exit = true;
        m_mainActivity = mainActivity;
        m_physics = physics;
    }

    public void setRunning(boolean run) {
        _run = run;
    }

    public void run() {
        while(m_level.exit) {
            while (_run) {
                m_physics.update();
                m_mainActivity.draw();
            }
            if (selection.equals("restart")) {
                m_mainActivity.restartLevel();
                selection = "none";
            } else if (selection.equals("next")) {
                m_mainActivity.nextLevel();
                selection = "none";
            }
            Thread.yield();
        }
    }
}