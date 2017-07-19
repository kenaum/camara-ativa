package br.gov.ms.camara.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.gov.ms.camara.MainActivity;
import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.NoticiasController;
import br.gov.ms.camara.enums.EnumFragmentScreens;
import br.gov.ms.camara.model.Noticia;
import br.gov.ms.camara.util.Utilities;

/**
 * Created by rodolfoortale on 03/03/16.
 */
public class NoticiasCamaraFragment extends Fragment {
    private ArrayList<Noticia> noticiaList;
    private LinearLayout lnNoticias;
    private NoticiasController noticiasController;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vInflater = inflater.inflate(br.gov.ms.camara.R.layout.fragment_noticias_camara, container, false);

        lnNoticias = (LinearLayout) vInflater.findViewById(br.gov.ms.camara.R.id.lnNoticias);

        noticiasController = new NoticiasController(JSONCons.getURLFindNoticia()) {
            @Override
            protected void onInit() {
                super.onInit();
                iniciaConsulta();
            }

            @Override
            protected void result(JSONObject jsonObject) {
                super.result(jsonObject);

                Noticia noticia = new Noticia();
                noticiaList = new ArrayList<>();

                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("noticias");

                    JSONObject jsonObjectResultado = jsonObject.getJSONObject("resultado");
                    noticia.setPg(jsonObjectResultado.getInt("pg"));
                    noticia.setPgs(jsonObjectResultado.getInt("pgs"));

                    for (int i=0; i<jsonArray.length(); i++) {
                        String strJson = jsonArray.get(i).toString();
                        JSONObject auxJsonObject = new JSONObject(strJson);
                        //Log.d("LOG PARSER", auxJsonObject.getString("titulo"));

                        noticia = new Noticia();
                        noticia.setNoticia_id(auxJsonObject.getInt("noticia_id"));
                        noticia.setData(auxJsonObject.getString("data"));
                        noticia.setTitulo(auxJsonObject.getString("titulo"));
                        noticia.setResumo(auxJsonObject.getString("resumo"));
                        noticia.setLink(auxJsonObject.getString("link"));
                        noticiaList.add(noticia);
                    }

                    finalizaConsulta();

                    populaNoticias();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void timeoutUsersCallback() {
                super.timeoutUsersCallback();
            }
        };
        noticiasController.getNoticias();

        return vInflater;
    }

    public void iniciaConsulta() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Processando...");
        progressDialog.setMessage("Aguarde");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public void finalizaConsulta() {
        progressDialog.dismiss();
    }

    public void populaNoticias() {
        for (final Noticia noticia : noticiaList) {
            final LinearLayout lineNoticia = new LinearLayout(getActivity());
            LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsLinearLayout.setMargins(5,5,5,5);
            lineNoticia.setGravity(Gravity.CENTER);
            lineNoticia.setLayoutParams(layoutParamsLinearLayout);
            lineNoticia.setTag(noticia);
            lineNoticia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.isNetworkAvailable(getActivity())) {
                        Noticia noticiaAux = (Noticia) lineNoticia.getTag();

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.camara.ms.gov.br"));
                        startActivity(browserIntent);
                    }

                    else {
                        Toast.makeText(getActivity(), "Ops! No momento, você não possui conexão com a internet. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            //ImageView imageView = new ImageView(this);
            //imageView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            //imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_calendario));

            String auxYear  = noticia.getData().substring(0,4);
            String auxMonth = noticia.getData().substring(5,7);
            String auxDay   = noticia.getData().substring(8,10);
            String auxData  = auxDay + "/" + auxMonth + "/" + auxYear;

            TextView lbData = new TextView(getActivity());
            LinearLayout.LayoutParams layoutParamsData = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            layoutParamsData.setMargins(5,5,5,5);
            lbData.setLayoutParams(layoutParamsData);
            lbData.setGravity(Gravity.CENTER);
            lbData.setTextColor(Color.parseColor("#000000"));
            lbData.setText(auxData);
            lineNoticia.addView(lbData);

            TextView textTitulo = new TextView(getActivity());
            LinearLayout.LayoutParams layoutParamsTitulo = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
            layoutParamsTitulo.setMargins(5,5,5,5);
            textTitulo.setLayoutParams(layoutParamsTitulo);
            textTitulo.setText(noticia.getTitulo());
            textTitulo.setTextColor(Color.BLACK);

            lineNoticia.addView(textTitulo);

            lnNoticias.addView(lineNoticia);
        }
    }
}
