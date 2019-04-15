package com.crazy_iter.checkjobs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.crazy_iter.checkjobs.Data.LocalDB;
import com.crazy_iter.checkjobs.Data.User;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkjobs.StaticsAndAPIs.StaticString;

public class StartActivity extends AppCompatActivity implements StaticCodes, StaticString {

    public static User user;
    public static LocalDB localDB;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();

        localDB = new LocalDB(this);
        user = localDB.getUserInfo();
        prefs = getSharedPreferences("com.crazy_iter.checkarround", MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (prefs.getBoolean(S_FIRST_RUN, true)) {
                    startActivity(new Intent(StartActivity.this, MainSlidesActivity.class));
                    prefs.edit().putBoolean(S_FIRST_RUN, false).apply();
                } else {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                }
                StartActivity.this.finish();
            }
        }, WAIT_MSEC);
    }

}
