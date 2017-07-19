package br.gov.ms.camara.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import br.gov.ms.camara.model.solicitacao.RequestType;
import br.gov.ms.camara.model.solicitacao.Solicitacao;
import br.gov.ms.camara.util.Utilities;

/**
 * Created by rodolfoortale on 04/03/16.
 */
public abstract class RequestsContoller {
    private String url;
    private List<Pair<String, String>> paramsUser;
    private Solicitacao solicitacao;
    private SharedPreferences mPrefs;

    /*
     * @param url can be null when the instance is not for a HTTP request
     */
    public RequestsContoller(String url){
        this.url = url;
    }

    public RequestsContoller() { }

    public void getSolicitacoes() {
        paramsUser = new ArrayList<>();
        paramsUser.add(new Pair(JSONCons.keyTag, JSONCons.keyFindRequestTypes));

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

    public void saveRequest(Solicitacao solicitacao, Bitmap bitmap) {
        paramsUser = new ArrayList<>();
        paramsUser.add(new Pair(JSONCons.keyTag, JSONCons.keySaveRequest));
        paramsUser.add(new Pair(JSONCons.keyLatitude, solicitacao.getLatitude()));
        paramsUser.add(new Pair(JSONCons.keyLongitude, solicitacao.getLongitude()));
        paramsUser.add(new Pair(JSONCons.keyEndereco, solicitacao.getEndereco()));
        paramsUser.add(new Pair(JSONCons.keyRequestType, String.valueOf(solicitacao.getRequestType().getSolicitacao_id())));
        paramsUser.add(new Pair(JSONCons.keyStatus, String.valueOf(solicitacao.getSituacao().getId())));
        //paramsUser.add(new Pair(JSONCons.keyUser, String.valueOf("29")));
        paramsUser.add(new Pair(JSONCons.keyUser, String.valueOf(solicitacao.getUser().getId())));

        if (bitmap != null) {
            String base64Image = Utilities.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
            paramsUser.add(new Pair(JSONCons.keyRequestImage, base64Image));
        }

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

    public ArrayList<RequestType> getSolicitacoesFromSharedPreferences(Activity activity) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

        Gson gson = new Gson();
        String json = mPrefs.getString("RequestType", "");
        Type listType = new TypeToken<ArrayList<RequestType>>(){}.getType();

        return gson.fromJson(json, listType);
    }

    public void saveSolicitacaoToSharedPreferences(Activity activity, ArrayList<RequestType> requestTypeArrayList) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson   = new Gson();
        String json = gson.toJson(requestTypeArrayList);

        prefsEditor.putString("RequestType", json);
        prefsEditor.apply();
    }

    protected void onInit() { }

    protected void result(JSONObject jsonObject) { }

    protected void timeoutUsersCallback() { }
}
