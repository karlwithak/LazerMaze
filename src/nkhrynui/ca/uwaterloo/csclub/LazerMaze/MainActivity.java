package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;

public class MainActivity extends Activity {
    // GLOBAL VARIABLES, set in surfaceCreated
    public SharedPreferences m_sharedPrefs;
    public Level             m_level;
    public Vibrator          m_v = null;
    public boolean           lockListenerOkay = true;
    public Grid              m_grid;
    public PowerupManager    m_powerupMan;
    public MainPanel         m_panel;
    public Dialogues         m_dialogues;
    public MainThread        m_mt;
    public Physics           m_physics;

    /****** CONSTANTS END ************************************************************************/

    public void nextLevel() {
        if (!m_level.recover) {
            if (m_grid.lines.size() > 1) m_panel.gridShrink();
            if (m_level.num % 3 == 0 && m_level.num != 0) {
                m_powerupMan.setPowerup();
            }
            m_panel.updateColors();
            m_grid.makeGrid(m_powerupMan, m_level);
            if (m_level.num > 0 && lockListenerOkay) m_panel.gridExpand();
            m_panel.nextLevel();
        }
        m_level.recover = false;
        MainPanel.graphicCount = 0;
        if (lockListenerOkay) restartLevel();
    }

    public void restartLevel() {
        m_panel.restartLevel();
        m_level.restart = true;
    }

    public void settings() {
        m_level.inPrefs = true;
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void draw() { m_panel.draw(); }

    public void endGameDialog(final int level) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                m_dialogues.endGameDialog(level);
            }
        });
    }

    /****************************************** ON* - START***************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("crashing", "create");
        m_level = new Level();
        m_sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (m_sharedPrefs.getBoolean("vibrate", true)) m_v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        m_grid = new Grid();
        m_panel = new MainPanel(this);
        m_powerupMan = new PowerupManager(m_panel);
        m_physics = new Physics(m_powerupMan, m_grid, m_level, m_v, this);
        m_dialogues = new Dialogues(this, m_level, m_powerupMan, m_sharedPrefs);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (m_sharedPrefs.getBoolean("screenOn", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    
    public void onResume() {
        super.onResume();
        lockListenerOkay = true;
        Log.i("crashing", "resume");
        if (m_level.inPrefs) {
            m_panel.updateColors();
            if (m_sharedPrefs.getBoolean("screenOn", true)) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            m_level.inPrefs = false;
        }
        if (m_sharedPrefs.getBoolean("vibrate", true) && m_v == null) {
            m_v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        } else m_v = null;

        m_mt = new MainThread(this);
        m_panel.setup(m_mt, this);
        m_dialogues.setup(m_mt);
        m_mt.start();
        setContentView(m_panel);
    }
    
    public void onPause() {
        super.onPause();
        lockListenerOkay = false;
        if (m_powerupMan.m_waitForChoice) m_powerupMan.m_selection = 4;

        Log.i("crashing", "pause");
        if (m_v != null) m_v.cancel();
        m_level.recover = true;
        m_level.exit = false;
        m_mt.setRunning(false);
        m_mt.selection = "";
        if (m_powerupMan.m_selection == 0) m_powerupMan.m_selection = 4;
        try {
            m_mt.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("crashing", "join failed");
        }
    }
    
    public void onRestart() {
        super.onRestart();
        m_level.recover = true;
        m_level.exit = true;
    }
    
    public void onStop() {
        super.onStop();
        Log.i("crashing", "stop");
    }
    
    public void onDestroy() {
        super.onDestroy();
        Log.i("crashing", "destroy");
    }
    
    public void onStart() {
        super.onStart();
        Log.i("crashing", "start");
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        m_level.exit = false;
        settings();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.i("buttons", "back button");
    }
}