package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.os.Vibrator;
import android.util.Log;

import nkhrynui.ca.uwaterloo.csclub.LazerMaze.PowerupManager.Powerup;

public class Physics {
    private MainThread m_thread;
    private PowerupManager m_powerupMan;
    private Grid m_grid;
    private Level m_level;
    private Special m_target, m_target2;
    private Vibrator m_vib;
    private Laser m_laser;
    private MainActivity m_mainActivity;

    Physics(PowerupManager powerupMan, Grid grid, Level level, Vibrator vib, MainActivity mainActivity) {
        m_powerupMan = powerupMan;
        m_grid = grid;
        m_level = level;
        m_vib = vib;
        m_mainActivity = mainActivity;
    }

    public void setup(Laser laser, Special target, Special target2, MainThread thread) {
        m_thread = thread;
        m_laser = laser;
        m_target = target;
        m_target2 = target2;
    }

    public void update() {
        final int SCREEN_WIDTH = K.SCREEN_WIDTH;
        final int SCREEN_HEIGHT = K.SCREEN_HEIGHT;
        final int NAV_HEIGHT = K.NAV_HEIGHT;
        GraphicObject.Coordinates coord;
        GraphicObject.Speed speed;
        coord = m_laser.GO.coordinates;
        speed = m_laser.GO.speed;
        if (m_target.smallPointTest(coord.x, coord.y, m_powerupMan)
                || (m_powerupMan.get() == Powerup.TWO_TARGETS && m_target2.smallPointTest(coord.x, coord.y, m_powerupMan))) {
            m_level.num++;
            m_level.score+=100;
            m_thread.setRunning(false);
            m_thread.selection = "next";
            m_level.restart = false;
            if (m_level.num == 1) {
                m_level.score = 100;
            }
        }

        coord.setX(coord.x + speed.x);
        coord.setY(coord.y + speed.y);
        Line line;
        boolean ignoring = false;
        boolean doubleHit = true;
        for (int i = 0; i < m_grid.getLines().size(); i++) {
            line = m_grid.getLines().get(i);
            if ((coord.lastx != -1  && coord.lasty != -1)
                    && line.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0)
            {
                if (m_powerupMan.get() == Powerup.THROUGH_FIRST_LINE && m_laser.pts.size() == 4) {
                    ignoring = true;
                    m_laser.bounce();
                    continue;
                }
                if (m_level.score < 1) {
                    m_mainActivity.endGameDialog(m_level.num);
                    m_level.reset();
                    m_thread.setRunning(false);
                    m_thread.selection = "next";
                    break;
                }
                if (m_powerupMan.get() == Powerup.WRAP_AROUND_ENDS
                        && m_grid.getLines().indexOf(line) <= 1) {
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    m_laser.bounce();
                    if (line.starty < NAV_HEIGHT + 2) {
                        m_laser.starty = SCREEN_HEIGHT - NAV_HEIGHT - 2;
                        coord.setY(SCREEN_HEIGHT - NAV_HEIGHT - 2);
                        coord.lasty = (SCREEN_HEIGHT - NAV_HEIGHT - 1);
                    } else if (line.starty > SCREEN_HEIGHT - NAV_HEIGHT - 2) {
                        m_laser.starty = NAV_HEIGHT + 2;
                        coord.setY(NAV_HEIGHT + 2);
                        coord.lasty = NAV_HEIGHT + 1;
                    }
                    if (coord.x <= speed.x) {
                        coord.x = 2;
                        speed.toggleXDirection();
                        doubleHit = false;
                        soundAndVib();
                    } else if (coord.x >= SCREEN_WIDTH - speed.x) {
                        coord.x = SCREEN_WIDTH - 2;
                        speed.toggleXDirection();
                        doubleHit = false;
                        soundAndVib();
                    }
                    m_laser.startx = coord.x;
                    coord.lastx = coord.x;
                    m_laser.bounce();
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    continue;
                }

                if (m_powerupMan.get() == Powerup.WRAP_AROUND_SIDES
                        && (m_grid.getLines().indexOf(line) == 2
                                || m_grid.getLines().indexOf(line) == 3)) {
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    m_laser.bounce();
                    if (line.startx < 2) {
                        m_laser.startx = SCREEN_WIDTH - 2;
                        coord.setX(SCREEN_WIDTH - 1);
                        coord.lastx = SCREEN_WIDTH - 2;
                    } else if (line.startx > SCREEN_WIDTH - 2) {
                        m_laser.startx = 2;
                        coord.setX(2);
                        coord.lastx = 1;
                    }

                    if (coord.y <= NAV_HEIGHT - speed.y) {
                        coord.y = NAV_HEIGHT + 2;
                        speed.toggleYDirection();
                        doubleHit = false;
                        soundAndVib();
                    } else if ( coord.y >= SCREEN_HEIGHT - NAV_HEIGHT - speed.y) {
                        coord.y =  SCREEN_HEIGHT - NAV_HEIGHT - 2;
                        speed.toggleYDirection();
                        doubleHit = false;
                        soundAndVib();
                    }
                    m_laser.starty = coord.y;
                    coord.lasty = coord.x;
                    m_laser.bounce();
                    coord.setX(coord.x + speed.x);
                    coord.setY(coord.y + speed.y);
                    continue;
                }

                soundAndVib();
                coord.x =(coord.x - speed.x);
                coord.y =(coord.y - speed.y);
                float change;

                if (line.horizontal) {
                    speed.toggleYDirection();
                    change = Math.abs((coord.y - line.starty) / speed.y);
                    coord.y = (line.starty);
                    coord.setX(coord.x+ (change * speed.x));
                    for (Line line2 : m_grid.getLines()) {
                        if (!ignoring
                                && line2 != line
                                && line2.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0
                                && doubleHit)
                        {
                            Log.i("graphics", "double hit");
                            speed.toggleXDirection();
                            coord.x = (line2.endx);
                            coord.y =(line.endy);
                            m_laser.bounce();
                            coord.x =(coord.x + speed.x);
                            coord.y =(coord.y + speed.y);
                            if (m_level.score >0) m_level.score--;
                        }
                    }
                } else {
                    speed.toggleXDirection();
                    change = Math.abs((coord.x - line.startx) / speed.x);
                    coord.x = (line.startx);
                    coord.y = (coord.y+ (change * speed.y));
                    for (Line line2 : m_grid.getLines()) {
                        if (!ignoring && line2 != line
                                && line2.crossed(coord.x, coord.y, coord.lastx, coord.lasty) > 0
                                && doubleHit)
                        {
                            Log.i("graphics", "double hit");
                            speed.toggleYDirection();
                            coord.x = (line.endx);
                            coord.y = (line2.endy);
                            m_laser.bounce();
                            coord.x = (coord.x + speed.x);
                            coord.y = (coord.y + speed.y);
                            if (m_level.score >0) m_level.score--;
                        }
                    }
                }
                m_laser.bounce();
                break;
            }
        }
    }

    private void soundAndVib() {
        if (m_vib != null) m_vib.vibrate(10);
        if (m_level.score > 0) m_level.score--;
    }
}
