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
    public static int SCREENWIDTH;
    public static int SCREENHEIGHT;
    public static int NAVHEIGHT;
    public static int LINESPACING;
    public static int SPECIALWIDTH; //special refers to target and launcher
    public static int SPEED;

    public static SharedPreferences sharedPrefs;// = PreferenceManager.getDefaultSharedPreferences(this);
    public static Context context;
    public static MainThread _thread;
    public static Buttons buttons;
    public static Special target;// = new Target();
    public static Special launcher;
    public static Special target2 = null;// = new Target();
    public static Special launcher2 = null;
    public static Grid grid;// = new ArrayList<Line>();  //contains all of the grid and border lines
    public static Level level;
    Vibrator v = null;// = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    public static Laser laser;// = new Laser();
    public static boolean inAnimation = false;
    boolean upOnButtons = false;
    public static boolean lockListenerOkay = true;
    public static ColorHandler colorHandler = new ColorHandler();
    public static Powerups powerup;
    public static Resources resources;
    public static Panel panel;

    /****** CONSTANTS END ************************************************************************/

    /****** PANEL STARTS *************************************************************************/

    class Panel extends SurfaceView implements SurfaceHolder.Callback {
        public int graphicCount = 0;

        public Panel(Context context1) {
            super(context1);
            context = context1;
            getHolder().addCallback(this);
            _thread = new MainThread(getHolder(), MainActivity.this);
            setFocusable(true);
            _thread.start();
            _thread.setRunning(false);
        }

        float startX, startY, endX, endY, changeX, changeY;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            synchronized (_thread.getSurfaceHolder()) {
                if (powerup.waitForChoice) { //selecting upgrade
                    upOnButtons = false;
                    if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() <= SCREENWIDTH / 2) {
                        powerup.selection = 1;
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN
                            && event.getX() > SCREENWIDTH / 2) {
                        powerup.selection = 2;
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    _thread.setRunning(false);
                    _thread.selection = "restart";
                }
                //buttons
                if (event.getAction() == MotionEvent.ACTION_DOWN && event.getY() >=
                        SCREENHEIGHT - NAVHEIGHT && graphicCount == 0 && !inAnimation) {
                    upOnButtons = true;
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP && event.getY() >=
                        SCREENHEIGHT - NAVHEIGHT && graphicCount == 0 && !inAnimation && upOnButtons) {
                    upOnButtons = false;
                    if (event.getX() < SCREENWIDTH / 3) {
                        level.exit = false;
                        settings();
                    } else if (event.getX() > SCREENWIDTH * 2 / 3) {
                        Dialogues.restartDialog();

                    } else if (level.score > level.skipCost) {
                        Dialogues.skipLevelDialog();
                    }
                }
                //aiming launch
                if (event.getAction() == MotionEvent.ACTION_DOWN && graphicCount == 0 && !inAnimation) {
                    upOnButtons = false;
                    if (launcher.bigPointTest(event.getX(), event.getY())) {
                        if (_thread.isAlive()) {
                            _thread.setRunning(false);
                            _thread.selection = "restart";
                            level.restart = true;
                        }
                        startX = launcher.x;
                        startY = launcher.y;
                        launcher.active = true;
                        if (launcher2 != null) launcher2.active = false;
                        graphicCount = 1;
                    } else if (launcher2 != null && launcher2.bigPointTest(event.getX(), event.getY())) {
                        if (_thread.isAlive()) {
                            _thread.setRunning(false);
                            _thread.selection = "restart";
                            level.restart = true;
                        }
                        startX = launcher2.x;
                        startY = launcher2.y;
                        launcher2.active = true;
                        launcher.active = false;
                        graphicCount = 1;
                    } else if ((powerup == Powerups.LAUNCH_FROM_EITHER
                            && target.bigPointTest(event.getX(), event.getY()))) {
                        if (_thread.isAlive()) {
                            _thread.setRunning(false);
                            _thread.selection = "restart";
                            level.restart = true;
                        }
                        startX = target.x;
                        startY = target.y;
                        target.x = launcher.x;
                        target.y = launcher.y;
                        launcher.x = (int) startX;
                        launcher.y = (int) startY;
                        Bitmap temp = launcher.bitmap;
                        launcher.bitmap = target.bitmap;
                        target.bitmap = temp;
                        graphicCount = 1;
                    }
                }
                //aiming launch with aimer
                if (powerup == Powerups.AIMING_LASER
                        && event.getAction() == MotionEvent.ACTION_MOVE
                        && graphicCount == 1
                        && !inAnimation)
                {
                    if (Math.hypot((launcher.x - event.getX()), (launcher.y - event.getY())) < SPECIALWIDTH) {
                        return true;
                    }
                    Canvas c = null;
                    try {
                        c = _thread.getSurfaceHolder().lockCanvas();
                        level.draw(c);
                        double distance;
                        double min = 99999;
                        Line l = null;
                        float intersection = 0;
                        float intersectionTemp;
                        float pointx = launcher.x, pointy = launcher.y;
                        pointx += (event.getX() - launcher.x) * 1000;
                        pointy += (event.getY() - launcher.y) * 1000;
                        for (Line line : grid.lines) {
                            intersectionTemp = line.crossed(launcher.x, launcher.y, pointx, pointy);
                            if (intersectionTemp > 0) {
                                if (line.horizontal) {
                                    distance = Math.hypot((launcher.x - intersectionTemp),
                                            (launcher.y - line.starty));
                                } else {
                                    distance = Math.hypot((launcher.x - line.startx),
                                            (launcher.y - intersectionTemp));
                                }
                                if (distance < min) {
                                    min = distance;
                                    intersection = intersectionTemp;
                                    l = line;
                                }
                            }
                        }
                        if (l != null && l.horizontal) {
                            c.drawLine(launcher.x, launcher.y, intersection, l.starty, laser.paint);
                        } else if (l != null) {
                            c.drawLine(launcher.x, launcher.y, l.startx, intersection, laser.paint);
                        }
                        laser.draw(c);
                        grid.draw(c);
                        buttons.draw(c);
                        launcher.draw(c);
                        target.draw(c);
                    } finally {
                        if (c != null) {
                            _thread.getSurfaceHolder().unlockCanvasAndPost(c); ///KEY!
                        }
                    }
                }
                //launches laser
                if (event.getAction() == MotionEvent.ACTION_UP && graphicCount == 1 && !inAnimation) {
                    upOnButtons = false;
                    Log.i("powerup", Float.toString(startX) + " on up");
                    laser.GO.coordinates.setX((int) startX);
                    laser.GO.coordinates.setY((int) startY);
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
                        laser.GO.speed.x = (SPEED * Math.signum(changeX));
                        laser.GO.speed.y = (Math.abs(changeY) / Math.abs(changeX) * SPEED * Math.signum(changeY));
                    } else {
                        laser.GO.speed.y = (SPEED * Math.signum(changeY));
                        laser.GO.speed.x = (Math.abs(changeX) / Math.abs(changeY) * SPEED * Math.signum(changeX));
                    }
                    graphicCount = 0;
                    if (level.restart) {
                        if (launcher2 == null || launcher.active) laser.reset(launcher);
                        else laser.reset(launcher2);
                        _thread.setRunning(true);
                    } else {
                        _thread.setRunning(true);
                    }
                }
                return true;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (sharedPrefs.getInt("highScore", 0) == 0) {
                Dialogues.newGameDialog();
                SharedPreferences.Editor e = sharedPrefs.edit();
                e.putInt("highScore", 1);
                e.commit();
            }

            //initializing global variables, declared at top
            Log.i("settings", "creating surface");
            SCREENWIDTH = getWidth();
            SCREENHEIGHT = getHeight();
            LINESPACING = SCREENHEIGHT / 39;
            NAVHEIGHT = LINESPACING * 3;
            SPECIALWIDTH = SCREENHEIGHT / 20;
            SPEED = SCREENWIDTH / 50;
            if (!level.recover) {
                laser = new Laser();
                buttons = new Buttons(getResources());
                grid = new Grid();
                launcher = Special.LAUNCHER;
                target = Special.TARGET;
            }
            nextLevel(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
        }
    }

    /***** PANEL - END**************************************************************/

    /***** PHYSICS - START**********************************************************/

    public void updatePhysics(Canvas c) {
        GraphicObject.Coordinates coord;
        GraphicObject.Speed speed;
        coord = laser.GO.coordinates;
        speed = laser.GO.speed;
        if (target.smallPointTest(coord.x, coord.y)
                || (target2 != null && target2.smallPointTest(coord.x, coord.y))) {
            level.num++;
            level.score+=100;
            _thread.setRunning(false);
            _thread.selection = "next";
            level.restart = false;
            if (level.num == 1) {
                level.score = 100;
            }
        }

        coord.setX(coord.x + speed.x);
        coord.setY(coord.y + speed.y);
        Line line;
        boolean ignoring = false;
        boolean doubleHit = true;
        for (int i = 0; i < grid.getLines().size(); i++) {
            line = grid.getLines().get(i);
            if ((coord.lastx != -1  && coord.lasty != -1)
                    && line.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0)
            {
                if (powerup == Powerups.THROUGH_FIRST_LINE && laser.pts.size() == 4) {
                    ignoring = true;
                    laser.bounce();
                    continue;
                }
                if (level.score < 1) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Dialogues.endGameDialog(level.num);
                        }
                    });
                    level.reset();
                    powerup = Powerups.NONE;
                    _thread.setRunning(false);
                    _thread.selection = "next";
                    break;
                }
                if (powerup == Powerups.WRAP_AROUND_ENDS
                        && grid.getLines().indexOf(line) <= 1) {
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    laser.bounce();
                    if (line.starty < NAVHEIGHT + 2) {
                        laser.starty = SCREENHEIGHT - NAVHEIGHT - 2;
                        coord.setY(SCREENHEIGHT - NAVHEIGHT - 2);
                        coord.lasty = (SCREENHEIGHT - NAVHEIGHT - 1);
                    } else if (line.starty > SCREENHEIGHT - NAVHEIGHT - 2) {
                        laser.starty = NAVHEIGHT + 2;
                        coord.setY(NAVHEIGHT + 2);
                        coord.lasty = NAVHEIGHT + 1;
                    }
                    if (coord.x <= speed.x) {
                        coord.x = 2;
                        speed.toggleXDirection();
                        doubleHit = false;
                        soundAndVib();
                    } else if (coord.x >= SCREENWIDTH - speed.x) {
                        coord.x = SCREENWIDTH - 2;
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

                if (powerup == Powerups.WRAP_AROUND_SIDES
                        && (grid.getLines().indexOf(line) == 2
                                || grid.getLines().indexOf(line) == 3)) {
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    laser.bounce();
                    if (line.startx < 2) {
                        laser.startx = SCREENWIDTH - 2;
                        coord.setX(SCREENWIDTH - 1);
                        coord.lastx = SCREENWIDTH - 2;
                    } else if (line.startx > SCREENWIDTH - 2) {
                        laser.startx = 2;
                        coord.setX(2);
                        coord.lastx = 1;
                    }

                    if (coord.y <= NAVHEIGHT - speed.y) {
                        coord.y = NAVHEIGHT + 2;
                        speed.toggleYDirection();
                        doubleHit = false;
                        soundAndVib();
                    } else if ( coord.y >= SCREENHEIGHT - NAVHEIGHT - speed.y) {
                        coord.y =  SCREENHEIGHT - NAVHEIGHT - 2;
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
                    for (Line line2 : grid.getLines()) {
                        if (!ignoring
                                && line2 != line
                                && line2.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0
                                && doubleHit)
                        {
                            Log.i("graphics", "double hit");
                            speed.toggleXDirection();
                            coord.x = (line2.endx);
                            coord.y =(line.endy);
                            laser.draw(c);
                            laser.bounce();
                            coord.x =(coord.x + speed.x);
                            coord.y =(coord.y + speed.y);
                            if (level.score >0) level.score--;
                        }
                    }
                } else {
                    speed.toggleXDirection();
                    change = Math.abs((coord.x - line.startx) / speed.x);
                    coord.x = (line.startx);
                    coord.y = (coord.y+ (change * speed.y));
                    for (Line line2 : grid.getLines()) {
                        if (!ignoring && line2 != line
                                && line2.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0
                                && doubleHit)
                        {
                            Log.i("graphics", "double hit");
                            speed.toggleYDirection();
                            coord.x = (line.endx);
                            coord.y = (line2.endy);
                            laser.draw(c);
                            laser.bounce();
                            coord.x = (coord.x + speed.x);
                            coord.y = (coord.y + speed.y);
                            if (level.score >0) level.score--;

                        }
                    }
                }
                laser.draw(c);
                laser.bounce();
                break;
            }
        }

    }
     /****************************************** PHYSICS - END*****************************/

     /****************************************** DRAWING - START***************************/
    public void draw(Canvas canvas) {
        level.draw(canvas);
        laser.draw(canvas);
        if (level.num != 0) target.draw(canvas);
        if (target2 != null) target2.draw(canvas);
        launcher.draw(canvas);
        if (launcher2 != null) launcher2.draw(_thread.c);
        grid.draw(canvas);
        buttons.draw(canvas);
    }

    /****************************************** DRAWING - END**********************************/

    public void nextLevel(SurfaceHolder holder) {
        if (!level.recover) {
            if (grid.lines.size() > 1) gridShrink(holder);
            if (level.num % 3 == 0 && level.num != 0) powerup = powerup.pickPowerup(holder);
            buttons.update();
            colorHandler.update();
            grid.makeGrid();
            if (level.num > 0 && lockListenerOkay) gridExpand(holder);
            target.update(false);
            launcher.update(true);

        }
        level.recover = false;
        panel.graphicCount = 0;
        if (lockListenerOkay) restartLevel(holder);
    }

    public void restartLevel(SurfaceHolder holder) {
        laser.nextLevel();
        // VERY IMPORTANT: this is all the drawing that happens before the game
        // actually starts: ie maze and target
        _thread.c = null;
        try {
            _thread.c = holder.lockCanvas();
            draw(_thread.c);
            level.restart = true;
        }
        finally {
            if (_thread.c != null) {
                holder.unlockCanvasAndPost(_thread.c); ///KEY!
            }
        }
    }


    public static void gridShrink(SurfaceHolder holder) {
        inAnimation = true;
        for (int i = 0; i < SCREENHEIGHT / 30; i++) {
            try {
                Thread.sleep(10);
                _thread.c = holder.lockCanvas();
                level.draw(_thread.c);
                for (Line line: grid.lines) {
                    line.shrink(LINESPACING);
                }
                grid.draw(_thread.c);
                buttons.draw(_thread.c);
            } catch (InterruptedException ignored) {}
            finally {
                if (_thread.c != null) {
                    holder.unlockCanvasAndPost(_thread.c); ///KEY!
                }
            }
        }
        inAnimation = false;
    }
    public static void gridExpand(SurfaceHolder holder) {
        inAnimation = true;
        for (int i = 0; i < SCREENHEIGHT / 30; i++) {
            try {
                Thread.sleep(10);
                _thread.c = holder.lockCanvas();
                level.draw(_thread.c);
                for (Line line: grid.lines) {
                    line.expand(LINESPACING);
                }
                grid.expandDraw(_thread.c);
                buttons.draw(_thread.c);
            } catch (InterruptedException e) {}
            finally {
                if (_thread.c != null) {
                    holder.unlockCanvasAndPost(_thread.c); ///KEY!
                }
            }
        }
        inAnimation = false;
    }


    public void settings() {
        level.inPrefs = true;
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void soundAndVib() {
        if (v != null) v.vibrate(10);
        if (level.score > 0) level.score--;
    }

    /****************************************** ON* - START***************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();
        level = new Level();
        powerup = Powerups.AIMING_LASER;
        Log.i("crashing", "create");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        panel = new Panel(this);
        setContentView(panel);
        if (sharedPrefs.getBoolean("screenOn", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    
    public void onResume() {
        super.onResume();
        lockListenerOkay = true;
        Log.i("crashing", "resume");
        if (level.inPrefs) {
            colorHandler.update();
            if (sharedPrefs.getBoolean("screenOn", true)) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            level.inPrefs = false;
        }
        if (sharedPrefs.getBoolean("vibrate", true)) v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        else v = null;
        panel = new Panel(MainActivity.this);
        setContentView(panel);
    }
    
    public void onPause() {
        super.onPause();
        lockListenerOkay = false;
        if (powerup.waitForChoice) powerup.selection = 4;

        Log.i("crashing", "pause");
        if (v != null) v.cancel();
        level.recover = true;
        level.exit = false;
        _thread.setRunning(false);
        _thread.selection = "";
        if (powerup.selection == 0) powerup.selection = 4;
        try {
            _thread.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("crashing", "join failed");
        }
    }
    
    public void onRestart() {
        super.onRestart();
        level.recover = true;
        level.exit = true;
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
        level.exit = false;
        settings();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.i("buttons", "back button");
    }
}