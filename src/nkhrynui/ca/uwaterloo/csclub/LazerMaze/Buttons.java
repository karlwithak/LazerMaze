package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

public class Buttons {
    Paint text = new Paint();
    Paint button = new Paint();
    Paint warning = new Paint();
    MainActivity ma;
    Level level;
    int SCREENWIDTH;
    int SCREENHEIGHT;
    int NAVHEIGHT;
    int LINESPACING;
    int SPECIALWIDTH;
    Laser laser;
    Bitmap settings, restart, skip, noskip, powerup;
    Map<String, Integer> smallPics;


    Buttons(MainActivity mainActivity) {
        ma = mainActivity;
        level = ma.level;
        SCREENWIDTH = ma.SCREENWIDTH;
        SCREENHEIGHT = ma.SCREENHEIGHT;
        NAVHEIGHT = ma.NAVHEIGHT;
        LINESPACING = ma.LINESPACING;
        SPECIALWIDTH = ma.SPECIALWIDTH;
        laser = ma.laser;
        smallPics = ma.smallPics;
        button.setColor(Color.rgb(16, 16, 16));
        text.setColor(Color.WHITE);
        text.setTextSize((int) (SPECIALWIDTH / 1.5));
        text.setTextAlign(Align.CENTER);
        warning.set(text);
        warning.setColor(Color.RED);
        settings = BitmapFactory.decodeResource(ma.getResources(), smallPics.get("settings"));
        restart = BitmapFactory.decodeResource(ma.getResources(), smallPics.get("restart"));
        skip = BitmapFactory.decodeResource(ma.getResources(), smallPics.get("forward"));
        noskip = BitmapFactory.decodeResource(ma.getResources(), smallPics.get("forwardDisabled"));
    }

    void draw(Canvas c) {
        c.drawRect(0, (SCREENHEIGHT - NAVHEIGHT) + 2, SCREENWIDTH, SCREENHEIGHT, button);
        c.drawRect(0, 0, SCREENWIDTH, NAVHEIGHT - 1, button);

        c.drawRect((SCREENWIDTH / 3) - 1,
                (NAVHEIGHT / 7),
                (SCREENWIDTH / 3) + 1,
                NAVHEIGHT - (NAVHEIGHT / 7), text);
        c.drawRect((SCREENWIDTH * 2 / 3) - 1,
                (NAVHEIGHT / 7),
                (SCREENWIDTH * 2 / 3) + 1,
                NAVHEIGHT - (NAVHEIGHT / 7), text);

        c.drawBitmap(settings, null, new Rect((SCREENWIDTH / 4) - NAVHEIGHT,
                                            SCREENHEIGHT - 9 * NAVHEIGHT / 10,
                                            (SCREENWIDTH / 4),
                                            SCREENHEIGHT - NAVHEIGHT / 10), null);
        c.drawBitmap(restart, null, new Rect((3 * SCREENWIDTH / 4),
                                            SCREENHEIGHT - NAVHEIGHT,
                                            (3 * SCREENWIDTH / 4) + NAVHEIGHT,
                                            SCREENHEIGHT), null);
        c.drawBitmap((level.score > level.skipCost? skip: noskip), null,
                new Rect((SCREENWIDTH / 2) - NAVHEIGHT / 2,
                        SCREENHEIGHT - NAVHEIGHT,
                        (SCREENWIDTH / 2) + NAVHEIGHT / 2,
                        SCREENHEIGHT), null);


        float height = (NAVHEIGHT / 2) - (text.ascent() / 4);

        if (!level.activePowerup.equals("")) {
            c.drawBitmap(powerup, null, new Rect((5 * SCREENWIDTH / 6) - NAVHEIGHT / 2,
                                            1,
                                            (5 * SCREENWIDTH / 6) + NAVHEIGHT / 2,
                                            NAVHEIGHT - 1), null);
        }
        c.drawText("level: "+ level.num, SCREENWIDTH / 2, height, text);
        c.drawText("score: "+ level.score, SCREENWIDTH / 6, height, (level.score > 20?text: warning));
    }

    void update() {
        if (!level.activePowerup.equals("")) {
            powerup = BitmapFactory.decodeResource(ma.getResources(), smallPics.get(level.activePowerup));
        }
    }
}
