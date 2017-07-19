package br.gov.ms.camara.controller;

import android.content.SharedPreferences;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import br.gov.ms.camara.asynctask.JSONObjectParser;
import br.gov.ms.camara.model.solicitacao.Solicitacao;

/**
 * Created by rodolfoortale on 04/03/16.
 */
public abstract class NoticiasController {
    private String url;
    private List<Pair<String, String>> paramsUser;
    private Solicitacao solicitacao;
    private SharedPreferences mPrefs;

    /*
     * @param url can be null when the instance is not for a HTTP request
     */
    public NoticiasController(String url){
        this.url = url;
    }

    public NoticiasController() { }

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

    public void getNoticias() {
        paramsUser = new ArrayList<>();

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

    protected void onInit() { }

    protected void result(JSONObject jsonObject) { }

    protected void timeoutUsersCallback() { }
}
