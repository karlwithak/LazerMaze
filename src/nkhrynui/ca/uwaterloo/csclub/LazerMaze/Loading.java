package nkhrynui.ca.uwaterloo.csclub.LazerMaze;

import nkhrynui.ca.uwaterloo.csclub.LazerMaze.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Loading extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Loading.this, MainActivity.class);
                startActivity(intent);
                Loading.this.startActivity(intent);
                Loading.this.finish();
            }
        }, 2000);
    }


}
