package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.Utils.*;

public enum Special{
    TARGET(R.drawable.newtarget),
    TARGET2(R.drawable.newtarget),
    LAUNCHER(R.drawable.shoot),
    LAUNCHER2(R.drawable.shoot);

    Bitmap m_bitmap;
    boolean m_active = false;
    int m_y, m_x, m_size;

    Special(int b) {
        m_bitmap = BitmapFactory.decodeResource(MainPanel.m_mp.m_resources, b);
    }

    public void update(boolean isLauncher, Grid grid, boolean isLarge) {
        m_size = (isLarge ? K.SPECIAL_WIDTH : K.SPECIAL_WIDTH / 2);
        setXY();
        ArrayList<Line> lines = grid.getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lineTest(lines.get(i)) || (isLauncher && tooEasy(TARGET, lines))) {
                setXY();
                i = -1;
            }
        }
    }

    public void update2(boolean isLauncher, Grid grid) {
        ArrayList<Line> lines = grid.getLines();
        setXY();
        for (int i = 0; i < lines.size(); i++) {
            if (lineTest(lines.get(i))
                    || (isLauncher && tooEasy(TARGET, lines))
                    || (isLauncher && bigPointTest(LAUNCHER.m_x, LAUNCHER.m_y))
                    || (!isLauncher && bigPointTest(TARGET.m_x, TARGET.m_y))
                    || (!isLauncher && LAUNCHER.tooEasy(this, lines)))
            {
                setXY();
                i = -1;
            }
        }
    }

    boolean lineTest(Line line) {
        if (line.horizontal) {
            return Math.abs(line.starty - m_y) <= m_size
                    && (inBetween(line.startx, m_x + m_size, line.endx)
                    || inBetween(line.startx, m_x - m_size, line.endx)
                    || inBetween(m_x - m_size, line.startx, m_x + (K.SPECIAL_WIDTH / 2)));
        } else {
            return Math.abs(line.startx - m_x) <= m_size
                    && (inBetween(line.starty, m_y + m_size, line.endy)
                    || inBetween(line.starty, m_y - m_size, line.endy)
                    || inBetween(m_y - m_size, line.starty, m_y + (K.SPECIAL_WIDTH / 2)));
        }
    }

    boolean bigPointTest(double x1, double y1) {
        double x2 = Math.abs(x1 - m_x);
        double y2 = Math.abs(y1 - m_y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        return distance < K.SPECIAL_WIDTH * 2;
    }

    boolean smallPointTest(double x1, double y1) {
        double x2 = Math.abs(x1 - m_x);
        double y2 = Math.abs(y1 - m_y);
        double distance = Math.sqrt((x2 * x2) + (y2 * y2));
        return distance < m_size;

    }

    void draw(Canvas canvas) {
        Rect rect = new Rect(m_x - m_size, m_y - m_size, m_x + m_size, m_y + m_size);
        canvas.drawBitmap(m_bitmap, null, rect, null);

    }

    private boolean tooEasy(Special target, ArrayList<Line> lines) {
        for (Line line : lines) {
            if (line.crossed(this.m_x, this.m_y, target.m_x, target.m_y) > 0) {
                return false;
            }
        }
        return true;
    }

    private void setXY() {
        m_y = randomBetween(K.NAV_HEIGHT + K.SPECIAL_WIDTH, K.SCREEN_HEIGHT - (K.NAV_HEIGHT + K.SPECIAL_WIDTH));
        m_x = randomBetween(K.SPECIAL_WIDTH, K.SCREEN_WIDTH - (K.SPECIAL_WIDTH));
    }
}



