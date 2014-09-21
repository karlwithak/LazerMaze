package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;


public class Grid {
    ArrayList<Line> lines;
    Paint paint;
    int color = 0;
    final static int SCREENWIDTH = MainActivity.SCREENWIDTH;
    final static int SCREENHEIGHT = MainActivity.SCREENHEIGHT;
    final static int NAVHEIGHT = MainActivity.NAVHEIGHT;
    final static int LINESPACING = MainActivity.LINESPACING;
    boolean recovery;
    Grid() {
        recovery = false;
        lines = new ArrayList<Line>();
        paint = new Paint();
        paint.setStrokeWidth((float) Math.ceil(LINESPACING / 7) + 1);
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
        if (MainActivity.powerups.getActive().equals("lessLines")) linesAdjust = 0.666;
        double lengthAdjust = 1;
        if (MainActivity.powerups.getActive().equals("shortLines")) lengthAdjust = 0.666;
        lines.clear();
        lines.add(new Line(-1, NAVHEIGHT, SCREENWIDTH, LINESPACING * 3)); //top
        lines.add(new Line(-1, SCREENHEIGHT - NAVHEIGHT, SCREENWIDTH + 1, SCREENHEIGHT - NAVHEIGHT)); //bottom
        lines.add(new Line(1, NAVHEIGHT, 1, SCREENHEIGHT - NAVHEIGHT));                        //left
        lines.add(new Line(SCREENWIDTH - 1, NAVHEIGHT, SCREENWIDTH - 1, SCREENHEIGHT - NAVHEIGHT));    //right
        lines.get(0).addIntersections(lines.get(1));
        lines.get(0).addIntersections(lines.get(2));
        lines.get(1).addIntersections(lines.get(3));
        lines.get(3).addIntersections(lines.get(2));
        int a, b, length;
        for (int i = 0; i < (5 + (2 * Math.sqrt(MainActivity.level.num))) * linesAdjust && MainActivity.level.num != 0; i++) {
            //creates a new line and makes sure that it does not create an enclosed space(conflict)
            a = randomBetween(1, (SCREENWIDTH / LINESPACING) - 1) * LINESPACING;
            b = randomBetween(3, ((SCREENHEIGHT - NAVHEIGHT) / LINESPACING) - 1) * LINESPACING;
            if (a / 10 % 2 == 1) {
                length = (int) (Math.ceil(randomBetween(-SCREENHEIGHT / LINESPACING / 2,
                        SCREENHEIGHT / LINESPACING / 2) * lengthAdjust) * LINESPACING);
                lines.add(new Line(a, b, a + length, b));
            } else {
                length = (int) (Math.ceil(randomBetween(-(SCREENHEIGHT - NAVHEIGHT) / LINESPACING / 2,
                        (SCREENHEIGHT - NAVHEIGHT) / LINESPACING / 2) * lengthAdjust) * LINESPACING);
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
