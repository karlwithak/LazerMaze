package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

public class MainThread extends Thread {
    private boolean      _run = false;
    public  String       selection = "";
    private MainActivity m_ma;

    public MainThread(MainActivity mainActivity) {
        m_ma = mainActivity;
    }

    public void setRunning(boolean run) {
        _run = run;
    }

    public void run() {
        while(m_ma.m_level.exit) {
            while (_run) {
                m_ma.m_physics.update();
                m_ma.draw();
            }
            if (selection.equals("restart")) {
                m_ma.restartLevel();
                selection = "none";
            } else if (selection.equals("next")) {
                m_ma.nextLevel();
                selection = "none";
            }
            Thread.yield();
        }
    }
}