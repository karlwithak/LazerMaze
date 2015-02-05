package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import org.jetbrains.annotations.NotNull;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.PowerupManager.Powerup;


public class MainPanel extends SurfaceView implements SurfaceHolder.Callback {
    public Laser            m_laser;
    public Special          m_launcher, m_launcher2, m_target, m_target2;
    public Buttons          m_buttons;
    public Resources        m_resources;
    public ColorHandler     m_colorHandler;
    public MainThread       m_mt;
    public MainActivity     m_ma;
    public TouchHandler     m_touchHandler;
    public static MainPanel m_mp;

    public MainPanel(Context context) {
        super(context);
    }

    public void setup(MainThread mainThread, MainActivity mainActivity) {
        getHolder().addCallback(this);
        setFocusable(true);
        m_mt = mainThread;
        m_colorHandler = new ColorHandler();
        m_ma = mainActivity;
        m_mp = this;
    }

    public Canvas getCanvas() {
        return getHolder().lockCanvas();
    }

    public void postCanvas(Canvas canvas) {
        if (canvas != null) {
            getHolder().unlockCanvasAndPost(canvas);
        }
    }


    @Override
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        return m_touchHandler.handle(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("Surface", "Surface created");
        m_resources = getResources();
        if (m_ma.m_sharedPrefs.getInt("highScore", 0) == 0) {
            m_ma.m_dialogues.newGameDialog();
            SharedPreferences.Editor e = m_ma.m_sharedPrefs.edit();
            e.putInt("highScore", 1);
            e.apply();
        }

        K.init(this);
        m_laser = new Laser();
        m_buttons = new Buttons(getResources(), m_ma.m_powerupMan);
        m_buttons.updatePowerup();
        m_launcher = Special.LAUNCHER;
        m_target = Special.TARGET;
        m_launcher2 = Special.LAUNCHER2;
        m_target2 = Special.TARGET2;
        m_ma.m_grid.setup();
        m_ma.m_physics.setup(this, m_mt);
        m_touchHandler = new TouchHandler(m_ma, m_mt, this);
        m_ma.nextLevel();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }
    
    public void nextLevel() {
        m_buttons.updatePowerup();
        m_target.update(false, m_ma.m_grid, m_ma.m_powerupMan.get() == Powerup.BIG_TARGET);
        m_launcher.update(true, m_ma.m_grid, false);
        if (m_ma.m_powerupMan.get() == Powerup.TWO_TARGETS) {
            m_target2.update2(false, m_ma.m_grid);
        }
        if (m_ma.m_powerupMan.get() == Powerup.TWO_LAUNCHERS) {
            m_launcher2.update2(true, m_ma.m_grid);
        }
    }
    
    public void draw() {
        Canvas canvas = getCanvas();
        m_ma.m_level.draw(canvas);
        m_laser.draw(canvas);
        if (m_ma.m_level.num != 0) {
            m_target.draw(canvas);
            m_launcher.draw(canvas);
        }
        if (m_ma.m_powerupMan.get() == Powerup.TWO_TARGETS) m_target2.draw(canvas);
        if (m_ma.m_powerupMan.get() == Powerup.TWO_LAUNCHERS) m_launcher2.draw(canvas);
        m_ma.m_grid.draw(canvas);
        m_buttons.draw(canvas, m_ma.m_level);
        postCanvas(canvas);
    }

    public void updateColors() {
        m_colorHandler.update(m_ma.m_sharedPrefs, m_ma.m_level, m_ma.m_grid, m_laser);
    }

    public void restartLevel() {
        m_laser.nextLevel();
        draw();
    }

    public void gridShrink() {
        final int SCREEN_HEIGHT = K.SCREEN_HEIGHT;
        final int LINE_SPACING = K.LINE_SPACING;
        m_touchHandler.inAnimation = true;
        for (int i = 0; i < SCREEN_HEIGHT / 30; i++) {
            Canvas c = getCanvas();
            try {
                Thread.sleep(10);
                m_ma.m_level.draw(c);
                for (Line line: m_ma.m_grid.lines) {
                    line.shrink(LINE_SPACING);
                }
                m_ma.m_grid.draw(c);
                m_buttons.draw(c, m_ma.m_level);
            } catch (InterruptedException ignored) {}
            postCanvas(c);
        }
        m_touchHandler.inAnimation = false;
    }

    public void gridExpand() {
        final int SCREEN_HEIGHT = K.SCREEN_HEIGHT;
        final int LINE_SPACING = K.LINE_SPACING;
        m_touchHandler.inAnimation = true;
        for (int i = 0; i < SCREEN_HEIGHT / 30; i++) {
            Canvas c = getCanvas();
            try {
                Thread.sleep(10);
                m_ma.m_level.draw(c);
                for (Line line: m_ma.m_grid.lines) {
                    line.expand(LINE_SPACING);
                }
                m_ma.m_grid.expandDraw(c);
                m_buttons.draw(c, m_ma.m_level);
            } catch (InterruptedException ignored) {}
            postCanvas(c);
        }
        m_touchHandler.inAnimation = false;
    }
}
