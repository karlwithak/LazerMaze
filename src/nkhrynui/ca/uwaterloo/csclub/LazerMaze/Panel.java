package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.Utils.inBetween;
import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.PowerupManager.Powerup;


public class Panel extends SurfaceView implements SurfaceHolder.Callback {
    static int graphicCount = 0;
    static boolean upOnButtons = false;
    static boolean inAnimation;
    private static Level m_level;
    private static PowerupManager m_powerupMan;
    private static Dialogues m_dialogues;
    private static MainThread m_mainThread;
    private static Laser m_laser;
    private static Special m_launcher, m_launcher2, m_target, m_target2;
    private static Grid m_grid;
    private static Buttons m_buttons;
    private static SharedPreferences m_sharedPrefs;
    private static MainActivity m_mainActivity;
    private static Physics m_physics;
    static Resources m_resources;
    private static ColorHandler m_colorHandler;
    public Panel(Context context) {
        super(context);
    }

    public void setup(Level level, Dialogues dialogues, SharedPreferences sharedPrefs,
                 MainActivity mainActivity, Grid grid, PowerupManager powerupMan, MainThread mainThread, Physics physics) {
        m_level = level;
        m_dialogues = dialogues;
        m_sharedPrefs = sharedPrefs;
        m_mainActivity = mainActivity;
        getHolder().addCallback(this);
        setFocusable(true);
        m_grid = grid;
        m_powerupMan = powerupMan;
        m_mainThread = mainThread;
        m_physics = physics;
        m_colorHandler = new ColorHandler();
    }

    public Canvas getCanvas() {
        return getHolder().lockCanvas();
    }

    public void postCanvas(Canvas canvas) {
        if (canvas != null) {
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    float startX, startY, endX, endY, changeX, changeY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (m_powerupMan.m_waitForChoice) { //selecting upgrade
            upOnButtons = false;
            if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() <= K.SCREEN_WIDTH / 2) {
                m_powerupMan.m_selection = 1;
            } else if (event.getAction() == MotionEvent.ACTION_DOWN
                    && event.getX() > K.SCREEN_WIDTH / 2) {
                m_powerupMan.m_selection = 2;
            }
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            m_mainThread.setRunning(false);
            m_mainThread.selection = "restart";
        }
        //buttons
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getY() >=
                K.SCREEN_HEIGHT - K.NAV_HEIGHT && graphicCount == 0 && !inAnimation) {
            upOnButtons = true;
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP && event.getY() >=
                K.SCREEN_HEIGHT - K.NAV_HEIGHT && graphicCount == 0 && !inAnimation && upOnButtons) {
            upOnButtons = false;
            if (event.getX() < K.SCREEN_WIDTH / 3) {
                m_level.exit = false;
                m_mainActivity.settings();
            } else if (event.getX() > K.SCREEN_WIDTH * 2 / 3) {
                m_dialogues.restartDialog();

            } else if (m_level.score > m_level.skipCost) {
                m_dialogues.skipLevelDialog();
            }
        }
        //aiming launch
        if (event.getAction() == MotionEvent.ACTION_DOWN && graphicCount == 0 && !inAnimation) {
            upOnButtons = false;
            if (m_launcher.bigPointTest(event.getX(), event.getY())) {
                if (m_mainThread.isAlive()) {
                    m_mainThread.setRunning(false);
                    m_mainThread.selection = "restart";
                    m_level.restart = true;
                }
                startX = m_launcher.x;
                startY = m_launcher.y;
                m_launcher.active = true;
                if (m_powerupMan.get() == Powerup.TWO_LAUNCHERS) m_launcher2.active = false;
                graphicCount = 1;
            } else if (m_powerupMan.get() == Powerup.TWO_LAUNCHERS
                        && m_launcher2.bigPointTest(event.getX(), event.getY())) {
                if (m_mainThread.isAlive()) {
                    m_mainThread.setRunning(false);
                    m_mainThread.selection = "restart";
                    m_level.restart = true;
                }
                startX = m_launcher2.x;
                startY = m_launcher2.y;
                m_launcher2.active = true;
                m_launcher.active = false;
                graphicCount = 1;
            } else if ((m_powerupMan.get() == Powerup.LAUNCH_FROM_EITHER
                    && m_target.bigPointTest(event.getX(), event.getY()))) {
                if (m_mainThread.isAlive()) {
                    m_mainThread.setRunning(false);
                    m_mainThread.selection = "restart";
                    m_level.restart = true;
                }
                startX = m_target.x;
                startY = m_target.y;
                m_target.x = m_launcher.x;
                m_target.y = m_launcher.y;
                m_launcher.x = (int) startX;
                m_launcher.y = (int) startY;
                Bitmap temp = m_launcher.bitmap;
                m_launcher.bitmap = m_target.bitmap;
                m_target.bitmap = temp;
                graphicCount = 1;
            }
        }
        //aiming launch with aimer
        if (m_powerupMan.get() == Powerup.AIMING_LASER
                && event.getAction() == MotionEvent.ACTION_MOVE
                && graphicCount == 1
                && !inAnimation)
        {
            if (Math.hypot((m_launcher.x - event.getX()), (m_launcher.y - event.getY())) < K.SPECIAL_WIDTH) {
                return true;
            }
            Canvas c = getCanvas();
            m_level.draw(c);
            double distance;
            double min = 99999;
            Line l = null;
            float intersection = 0;
            float intersectionTemp;
            float pointx = m_launcher.x, pointy = m_launcher.y;
            pointx += (event.getX() - m_launcher.x) * 1000;
            pointy += (event.getY() - m_launcher.y) * 1000;
            for (Line line : m_grid.lines) {
                intersectionTemp = line.crossed(m_launcher.x, m_launcher.y, pointx, pointy);
                if (intersectionTemp > 0) {
                    if (line.horizontal) {
                        distance = Math.hypot((m_launcher.x - intersectionTemp),
                                (m_launcher.y - line.starty));
                    } else {
                        distance = Math.hypot((m_launcher.x - line.startx),
                                (m_launcher.y - intersectionTemp));
                    }
                    if (distance < min) {
                        min = distance;
                        intersection = intersectionTemp;
                        l = line;
                    }
                }
            }
            if (l != null && l.horizontal) {
                c.drawLine(m_launcher.x, m_launcher.y, intersection, l.starty, m_laser.paint);
            } else if (l != null) {
                c.drawLine(m_launcher.x, m_launcher.y, l.startx, intersection, m_laser.paint);
            }
            m_laser.draw(c);
            m_grid.draw(c);
            m_buttons.draw(c, m_level);
            m_launcher.draw(c, false);
            m_target.draw(c, false);
            postCanvas(c);
        }
        //launches laser
        if (event.getAction() == MotionEvent.ACTION_UP && graphicCount == 1 && !inAnimation) {
            upOnButtons = false;
            Log.i("powerup", Float.toString(startX) + " on up");
            m_laser.GO.coordinates.setX((int) startX);
            m_laser.GO.coordinates.setY((int) startY);
            endX = event.getX();
            endY = event.getY();
            changeX = endX - startX;
            changeY = endY - startY;
            if (inBetween(-0.01, changeX, 0.01))
                changeX = (float) (Math.signum(changeX) + 0.01);
            if (inBetween(-0.01, changeY, 0.01))
                changeY = (float) (Math.signum(changeY) + 0.01);
            //sets up the line speed and direction according to starting swipe
            if (Math.abs(changeX) < 10 && Math.abs(changeY) < 10) {
                graphicCount = 0;
                return true;
            }
            if (Math.abs(changeX) > (Math.abs(changeY))) {
                m_laser.GO.speed.x = (K.SPEED * Math.signum(changeX));
                m_laser.GO.speed.y = (Math.abs(changeY) / Math.abs(changeX) * K.SPEED * Math.signum(changeY));
            } else {
                m_laser.GO.speed.y = (K.SPEED * Math.signum(changeY));
                m_laser.GO.speed.x = (Math.abs(changeX) / Math.abs(changeY) * K.SPEED * Math.signum(changeX));
            }
            graphicCount = 0;
            if (m_level.restart) {
                if (m_powerupMan.get() != Powerup.TWO_LAUNCHERS || m_launcher.active) m_laser.reset(m_launcher);
                else m_laser.reset(m_launcher2);
                m_mainThread.setRunning(true);
            } else {
                m_mainThread.setRunning(true);
            }
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("Surface", "Surface created");
        m_resources = getResources();
        if (m_sharedPrefs.getInt("highScore", 0) == 0) {
            m_dialogues.newGameDialog();
            SharedPreferences.Editor e = m_sharedPrefs.edit();
            e.putInt("highScore", 1);
            e.commit();
        }

        K.init(this);
        m_laser = new Laser();
        m_buttons = new Buttons(getResources(), m_powerupMan);
        m_buttons.updatePowerup();
        m_launcher = Special.LAUNCHER;
        m_target = Special.TARGET;
        m_launcher2 = Special.LAUNCHER2;
        m_target2 = Special.TARGET2;
        m_grid.setup();
        m_physics.setup(m_laser, m_target, m_target2, m_mainThread);
        m_mainActivity.nextLevel();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }
    
    public void nextLevel() {
        m_buttons.updatePowerup();
        m_target.update(false, m_grid);
        m_launcher.update(true, m_grid);
        if (m_powerupMan.get() == Powerup.TWO_TARGETS) {
            m_target2.update2(false, m_grid);
        }
        if (m_powerupMan.get() == Powerup.TWO_LAUNCHERS) {
            m_launcher2.update2(true, m_grid);
        }
    }
    
    public void draw() {
        Canvas canvas = getCanvas();
        m_level.draw(canvas);
        m_laser.draw(canvas);
        if (m_powerupMan.get() == Powerup.BIG_TARGETS) {
            m_target.draw(canvas, true);
        } else if (m_level.num != 0) {
            m_target.draw(canvas, false);
        }
        if (m_powerupMan.get() == Powerup.TWO_TARGETS) m_target2.draw(canvas, false);
        m_launcher.draw(canvas, false);
        if (m_powerupMan.get() == Powerup.TWO_LAUNCHERS) m_launcher2.draw(canvas, false);
        m_grid.draw(canvas);
        m_buttons.draw(canvas, m_level);
        postCanvas(canvas);
    }

    public void updateColors() {
        m_colorHandler.update(m_sharedPrefs, m_level, m_grid, m_laser);
    }

    public void restartLevel() {
        m_laser.nextLevel();
        draw();
    }

    public void gridShrink() {
        final int SCREEN_HEIGHT = K.SCREEN_HEIGHT;
        final int LINE_SPACING = K.LINE_SPACING;
        inAnimation = true;
        for (int i = 0; i < SCREEN_HEIGHT / 30; i++) {
            Canvas c = getCanvas();
            try {
                Thread.sleep(10);
                m_level.draw(c);
                for (Line line: m_grid.lines) {
                    line.shrink(LINE_SPACING);
                }
                m_grid.draw(c);
                m_buttons.draw(c, m_level);
            } catch (InterruptedException ignored) {}
            postCanvas(c);
        }
        inAnimation = false;
    }
    public void gridExpand() {
        final int SCREEN_HEIGHT = K.SCREEN_HEIGHT;
        final int LINE_SPACING = K.LINE_SPACING;
        inAnimation = true;
        for (int i = 0; i < SCREEN_HEIGHT / 30; i++) {
            Canvas c = getCanvas();
            try {
                Thread.sleep(10);
                m_level.draw(c);
                for (Line line: m_grid.lines) {
                    line.expand(LINE_SPACING);
                }
                m_grid.expandDraw(c);
                m_buttons.draw(c, m_level);
            } catch (InterruptedException ignored) {}
            postCanvas(c);
        }
        inAnimation = false;
    }
}
