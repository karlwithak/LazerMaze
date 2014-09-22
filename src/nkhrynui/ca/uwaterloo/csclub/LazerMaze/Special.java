package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;

public enum Special{
    TARGET(BitmapFactory.decodeResource(MainActivity.resources, R.drawable.newtarget)),
    LAUNCHER(BitmapFactory.decodeResource(MainActivity.resources, R.drawable.shoot));

    Bitmap bitmap;
    final int SPECIALWIDTH = MainActivity.SPECIALWIDTH;
    final int SCREENWIDTH = MainActivity.SCREENWIDTH;
    final int SCREENHEIGHT = MainActivity.SCREENHEIGHT;
    final int NAVHEIGHT = MainActivity.NAVHEIGHT;
    boolean active = false;
    int y;
    int x;
    Rect r;

    Special(Bitmap b) {
        bitmap = b;
    }

    public void update(boolean isLauncher) {
        y = randomBetween(NAVHEIGHT + SPECIALWIDTH, SCREENHEIGHT - (NAVHEIGHT + SPECIALWIDTH));
        x = randomBetween(SPECIALWIDTH, SCREENWIDTH - (SPECIALWIDTH));
        ArrayList<Line> lines = MainActivity.grid.getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lineTest(lines.get(i))
                    || (isLauncher && tooEasy(TARGET, lines))) {
                y = randomBetween(NAVHEIGHT + SPECIALWIDTH, SCREENHEIGHT - (NAVHEIGHT + SPECIALWIDTH));
                x = randomBetween(SPECIALWIDTH, SCREENWIDTH - (SPECIALWIDTH));
                i = -1;
            }
        }
        r = new Rect(x - (SPECIALWIDTH / 2),
                    y - (SPECIALWIDTH / 2),
                    x + (SPECIALWIDTH / 2),
                    y + (SPECIALWIDTH / 2));
    }



/*

        target2 = null;
        launcher2 = null;

        target = new Target(targetBitmap);
        for (int i = 0; i < grid.getLines().size(); i++) {
            if (target.lineTest(grid.getLines().get(i))) {
                target = new Target(targetBitmap);
                i = -1;
            }
        }

        if (powerup == Powerups.TWO_TARGETS) {
            target2 = new Target(targetBitmap);
            for (int i = 0; i < grid.getLines().size(); i++) {
                if (target2.lineTest(grid.getLines().get(i))
                        || target.bigPointTest(target2.x, target2.y)) {
                    target2 = new Target(targetBitmap);
                    i = -1;
                }
            }
        }

        launcher = new Launcher(launcherBitmap);
        for (int i = 0; i < grid.getLines().size(); i++) {
            if (launcher.lineTest(grid.getLines().get(i))
                    || launcher.tooEasy(target, grid.getLines())
                    || (target2 != null && launcher.tooEasy(target2, grid.getLines()))) {
                launcher = new Launcher(launcherBitmap);
                i = -1;
            }
        }

        if (powerup == Powerups.TWO_LAUNCHERS) {
            launcher2 = new Launcher(launcherBitmap);
            for (int i = 0; i < grid.getLines().size(); i++) {
                if (launcher2.lineTest(grid.getLines().get(i))
                        || launcher2.tooEasy(target, grid.getLines())
                        || launcher.bigPointTest(launcher2.x, launcher2.y)) {
                    launcher2 = new Launcher(launcherBitmap);
                    i = -1;
                }
            }
        }
    } */

    boolean lineTest(Line line) {
        if (line.horizontal) {
            return Math.abs(line.starty - y) <= (SPECIALWIDTH / 2)
                    && (inBetween(line.startx, x + (SPECIALWIDTH / 2), line.endx)
                    || inBetween(line.startx, x - (SPECIALWIDTH / 2), line.endx)
                    || inBetween(x - (SPECIALWIDTH / 2), line.startx, x + (SPECIALWIDTH / 2)));
        } else {
            return Math.abs(line.startx - x) <= (SPECIALWIDTH / 2)
                    && (inBetween(line.starty, y + (SPECIALWIDTH / 2), line.endy)
                    || inBetween(line.starty, y - (SPECIALWIDTH / 2), line.endy)
                    || inBetween(y - (SPECIALWIDTH / 2), line.starty, y + (SPECIALWIDTH / 2)));
        }
    }

    boolean bigPointTest(double x1, double y1) {
        double x2 = Math.abs(x1 - x);
        double y2 = Math.abs(y1 - y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        return distance < SPECIALWIDTH * 2;
    }

    boolean smallPointTest(double x1, double y1) {
        double x2 = Math.abs(x1 - x);
        double y2 = Math.abs(y1 - y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        return distance < (SPECIALWIDTH / 2);
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



