package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.res.Resources;
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
    final static int SCREENWIDTH = MainActivity.SCREENWIDTH;
    final static int SCREENHEIGHT = MainActivity.SCREENHEIGHT;
    final static int NAVHEIGHT = MainActivity.NAVHEIGHT;
    final static int SPECIALWIDTH = MainActivity.SPECIALWIDTH;
    Bitmap settings, restart, skip, noskip, powerup;
    Resources resources;

    Buttons(Resources resourcesIn) {
        button.setColor(Color.rgb(16, 16, 16));
        text.setColor(Color.WHITE);
        text.setTextSize((int) (SPECIALWIDTH / 1.5));
        text.setTextAlign(Align.CENTER);
        warning.set(text);
        warning.setColor(Color.RED);
        resources = resourcesIn;
        settings = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_moreoverflow);
        restart = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_refresh);
        skip = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_forward);
        noskip = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_forward2);
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
        c.drawBitmap((MainActivity.level.score > MainActivity.level.skipCost? skip: noskip), null,
                new Rect((SCREENWIDTH / 2) - NAVHEIGHT / 2,
                        SCREENHEIGHT - NAVHEIGHT,
                        (SCREENWIDTH / 2) + NAVHEIGHT / 2,
                        SCREENHEIGHT), null);


        float height = (NAVHEIGHT / 2) - (text.ascent() / 4);

        if (MainActivity.powerup != Powerups.NONE) {
            c.drawBitmap(powerup, null, new Rect((5 * SCREENWIDTH / 6) - NAVHEIGHT / 2,
                                            1,
                                            (5 * SCREENWIDTH / 6) + NAVHEIGHT / 2,
                                            NAVHEIGHT - 1), null);
        }
        c.drawText("level: "+ MainActivity.level.num, SCREENWIDTH / 2, height, text);
        c.drawText("score: "+ MainActivity.level.score, SCREENWIDTH / 6, height, (MainActivity.level.score > 20?text: warning));
    }

    void update() {
        if (MainActivity.powerup != Powerups.NONE) {
            powerup = BitmapFactory.decodeResource(resources, MainActivity.powerup.smallPic);
        }
    }
}
