package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.K;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;


public class Grid {
    ArrayList<Line> lines;
    Paint paint;
    int color = 0;

    boolean recovery;
    Grid() {
        recovery = false;
        lines = new ArrayList<Line>();
        paint = new Paint();
        paint.setStrokeWidth((float) Math.ceil(K.LINE_SPACING / 7) + 1);
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
    
    void makeGrid(Powerups powerup, Level level) {
        double linesAdjust = 1;
        if (powerup == Powerups.LESS_LINES) linesAdjust = 0.666;
        double lengthAdjust = 1;
        if (powerup == Powerups.SHORT_LINES) lengthAdjust = 0.666;
        lines.clear();
        lines.add(new Line(-1, K.NAV_HEIGHT, K.SCREEN_WIDTH, K.LINE_SPACING * 3)); //top
        lines.add(new Line(-1, K.SCREEN_HEIGHT - K.NAV_HEIGHT, K.SCREEN_WIDTH + 1, K.SCREEN_HEIGHT - K.NAV_HEIGHT)); //bottom
        lines.add(new Line(1, K.NAV_HEIGHT, 1, K.SCREEN_HEIGHT - K.NAV_HEIGHT));                        //left
        lines.add(new Line(K.SCREEN_WIDTH - 1, K.NAV_HEIGHT, K.SCREEN_WIDTH - 1, K.SCREEN_HEIGHT - K.NAV_HEIGHT));    //right
        lines.get(0).addIntersections(lines.get(1));
        lines.get(0).addIntersections(lines.get(2));
        lines.get(1).addIntersections(lines.get(3));
        lines.get(3).addIntersections(lines.get(2));
        int a, b, length;
        for (int i = 0; i < (5 + (2 * Math.sqrt(level.num))) * linesAdjust && level.num != 0; i++) {
            //creates a new line and makes sure that it does not create an enclosed space(conflict)
            a = randomBetween(1, (K.SCREEN_WIDTH / K.LINE_SPACING) - 1) * K.LINE_SPACING;
            b = randomBetween(3, ((K.SCREEN_HEIGHT - K.NAV_HEIGHT) / K.LINE_SPACING) - 1) * K.LINE_SPACING;
            if (a / 10 % 2 == 1) {
                length = (int) (Math.ceil(randomBetween(-K.SCREEN_HEIGHT / K.LINE_SPACING / 2,
                        K.SCREEN_HEIGHT / K.LINE_SPACING / 2) * lengthAdjust) * K.LINE_SPACING);
                lines.add(new Line(a, b, a + length, b));
            } else {
                length = (int) (Math.ceil(randomBetween(-(K.SCREEN_HEIGHT - K.NAV_HEIGHT) / K.LINE_SPACING / 2,
                        (K.SCREEN_HEIGHT - K.NAV_HEIGHT) / K.LINE_SPACING / 2) * lengthAdjust) * K.LINE_SPACING);
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
