package br.gov.ms.camara.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.asynctask.JSONObjectParser;
import br.gov.ms.camara.model.User;
import br.gov.ms.camara.model.solicitacao.Solicitacao;

/**
 * Created by rodolfoortale on 04/03/16.
 */
public abstract class UsersController {
    private String url;
    private List<Pair<String, String>> paramsUser;
    private Solicitacao solicitacao;
    private SharedPreferences mPrefs;

    private User user;

    /*
     * @param url can be null when the instance is not for a HTTP request
     */
    public UsersController(String url){
        this.url = url;
    }

    public UsersController() { }

    public void login(String user, String password) {
        paramsUser = new ArrayList<>();
        paramsUser.add(new Pair(JSONCons.keyTag, JSONCons.keyLogin));
        paramsUser.add(new Pair(JSONCons.keyUser, user));
        paramsUser.add(new Pair(JSONCons.keyPassword, password));

        JSONObjectParser jsonObjectParser = new JSONObjectParser(url, paramsUser) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                onInit();
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                result(jsonObject);
            }

            @Override
            protected void timeoutCallback() {
                super.timeoutCallback();
                timeoutUsersCallback();
            }
        };
        jsonObjectParser.execute();
    }

    public void save(User user) {
        paramsUser = new ArrayList<>();
        paramsUser.add(new Pair(JSONCons.keyTag, JSONCons.keyRegister));
        paramsUser.add(new Pair(JSONCons.keyUser, user.getUsername()));
        paramsUser.add(new Pair(JSONCons.keyPassword, user.getPassword()));
        paramsUser.add(new Pair(JSONCons.keyNome, user.getName()));
        paramsUser.add(new Pair(JSONCons.keyTelefone, user.getTelefone()));

        JSONObjectParser jsonObjectParser = new JSONObjectParser(url, paramsUser) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                onInit();
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                result(jsonObject);
            }

            @Override
            protected void timeoutCallback() {
                super.timeoutCallback();
                timeoutUsersCallback();
            }
        };
        jsonObjectParser.execute();
    }

    public void checkUser(User user) {
        paramsUser = new ArrayList<>();
        paramsUser.add(new Pair(JSONCons.keyTag, JSONCons.keyCheckUser));
        paramsUser.add(new Pair(JSONCons.keyUser, user.getUsername()));

        JSONObjectParser jsonObjectParser = new JSONObjectParser(url, paramsUser) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                onInit();
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                result(jsonObject);
            }

            @Override
            protected void timeoutCallback() {
                super.timeoutCallback();
                timeoutUsersCallback();
            }
        };
        jsonObjectParser.execute();
    }

    public void alterarSenha(String email) {
        paramsUser = new ArrayList<>();
        paramsUser.add(new Pair(JSONCons.keyTag, JSONCons.keyForgotPassword));
        paramsUser.add(new Pair(JSONCons.keyUser, email));

        JSONObjectParser jsonObjectParser = new JSONObjectParser(url, paramsUser) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                onInit();
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                result(jsonObject);
            }

            @Override
            protected void timeoutCallback() {
                super.timeoutCallback();
                timeoutUsersCallback();
            }
        };
        jsonObjectParser.execute();
    }

    public User getUserFromSharedPreferences(Activity activity) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

        Gson gson   = new Gson();
        String json = mPrefs.getString("User", "");
        user        = gson.fromJson(json, User.class);

        return user;
    }

    public void saveUserToSharedPreferences(Activity activity, User user) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson   = new Gson();
        String json = gson.toJson(user);

        prefsEditor.putString("User", json);
        prefsEditor.apply();
    }

    protected void onInit() { }

    protected void result(JSONObject jsonObject) { }

    protected void resultEmailCheck(JSONObject jsonObject) { }

    protected void resultEmailPasswordCheck(Boolean isValid, String msg) { }

    protected void timeoutUsersCallback() { }
}
