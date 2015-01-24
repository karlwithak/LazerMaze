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
    final static int SCREEN_WIDTH = MainActivity.SCREEN_WIDTH;
    final static int SCREEN_HEIGHT = MainActivity.SCREEN_HEIGHT;
    final static int NAV_HEIGHT = MainActivity.NAV_HEIGHT;
    final static int SPECIAL_WIDTH = MainActivity.SPECIAL_WIDTH;
    Bitmap settings, restart, skip, noskip, powerupPic;
    Resources resources;

    Buttons(Resources resourcesIn) {
        button.setColor(Color.rgb(16, 16, 16));
        text.setColor(Color.WHITE);
        text.setTextSize((int) (SPECIAL_WIDTH / 1.5));
        text.setTextAlign(Align.CENTER);
        text.setAntiAlias(true);
        warning.set(text);
        warning.setColor(Color.RED);
        warning.setAntiAlias(true);
        resources = resourcesIn;
        settings = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_moreoverflow);
        restart = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_refresh);
        skip = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_forward);
        noskip = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_forward2);
    }

    void draw(Canvas c, Level level, Powerups powerup) {
        c.drawRect(0, (SCREEN_HEIGHT - NAV_HEIGHT) + 2, SCREEN_WIDTH, SCREEN_HEIGHT, button);
        c.drawRect(0, 0, SCREEN_WIDTH, NAV_HEIGHT - 1, button);

        c.drawRect((SCREEN_WIDTH / 3) - 1,
                (NAV_HEIGHT / 7),
                (SCREEN_WIDTH / 3) + 1,
                NAV_HEIGHT - (NAV_HEIGHT / 7), text);
        c.drawRect((SCREEN_WIDTH * 2 / 3) - 1,
                (NAV_HEIGHT / 7),
                (SCREEN_WIDTH * 2 / 3) + 1,
                NAV_HEIGHT - (NAV_HEIGHT / 7), text);

        c.drawBitmap(settings, null, new Rect((SCREEN_WIDTH / 4) - NAV_HEIGHT,
                                            SCREEN_HEIGHT - 9 * NAV_HEIGHT / 10,
                                            (SCREEN_WIDTH / 4),
                                            SCREEN_HEIGHT - NAV_HEIGHT / 10), null);
        c.drawBitmap(restart, null, new Rect((3 * SCREEN_WIDTH / 4),
                                            SCREEN_HEIGHT - NAV_HEIGHT,
                                            (3 * SCREEN_WIDTH / 4) + NAV_HEIGHT,
                SCREEN_HEIGHT), null);
        c.drawBitmap((level.score > level.skipCost? skip: noskip), null,
                new Rect((SCREEN_WIDTH / 2) - NAV_HEIGHT / 2,
                        SCREEN_HEIGHT - NAV_HEIGHT,
                        (SCREEN_WIDTH / 2) + NAV_HEIGHT / 2,
                        SCREEN_HEIGHT), null);


        float height = (NAV_HEIGHT / 2) - (text.ascent() / 4);

        if (powerup != Powerups.NONE) {
            c.drawBitmap(powerupPic, null, new Rect((5 * SCREEN_WIDTH / 6) - NAV_HEIGHT / 2,
                                            1,
                                            (5 * SCREEN_WIDTH / 6) + NAV_HEIGHT / 2,
                                            NAV_HEIGHT - 1), null);
        }
        c.drawText("level: "+ level.num, SCREEN_WIDTH / 2, height, text);
        c.drawText("score: "+ level.score, SCREEN_WIDTH / 6, height, (level.score > 20?text: warning));
    }

    void update(Powerups powerup) {
        if (powerup != Powerups.NONE) {
            powerupPic = BitmapFactory.decodeResource(resources, powerup.smallPic);
        }
    }
}
