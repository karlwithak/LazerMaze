package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.graphics.Color;
import android.util.Log;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.Utils.*;

public class ColorHandler {
    public void update(Object obj) {
        String color;
        if (obj.equals(MainActivity.grid)) {
            color = MainActivity.sharedPrefs.getString("pref_lineColor", "white");
            if (color.equalsIgnoreCase("random")) color = randomHexColor(MainActivity.level.color, 0);
            MainActivity.grid.setColor(Color.parseColor(color));
        } else if (obj.equals(MainActivity.laser)) {
            color = MainActivity.sharedPrefs.getString("pref_laserColor", "random");
            if (color.equalsIgnoreCase("random")) color = randomHexColor(MainActivity.level.color, 0);
            MainActivity.laser.setColor(Color.parseColor(color));
        } else if (obj.equals(MainActivity.level)) {
            color = MainActivity.sharedPrefs.getString("pref_bgColor", "random");
            Log.i("test", Integer.toString(MainActivity.grid.color));
            if (color.equalsIgnoreCase("random")) color = randomHexColor(MainActivity.laser.color, MainActivity.grid.color);
            MainActivity.level.color = Color.parseColor(color);
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
            temp = randomBetween(0, 16);
            s += Integer.toHexString(temp);
            if (c != 0) {
                difference += Math.abs((temp - (Integer.parseInt(j.substring(i + 2, i + 3), 16)))
                        * (i%2==0? 16 :1));
            }
            if (d != 0) {
                difference2 += Math.abs((temp - (Integer.parseInt(k.substring(i + 2, i + 3), 16)))
                        * (i%2==0? 16 :1));
            }
        }
        if ((c == 0 || difference > 300) && (d == 0 || difference2 > 300)) return s;
        return randomHexColor(c, d);
    }
}
