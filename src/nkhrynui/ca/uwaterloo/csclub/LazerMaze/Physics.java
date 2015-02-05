package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.util.Log;

import nkhrynui.ca.uwaterloo.csclub.LazerMaze.PowerupManager.Powerup;

public class Physics {
    private MainThread m_mt;
    private MainPanel m_mp;
    private MainActivity m_ma;
    private GraphicObject.Coordinates m_coord;
    private GraphicObject.Speed m_speed;

    Physics(MainActivity mainActivity) {
        m_ma = mainActivity;
    }

    public void setup(MainPanel mainPanel, MainThread thread) {
        m_mt = thread;
        m_mp = mainPanel;
    }

    public void update() {
        m_coord = m_mp.m_laser.GO.coordinates;
        m_speed = m_mp.m_laser.GO.speed;
        if (m_mp.m_target.smallPointTest(m_coord.x, m_coord.y)
                || (m_ma.m_powerupMan.get() == Powerup.TWO_TARGETS &&
                    m_mp.m_target2.smallPointTest(m_coord.x, m_coord.y))) {
            m_ma.m_level.num++;
            m_ma.m_level.score+=100;
            m_mt.setRunning(false);
            m_mt.selection = "next";
            m_ma.m_level.restart = false;
            if (m_ma.m_level.num == 1) {
                m_ma.m_level.score = 100;
            }
        }

        m_coord.setX(m_coord.x + m_speed.x);
        m_coord.setY(m_coord.y + m_speed.y);
        Line line;
        boolean ignoring = false;
        boolean doubleHit = true;
        for (int i = 0; i < m_ma.m_grid.getLines().size(); i++) {
            line = m_ma.m_grid.getLines().get(i);
            if ((m_coord.lastx != -1  && m_coord.lasty != -1)
                    && line.crossed(m_coord.x, m_coord.y, m_coord.lastx, m_coord.lasty) > 0)
            {
                if (m_ma.m_powerupMan.get() == Powerup.THROUGH_FIRST_LINE && m_mp.m_laser.pts.size() == 4) {
                    ignoring = true;
                    m_mp.m_laser.bounce();
                    continue;
                }
                if (m_ma.m_level.score < 1) {
                    m_ma.endGameDialog(m_ma.m_level.num);
                    m_ma.m_level.reset();
                    m_mt.setRunning(false);
                    m_mt.selection = "next";
                    break;
                }
                if (m_ma.m_powerupMan.get() == Powerup.WRAP_AROUND_ENDS
                        && m_ma.m_grid.getLines().indexOf(line) <= 1) {
                    m_coord.setX(m_coord.x + m_speed.x);
                    m_coord.setY(m_coord.y + m_speed.y);
                    m_mp.m_laser.bounce();
                    if (line.starty < K.NAV_HEIGHT + 2) {
                        m_mp.m_laser.starty = K.SCREEN_HEIGHT - K.NAV_HEIGHT - 2;
                        m_coord.setY(K.SCREEN_HEIGHT - K.NAV_HEIGHT - 2);
                        m_coord.lasty = (K.SCREEN_HEIGHT - K.NAV_HEIGHT - 1);
                    } else if (line.starty > K.SCREEN_HEIGHT - K.NAV_HEIGHT - 2) {
                        m_mp.m_laser.starty = K.NAV_HEIGHT + 2;
                        m_coord.setY(K.NAV_HEIGHT + 2);
                        m_coord.lasty = K.NAV_HEIGHT + 1;
                    }
                    if (m_coord.x <= m_speed.x) {
                        m_coord.x = 2;
                        m_speed.toggleXDirection();
                        doubleHit = false;
                        m_ma.soundAndVib();
                    } else if (m_coord.x >= K.SCREEN_WIDTH - m_speed.x) {
                        m_coord.x = K.SCREEN_WIDTH - 2;
                        m_speed.toggleXDirection();
                        doubleHit = false;
                        m_ma.soundAndVib();
                    }
                    m_mp.m_laser.startx = m_coord.x;
                    m_coord.lastx = m_coord.x;
                    m_mp.m_laser.bounce();
                    m_coord.setX(m_coord.x + m_speed.x);
                    m_coord.setY(m_coord.y + m_speed.y);
                    continue;
                }

                if (m_ma.m_powerupMan.get() == Powerup.WRAP_AROUND_SIDES
                        && (m_ma.m_grid.getLines().indexOf(line) == 2
                                || m_ma.m_grid.getLines().indexOf(line) == 3)) {
                    m_coord.setX(m_coord.x + m_speed.x);
                    m_coord.setY(m_coord.y + m_speed.y);
                    m_mp.m_laser.bounce();
                    if (line.startx < 2) {
                        m_mp.m_laser.startx = K.SCREEN_WIDTH - 2;
                        m_coord.setX(K.SCREEN_WIDTH - 1);
                        m_coord.lastx = K.SCREEN_WIDTH - 2;
                    } else if (line.startx > K.SCREEN_WIDTH - 2) {
                        m_mp.m_laser.startx = 2;
                        m_coord.setX(2);
                        m_coord.lastx = 1;
                    }

                    if (m_coord.y <= K.NAV_HEIGHT - m_speed.y) {
                        m_coord.y = K.NAV_HEIGHT + 2;
                        m_speed.toggleYDirection();
                        doubleHit = false;
                        m_ma.soundAndVib();
                    } else if ( m_coord.y >= K.SCREEN_HEIGHT - K.NAV_HEIGHT - m_speed.y) {
                        m_coord.y =  K.SCREEN_HEIGHT - K.NAV_HEIGHT - 2;
                        m_speed.toggleYDirection();
                        doubleHit = false;
                        m_ma.soundAndVib();
                    }
                    m_mp.m_laser.starty = m_coord.y;
                    m_coord.lasty = m_coord.x;
                    m_mp.m_laser.bounce();
                    m_coord.setX(m_coord.x + m_speed.x);
                    m_coord.setY(m_coord.y + m_speed.y);
                    continue;
                }

                m_ma.soundAndVib();
                m_coord.x =(m_coord.x - m_speed.x);
                m_coord.y =(m_coord.y - m_speed.y);
                float change;

                if (line.horizontal) {
                    m_speed.toggleYDirection();
                    change = Math.abs((m_coord.y - line.starty) / m_speed.y);
                    m_coord.y = (line.starty);
                    m_coord.setX(m_coord.x+ (change * m_speed.x));
                    for (Line line2 : m_ma.m_grid.getLines()) {
                        if (!ignoring
                                && line2 != line
                                && line2.crossed(m_coord.x, m_coord.y, m_coord.lastx, m_coord.lasty) > 0
                                && doubleHit)
                        {
                            Log.i("graphics", "double hit");
                            m_speed.toggleXDirection();
                            m_coord.x = (line2.endx);
                            m_coord.y =(line.endy);
                            m_mp.m_laser.bounce();
                            m_coord.x =(m_coord.x + m_speed.x);
                            m_coord.y =(m_coord.y + m_speed.y);
                            if (m_ma.m_level.score >0) m_ma.m_level.score--;
                        }
                    }
                } else {
                    m_speed.toggleXDirection();
                    change = Math.abs((m_coord.x - line.startx) / m_speed.x);
                    m_coord.x = (line.startx);
                    m_coord.y = (m_coord.y+ (change * m_speed.y));
                    for (Line line2 : m_ma.m_grid.getLines()) {
                        if (!ignoring && line2 != line
                                && line2.crossed(m_coord.x, m_coord.y, m_coord.lastx, m_coord.lasty) > 0
                                && doubleHit)
                        {
                            Log.i("graphics", "double hit");
                            m_speed.toggleYDirection();
                            m_coord.x = (line.endx);
                            m_coord.y = (line2.endy);
                            m_mp.m_laser.bounce();
                            m_coord.x = (m_coord.x + m_speed.x);
                            m_coord.y = (m_coord.y + m_speed.y);
                            if (m_ma.m_level.score >0) m_ma.m_level.score--;
                        }
                    }
                }
                m_mp.m_laser.bounce();
                break;
            }
        }
    }
}
