package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.util.Utils.inBetween;

public class TouchHandler {
    private MainActivity m_ma;
    private MainThread   m_mt;
    private MainPanel    m_mp;
    public boolean       upOnButtons = false;
    public boolean       inAnimation;
    public float         startX, startY, endX, endY, changeX, changeY;
    public int           graphicCount = 0;

    TouchHandler(MainActivity mainActivity, MainThread mainThread, MainPanel mainPanel) {
        m_ma = mainActivity;
        m_mt = mainThread;
        m_mp = mainPanel;
    }
    
    public boolean handle(MotionEvent event) {
        if (m_ma.m_powerupMan.m_waitForChoice) { //selecting upgrade
            upOnButtons = false;
            if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() <= K.SCREEN_WIDTH / 2) {
                m_ma.m_powerupMan.m_selection = 1;
            } else if (event.getAction() == MotionEvent.ACTION_DOWN
            && event.getX() > K.SCREEN_WIDTH / 2) {
                m_ma.m_powerupMan.m_selection = 2;
            }
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            m_mt.setRunning(false);
            m_mt.selection = "restart";
        }
        //buttons
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getY() >=
        K.SCREEN_HEIGHT - K.NAV_HEIGHT && graphicCount == 0 && !inAnimation) {
            upOnButtons = true;
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP && event.getY() >=
        K.SCREEN_HEIGHT - K.NAV_HEIGHT && graphicCount == 0 && !inAnimation && upOnButtons) {
            upOnButtons = false;
            if (event.getX() < K.SCREEN_WIDTH / 3) {
                m_ma.m_level.exit = false;
                m_ma.settings();
            } else if (event.getX() > K.SCREEN_WIDTH * 2 / 3) {
                m_ma.m_dialogues.restartDialog(m_ma.m_level.num);

            } else if (m_ma.m_level.score > m_ma.m_level.skipCost) {
                m_ma.m_dialogues.skipLevelDialog();
            }
        }
        //aiming launch
        if (event.getAction() == MotionEvent.ACTION_DOWN && graphicCount == 0 && !inAnimation) {
            upOnButtons = false;
            if (m_mp.m_launcher.bigPointTest(event.getX(), event.getY())) {
                if (m_mt.isAlive()) {
                    m_mt.setRunning(false);
                    m_mt.selection = "restart";
                    m_ma.m_level.restart = true;
                }
                startX = m_mp.m_launcher.m_x;
                startY = m_mp.m_launcher.m_y;
                m_mp.m_launcher.m_active = true;
                if (m_ma.m_powerupMan.get() == PowerupManager.Powerup.TWO_LAUNCHERS) m_mp.m_launcher2.m_active = false;
                graphicCount = 1;
            } else if (m_ma.m_powerupMan.get() == PowerupManager.Powerup.TWO_LAUNCHERS
            && m_mp.m_launcher2.bigPointTest(event.getX(), event.getY())) {
                if (m_mt.isAlive()) {
                    m_mt.setRunning(false);
                    m_mt.selection = "restart";
                    m_ma.m_level.restart = true;
                }
                startX = m_mp.m_launcher2.m_x;
                startY = m_mp.m_launcher2.m_y;
                m_mp.m_launcher2.m_active = true;
                m_mp.m_launcher.m_active = false;
                graphicCount = 1;
            } else if ((m_ma.m_powerupMan.get() == PowerupManager.Powerup.LAUNCH_FROM_EITHER
            && m_mp.m_target.bigPointTest(event.getX(), event.getY()))) {
                if (m_mt.isAlive()) {
                    m_mt.setRunning(false);
                    m_mt.selection = "restart";
                    m_ma.m_level.restart = true;
                }
                startX = m_mp.m_target.m_x;
                startY = m_mp.m_target.m_y;
                m_mp.m_target.m_x = m_mp.m_launcher.m_x;
                m_mp.m_target.m_y = m_mp.m_launcher.m_y;
                m_mp.m_launcher.m_x = (int) startX;
                m_mp.m_launcher.m_y = (int) startY;
                Bitmap temp = m_mp.m_launcher.m_bitmap;
                m_mp.m_launcher.m_bitmap = m_mp.m_target.m_bitmap;
                m_mp.m_target.m_bitmap = temp;
                graphicCount = 1;
            }
        }
        //aiming launch with aimer
        if (m_ma.m_powerupMan.get() == PowerupManager.Powerup.AIMING_LASER
        && event.getAction() == MotionEvent.ACTION_MOVE
        && graphicCount == 1
        && !inAnimation)
        {
            if (Math.hypot((m_mp.m_launcher.m_x - event.getX()), (m_mp.m_launcher.m_y - event.getY())) < K.SPECIAL_WIDTH) {
                return true;
            }
            Canvas c = m_mp.getCanvas();
            m_ma.m_level.draw(c);
            double distance;
            double min = 99999;
            Line l = null;
            float intersection = 0;
            float intersectionTemp;
            float pointx = m_mp.m_launcher.m_x, pointy = m_mp.m_launcher.m_y;
            pointx += (event.getX() - m_mp.m_launcher.m_x) * 1000;
            pointy += (event.getY() - m_mp.m_launcher.m_y) * 1000;
            for (Line line : m_ma.m_grid.lines) {
                intersectionTemp = line.crossed(m_mp.m_launcher.m_x, m_mp.m_launcher.m_y, pointx, pointy);
                if (intersectionTemp > 0) {
                    if (line.horizontal) {
                        distance = Math.hypot((m_mp.m_launcher.m_x - intersectionTemp),
                        (m_mp.m_launcher.m_y - line.starty));
                    } else {
                        distance = Math.hypot((m_mp.m_launcher.m_x - line.startx),
                        (m_mp.m_launcher.m_y - intersectionTemp));
                    }
                    if (distance < min) {
                        min = distance;
                        intersection = intersectionTemp;
                        l = line;
                    }
                }
            }
            if (l != null && l.horizontal) {
                c.drawLine(m_mp.m_launcher.m_x, m_mp.m_launcher.m_y, intersection, l.starty, m_mp.m_laser.paint);
            } else if (l != null) {
                c.drawLine(m_mp.m_launcher.m_x, m_mp.m_launcher.m_y, l.startx, intersection, m_mp.m_laser.paint);
            }
            m_mp.m_laser.draw(c);
            m_ma.m_grid.draw(c);
            m_mp.m_buttons.draw(c, m_ma.m_level);
            m_mp.m_launcher.draw(c);
            m_mp.m_target.draw(c);
            m_mp.postCanvas(c);
        }
        //launches laser
        if (event.getAction() == MotionEvent.ACTION_UP && graphicCount == 1 && !inAnimation) {
            upOnButtons = false;
            Log.i("powerup", Float.toString(startX) + " on up");
            m_mp.m_laser.GO.coordinates.setX((int) startX);
            m_mp.m_laser.GO.coordinates.setY((int) startY);
            endX = event.getX();
            endY = event.getY();
            changeX = endX - startX;
            changeY = endY - startY;
            if (inBetween(-0.01, changeX, 0.01))
                changeX = (float) (Math.signum(changeX) + 0.01);
            if (inBetween(-0.01, changeY, 0.01))
                changeY = (float) (Math.signum(changeY) + 0.01);
            //sets up the line speed and direction according to starting swipe
            if (Math.abs(changeX) < 10 && Math.abs(changeY) < 10) {
                graphicCount = 0;
                return true;
            }
            if (Math.abs(changeX) > (Math.abs(changeY))) {
                m_mp.m_laser.GO.speed.x = (K.SPEED * Math.signum(changeX));
                m_mp.m_laser.GO.speed.y = (Math.abs(changeY) / Math.abs(changeX) * K.SPEED * Math.signum(changeY));
            } else {
                m_mp.m_laser.GO.speed.y = (K.SPEED * Math.signum(changeY));
                m_mp.m_laser.GO.speed.x = (Math.abs(changeX) / Math.abs(changeY) * K.SPEED * Math.signum(changeX));
            }
            graphicCount = 0;
            if (m_ma.m_level.restart) {
                if (m_ma.m_powerupMan.get() != PowerupManager.Powerup.TWO_LAUNCHERS || m_mp.m_launcher.m_active) m_mp.m_laser.reset(m_mp.m_launcher);
                else m_mp.m_laser.reset(m_mp.m_launcher2);
                m_mt.setRunning(true);
            } else {
                m_mt.setRunning(true);
            }
        }
        return true;
    }
}
