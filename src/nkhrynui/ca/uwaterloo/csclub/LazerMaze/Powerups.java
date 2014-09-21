package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.res.Resources;
import android.graphics.*;
import android.view.SurfaceHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.differentRandomBetween;
import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.randomBetween;

public enum Powerups {
    LAUNCH_FROM_EITHER("Shoot From Target", R.drawable.launcheithersmall, R.drawable.launcheither),
    THROUGH_FIRST_LINE("Through First Line", R.drawable.throughfirstsmall, R.drawable.throughfirst),
    TWO_LAUNCHERS("2 Launchers", R.drawable.twolauncherssmall, R.drawable.twolaunchers),
    TWO_TARGETS("2 Targets", R.drawable.twotargetssmall, R.drawable.twotargets),
    SHORT_LINES("Short Lines", R.drawable.shorterlinessmall, R.drawable.shorterlines),
    LESS_LINES("Less Lines", R.drawable.lesslinessmall, R.drawable.lesslines),
    AIMING_LASER("Aiming Lazer", R.drawable.aiminglasersmall, R.drawable.aiminglaser),
    WRAP_AROUND_SIDES("No Sides", R.drawable.wraparoundsidessmall, R.drawable.wraparoundsides),
    WRAP_AROUND_ENDS("No Ends", R.drawable.wraparoundendssmall, R.drawable.wraparoundends),
    BIG_TARGETS("Big Target", R.drawable.largetargetsmall, R.drawable.largetarget),
    NONE("", 0, 0);

    public int selection = 3;
    Canvas c ;
    Resources resources = MainActivity.resources;
    public boolean waitForChoice = false;
    String explanation;
    int smallPic;
    int bigPic;
    private static final List<Powerups> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));

    Powerups(String _explanation, int _smallPic, int _bigPic) {
        explanation = _explanation;
        smallPic = _smallPic;
        bigPic = _bigPic;
    }

    Powerups pickPowerup(SurfaceHolder holder) {
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
        c = MainActivity._thread.c;
        waitForChoice = true;
        int option1int = randomBetween(0, VALUES.size() - 1);
        int option2int = differentRandomBetween(0, VALUES.size() - 1, option1int);
        Powerups option1PowerUp = VALUES.get(option1int);
        Powerups option2PowerUp = VALUES.get(option2int);
        c = null;
        Bitmap bitmap1, bitmap2;
        try {
            c = holder.lockCanvas();
            c.drawColor(Color.rgb(16, 16, 16));
            c.drawText("Choose powerup",  SCREENWIDTH / 2, NAVHEIGHT, text);
            bitmap1 = BitmapFactory.decodeResource(resources, option1PowerUp.bigPic);
            bitmap2 = BitmapFactory.decodeResource(resources, option2PowerUp.bigPic);

            c.drawBitmap(bitmap1, null, new Rect(0,
                    NAVHEIGHT * 4,
                    (SCREENWIDTH / 2),
                    (SCREENWIDTH / 2) + NAVHEIGHT * 4), null);
            c.drawBitmap(bitmap2, null, new Rect((SCREENWIDTH / 2),
                    NAVHEIGHT * 4,
                    SCREENWIDTH,
                    (SCREENWIDTH / 2) + NAVHEIGHT * 4), null);
            c.drawText(option1PowerUp.explanation,  SCREENWIDTH / 4, NAVHEIGHT * 3, smallText);
            c.drawText(option2PowerUp.explanation,  (3 * SCREENWIDTH / 4), NAVHEIGHT * 3, smallText);
            c.drawLine(SCREENWIDTH / 2, NAVHEIGHT * 2, SCREENWIDTH / 2, SCREENHEIGHT, text);
        }
        finally {
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

        bitmap1.recycle();
        bitmap2.recycle();
        waitForChoice = false;

        if (selection == 1) {
            selection = 1;
            return option1PowerUp;
        }
        else if (selection == 2) {
            selection = 1;
            return option2PowerUp;
        }
        else return  Powerups.NONE;
    }
}
