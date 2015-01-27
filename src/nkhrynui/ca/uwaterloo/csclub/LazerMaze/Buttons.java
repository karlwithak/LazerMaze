package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.PowerupManager.Powerup;

public class Buttons {
    private Paint m_text = new Paint();
    private Paint m_button = new Paint();
    private Paint m_warning = new Paint();
    private Bitmap settings, restart, skip, noskip, powerupPic;
    private Resources m_resources;
    private PowerupManager m_powerupMan;

    Buttons(Resources resourcesIn, PowerupManager powerupMan) {
        m_button.setColor(Color.rgb(16, 16, 16));
        m_text.setColor(Color.WHITE);
        m_text.setTextSize((int) (K.SPECIAL_WIDTH / 1.5));
        m_text.setTextAlign(Align.CENTER);
        m_text.setAntiAlias(true);
        m_warning.set(m_text);
        m_warning.setColor(Color.RED);
        m_warning.setAntiAlias(true);
        m_resources = resourcesIn;
        settings = BitmapFactory.decodeResource(m_resources, R.drawable.ic_menu_moreoverflow);
        restart = BitmapFactory.decodeResource(m_resources, R.drawable.ic_menu_refresh);
        skip = BitmapFactory.decodeResource(m_resources, R.drawable.ic_menu_forward);
        noskip = BitmapFactory.decodeResource(m_resources, R.drawable.ic_menu_forward2);
        m_powerupMan = powerupMan;
    }

    void draw(Canvas c, Level level) {
        c.drawRect(0, (K.SCREEN_HEIGHT - K.NAV_HEIGHT) + 2, K.SCREEN_WIDTH, K.SCREEN_HEIGHT, m_button);
        c.drawRect(0, 0, K.SCREEN_WIDTH, K.NAV_HEIGHT - 1, m_button);

        c.drawRect((K.SCREEN_WIDTH / 3) - 1, (K.NAV_HEIGHT / 7),
                (K.SCREEN_WIDTH / 3) + 1, K.NAV_HEIGHT - (K.NAV_HEIGHT / 7), m_text);
        c.drawRect((K.SCREEN_WIDTH * 2 / 3) - 1, (K.NAV_HEIGHT / 7),
                (K.SCREEN_WIDTH * 2 / 3) + 1, K.NAV_HEIGHT - (K.NAV_HEIGHT / 7), m_text);

        c.drawBitmap(settings, null, new Rect((K.SCREEN_WIDTH / 4) - K.NAV_HEIGHT,
                    K.SCREEN_HEIGHT - 9 * K.NAV_HEIGHT / 10,
                    (K.SCREEN_WIDTH / 4), K.SCREEN_HEIGHT - K.NAV_HEIGHT / 10), null);
        c.drawBitmap(restart, null, new Rect((3 * K.SCREEN_WIDTH / 4),
                    K.SCREEN_HEIGHT - K.NAV_HEIGHT, (3 * K.SCREEN_WIDTH / 4) + K.NAV_HEIGHT,
                K.SCREEN_HEIGHT), null);
        c.drawBitmap((level.score > level.skipCost? skip: noskip), null,
                new Rect((K.SCREEN_WIDTH / 2) - K.NAV_HEIGHT / 2, K.SCREEN_HEIGHT - K.NAV_HEIGHT,
                        (K.SCREEN_WIDTH / 2) + K.NAV_HEIGHT / 2, K.SCREEN_HEIGHT), null);


        float height = (K.NAV_HEIGHT / 2) - (m_text.ascent() / 4);

        if (powerupPic != null) {
            c.drawBitmap(powerupPic, null, new Rect((5 * K.SCREEN_WIDTH / 6) - K.NAV_HEIGHT / 2,
                         1, (5 * K.SCREEN_WIDTH / 6) + K.NAV_HEIGHT / 2, K.NAV_HEIGHT - 1), null);
        }
        c.drawText("level: "+ level.num, K.SCREEN_WIDTH / 2, height, m_text);
        c.drawText("score: "+ level.score, K.SCREEN_WIDTH / 6, height, (level.score > 20? m_text : m_warning));
    }

    void updatePowerup() {
        if (m_powerupMan.get() == Powerup.NONE) {
            powerupPic = null;
        } else {
            powerupPic = BitmapFactory.decodeResource(m_resources, m_powerupMan.get().m_smallPic);
        }
    }
}
