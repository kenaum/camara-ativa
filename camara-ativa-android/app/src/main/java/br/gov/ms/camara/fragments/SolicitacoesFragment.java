package br.gov.ms.camara.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.gov.ms.camara.MainActivity;
import br.gov.ms.camara.R;
import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.RequestsContoller;
import br.gov.ms.camara.enums.EnumFragmentScreens;
import br.gov.ms.camara.interfaces.IUpdateData;
import br.gov.ms.camara.model.solicitacao.RequestType;
import br.gov.ms.camara.util.CustomToast;
import br.gov.ms.camara.util.Utilities;

/**
 * Created by Elisa Freitas on 01/03/2016.
 */
public class SolicitacoesFragment extends Fragment implements IUpdateData {
    private LinearLayout lineSolicitacoes;
    private ArrayList<RequestType> solicitacaoList;
    private RequestsContoller requestsContoller;
    private ProgressDialog progressDialog;

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vInflater = inflater.inflate(R.layout.fragment_solicitacoes, container, false);

        lineSolicitacoes = (LinearLayout) vInflater.findViewById(R.id.lnSolicitacoes);
        findSolicitacoes();

        return vInflater;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    public void iniciaConsulta() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Processando...");
        progressDialog.setMessage("Aguarde");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public void finalizaConsulta() {
        progressDialog.dismiss();
    }

    private void findSolicitacoes() {
        requestsContoller = new RequestsContoller() {};
        solicitacaoList = requestsContoller.getSolicitacoesFromSharedPreferences(activity);

        if (solicitacaoList == null || solicitacaoList.isEmpty()) {
            requestsContoller = new RequestsContoller(JSONCons.getBaseURL()) {
                @Override
                protected void onInit() {
                    super.onInit();
                    iniciaConsulta();
                }

                @Override
                protected void result(JSONObject jsonObject) {
                    super.result(jsonObject);

                    if (jsonObject != null) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("request_types");

                            solicitacaoList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String strJson = jsonArray.get(i).toString();
                                JSONObject auxJsonObject = new JSONObject(strJson);
                                //Log.d("LOG PARSER", auxJsonObject.getString("nome"));

                                RequestType requestType = new RequestType();
                                requestType.setSolicitacao_id(auxJsonObject.getInt("id"));
                                requestType.setNomeSolicitacao(auxJsonObject.getString("nome"));
                                requestType.setDescSolicitacao(auxJsonObject.getString("descricao"));
                                requestType.setImgSolicitacao(auxJsonObject.getString("icone"));
                                solicitacaoList.add(requestType);

                                finalizaConsulta();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        populaSolicitacoes();
                    }

                    else {
                        CustomToast.show(activity, "Houve um erro ao tentar consultar as solicitações.", CustomToast.TOAST_ERROR, Toast.LENGTH_SHORT);
                    }

                }

                @Override
                protected void timeoutUsersCallback() {
                    super.timeoutUsersCallback();
                }
            };
            requestsContoller.getSolicitacoes();
        }

        else {
            populaSolicitacoes();
        }
    }

    public void populaSolicitacoes() {
        for (final RequestType requestType : solicitacaoList) {
            //for (int i = 0; i < 10; i++) {
            final LinearLayout lineSolicitacao = new LinearLayout(getActivity());
            LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsLinearLayout.setMargins(5, 5, 5, 5);
            lineSolicitacao.setGravity(Gravity.CENTER);
            lineSolicitacao.setLayoutParams(layoutParamsLinearLayout);
            lineSolicitacao.setTag(requestType);
            lineSolicitacao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.requestTypeConsultada = (RequestType) lineSolicitacao.getTag();
                    ((MainActivity) getActivity()).setFragment(EnumFragmentScreens.SOLICITACAOCONSULTADA, null, null);
                }
            });

            LinearLayout lineImg = new LinearLayout(getActivity());
            LinearLayout.LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
            imgLayoutParams.setMargins(5, 5, 5, 5);
            lineImg.setGravity(Gravity.CENTER);
            lineImg.setLayoutParams(imgLayoutParams);

            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            Drawable imgDrawable = getResources().getDrawable(getResources().getIdentifier(requestType.getImgSolicitacao(), "drawable", getActivity().getPackageName()));
            imageView.setImageDrawable(imgDrawable);
            lineImg.addView(imageView);

            LinearLayout lineTitulo = new LinearLayout(getActivity());
            LinearLayout.LayoutParams tituloLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 6);
            tituloLayoutParams.setMargins(5, 5, 5, 5);
            lineTitulo.setOrientation(LinearLayout.VERTICAL);
            lineTitulo.setGravity(Gravity.CENTER);
            lineTitulo.setLayoutParams(tituloLayoutParams);

            TextView textTitulo = new TextView(getActivity());
            textTitulo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            textTitulo.setText(requestType.getNomeSolicitacao());
            textTitulo.setTextColor(Color.parseColor("#024da1"));
            textTitulo.setTextSize(18.0f);
            textTitulo.setTypeface(null, Typeface.BOLD);
            textTitulo.setGravity(Gravity.CENTER_VERTICAL);
            lineTitulo.addView(textTitulo);

            TextView textDesc = new TextView(getActivity());
            textDesc.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            textDesc.setText(requestType.getDescSolicitacao());
            textDesc.setTextColor(Color.parseColor("#000000"));
            lineTitulo.addView(textDesc);

            LinearLayout lineImgBalao = new LinearLayout(getActivity());
            LinearLayout.LayoutParams imgBalaoLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
            imgBalaoLayoutParams.setMargins(5, 5, 5, 5);
            lineImgBalao.setGravity(Gravity.CENTER);
            lineImgBalao.setLayoutParams(imgBalaoLayoutParams);

            ImageView imgBalao = new ImageView(getActivity());
            imgBalao.setLayoutParams(imgBalaoLayoutParams);
            imgBalao.setImageDrawable(getResources().getDrawable(R.drawable.img_balao_solicitacao));
            lineImgBalao.addView(imgBalao);

            lineSolicitacao.removeAllViews();

            lineSolicitacao.addView(lineImg);
            lineSolicitacao.addView(lineTitulo);
            lineSolicitacao.addView(lineImgBalao);

            lineSolicitacoes.addView(lineSolicitacao);
        }
    }

    @Override
    public void updateData() {
        findSolicitacoes();
    }
}