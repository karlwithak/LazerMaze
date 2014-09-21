package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

public class ColorHandler {
    MainActivity ma;
    Grid grid;
    Laser laser;
    Level level;
    SharedPreferences sharedPrefs;
    Color c;

    public ColorHandler(MainActivity mainActivity) {
        ma = mainActivity;
        grid = ma.grid;
        laser = ma.laser;
        level = ma.level;
        sharedPrefs = ma.sharedPrefs;
    }

    public void update(Object obj) {
        sharedPrefs = ma.sharedPrefs;
        grid = ma.grid;
        laser = ma.laser;
        level = ma.level;
        String color;
        if (obj.equals(grid)) {
            color = sharedPrefs.getString("pref_lineColor", "white");
            if (color.equalsIgnoreCase("random")) color = randomHexColor(level.color, 0);
            grid.setColor(Color.parseColor(color));
        }
        else if (obj.equals(laser)) {
            color = sharedPrefs.getString("pref_laserColor", "random");
            if (color.equalsIgnoreCase("random")) color = randomHexColor(level.color, 0);
            laser.setColor(Color.parseColor(color));
        }
        else if (obj.equals(level)) {
            color = sharedPrefs.getString("pref_bgColor", "random");
            Log.i("test",Integer.toString(grid.color));
            if (color.equalsIgnoreCase("random")) color = randomHexColor(laser.color, grid.color);
            level.color = Color.parseColor(color);
        }
    }


    String randomHexColor(int c, int d) {
        String j = "";
        String k = "";
        if (c != 0) j = Integer.toHexString(c);
        if (d != 0) k = Integer.toHexString(d);
        String s = "#ff";
        int difference = 0;
        int difference2 = 0;
        int temp;
        for (int i = 0; i < 6; i++) {
            temp = ma.randomBetween(0,16);
            s+=Integer.toHexString(temp);
            if (c != 0) difference += Math.abs((temp - (Integer.parseInt(j.substring(i+2, i+3), 16))) * (i%2==0? 16 :1));
            if (d != 0) difference2 += Math.abs((temp - (Integer.parseInt(k.substring(i+2, i+3), 16))) * (i%2==0? 16 :1));
        }
        if ((c == 0 || difference > 300) && (d == 0 || difference2 > 300)) return s;
        return randomHexColor(c, d);
    }
}
