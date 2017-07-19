package br.gov.ms.camara;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import br.gov.ms.camara.controller.UsersController;
import br.gov.ms.camara.model.User;

/**
 * Created by rodolfoortale on 17/03/16.
 */
public class SplashScreenActivity extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // TODO: testar codigo abaixo
        //WVersionManager versionManager = new WVersionManager(this);
        //versionManager.setVersionContentUrl("http://bit.ly/11c7Pnb"); // your update content url, see the response format below
        //versionManager.checkVersion();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                UsersController usersController = new UsersController() {};
                User user = usersController.getUserFromSharedPreferences(SplashScreenActivity.this);

                if (user != null) {
                    if (user.isLogged()) {
                        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(i);
                    }

                    else {
                        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }

                else {
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                }

                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
