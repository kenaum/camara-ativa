package br.gov.ms.camara.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.asynctask.JSONObjectParser;
import br.gov.ms.camara.model.solicitacao.Solicitacao;
import br.gov.ms.camara.model.vereador.Vereador;

/**
 * Created by rodolfoortale on 04/03/16.
 */
public abstract class AldermenController {
    private String url;
    private List<Pair<String, String>> paramsUser;
    private Solicitacao solicitacao;
    private SharedPreferences mPrefs;

    /*
     * @param url can be null when the instance is not for a HTTP request
     */
    public AldermenController(String url){
        this.url = url;
    }

    public AldermenController() { }

    public Boolean save(Solicitacao solicitacao) throws JSONException {
        paramsUser = new ArrayList<>();

        new JSONObjectParser(url, paramsUser) {
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
        }.execute();

        return true;
    }

    public ArrayList<Solicitacao> getSolicitacoesJSON(JSONObject jsonObject, Solicitacao solicitacao) throws JSONException {
        ArrayList<Solicitacao> solicitacaos = new ArrayList<>();

        /*
        for (int i = 0; i < ) {
            solicitacao = new Solicitacao();

            String idConta = !jsonObject.isNull(JSONCons.keyIdData) ? jsonObject.getString(JSONCons.keyIdData) : "0";
            conta.setId(idConta);

            String token = !jsonObject.isNull(JSONCons.keyTokenData) ? jsonObject.getString(JSONCons.keyTokenData) : "";
            conta.setToken(token);

            String identificacao = !jsonObject.isNull(JSONCons.keyIdentificacao) ? jsonObject.getString(JSONCons.keyIdentificacao) : "";
            conta.setIdentificacao(identificacao);

            user.getContas().add(conta);
        }
        */

        return solicitacaos;
    }

    public void getAldermen() {
        paramsUser = new ArrayList<>();
        paramsUser.add(new Pair(JSONCons.keyTag, JSONCons.keyFindAldermen));

        byte[] bytesOfMessage = new byte[0];
        try {
            bytesOfMessage = "Câmara Ativa20112016".getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        new JSONObjectParser(url, paramsUser) {
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
        }.execute();
    }

    public ArrayList<Vereador> getVereadoresFromSharedPreferences(Activity activity) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

        Gson gson = new Gson();
        String json = mPrefs.getString("Vereador", "");
        Type listType = new TypeToken<ArrayList<Vereador>>(){}.getType();

        return gson.fromJson(json, listType);
    }

    public void saveVereadorToSharedPreferences(Activity activity, ArrayList<Vereador> vereadorArrayList) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson   = new Gson();
        String json = gson.toJson(vereadorArrayList);

        prefsEditor.putString("Vereador", json);
        prefsEditor.apply();
    }

    protected void onInit() { }

    protected void result(JSONObject jsonObject) { }

    protected void timeoutUsersCallback() { }
}