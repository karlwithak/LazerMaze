package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.Utils.*;

public class Line {
    public float startx;
    public float starty;
    public float endx;
    public float endy;
    private float temp2;
    public float temp1 = temp2 = 0;
    public boolean horizontal;
    public ArrayList<Line> intersections = new ArrayList<Line>();

    Line(int x1, int y1, int x2, int y2) {
        startx = x1;
        starty = y1;
        endx = x2;
        endy = y2;
        if (x2 == x1) {
            horizontal = false;
            temp1 = temp2 = (y2 > y1? y1 + (y2 - y1) / 2: y2 + (y1 - y2) / 2);
        } else if (y1 == y2) {
            horizontal = true;
            temp1 = temp2 = (x2 > x1? x1 + (x2 - x1) / 2: x2 + (x1 - x2) / 2);
        }
    }

    void addIntersections(Line _line) {
        intersections.add(_line);
    }

    ArrayList<Line> getIntersection() {
        return intersections;
    }

    void removeIntersections(Line _line) {
        intersections.remove(_line);
    }

    boolean overlapped(Line line) {
        if (this.horizontal != line.horizontal) return false;
        if (this.horizontal && this.starty != line.starty) return false;
        if (!this.horizontal && this.startx != line.startx) return false;
        return this.horizontal
                && (inBetween(line.startx, this.startx, line.endx)
                    || inBetween(line.startx, this.endx, line.endx)
                    || inBetween(this.endx, line.startx, this.startx)) ||
                !this.horizontal
                && (inBetween(line.starty, this.starty, line.endy)
                    || inBetween(line.starty, this.endy, line.endy)
                    || inBetween(this.endy, line.starty, this.starty));
    }

    float crossed(float firstLaserx, float firstLasery, float lastLaserx, float lastLasery) {
        //covers the case that the line is perfectly horizontal or vertical
        //also used when checking intersection between lines when building maze
        if (lastLaserx - firstLaserx == 0 || lastLasery - firstLasery == 0 ) {
             return -1;
        }
        float intersection = findIntersection(firstLaserx,  firstLasery,  lastLaserx,  lastLasery);
        //basically finds the equation for the laser line, then checks to see if that laser line
        // intersects this line between the start and end points of both lines
        if (horizontal) {
            if (inBetweenStrict(startx, intersection, endx)
                    && (inBetweenStrict(lastLasery, starty, firstLasery)
                        || starty == firstLasery))
            {
                return intersection;
            }
        } else {
            if (inBetweenStrict(starty, intersection, endy)
                    && (inBetweenStrict(lastLaserx, startx, firstLaserx)
                        || startx == firstLaserx))
            {
                return intersection;
            }
        }
        return -1;
    }

    boolean crossed(Line newLine) {
        float firstLaserx = newLine.startx;
        float firstLasery = newLine.starty;
        float lastLaserx = newLine.endx;
        float lastLasery = newLine.endy;
        //covers the case that the line is perfectly horizontal or vertical
        //also used when checking intersection between lines when building maze
        if (lastLasery - firstLasery == 0 ) {
            if (horizontal) return false;
            if (inBetween(firstLaserx, startx, lastLaserx)) return true;
        }
        if (lastLaserx - firstLaserx == 0) {
            if (!horizontal) return false;
            if (inBetween(firstLasery, starty, lastLasery)) return true;
        }
        return false;
    }

    void draw(Canvas canvas, Paint paint) {
        canvas.drawLine(startx, starty, endx, endy, paint);
    }

    void shrink(int spacing) {
        float change;
        if (horizontal) {
            if ((change = Math.abs(startx - endx)) < 10) startx = endx;
            else if (startx > endx) {
                startx -= (change / spacing) + 3;
                endx += (change / spacing) + 3;
            } else {
                startx += (change / spacing) + 3;
                endx -= (change / spacing) + 3;
            }
        } else {
            if ((change = Math.abs(starty - endy)) < 10) starty = endy;
            else if (starty > endy) {
                starty -= (change / spacing) + 5;
                endy += (change / spacing) + 5;
            } else {
                starty += (change / spacing) + 5;
                endy -= (change / spacing) + 5;
            }
        }
    }

    void expand(int spacing) {
        float change;
        float current = temp2 - temp1;
        if (horizontal) change = Math.abs(startx - endx);
        else change = Math.abs(starty - endy);
        if (change - current > 2) {
            temp1 -= ((change - current) / spacing) + 3;
            temp2 += ((change - current) / spacing) + 3;
        }
    }

    void expandDraw(Canvas canvas, Paint paint) {
        if (horizontal) canvas.drawLine(temp1, starty, temp2, endy, paint);
        else canvas.drawLine(startx, temp1, endx, temp2, paint);
    }
    
    float findIntersection(float firstLaserx, float firstLasery, float lastLaserx, float lastLasery) {
        float m = (lastLasery - firstLasery) / (lastLaserx - firstLaserx);
        float b = firstLasery - (m * firstLaserx);
        if (horizontal) {
            return (starty - b) / m;
        } else {
            return (m * startx) + b;
        }
    }
}
