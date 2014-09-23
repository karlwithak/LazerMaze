package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;

public enum Special{
    TARGET(BitmapFactory.decodeResource(MainActivity.g_resources, R.drawable.newtarget)),
    TARGET2(BitmapFactory.decodeResource(MainActivity.g_resources, R.drawable.newtarget)),
    LAUNCHER(BitmapFactory.decodeResource(MainActivity.g_resources, R.drawable.shoot)),
    LAUNCHER2(BitmapFactory.decodeResource(MainActivity.g_resources, R.drawable.shoot));

    Bitmap bitmap;
    final static int SPECIAL_WIDTH = MainActivity.SPECIAL_WIDTH;
    final static int SCREEN_WIDTH = MainActivity.SCREEN_WIDTH;
    final static int SCREEN_HEIGHT = MainActivity.SCREEN_HEIGHT;
    final static int NAV_HEIGHT = MainActivity.NAV_HEIGHT;
    boolean active = false;
    int y;
    int x;
    Rect r;

    Special(Bitmap b) {
        bitmap = b;
    }

    public void update(boolean isLauncher) {
        y = randomBetween(NAV_HEIGHT + SPECIAL_WIDTH, SCREEN_HEIGHT - (NAV_HEIGHT + SPECIAL_WIDTH));
        x = randomBetween(SPECIAL_WIDTH, SCREEN_WIDTH - (SPECIAL_WIDTH));
        ArrayList<Line> lines = MainActivity.g_grid.getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lineTest(lines.get(i))
                    || (isLauncher && tooEasy(TARGET, lines))) {
                y = randomBetween(NAV_HEIGHT + SPECIAL_WIDTH, SCREEN_HEIGHT - (NAV_HEIGHT + SPECIAL_WIDTH));
                x = randomBetween(SPECIAL_WIDTH, SCREEN_WIDTH - (SPECIAL_WIDTH));
                i = -1;
            }
        }
        r = new Rect(x - (SPECIAL_WIDTH / 2),
                    y - (SPECIAL_WIDTH / 2),
                    x + (SPECIAL_WIDTH / 2),
                    y + (SPECIAL_WIDTH / 2));
    }

    public void update2(boolean isLauncher) {
        y = randomBetween(NAV_HEIGHT + SPECIAL_WIDTH, SCREEN_HEIGHT - (NAV_HEIGHT + SPECIAL_WIDTH));
        x = randomBetween(SPECIAL_WIDTH, SCREEN_WIDTH - (SPECIAL_WIDTH));
        ArrayList<Line> lines = MainActivity.g_grid.getLines();
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
        r = new Rect(x - (SPECIAL_WIDTH / 2),
                y - (SPECIAL_WIDTH / 2),
                x + (SPECIAL_WIDTH / 2),
                y + (SPECIAL_WIDTH / 2));
    }

    boolean lineTest(Line line) {
        if (line.horizontal) {
            return Math.abs(line.starty - y) <= (SPECIAL_WIDTH / 2)
                    && (inBetween(line.startx, x + (SPECIAL_WIDTH / 2), line.endx)
                    || inBetween(line.startx, x - (SPECIAL_WIDTH / 2), line.endx)
                    || inBetween(x - (SPECIAL_WIDTH / 2), line.startx, x + (SPECIAL_WIDTH / 2)));
        } else {
            return Math.abs(line.startx - x) <= (SPECIAL_WIDTH / 2)
                    && (inBetween(line.starty, y + (SPECIAL_WIDTH / 2), line.endy)
                    || inBetween(line.starty, y - (SPECIAL_WIDTH / 2), line.endy)
                    || inBetween(y - (SPECIAL_WIDTH / 2), line.starty, y + (SPECIAL_WIDTH / 2)));
        }
    }

    boolean bigPointTest(double x1, double y1) {
        double x2 = Math.abs(x1 - x);
        double y2 = Math.abs(y1 - y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        return distance < SPECIAL_WIDTH * 2;
    }

    boolean smallPointTest(double x1, double y1) {
        double x2 = Math.abs(x1 - x);
        double y2 = Math.abs(y1 - y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        return distance < (SPECIAL_WIDTH / 2);
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, r, null);
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



