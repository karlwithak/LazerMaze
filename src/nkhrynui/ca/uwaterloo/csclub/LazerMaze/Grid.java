package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;


public class Grid {
    ArrayList<Line> lines;
    Paint paint;
    int color = 0;
    final static int SCREEN_WIDTH = MainActivity.SCREEN_WIDTH;
    final static int SCREEN_HEIGHT = MainActivity.SCREEN_HEIGHT;
    final static int NAV_HEIGHT = MainActivity.NAV_HEIGHT;
    final static int LINE_SPACING = MainActivity.LINE_SPACING;
    boolean recovery;
    Grid() {
        recovery = false;
        lines = new ArrayList<Line>();
        paint = new Paint();
        paint.setStrokeWidth((float) Math.ceil(LINE_SPACING / 7) + 1);
    }

    boolean overlapping(ArrayList<Line> lines) {
        Line newest = lines.get(lines.size() - 1);
        for (Line line : lines) {
            if (line != newest && (line.overlapped(newest) || newest.overlapped(line))) {
                return true;
            }
        }
        return false;
    }
     
    boolean boxedIn(ArrayList<Line> lines) {
        Line newest = lines.get(lines.size() - 1);
        for (Line line : lines) {
            if (line != newest && line.crossed(newest) && newest.crossed(line)) {
                newest.addIntersections(line);
                line.addIntersections(newest);
            }
        }

        if (boxedIn2(newest, null, null, newest.getIntersection())) {
            for (Line line : lines) {
                line.removeIntersections(newest);
            }
            return true;
        }
        return false;
    }
     
    boolean boxedIn2(Line newest, Line origin, Line ancestor, ArrayList<Line> checking) {
        if (checking.size() < 2) return false;
        for (Line current: checking) {
            if (current != ancestor) {
               if (newest == current) return true;
               if (boxedIn2(newest, current, origin, current.getIntersection())) return true;
            }
         }
        return false;
     }
    
    ArrayList<Line> getLines() {
        return lines;
    }        
    
    void makeGrid() {
        double linesAdjust = 1;
        if (MainActivity.g_powerup == Powerups.LESS_LINES) linesAdjust = 0.666;
        double lengthAdjust = 1;
        if (MainActivity.g_powerup == Powerups.SHORT_LINES) lengthAdjust = 0.666;
        lines.clear();
        lines.add(new Line(-1, NAV_HEIGHT, SCREEN_WIDTH, LINE_SPACING * 3)); //top
        lines.add(new Line(-1, SCREEN_HEIGHT - NAV_HEIGHT, SCREEN_WIDTH + 1, SCREEN_HEIGHT - NAV_HEIGHT)); //bottom
        lines.add(new Line(1, NAV_HEIGHT, 1, SCREEN_HEIGHT - NAV_HEIGHT));                        //left
        lines.add(new Line(SCREEN_WIDTH - 1, NAV_HEIGHT, SCREEN_WIDTH - 1, SCREEN_HEIGHT - NAV_HEIGHT));    //right
        lines.get(0).addIntersections(lines.get(1));
        lines.get(0).addIntersections(lines.get(2));
        lines.get(1).addIntersections(lines.get(3));
        lines.get(3).addIntersections(lines.get(2));
        int a, b, length;
        for (int i = 0; i < (5 + (2 * Math.sqrt(MainActivity.g_level.num))) * linesAdjust && MainActivity.g_level.num != 0; i++) {
            //creates a new line and makes sure that it does not create an enclosed space(conflict)
            a = randomBetween(1, (SCREEN_WIDTH / LINE_SPACING) - 1) * LINE_SPACING;
            b = randomBetween(3, ((SCREEN_HEIGHT - NAV_HEIGHT) / LINE_SPACING) - 1) * LINE_SPACING;
            if (a / 10 % 2 == 1) {
                length = (int) (Math.ceil(randomBetween(-SCREEN_HEIGHT / LINE_SPACING / 2,
                        SCREEN_HEIGHT / LINE_SPACING / 2) * lengthAdjust) * LINE_SPACING);
                lines.add(new Line(a, b, a + length, b));
            } else {
                length = (int) (Math.ceil(randomBetween(-(SCREEN_HEIGHT - NAV_HEIGHT) / LINE_SPACING / 2,
                        (SCREEN_HEIGHT - NAV_HEIGHT) / LINE_SPACING / 2) * lengthAdjust) * LINE_SPACING);
                lines.add(new Line(a, b, a, b + length));
            }

            if (overlapping(lines) || boxedIn(lines)) {
                lines.remove(lines.size() - 1);
                i--;
            }
        }
    }

    void draw(Canvas canvas) {
        for (Line line : lines) {
            line.draw(canvas, paint);
        }
    }
    
    void expandDraw(Canvas canvas) {
        for (Line line : lines) {
            line.expandDraw(canvas, paint);
        }
    }
    
    void setColor(int c) {
        paint.setColor(c);
        color = c;
    }
}
