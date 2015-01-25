package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

public class K {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static int NAV_HEIGHT;
    public static int LINE_SPACING;
    public static int SPECIAL_WIDTH;
    public static int SPEED;

    public static void init(Panel panel) {
        SCREEN_WIDTH = panel.getWidth();
        SCREEN_HEIGHT = panel.getHeight();
        LINE_SPACING = SCREEN_HEIGHT / 39;
        NAV_HEIGHT = LINE_SPACING * 3;
        SPECIAL_WIDTH = SCREEN_HEIGHT / 20;
        SPEED = SCREEN_WIDTH / 50;
    }
}
