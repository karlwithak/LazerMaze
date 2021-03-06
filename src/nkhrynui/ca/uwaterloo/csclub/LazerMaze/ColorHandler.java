package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.content.SharedPreferences;
import android.graphics.Color;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.Utils.*;

public class ColorHandler {
    SharedPreferences m_sharedPrefs;
    Level m_level;
    Grid m_grid;
    Laser m_laser;
    public void update(SharedPreferences sharedPrefs, Level level, Grid grid, Laser laser) {
        m_sharedPrefs = sharedPrefs;
        m_level = level;
        m_grid = grid;
        m_laser = laser;
        updateGrid();
        updateLaser();
        updateLevel();
    }

    private void updateGrid() {
        String color = m_sharedPrefs.getString("pref_lineColor", "white");
        if (color.equalsIgnoreCase("random")) color = randomHexColor(m_level.color, 0);
        m_grid.setColor(Color.parseColor(color));
    }
    private void updateLaser() {
        String color = m_sharedPrefs.getString("pref_laserColor", "random");
        if (color.equalsIgnoreCase("random")) color = randomHexColor(m_level.color, 0);
        m_laser.setColor(Color.parseColor(color));
    }
    private void updateLevel() {
        String color = m_sharedPrefs.getString("pref_bgColor", "random");
        if (color.equalsIgnoreCase("random")) color = randomHexColor(m_laser.color, m_grid.color);
        m_level.color = Color.parseColor(color);
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
