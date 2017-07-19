package br.gov.ms.camara.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import br.gov.ms.camara.LoginActivity;
import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.UsersController;
import br.gov.ms.camara.model.User;

/**
 * Created by Ortale on 04/04/16.
 */

public class ScheduledService {
    Timer timer;
    CustomTimerTask timerTask;
    private UsersController usersController;
    private User user;
    private Activity activity;

    public ScheduledService (Activity activity) {
        this.activity = activity;
    }

    public void startService() {
        timer = new Timer();
        timerTask = new CustomTimerTask();
        timer.schedule(timerTask, 3600000);
    }

    class CustomTimerTask extends TimerTask {
        public void run() {
            usersController = new UsersController() {};
            user = usersController.getUserFromSharedPreferences(activity);

            usersController = new UsersController(JSONCons.getBaseURL()) {
                @Override
                protected void onInit() {
                    super.onInit();
                }

                @Override
                protected void result(JSONObject jsonObject) {
                    super.result(jsonObject);

                    if (jsonObject != null) {
                        try {
                            Boolean isBlocked = jsonObject.getBoolean("is_blocked");

                            if (isBlocked) {
                                new AlertDialog.Builder(activity)
                                        //new AlertDialog.Builder(activity)
                                        .setTitle("Usuário bloqueado")
                                        .setMessage("Seu usuário se encontra bloqueado.")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(activity, LoginActivity.class);
                                                activity.startActivity(intent);
                                                activity.finish();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                protected void timeoutUsersCallback() {
                    super.timeoutUsersCallback();
                }
            };
            usersController.checkUser(user);
        }
    }
}
