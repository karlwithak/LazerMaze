package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;

public class Special{
    Bitmap bitmap;
    final static int SPECIALWIDTH = MainActivity.SPECIALWIDTH;
    final static int SCREENWIDTH = MainActivity.SCREENWIDTH;
    final static int SCREENHEIGHT = MainActivity.SCREENHEIGHT;
    final static int NAVHEIGHT = MainActivity.NAVHEIGHT;
    boolean active = false;
    int y;
    int x;
    Rect r;

    void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, r, null);
    }

    Special(Bitmap b) {
        bitmap =  b;
        y = randomBetween(NAVHEIGHT + SPECIALWIDTH, SCREENHEIGHT - (NAVHEIGHT + SPECIALWIDTH));
        x = randomBetween(SPECIALWIDTH, SCREENWIDTH - (SPECIALWIDTH));
        r = new Rect(x - (SPECIALWIDTH / 2),
                    y - (SPECIALWIDTH / 2),
                    x + (SPECIALWIDTH / 2),
                    y + (SPECIALWIDTH / 2));
    }

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
}

    /******************************* SPECIAL  - END ***********************************************/
    /******************************* TARGET  - START **********************************************/
  
class Target extends Special{
    Target(Bitmap b) {
        super(b);
        // TODO Auto-generated constructor stub
    }
}
    /******************************* TARGET  - END ************************************************/
    /******************************* LAUNCHER  - START ********************************************/
class Launcher extends Special{
    Launcher(Bitmap b) {
        super(b);
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



