package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;

public class Laser {
    GraphicObject GO;
    //int bounces;
    Paint paint = new Paint();
    int oldLasers;
    public int color = 0;
    float startx, starty;
    int oldSave = 0;
    int SCREENHEIGHT;
    int SCREENWIDTH;
    int NAVHEIGHT;
    int LINESPACING;
    MainActivity ma;
    SharedPreferences sharedPrefs;
    
    ArrayList<Float> pts;// = new ArrayList<Line>();
    Laser(MainActivity mainActivity) {
        ma = mainActivity;
        SCREENHEIGHT = ma.SCREENHEIGHT;
        SCREENWIDTH = ma.SCREENWIDTH;
        NAVHEIGHT = ma.NAVHEIGHT;
        LINESPACING = ma.LINESPACING;
        sharedPrefs = ma.sharedPrefs;
        pts = new  ArrayList<Float>();
        startx = -10;
        starty = -10;
        GO = new GraphicObject(ma.SPEED);
        paint.setStrokeWidth((float) Math.floor(LINESPACING / 7));
        oldLasers = 0;
    }

    void draw(Canvas canvas) {
        if (pts.size() > 2) {
             canvas.drawLine(startx, starty, GO.coordinates.x, GO.coordinates.y, paint);
             if (pts.size() >= 4) canvas.drawLines(ALtoArray(pts), paint);
        }
    }

    void reset(Launcher launcher) {
        pts.clear();
        Log.i("powerup", Integer.toString(launcher.x));
        pts.add((float) launcher.x);
        pts.add((float) launcher.y);
        pts.add((float) launcher.x);
        pts.add((float) launcher.y);
        oldSave = 0;
        startx = launcher.x;
        starty = launcher.y;
    }

    void bounce() {
        pts.add(startx);
        pts.add(starty);
        startx = GO.coordinates.x;
        starty = GO.coordinates.y;
        pts.add(startx);
        pts.add(starty);
    }

    void nextLevel() {
        GO = new GraphicObject(ma.SPEED);
        pts.clear();
    }

    void setColor(int c) {
        paint.setColor(c);
        color = c;
    }
}


