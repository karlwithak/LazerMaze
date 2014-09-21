package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

public class Utils {
    static boolean inBetween(double left, double center, double right) {
        return (left <= center && center <= right)
                || (left >= center && center >= right);
    }

    static boolean inBetweenStrict(double left, double center, double right) {
        return (left < center && center < right)
                || (left > center && center > right);
    }

    static int randomBetween(double low, double high) {
        double ran = Math.random();
        return (int) (low + (ran * (high - low)));
    }

    static float[] ALtoArray(ArrayList<Float> pts) {
        int i = 0;
        float[] f2 = new float[pts.size()];
        for (float f: pts) {
            f2[i] = f;
            i++;
        }
        return f2;
    }
}
