package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.K;

public class MainActivity extends Activity {
    // GLOBAL VARIABLES, set in surfaceCreated
    private static SharedPreferences g_sharedPrefs;
    private static Level g_level;
    private static Vibrator g_v = null;
    private static boolean lockListenerOkay = true;
    private static ColorHandler g_colorHandler = new ColorHandler();
    private static Grid g_grid;
    private static Powerups g_powerup;
    private static Panel g_panel;
    private static Dialogues g_dialogues;
    private static MainActivity g_mainActivity;

    /****** CONSTANTS END ************************************************************************/

    /***** PHYSICS - START**********************************************************/

    public void updatePhysics() {
        Special target = g_panel.m_target;
        Special target2 = g_panel.m_target2;
        Laser laser = g_panel.m_laser;
        final int SCREEN_WIDTH = K.SCREEN_WIDTH;
        final int SCREEN_HEIGHT = K.SCREEN_HEIGHT;
        final int NAV_HEIGHT = K.NAV_HEIGHT;
        GraphicObject.Coordinates coord;
        GraphicObject.Speed speed;
        coord = laser.GO.coordinates;
        speed = laser.GO.speed;
        if (target.smallPointTest(coord.x, coord.y, g_powerup)
                || (g_powerup == Powerups.TWO_TARGETS && target2.smallPointTest(coord.x, coord.y, g_powerup))) {
            g_level.num++;
            g_level.score+=100;
            g_panel.m_mainThread.setRunning(false);
            g_panel.m_mainThread.selection = "next";
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
                if (g_powerup == Powerups.THROUGH_FIRST_LINE && laser.pts.size() == 4) {
                    ignoring = true;
                    laser.bounce();
                    continue;
                }
                if (g_level.score < 1) {
                    g_mainActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            g_dialogues.endGameDialog();
                        }
                    });
                    g_level.reset();
                    g_panel.m_mainThread.setRunning(false);
                    g_panel.m_mainThread.selection = "next";
                    break;
                }
                if (g_powerup == Powerups.WRAP_AROUND_ENDS
                        && g_grid.getLines().indexOf(line) <= 1) {
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    laser.bounce();
                    if (line.starty < NAV_HEIGHT + 2) {
                        laser.starty = SCREEN_HEIGHT - NAV_HEIGHT - 2;
                        coord.setY(SCREEN_HEIGHT - NAV_HEIGHT - 2);
                        coord.lasty = (SCREEN_HEIGHT - NAV_HEIGHT - 1);
                    } else if (line.starty > SCREEN_HEIGHT - NAV_HEIGHT - 2) {
                        laser.starty = NAV_HEIGHT + 2;
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
                    laser.startx = coord.x;
                    coord.lastx = coord.x;
                    laser.bounce();
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    continue;
                }

                if (g_powerup == Powerups.WRAP_AROUND_SIDES
                        && (g_grid.getLines().indexOf(line) == 2
                                || g_grid.getLines().indexOf(line) == 3)) {
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    laser.bounce();
                    if (line.startx < 2) {
                        laser.startx = SCREEN_WIDTH - 2;
                        coord.setX(SCREEN_WIDTH - 1);
                        coord.lastx = SCREEN_WIDTH - 2;
                    } else if (line.startx > SCREEN_WIDTH - 2) {
                        laser.startx = 2;
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
                    laser.starty = coord.y;
                    coord.lasty = coord.x;
                    laser.bounce();
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
                            laser.bounce();
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
                            laser.bounce();
                            coord.x = (coord.x + speed.x);
                            coord.y = (coord.y + speed.y);
                            if (g_level.score >0) g_level.score--;

                        }
                    }
                }
                laser.bounce();
                break;
            }
        }
    }
     /****************************************** PHYSICS - END*****************************/

    public void nextLevel() {
        if (!g_level.recover) {
            if (g_grid.lines.size() > 1) gridShrink();
            if (g_level.num % 3 == 0 && g_level.num != 0) {
                g_powerup = g_powerup.pickPowerup(g_panel);
                Log.i("powerups", "done picking");
            }
            g_panel.m_powerup = g_powerup;
            g_panel.m_buttons.update(g_powerup);
            g_colorHandler.update(g_sharedPrefs, g_level, g_grid, g_panel.m_laser);
            g_grid.makeGrid(g_powerup, g_level);
            if (g_level.num > 0 && lockListenerOkay) gridExpand();
            g_panel.nextLevel();
        }
        g_level.recover = false;
        g_panel.graphicCount = 0;
        if (lockListenerOkay) restartLevel();
    }

    public void restartLevel() {
        g_panel.m_laser.nextLevel();
        // VERY IMPORTANT: this is all the drawing that happens before the game
        // actually starts: ie maze and target
        g_panel.draw();
        g_level.restart = true;
    }


    public static void gridShrink() {
        final int SCREEN_HEIGHT = K.SCREEN_HEIGHT;
        final int LINE_SPACING = K.LINE_SPACING;
        g_panel.inAnimation = true;
        for (int i = 0; i < SCREEN_HEIGHT / 30; i++) {
            Canvas c = g_panel.getCanvas();
            try {
                Thread.sleep(10);
                g_level.draw(c);
                for (Line line: g_grid.lines) {
                    line.shrink(LINE_SPACING);
                }
                g_grid.draw(c);
                g_panel.m_buttons.draw(c, g_level, g_powerup);
            } catch (InterruptedException ignored) {}
            g_panel.postCanvas(c);
        }
        g_panel.inAnimation = false;
    }
    public static void gridExpand() {
        final int SCREEN_HEIGHT = K.SCREEN_HEIGHT;
        final int LINE_SPACING = K.LINE_SPACING;
        g_panel.inAnimation = true;
        for (int i = 0; i < SCREEN_HEIGHT / 30; i++) {
            Canvas c = g_panel.getCanvas();
            try {
                Thread.sleep(10);
                g_level.draw(c);
                for (Line line: g_grid.lines) {
                    line.expand(LINE_SPACING);
                }
                g_grid.expandDraw(c);
                g_panel.m_buttons.draw(c, g_level, g_powerup);
            } catch (InterruptedException ignored) {}
            g_panel.postCanvas(c);
        }
        g_panel.inAnimation = false;
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
        g_grid = new Grid();
        g_powerup = Powerups.NONE;
        g_panel = new Panel(MainActivity.this, g_level, g_dialogues,
                            g_sharedPrefs, g_mainActivity, g_grid, g_powerup);
        setContentView(g_panel);
        if (g_sharedPrefs.getBoolean("screenOn", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        g_dialogues = new Dialogues(this, g_panel, g_sharedPrefs);
    }
    
    public void onResume() {
        super.onResume();
        lockListenerOkay = true;
        Log.i("crashing", "resume");
        if (g_level.inPrefs) {
            g_colorHandler.update(g_sharedPrefs, g_level, g_panel.m_grid, g_panel.m_laser);
            if (g_sharedPrefs.getBoolean("screenOn", true)) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            g_level.inPrefs = false;
        }
        if (g_sharedPrefs.getBoolean("vibrate", true)) g_v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        else g_v = null;
        g_panel = new Panel(MainActivity.this, g_level, g_dialogues, g_sharedPrefs, g_mainActivity,
                            g_grid, g_powerup);
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
        g_panel.m_mainThread.setRunning(false);
        g_panel.m_mainThread.selection = "";
        if (g_powerup.selection == 0) g_powerup.selection = 4;
        try {
            g_panel.m_mainThread.join(100);
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