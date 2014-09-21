package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;


public class Level {
    Canvas c ;//= MainActivity._thread.c;
    int color; //this is background colour
    int num = 1;
    boolean exit = true;
    boolean recover = false;
    boolean restart = false;
    boolean inPrefs = false;
    boolean listening = false;
    MainActivity ma;
    int score = 100;
    int skipCost = 100;
    int selection = 3;
    int option1, option2;
    Bitmap b1, b2;
    String[] powerupNames = {"launchFromEither", "throughFirstLine", "twoLaunchers", "twoTargets",
                            "shortLines", "lessLines", "aimingLaser", "wrapAroundSides",
                            "wrapAroundEnds", "bigTargets"};
    String[] readableNames = {"Shoot From Target", "Through First Line", "2 Launchers", "2 Targets",
                            "Short Lines", "Less Lines", "Aiming Lazer", "No Sides", "No Ends",
                            "Big Target"};

    public Level(MainActivity ma) {
           this.ma = ma;
    }

    String activePowerup = "";
    void draw(Canvas c) {
       c.drawColor(color);
   }
    boolean pickPowerup(SurfaceHolder holder) {
        Map<String, Integer> bigPics = ma.bigPics;
        selection = 0;
        int SCREENWIDTH = ma.SCREENWIDTH;
        int SCREENHEIGHT = ma.SCREENHEIGHT;
        int NAVHEIGHT = ma.NAVHEIGHT;
        Paint text = new Paint();
        text.setTextAlign(Align.CENTER);
        Paint smallText = new Paint();
        smallText.setTextAlign(Align.CENTER);
        text.setColor(Color.WHITE);
        smallText.setColor(Color.WHITE);
        text.setTextSize(NAVHEIGHT);
        smallText.setTextSize(NAVHEIGHT / 2);
        Log.i("powerup", Integer.toString(SCREENWIDTH / 20));
        c = ma._thread.c;
        listening = true;
        option1 = randomBetween(0, powerupNames.length);
        option2 = randomBetween(0, powerupNames.length);
        while(option1 == option2) option2 = randomBetween(0, powerupNames.length);
        c = null;
        try {
            c = holder.lockCanvas();
            c.drawColor(Color.rgb(16, 16, 16));
            c.drawText("Choose powerup",  SCREENWIDTH / 2, NAVHEIGHT, text);
            Log.i("powerup", Integer.toString(bigPics.size()));
            String name = powerupNames[option1];
            b1 = BitmapFactory.decodeResource(ma.getResources(), bigPics.get(name));
            name = powerupNames[option2];
            b2 = BitmapFactory.decodeResource(ma.getResources(), bigPics.get(name));

            c.drawBitmap(b1, null, new Rect(0,
                                        NAVHEIGHT * 4,
                                        (SCREENWIDTH / 2),
                                        (SCREENWIDTH / 2) + NAVHEIGHT * 4), null);
            c.drawBitmap(b2, null, new Rect((SCREENWIDTH / 2),
                                        NAVHEIGHT * 4,
                                        SCREENWIDTH,
                                        (SCREENWIDTH / 2) + NAVHEIGHT * 4), null);
            c.drawText(readableNames[option1],  SCREENWIDTH / 4, NAVHEIGHT * 3, smallText);
            c.drawText(readableNames[option2],  (3 * SCREENWIDTH / 4), NAVHEIGHT * 3, smallText);
            c.drawLine(SCREENWIDTH / 2, NAVHEIGHT * 2, SCREENWIDTH / 2, SCREENHEIGHT, text);
        }
        finally {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the Surface in an
            // inconsistent state
            if (c != null) {
                holder.unlockCanvasAndPost(c); ///KEY!
            }
        }
        Log.i("powerup", "entered about to start loop");
        while(selection == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.i("powerup", "done loop");

        if (selection == 1) activePowerup = powerupNames[option1];
        else if (selection == 2) activePowerup = powerupNames[option2];
        Log.i("powerup", "out of loop " + Integer.toString(selection));
        listening = false;
        b1.recycle();
        b2.recycle();
        if (selection==4) {
            activePowerup = "";
            return false;
        } else {
            selection = 1;
            return true;
        }
    }

    int randomBetween(double low, double high) {
        double ran = Math.random();
        return (int) Math.floor((low + (ran * (high - low))));
    }

    void skip() {
        num++;
        score-=skipCost;
        skipCost += 100;
        restart = false;
        if (num == 1) score = 100;
    }

    void reset() {
        activePowerup = "";
        num = 1;
        score = 100;
        restart = false;
        skipCost = 100;
    }
}
