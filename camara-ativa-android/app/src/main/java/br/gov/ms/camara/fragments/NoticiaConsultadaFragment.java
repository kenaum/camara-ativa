package br.gov.ms.camara.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import br.gov.ms.camara.R;
import br.gov.ms.camara.util.Utilities;

/**
 * Created by rodolfoortale on 03/03/16.
 */
public class NoticiaConsultadaFragment extends Fragment {
    private String urlNoticia;
    private WebView webViewNoticia;
    private ProgressDialog progressDialog;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vInflater =  inflater.inflate(R.layout.fragment_noticia_consultada, container, false);

        urlNoticia = Utilities.urlNoticia;
        urlNoticia = urlNoticia.replace("amp;", "");
        //Log.v("LOG ID", "id: " + urlNoticia);

        webViewNoticia = (WebView) vInflater.findViewById(R.id.webViewNoticia);
        webViewNoticia.loadUrl(urlNoticia);

        webViewNoticia.getSettings().setJavaScriptEnabled(true); // enable javascript

        webViewNoticia.getSettings().setLoadWithOverviewMode(true);
        webViewNoticia.getSettings().setUseWideViewPort(true);
        webViewNoticia.getSettings().setBuiltInZoomControls(true);

        webViewNoticia.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                iniciaConsulta();
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                finalizaConsulta();
            }

        });

        return vInflater;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public void iniciaConsulta() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Carregando a página...");
        progressDialog.setMessage("Aguarde");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public void finalizaConsulta() {
        progressDialog.dismiss();
    }
}
