package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.K;

public class Buttons {
    Paint text = new Paint();
    Paint button = new Paint();
    Paint warning = new Paint();
    Bitmap settings, restart, skip, noskip, powerupPic;
    Resources resources;

    Buttons(Resources resourcesIn) {
        button.setColor(Color.rgb(16, 16, 16));
        text.setColor(Color.WHITE);
        text.setTextSize((int) (K.SPECIAL_WIDTH / 1.5));
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
        c.drawRect(0, (K.SCREEN_HEIGHT - K.NAV_HEIGHT) + 2, K.SCREEN_WIDTH, K.SCREEN_HEIGHT, button);
        c.drawRect(0, 0, K.SCREEN_WIDTH, K.NAV_HEIGHT - 1, button);

        c.drawRect((K.SCREEN_WIDTH / 3) - 1, (K.NAV_HEIGHT / 7),
                (K.SCREEN_WIDTH / 3) + 1, K.NAV_HEIGHT - (K.NAV_HEIGHT / 7), text);
        c.drawRect((K.SCREEN_WIDTH * 2 / 3) - 1, (K.NAV_HEIGHT / 7),
                (K.SCREEN_WIDTH * 2 / 3) + 1, K.NAV_HEIGHT - (K.NAV_HEIGHT / 7), text);

        c.drawBitmap(settings, null, new Rect((K.SCREEN_WIDTH / 4) - K.NAV_HEIGHT,
                    K.SCREEN_HEIGHT - 9 * K.NAV_HEIGHT / 10,
                    (K.SCREEN_WIDTH / 4), K.SCREEN_HEIGHT - K.NAV_HEIGHT / 10), null);
        c.drawBitmap(restart, null, new Rect((3 * K.SCREEN_WIDTH / 4),
                    K.SCREEN_HEIGHT - K.NAV_HEIGHT, (3 * K.SCREEN_WIDTH / 4) + K.NAV_HEIGHT,
                K.SCREEN_HEIGHT), null);
        c.drawBitmap((level.score > level.skipCost? skip: noskip), null,
                new Rect((K.SCREEN_WIDTH / 2) - K.NAV_HEIGHT / 2, K.SCREEN_HEIGHT - K.NAV_HEIGHT,
                        (K.SCREEN_WIDTH / 2) + K.NAV_HEIGHT / 2, K.SCREEN_HEIGHT), null);


        float height = (K.NAV_HEIGHT / 2) - (text.ascent() / 4);

        if (powerup != Powerups.NONE) {
            c.drawBitmap(powerupPic, null, new Rect((5 * K.SCREEN_WIDTH / 6) - K.NAV_HEIGHT / 2,
                         1, (5 * K.SCREEN_WIDTH / 6) + K.NAV_HEIGHT / 2, K.NAV_HEIGHT - 1), null);
        }
        c.drawText("level: "+ level.num, K.SCREEN_WIDTH / 2, height, text);
        c.drawText("score: "+ level.score, K.SCREEN_WIDTH / 6, height, (level.score > 20?text: warning));
    }

    void update(Powerups powerup) {
        if (powerup != Powerups.NONE) {
            powerupPic = BitmapFactory.decodeResource(resources, powerup.smallPic);
        }
    }
}
