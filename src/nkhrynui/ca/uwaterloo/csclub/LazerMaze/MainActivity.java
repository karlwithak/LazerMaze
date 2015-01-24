package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.inBetween;

public class MainActivity extends Activity {

    // GLOBAL VARIABLES, set in surfaceCreated
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static int NAV_HEIGHT;
    public static int LINE_SPACING;
    public static int SPECIAL_WIDTH; //special refers to target and launcher
    public static int SPEED;

    public static Resources g_resources;

    private static SharedPreferences g_sharedPrefs;
    private static Context g_context;
    public static MainThread g_thread;
    private static Buttons g_buttons;
    private static Special g_target;
    private static Special g_launcher;
    private static Special g_target2;
    private static Special g_launcher2;
    private static Grid g_grid;
    private static Level g_level;
    private static Vibrator g_v = null;
    private static Laser g_laser;
    private static boolean inAnimation = false;
    private static boolean lockListenerOkay = true;
    private static ColorHandler g_colorHandler = new ColorHandler();
    private static Powerups g_powerup;
    private static Panel g_panel;
    private static Dialogues g_dialogues;
    private static MainActivity g_mainActivity;

    /****** CONSTANTS END ************************************************************************/

    /****** PANEL STARTS *************************************************************************/

    public class Panel extends SurfaceView implements SurfaceHolder.Callback {
        public int graphicCount = 0;
        boolean upOnButtons = false;

        public Panel(Context context1) {
            super(context1);
            g_context = context1;
            getHolder().addCallback(this);
            g_thread = new MainThread(g_level);
            setFocusable(true);
            g_thread.start();
            g_thread.setRunning(false);
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
            if (g_powerup.waitForChoice) { //selecting upgrade
                upOnButtons = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() <= SCREEN_WIDTH / 2) {
                    g_powerup.selection = 1;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN
                        && event.getX() > SCREEN_WIDTH / 2) {
                    g_powerup.selection = 2;
                }
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                g_thread.setRunning(false);
                g_thread.selection = "restart";
            }
            //buttons
            if (event.getAction() == MotionEvent.ACTION_DOWN && event.getY() >=
                    SCREEN_HEIGHT - NAV_HEIGHT && graphicCount == 0 && !inAnimation) {
                upOnButtons = true;
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP && event.getY() >=
                    SCREEN_HEIGHT - NAV_HEIGHT && graphicCount == 0 && !inAnimation && upOnButtons) {
                upOnButtons = false;
                if (event.getX() < SCREEN_WIDTH / 3) {
                    g_level.exit = false;
                    settings();
                } else if (event.getX() > SCREEN_WIDTH * 2 / 3) {
                    g_dialogues.restartDialog();

                } else if (g_level.score > g_level.skipCost) {
                    g_dialogues.skipLevelDialog();
                }
            }
            //aiming launch
            if (event.getAction() == MotionEvent.ACTION_DOWN && graphicCount == 0 && !inAnimation) {
                upOnButtons = false;
                if (g_launcher.bigPointTest(event.getX(), event.getY())) {
                    if (g_thread.isAlive()) {
                        g_thread.setRunning(false);
                        g_thread.selection = "restart";
                        g_level.restart = true;
                    }
                    startX = g_launcher.x;
                    startY = g_launcher.y;
                    g_launcher.active = true;
                    if (g_launcher2 != null) g_launcher2.active = false;
                    graphicCount = 1;
                } else if (g_launcher2 != null && g_launcher2.bigPointTest(event.getX(), event.getY())) {
                    if (g_thread.isAlive()) {
                        g_thread.setRunning(false);
                        g_thread.selection = "restart";
                        g_level.restart = true;
                    }
                    startX = g_launcher2.x;
                    startY = g_launcher2.y;
                    g_launcher2.active = true;
                    g_launcher.active = false;
                    graphicCount = 1;
                } else if ((g_powerup == Powerups.LAUNCH_FROM_EITHER
                        && g_target.bigPointTest(event.getX(), event.getY()))) {
                    if (g_thread.isAlive()) {
                        g_thread.setRunning(false);
                        g_thread.selection = "restart";
                        g_level.restart = true;
                    }
                    startX = g_target.x;
                    startY = g_target.y;
                    g_target.x = g_launcher.x;
                    g_target.y = g_launcher.y;
                    g_launcher.x = (int) startX;
                    g_launcher.y = (int) startY;
                    Bitmap temp = g_launcher.bitmap;
                    g_launcher.bitmap = g_target.bitmap;
                    g_target.bitmap = temp;
                    graphicCount = 1;
                }
            }
            //aiming launch with aimer
            if (g_powerup == Powerups.AIMING_LASER
                    && event.getAction() == MotionEvent.ACTION_MOVE
                    && graphicCount == 1
                    && !inAnimation)
            {
                if (Math.hypot((g_launcher.x - event.getX()), (g_launcher.y - event.getY())) < SPECIAL_WIDTH) {
                    return true;
                }
                Canvas c = g_panel.getCanvas();
                g_level.draw(c);
                double distance;
                double min = 99999;
                Line l = null;
                float intersection = 0;
                float intersectionTemp;
                float pointx = g_launcher.x, pointy = g_launcher.y;
                pointx += (event.getX() - g_launcher.x) * 1000;
                pointy += (event.getY() - g_launcher.y) * 1000;
                for (Line line : g_grid.lines) {
                    intersectionTemp = line.crossed(g_launcher.x, g_launcher.y, pointx, pointy);
                    if (intersectionTemp > 0) {
                        if (line.horizontal) {
                            distance = Math.hypot((g_launcher.x - intersectionTemp),
                                    (g_launcher.y - line.starty));
                        } else {
                            distance = Math.hypot((g_launcher.x - line.startx),
                                    (g_launcher.y - intersectionTemp));
                        }
                        if (distance < min) {
                            min = distance;
                            intersection = intersectionTemp;
                            l = line;
                        }
                    }
                }
                if (l != null && l.horizontal) {
                    c.drawLine(g_launcher.x, g_launcher.y, intersection, l.starty, g_laser.paint);
                } else if (l != null) {
                    c.drawLine(g_launcher.x, g_launcher.y, l.startx, intersection, g_laser.paint);
                }
                g_laser.draw(c);
                g_grid.draw(c);
                g_buttons.draw(c, g_level, g_powerup);
                g_launcher.draw(c, false);
                g_target.draw(c, false);
                g_panel.postCanvas(c);
            }
            //launches laser
            if (event.getAction() == MotionEvent.ACTION_UP && graphicCount == 1 && !inAnimation) {
                upOnButtons = false;
                Log.i("powerup", Float.toString(startX) + " on up");
                g_laser.GO.coordinates.setX((int) startX);
                g_laser.GO.coordinates.setY((int) startY);
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
                    g_laser.GO.speed.x = (SPEED * Math.signum(changeX));
                    g_laser.GO.speed.y = (Math.abs(changeY) / Math.abs(changeX) * SPEED * Math.signum(changeY));
                } else {
                    g_laser.GO.speed.y = (SPEED * Math.signum(changeY));
                    g_laser.GO.speed.x = (Math.abs(changeX) / Math.abs(changeY) * SPEED * Math.signum(changeX));
                }
                graphicCount = 0;
                if (g_level.restart) {
                    if (g_launcher2 == null || g_launcher.active) g_laser.reset(g_launcher);
                    else g_laser.reset(g_launcher2);
                    g_thread.setRunning(true);
                } else {
                    g_thread.setRunning(true);
                }
            }
            return true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            g_resources = getResources();
            if (g_sharedPrefs.getInt("highScore", 0) == 0) {
                g_dialogues.newGameDialog();
                SharedPreferences.Editor e = g_sharedPrefs.edit();
                e.putInt("highScore", 1);
                e.commit();
            }

            setUpDimensions();
            if (!g_level.recover) {
                g_powerup = Powerups.NONE;
                g_laser = new Laser();
                g_buttons = new Buttons(getResources());
                g_grid = new Grid();
                g_launcher = Special.LAUNCHER;
                g_target = Special.TARGET;
                g_launcher2 = Special.LAUNCHER2;
                g_target2 = Special.TARGET2;
            }
            nextLevel();
        }

        public void setUpDimensions() {
            SCREEN_WIDTH = getWidth();
            SCREEN_HEIGHT = getHeight();
            LINE_SPACING = SCREEN_HEIGHT / 39;
            NAV_HEIGHT = LINE_SPACING * 3;
            SPECIAL_WIDTH = SCREEN_HEIGHT / 20;
            SPEED = SCREEN_WIDTH / 50;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
        }
    }

    /***** PANEL - END**************************************************************/

    /***** PHYSICS - START**********************************************************/

    public static void updatePhysics() {
        GraphicObject.Coordinates coord;
        GraphicObject.Speed speed;
        coord = g_laser.GO.coordinates;
        speed = g_laser.GO.speed;
        if (g_target.smallPointTest(coord.x, coord.y, g_powerup)
                || (g_powerup == Powerups.TWO_TARGETS && g_target2.smallPointTest(coord.x, coord.y, g_powerup))) {
            g_level.num++;
            g_level.score+=100;
            g_thread.setRunning(false);
            g_thread.selection = "next";
            g_level.restart = false;
            if (g_level.num == 1) {
                g_level.score = 100;
            }
        }

        coord.setX(coord.x + speed.x);
        coord.setY(coord.y + speed.y);
        Line line;
        boolean ignoring = false;
        boolean doubleHit = true;
        for (int i = 0; i < g_grid.getLines().size(); i++) {
            line = g_grid.getLines().get(i);
            if ((coord.lastx != -1  && coord.lasty != -1)
                    && line.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0)
            {
                if (g_powerup == Powerups.THROUGH_FIRST_LINE && g_laser.pts.size() == 4) {
                    ignoring = true;
                    g_laser.bounce();
                    continue;
                }
                if (g_level.score < 1) {
                    g_mainActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            g_dialogues.endGameDialog();
                        }
                    });
                    g_level.reset();
                    g_powerup = Powerups.NONE;
                    g_thread.setRunning(false);
                    g_thread.selection = "next";
                    break;
                }
                if (g_powerup == Powerups.WRAP_AROUND_ENDS
                        && g_grid.getLines().indexOf(line) <= 1) {
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    g_laser.bounce();
                    if (line.starty < NAV_HEIGHT + 2) {
                        g_laser.starty = SCREEN_HEIGHT - NAV_HEIGHT - 2;
                        coord.setY(SCREEN_HEIGHT - NAV_HEIGHT - 2);
                        coord.lasty = (SCREEN_HEIGHT - NAV_HEIGHT - 1);
                    } else if (line.starty > SCREEN_HEIGHT - NAV_HEIGHT - 2) {
                        g_laser.starty = NAV_HEIGHT + 2;
                        coord.setY(NAV_HEIGHT + 2);
                        coord.lasty = NAV_HEIGHT + 1;
                    }
                    if (coord.x <= speed.x) {
                        coord.x = 2;
                        speed.toggleXDirection();
                        doubleHit = false;
                        soundAndVib();
                    } else if (coord.x >= SCREEN_WIDTH - speed.x) {
                        coord.x = SCREEN_WIDTH - 2;
                        speed.toggleXDirection();
                        doubleHit = false;
                        soundAndVib();
                    }
                    g_laser.startx = coord.x;
                    coord.lastx = coord.x;
                    g_laser.bounce();
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    continue;
                }

                if (g_powerup == Powerups.WRAP_AROUND_SIDES
                        && (g_grid.getLines().indexOf(line) == 2
                                || g_grid.getLines().indexOf(line) == 3)) {
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    g_laser.bounce();
                    if (line.startx < 2) {
                        g_laser.startx = SCREEN_WIDTH - 2;
                        coord.setX(SCREEN_WIDTH - 1);
                        coord.lastx = SCREEN_WIDTH - 2;
                    } else if (line.startx > SCREEN_WIDTH - 2) {
                        g_laser.startx = 2;
                        coord.setX(2);
                        coord.lastx = 1;
                    }

                    if (coord.y <= NAV_HEIGHT - speed.y) {
                        coord.y = NAV_HEIGHT + 2;
                        speed.toggleYDirection();
                        doubleHit = false;
                        soundAndVib();
                    } else if ( coord.y >= SCREEN_HEIGHT - NAV_HEIGHT - speed.y) {
                        coord.y =  SCREEN_HEIGHT - NAV_HEIGHT - 2;
                        speed.toggleYDirection();
                        doubleHit = false;
                        soundAndVib();
                    }
                    g_laser.starty = coord.y;
                    coord.lasty = coord.x;
                    g_laser.bounce();
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    continue;
                }

                soundAndVib();
                coord.x =(coord.x - speed.x);
                coord.y =(coord.y - speed.y);
                float change;

                if (line.horizontal) {
                    speed.toggleYDirection();
                    change = Math.abs((coord.y - line.starty) / speed.y);
                    coord.y = (line.starty);
                    coord.setX(coord.x+ (change * speed.x));
                    for (Line line2 : g_grid.getLines()) {
                        if (!ignoring
                                && line2 != line
                                && line2.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0
                                && doubleHit)
                        {
                            Log.i("graphics", "double hit");
                            speed.toggleXDirection();
                            coord.x = (line2.endx);
                            coord.y =(line.endy);
                            g_laser.bounce();
                            coord.x =(coord.x + speed.x);
                            coord.y =(coord.y + speed.y);
                            if (g_level.score >0) g_level.score--;
                        }
                    }
                } else {
                    speed.toggleXDirection();
                    change = Math.abs((coord.x - line.startx) / speed.x);
                    coord.x = (line.startx);
                    coord.y = (coord.y+ (change * speed.y));
                    for (Line line2 : g_grid.getLines()) {
                        if (!ignoring && line2 != line
                                && line2.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0
                                && doubleHit)
                        {
                            Log.i("graphics", "double hit");
                            speed.toggleYDirection();
                            coord.x = (line.endx);
                            coord.y = (line2.endy);
                            g_laser.bounce();
                            coord.x = (coord.x + speed.x);
                            coord.y = (coord.y + speed.y);
                            if (g_level.score >0) g_level.score--;

                        }
                    }
                }
                g_laser.bounce();
                break;
            }
        }
    }
     /****************************************** PHYSICS - END*****************************/

     /****************************************** DRAWING - START***************************/
    public static void draw() {
        if (g_resources == null) g_resources = g_context.getResources();
        Canvas canvas = g_panel.getCanvas();
        g_level.draw(canvas);
        g_laser.draw(canvas);
        if (g_powerup == Powerups.BIG_TARGETS) {
            g_target.draw(canvas, true);
        } else if (g_level.num != 0) {
            g_target.draw(canvas, false);
        }
        if (g_powerup == Powerups.TWO_TARGETS) g_target2.draw(canvas, false);
        g_launcher.draw(canvas, false);
        if (g_powerup == Powerups.TWO_LAUNCHERS) g_launcher2.draw(canvas, false);
        g_grid.draw(canvas);
        g_buttons.draw(canvas, g_level, g_powerup);
        g_panel.postCanvas(canvas);
    }

    /****************************************** DRAWING - END**********************************/

    public static void nextLevel() {
        if (!g_level.recover) {
            if (g_grid.lines.size() > 1) gridShrink();
            if (g_level.num % 3 == 0 && g_level.num != 0) g_powerup = g_powerup.pickPowerup(g_panel);
            g_buttons.update(g_powerup);
            g_colorHandler.update(g_sharedPrefs, g_level, g_grid, g_laser);
            g_grid.makeGrid(g_powerup, g_level);
            if (g_level.num > 0 && lockListenerOkay) gridExpand();
            g_target.update(false, g_grid);
            g_launcher.update(true, g_grid);
            if (g_powerup == Powerups.TWO_TARGETS) {
                g_target2.update2(false, g_grid);
            }
            if (g_powerup == Powerups.TWO_LAUNCHERS) {
                g_launcher2.update2(true, g_grid);
            }
        }
        g_level.recover = false;
        g_panel.graphicCount = 0;
        if (lockListenerOkay) restartLevel();
    }

    public static void restartLevel() {
        g_laser.nextLevel();
        // VERY IMPORTANT: this is all the drawing that happens before the game
        // actually starts: ie maze and target
        draw();
        g_level.restart = true;
    }


    public static void gridShrink() {
        inAnimation = true;
        for (int i = 0; i < SCREEN_HEIGHT / 30; i++) {
            Canvas c = g_panel.getCanvas();
            try {
                Thread.sleep(10);
                g_level.draw(c);
                for (Line line: g_grid.lines) {
                    line.shrink(LINE_SPACING);
                }
                g_grid.draw(c);
                g_buttons.draw(c, g_level, g_powerup);
            } catch (InterruptedException ignored) {}
            g_panel.postCanvas(c);
        }
        inAnimation = false;
    }
    public static void gridExpand() {
        inAnimation = true;
        for (int i = 0; i < SCREEN_HEIGHT / 30; i++) {
            Canvas c = g_panel.getCanvas();
            try {
                Thread.sleep(10);
                g_level.draw(c);
                for (Line line: g_grid.lines) {
                    line.expand(LINE_SPACING);
                }
                g_grid.expandDraw(c);
                g_buttons.draw(c, g_level, g_powerup);
            } catch (InterruptedException ignored) {}
            g_panel.postCanvas(c);
        }
        inAnimation = false;
    }


    public void settings() {
        g_level.inPrefs = true;
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public static void soundAndVib() {
        if (g_v != null) g_v.vibrate(10);
        if (g_level.score > 0) g_level.score--;
    }

    /****************************************** ON* - START***************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g_level = new Level();
        g_mainActivity = this;
        Log.i("crashing", "create");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        g_sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        g_panel = new Panel(this);
        setContentView(g_panel);
        if (g_sharedPrefs.getBoolean("screenOn", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        g_dialogues = new Dialogues(g_context, g_level, g_thread, g_powerup, g_sharedPrefs);
    }
    
    public void onResume() {
        super.onResume();
        lockListenerOkay = true;
        Log.i("crashing", "resume");
        if (g_level.inPrefs) {
            g_colorHandler.update(g_sharedPrefs, g_level, g_grid, g_laser);
            if (g_sharedPrefs.getBoolean("screenOn", true)) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            g_level.inPrefs = false;
        }
        if (g_sharedPrefs.getBoolean("vibrate", true)) g_v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        else g_v = null;
        g_panel = new Panel(MainActivity.this);
        setContentView(g_panel);
    }
    
    public void onPause() {
        super.onPause();
        lockListenerOkay = false;
        if (g_powerup.waitForChoice) g_powerup.selection = 4;

        Log.i("crashing", "pause");
        if (g_v != null) g_v.cancel();
        g_level.recover = true;
        g_level.exit = false;
        g_thread.setRunning(false);
        g_thread.selection = "";
        if (g_powerup.selection == 0) g_powerup.selection = 4;
        try {
            g_thread.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("crashing", "join failed");
        }
    }
    
    public void onRestart() {
        super.onRestart();
        g_level.recover = true;
        g_level.exit = true;
    }
    
    public void onStop() {
        super.onStop();
        Log.i("crashing", "stop");
    }
    
    public void onDestroy() {
        super.onDestroy();
        Log.i("crashing", "destroy");
    }
    
    public void onStart() {
        super.onStart();
        Log.i("crashing", "start");
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        g_level.exit = false;
        settings();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.i("buttons", "back button");
    }
}