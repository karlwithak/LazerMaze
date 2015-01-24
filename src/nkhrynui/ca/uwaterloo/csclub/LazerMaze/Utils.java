package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import java.util.ArrayList;

public class Utils {
    public static boolean inBetween(double left, double center, double right) {
        return (left <= center && center <= right)
                || (left >= center && center >= right);
    }

    public static boolean inBetweenStrict(double left, double center, double right) {
        return (left < center && center < right)
                || (left > center && center > right);
    }

    public static int randomBetween(double low, double high) {
        double ran = Math.random();
        return (int) (low + (ran * (high - low)));
    }

    public static float[] ALtoArray(ArrayList<Float> pts) {
        int i = 0;
        float[] f2 = new float[pts.size()];
        for (float f: pts) {
            f2[i] = f;
            i++;
        }
        return f2;
    }

    public static int differentRandomBetween(double low, double high, int other) {
        int x = randomBetween(low, high);
        while (x == other) x = randomBetween(low, high);
        return x;
    }
}
