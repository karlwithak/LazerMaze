package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.Utils.*;
import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.PowerupManager.Powerup;

public enum Special{
    TARGET(R.drawable.newtarget),
    TARGET2(R.drawable.newtarget),
    LAUNCHER(R.drawable.shoot),
    LAUNCHER2(R.drawable.shoot);

    Bitmap bitmap;
    boolean active = false;
    int y, x, stdSize, largeSize;

    Special(int b) {
        stdSize = K.SPECIAL_WIDTH / 2;
        largeSize = K.SPECIAL_WIDTH;
        bitmap = BitmapFactory.decodeResource(MainPanel.m_mp.m_resources, b);
    }

    public void update(boolean isLauncher, Grid grid) {
        y = randomBetween(K.NAV_HEIGHT + K.SPECIAL_WIDTH, K.SCREEN_HEIGHT - (K.NAV_HEIGHT + K.SPECIAL_WIDTH));
        x = randomBetween(K.SPECIAL_WIDTH, K.SCREEN_WIDTH - (K.SPECIAL_WIDTH));
        ArrayList<Line> lines = grid.getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lineTest(lines.get(i))
                    || (isLauncher && tooEasy(TARGET, lines))) {
                y = randomBetween(K.NAV_HEIGHT + K.SPECIAL_WIDTH, K.SCREEN_HEIGHT - (K.NAV_HEIGHT + K.SPECIAL_WIDTH));
                x = randomBetween(K.SPECIAL_WIDTH, K.SCREEN_WIDTH - (K.SPECIAL_WIDTH));
                i = -1;
            }
        }
    }

    public void update2(boolean isLauncher, Grid grid) {
        y = randomBetween(K.NAV_HEIGHT + K.SPECIAL_WIDTH, K.SCREEN_HEIGHT - (K.NAV_HEIGHT + K.SPECIAL_WIDTH));
        x = randomBetween(K.SPECIAL_WIDTH, K.SCREEN_WIDTH - (K.SPECIAL_WIDTH));
        ArrayList<Line> lines = grid.getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lineTest(lines.get(i))
                    || (isLauncher && tooEasy(TARGET, lines))
                    || (isLauncher && bigPointTest(LAUNCHER.x, LAUNCHER.y))
                    || (!isLauncher && bigPointTest(TARGET.x, TARGET.y))
                    || (!isLauncher && LAUNCHER.tooEasy(this, lines)))
            {
                y = randomBetween(K.NAV_HEIGHT + K.SPECIAL_WIDTH, K.SCREEN_HEIGHT - (K.NAV_HEIGHT + K.SPECIAL_WIDTH));
                x = randomBetween(K.SPECIAL_WIDTH, K.SCREEN_WIDTH - (K.SPECIAL_WIDTH));
                i = -1;
            }
        }
    }

    boolean lineTest(Line line) {
        if (line.horizontal) {
            return Math.abs(line.starty - y) <= stdSize
                    && (inBetween(line.startx, x + stdSize, line.endx)
                    || inBetween(line.startx, x - stdSize, line.endx)
                    || inBetween(x - stdSize, line.startx, x + (K.SPECIAL_WIDTH / 2)));
        } else {
            return Math.abs(line.startx - x) <= stdSize
                    && (inBetween(line.starty, y + stdSize, line.endy)
                    || inBetween(line.starty, y - stdSize, line.endy)
                    || inBetween(y - stdSize, line.starty, y + (K.SPECIAL_WIDTH / 2)));
        }
    }

    boolean bigPointTest(double x1, double y1) {
        double x2 = Math.abs(x1 - x);
        double y2 = Math.abs(y1 - y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        return distance < K.SPECIAL_WIDTH * 2;
    }

    boolean smallPointTest(double x1, double y1, PowerupManager powerupMan) {
        double x2 = Math.abs(x1 - x);
        double y2 = Math.abs(y1 - y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        if (powerupMan.get() == Powerup.BIG_TARGETS) {
            return distance < largeSize;
        } else {
            return distance < stdSize;
        }
    }

    void draw(Canvas canvas, boolean large) {
        Rect normalRect = new Rect(x - stdSize, y - stdSize, x + stdSize, y + stdSize);
        Rect largeRect = new Rect(x - largeSize, y - largeSize, x + largeSize, y + largeSize);
        if (large) {
            canvas.drawBitmap(bitmap, null, largeRect, null);
        } else {
            canvas.drawBitmap(bitmap, null, normalRect, null);
        }
    }

    public boolean tooEasy(Special target, ArrayList<Line> lines) {
        for (Line line : lines) {
            if (line.crossed(this.x, this.y, target.x, target.y) > 0) {
                return false;
            }
        }
        return true;
    }
}



