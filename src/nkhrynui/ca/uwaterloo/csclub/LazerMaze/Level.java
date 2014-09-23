package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.graphics.Canvas;

public class Level {
    int color; //this is background colour
    int num = 1;
    boolean exit = true;
    boolean recover = false;
    boolean restart = false;
    boolean inPrefs = false;
    int score = 100;
    int skipCost = 100;

    void draw(Canvas c) {
       c.drawColor(color);
    }


    void skip() {
        num++;
        score -= skipCost;
        skipCost += 100;
        restart = false;
        if (num == 1) score = 100;
    }

    void reset() {
        num = 1;
        score = 100;
        restart = false;
        skipCost = 100;
    }
}
