package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;

import static nkhrynui.ca.uwaterloo.csclub.LazerMaze.PowerupManager.Powerup;

public class MainActivity extends Activity {
    // GLOBAL VARIABLES, set in surfaceCreated
    private static SharedPreferences g_sharedPrefs;
    private static Level g_level;
    private static Vibrator g_v = null;
    private static boolean lockListenerOkay = true;
    private static Grid g_grid;
    private static PowerupManager g_powerupMan;
    private static Panel g_panel;
    private static Dialogues g_dialogues;
    private static MainActivity g_mainActivity;
    private static MainThread g_mainThread;
    private static Physics g_physics;

    /****** CONSTANTS END ************************************************************************/

    public void nextLevel() {
        if (!g_level.recover) {
            if (g_grid.lines.size() > 1) g_panel.gridShrink();
            if (g_level.num % 3 == 0 && g_level.num != 0) {
                g_powerupMan.setPowerup();
            }
            g_panel.updateColors();
            g_grid.makeGrid(g_powerupMan, g_level);
            if (g_level.num > 0 && lockListenerOkay) g_panel.gridExpand();
            g_panel.nextLevel();
        }
        g_level.recover = false;
        Panel.graphicCount = 0;
        if (lockListenerOkay) restartLevel();
    }

    public void restartLevel() {
        g_panel.restartLevel();
        // VERY IMPORTANT: this is all the drawing that happens before the game
        // actually starts: ie maze and target
        g_level.restart = true;
    }

    public void settings() {
        g_level.inPrefs = true;
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void draw() { g_panel.draw(); }

    public void endGameDialog() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                g_dialogues.endGameDialog();
            }
        });
    }

    /****************************************** ON* - START***************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("crashing", "create");

        g_level = new Level();
        g_mainActivity = this;
        g_sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (g_sharedPrefs.getBoolean("vibrate", true)) g_v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        g_grid = new Grid();
        g_panel = new Panel(g_mainActivity);
        g_powerupMan = new PowerupManager(g_panel);
        g_physics = new Physics(g_powerupMan, g_grid, g_level, g_v, this);
        g_mainThread = new MainThread(g_level, this, g_physics);
        g_dialogues = new Dialogues(this, g_level, g_mainThread, g_powerupMan, g_sharedPrefs);
        g_panel.setup(g_level, g_dialogues, g_sharedPrefs, g_mainActivity, g_grid, g_powerupMan,
                    g_mainThread, g_physics);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (g_sharedPrefs.getBoolean("screenOn", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        g_mainThread.start();
    }
    
    public void onResume() {
        super.onResume();
        lockListenerOkay = true;
        Log.i("crashing", "resume");
        if (g_level.inPrefs) {
            g_panel.updateColors();
            if (g_sharedPrefs.getBoolean("screenOn", true)) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            g_level.inPrefs = false;
        }
        if (g_sharedPrefs.getBoolean("vibrate", true) && g_v == null) {
            g_v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        } else g_v = null;
        setContentView(g_panel);
//        g_mainThread.start();
    }
    
    public void onPause() {
        super.onPause();
        lockListenerOkay = false;
        if (g_powerupMan.m_waitForChoice) g_powerupMan.m_selection = 4;

        Log.i("crashing", "pause");
        if (g_v != null) g_v.cancel();
        g_level.recover = true;
        g_level.exit = false;
        g_mainThread.setRunning(false);
        g_mainThread.selection = "";
        if (g_powerupMan.m_selection == 0) g_powerupMan.m_selection = 4;
        try {
            g_mainThread.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("crashing", "join failed");
        }
    }
    
    public void onRestart() {
        super.onRestart();
        g_level.recover = true;
        g_level.exit = true;
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
        g_level.exit = false;
        settings();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.i("buttons", "back button");
    }
}