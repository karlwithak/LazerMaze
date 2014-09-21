package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.res.Resources;
import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.randomBetween;

public class Powerups {
    int option1, option2;
    Bitmap b1, b2;
    public int selection = 3;
    Canvas c ;
    Resources resources;
    private String activePowerup = "";
    public boolean listening = false;
    String[] powerupNames = {"launchFromEither", "throughFirstLine", "twoLaunchers", "twoTargets",
            "shortLines", "lessLines", "aimingLaser", "wrapAroundSides",
            "wrapAroundEnds", "bigTargets"};
    String[] readableNames = {"Shoot From Target", "Through First Line", "2 Launchers", "2 Targets",
            "Short Lines", "Less Lines", "Aiming Lazer", "No Sides", "No Ends",
            "Big Target"};

    public Powerups(Resources resourcesIn) {
        resources = resourcesIn;
    }

    boolean pickPowerup(SurfaceHolder holder) {
        int SCREENWIDTH = MainActivity.SCREENWIDTH;
        int SCREENHEIGHT = MainActivity.SCREENHEIGHT;
        int NAVHEIGHT = MainActivity.NAVHEIGHT;
        selection = 0;
        Paint text = new Paint();
        text.setTextAlign(Paint.Align.CENTER);
        Paint smallText = new Paint();
        smallText.setTextAlign(Paint.Align.CENTER);
        text.setColor(Color.WHITE);
        smallText.setColor(Color.WHITE);
        text.setTextSize(NAVHEIGHT);
        smallText.setTextSize(NAVHEIGHT / 2);
        Log.i("powerup", Integer.toString(SCREENWIDTH / 20));
        c = MainActivity._thread.c;
        listening = true;
        option1 = randomBetween(0, powerupNames.length);
        option2 = randomBetween(0, powerupNames.length);
        while (option1 == option2) option2 = randomBetween(0, powerupNames.length);
        c = null;
        try {
            c = holder.lockCanvas();
            c.drawColor(Color.rgb(16, 16, 16));
            c.drawText("Choose powerup",  SCREENWIDTH / 2, NAVHEIGHT, text);
            Log.i("powerup", Integer.toString(MainActivity.bigPics.size()));
            String name = powerupNames[option1];
            b1 = BitmapFactory.decodeResource(resources, MainActivity.bigPics.get(name));
            name = powerupNames[option2];
            b2 = BitmapFactory.decodeResource(resources, MainActivity.bigPics.get(name));

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
        while(selection == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (selection == 1) activePowerup = powerupNames[option1];
        else if (selection == 2) activePowerup = powerupNames[option2];
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

    void reset() {
        activePowerup = "";
    }

    String getActive() {
        return activePowerup;
    }
}
