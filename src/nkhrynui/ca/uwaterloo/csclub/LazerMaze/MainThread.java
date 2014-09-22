package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private final SurfaceHolder _surfaceHolder;
    private MainActivity _mainActivity;
    private boolean _run = false;
    public Canvas c;
    public String selection = "";

    public MainThread(SurfaceHolder surfaceHolder, MainActivity mainActivity) {
        _surfaceHolder = surfaceHolder;
        _mainActivity = mainActivity;
        c = null;
        MainActivity.level.exit = true;
    }

    public void setRunning(boolean run) {
        _run = run;
    }

    public SurfaceHolder getSurfaceHolder() {
        return _surfaceHolder;

    }

    @SuppressLint("WrongCall") @Override
    public void run() {
        while(MainActivity.level.exit) {
            while (_run) {
                c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        _mainActivity.updatePhysics(c);
                        _mainActivity.draw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
            if (selection.equals("restart")) {
                _mainActivity.restartLevel(_surfaceHolder);
                selection = "none";
            } else if (selection.equals("next")) {
                _mainActivity.nextLevel(_surfaceHolder);
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