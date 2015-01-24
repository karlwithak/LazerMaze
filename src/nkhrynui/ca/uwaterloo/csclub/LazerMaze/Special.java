package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;

public enum Special{
    TARGET(R.drawable.newtarget),
    TARGET2(R.drawable.newtarget),
    LAUNCHER(R.drawable.shoot),
    LAUNCHER2(R.drawable.shoot);

    Bitmap bitmap;
    final int SPECIAL_WIDTH = MainActivity.SPECIAL_WIDTH;
    final int SCREEN_WIDTH = MainActivity.SCREEN_WIDTH;
    final int SCREEN_HEIGHT = MainActivity.SCREEN_HEIGHT;
    final int NAV_HEIGHT = MainActivity.NAV_HEIGHT;
    boolean active = false;
    int y;
    int x;
    int stdSize;
    int largeSize;
    Rect normalRect, largeRect;
    Special(int b) {
        if (MainActivity.g_resources == null) throw new NullPointerException("in special");
        bitmap = BitmapFactory.decodeResource(MainActivity.g_resources, b);
    }

    public void update(boolean isLauncher, Grid grid) {
        y = randomBetween(NAV_HEIGHT + SPECIAL_WIDTH, SCREEN_HEIGHT - (NAV_HEIGHT + SPECIAL_WIDTH));
        x = randomBetween(SPECIAL_WIDTH, SCREEN_WIDTH - (SPECIAL_WIDTH));
        ArrayList<Line> lines = grid.getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lineTest(lines.get(i))
                    || (isLauncher && tooEasy(TARGET, lines))) {
                y = randomBetween(NAV_HEIGHT + SPECIAL_WIDTH, SCREEN_HEIGHT - (NAV_HEIGHT + SPECIAL_WIDTH));
                x = randomBetween(SPECIAL_WIDTH, SCREEN_WIDTH - (SPECIAL_WIDTH));
                i = -1;
            }
        }
        stdSize = SPECIAL_WIDTH / 2;
        largeSize = SPECIAL_WIDTH;
        normalRect = new Rect(x - stdSize, y - stdSize, x + stdSize, y + stdSize);
        largeRect = new Rect(x - largeSize, y - largeSize, x + largeSize, y + largeSize);

    }

    public void update2(boolean isLauncher, Grid grid) {
        y = randomBetween(NAV_HEIGHT + SPECIAL_WIDTH, SCREEN_HEIGHT - (NAV_HEIGHT + SPECIAL_WIDTH));
        x = randomBetween(SPECIAL_WIDTH, SCREEN_WIDTH - (SPECIAL_WIDTH));
        ArrayList<Line> lines = grid.getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lineTest(lines.get(i))
                    || (isLauncher && tooEasy(TARGET, lines))
                    || (isLauncher && bigPointTest(LAUNCHER.x, LAUNCHER.y))
                    || (!isLauncher && bigPointTest(TARGET.x, TARGET.y))
                    || (!isLauncher && LAUNCHER.tooEasy(this, lines)))
            {
                y = randomBetween(NAV_HEIGHT + SPECIAL_WIDTH, SCREEN_HEIGHT - (NAV_HEIGHT + SPECIAL_WIDTH));
                x = randomBetween(SPECIAL_WIDTH, SCREEN_WIDTH - (SPECIAL_WIDTH));
                i = -1;
            }
        }
        stdSize = SPECIAL_WIDTH / 2;
        largeSize = SPECIAL_WIDTH;
        normalRect = new Rect(x - stdSize, y - stdSize, x + stdSize, y + stdSize);
        largeRect = new Rect(x - largeSize, y - largeSize, x + largeSize, y + largeSize);
    }

    boolean lineTest(Line line) {
        if (line.horizontal) {
            return Math.abs(line.starty - y) <= stdSize
                    && (inBetween(line.startx, x + stdSize, line.endx)
                    || inBetween(line.startx, x - stdSize, line.endx)
                    || inBetween(x - stdSize, line.startx, x + (SPECIAL_WIDTH / 2)));
        } else {
            return Math.abs(line.startx - x) <= stdSize
                    && (inBetween(line.starty, y + stdSize, line.endy)
                    || inBetween(line.starty, y - stdSize, line.endy)
                    || inBetween(y - stdSize, line.starty, y + (SPECIAL_WIDTH / 2)));
        }
    }

    boolean bigPointTest(double x1, double y1) {
        double x2 = Math.abs(x1 - x);
        double y2 = Math.abs(y1 - y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        return distance < SPECIAL_WIDTH * 2;
    }

    boolean smallPointTest(double x1, double y1, Powerups g_powerup) {
        double x2 = Math.abs(x1 - x);
        double y2 = Math.abs(y1 - y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        if (g_powerup == Powerups.BIG_TARGETS) {
            return distance < largeSize;
        } else {
            return distance < stdSize;
        }
    }

    void draw(Canvas canvas, boolean large) {
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



