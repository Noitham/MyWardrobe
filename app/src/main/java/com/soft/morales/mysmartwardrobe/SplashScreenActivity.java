package com.soft.morales.mysmartwardrobe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.soft.morales.mysmartwardrobe.model.User;

public class SplashScreenActivity extends Activity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splashscreen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences shared = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String userString = shared.getString("user", "");

                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                if (!userString.equals("")) {
                    intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}