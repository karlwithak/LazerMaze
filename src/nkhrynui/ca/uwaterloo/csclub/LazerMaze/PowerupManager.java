package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.graphics.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.Utils.*;

public class PowerupManager {
    public int m_selection = 3;
    private Panel m_panel;
    public boolean m_waitForChoice = false;
    private Powerup m_active;
    private static final List<Powerup> VALUES = Collections.unmodifiableList(Arrays.asList(Powerup.values()));

    PowerupManager(Panel panel) {
        m_active = Powerup.NONE;
        m_panel = panel;
    }

    public Powerup get() {
        return m_active;
    }

    public void set(Powerup powerup) {
        m_active = powerup;
    }

    void setPowerup() {
        m_selection = 0;
        Paint text = new Paint();
        text.setTextSize(K.NAV_HEIGHT);
        text.setTextAlign(Paint.Align.CENTER);
        text.setColor(Color.WHITE);
        text.setAntiAlias(true);
        Paint smallText = new Paint();
        smallText.setTextAlign(Paint.Align.CENTER);
        smallText.setColor(Color.WHITE);
        smallText.setTextSize(K.NAV_HEIGHT / 2);
        smallText.setAntiAlias(true);
        Canvas c = m_panel.getCanvas();
        m_waitForChoice = true;
        int option1int = randomBetween(0, VALUES.size() - 1);
        int option2int = differentRandomBetween(0, VALUES.size() - 1, option1int);
        Powerup option1PowerUp = VALUES.get(option1int);
        Powerup option2PowerUp = VALUES.get(option2int);
        Bitmap bitmap1, bitmap2;
        c.drawColor(Color.rgb(16, 16, 16));
        c.drawText("Choose powerup", K.SCREEN_WIDTH / 2, K.NAV_HEIGHT, text);
        bitmap1 = BitmapFactory.decodeResource(m_panel.getResources(), option1PowerUp.m_bigPic);
        bitmap2 = BitmapFactory.decodeResource(m_panel.getResources(), option2PowerUp.m_bigPic);

        c.drawBitmap(bitmap1, null, new Rect(0, K.NAV_HEIGHT * 4,
                    (K.SCREEN_WIDTH / 2), (K.SCREEN_WIDTH / 2) + K.NAV_HEIGHT * 4), null);
        c.drawBitmap(bitmap2, null, new Rect((K.SCREEN_WIDTH / 2), K.NAV_HEIGHT * 4,
        K.SCREEN_WIDTH, (K.SCREEN_WIDTH / 2) + K.NAV_HEIGHT * 4), null);
        c.drawText(option1PowerUp.m_explanation, K.SCREEN_WIDTH / 4, K.NAV_HEIGHT * 3, smallText);
        c.drawText(option2PowerUp.m_explanation, (3 * K.SCREEN_WIDTH / 4), K.NAV_HEIGHT * 3, smallText);
        c.drawLine(K.SCREEN_WIDTH / 2, K.NAV_HEIGHT * 2, K.SCREEN_WIDTH / 2, K.SCREEN_HEIGHT, text);
        m_panel.postCanvas(c);
        while (m_selection == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        bitmap1.recycle();
        bitmap2.recycle();
        m_waitForChoice = false;

        if (m_selection == 1) {
            m_selection = 1;
            m_active = option1PowerUp;
        } else if (m_selection == 2) {
            m_selection = 1;
            m_active = option2PowerUp;
        } else m_active = Powerup.NONE;
    }

    public enum Powerup {
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

        String m_explanation;
        int m_smallPic;
        int m_bigPic;

        Powerup(String explanation, int smallPic, int bigPic) {
            m_explanation = explanation;
            m_smallPic = smallPic;
            m_bigPic = bigPic;
        }


    }
}
