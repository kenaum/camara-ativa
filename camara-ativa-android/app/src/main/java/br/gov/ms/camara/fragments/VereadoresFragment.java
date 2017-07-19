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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.gov.ms.camara.MainActivity;
import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.AldermenController;
import br.gov.ms.camara.enums.EnumFragmentScreens;
import br.gov.ms.camara.interfaces.IUpdateData;
import br.gov.ms.camara.model.vereador.Vereador;
import br.gov.ms.camara.model.vereador.VereadorTelefone;
import br.gov.ms.camara.util.Utilities;

/**
 * Created by rodolfoortale on 03/03/16.
 */
public class VereadoresFragment extends Fragment implements IUpdateData {
    private Activity activity;

    private LinearLayout lineVereadores;
    private ArrayList<Vereador> vereadoresList;
    private ProgressDialog progressDialog;
    private AldermenController aldermenController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vInflater = inflater.inflate(br.gov.ms.camara.R.layout.fragment_vereadores, container, false);

        lineVereadores = (LinearLayout) vInflater.findViewById(br.gov.ms.camara.R.id.lineVereadores);
        findVereadores();

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

    private void findVereadores() {
        aldermenController = new AldermenController() {};
        vereadoresList = aldermenController.getVereadoresFromSharedPreferences(activity);

        if (vereadoresList == null || vereadoresList.isEmpty()) {
            aldermenController = new AldermenController(JSONCons.getBaseURL()) {
                @Override
                protected void onInit() {
                    super.onInit();
                    iniciaConsulta();
                }

                @Override
                protected void result(JSONObject jsonObject) {
                    super.result(jsonObject);

                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("aldermen");

                        vereadoresList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String strJson = jsonArray.get(i).toString();
                            JSONObject auxJsonObject = new JSONObject(strJson);
                            //Log.d("LOG PARSER", auxJsonObject.getString("name"));

                            Vereador vereador = new Vereador();
                            vereador.setId(auxJsonObject.getInt("id"));
                            vereador.setVereadorNome(auxJsonObject.getString("name"));
                            vereador.setVereadorBiografia(auxJsonObject.getString("biografia"));
                            vereador.setVereadorFoto(auxJsonObject.getString("foto"));
                            vereador.setVereadorPartido(auxJsonObject.getString("partido"));
                            vereador.setVereadorSite(auxJsonObject.getString("website"));
                            vereador.setVereadorEmail(auxJsonObject.getString("email"));

                            //telefone
                            ArrayList<VereadorTelefone> vereadorTelefoneArrayList = new ArrayList<VereadorTelefone>();

                            if (!auxJsonObject.isNull("phones")) {
                                JSONArray jsonArrayVereadorTelefone = auxJsonObject.getJSONArray("phones");
                                for (int j = 0; j < jsonArrayVereadorTelefone.length(); j++) {
                                    VereadorTelefone vereadorTelefone = new VereadorTelefone();
                                    String strJsonTelefone = jsonArrayVereadorTelefone.get(j).toString();
                                    JSONObject jsonObjectTelefone = new JSONObject(strJsonTelefone);

                                    String telefoneVereadorTelefone = jsonObjectTelefone.getString("phone");
                                    vereadorTelefone.setTelefone(telefoneVereadorTelefone);
                                    vereadorTelefoneArrayList.add(vereadorTelefone);
                                }
                            }

                            vereador.setVereadorTelefone(vereadorTelefoneArrayList);
                            vereadoresList.add(vereador);

                            finalizaConsulta();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    populaVereadores();
                }

                @Override
                protected void timeoutUsersCallback() {
                    super.timeoutUsersCallback();
                }
            };
            aldermenController.getAldermen();
        }

        else {
            populaVereadores();
        }
    }

    public void populaVereadores() {
        for (final Vereador vereador : vereadoresList) {
            final LinearLayout lineVereador = new LinearLayout(getActivity());
            LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsLinearLayout.setMargins(5, 2, 5, 2);
            lineVereador.setLayoutParams(layoutParamsLinearLayout);
            lineVereador.setOrientation(LinearLayout.HORIZONTAL);
            lineVereador.setTag(vereador);
            lineVereador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.vereadorConsultado = (Vereador) lineVereador.getTag();
                    ((MainActivity) getActivity()).setFragment(EnumFragmentScreens.VEREADORCONSULTADO, null, null);
                }
            });

            LinearLayout lineImgVereador = new LinearLayout(getActivity());
            LinearLayout.LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
            imgLayoutParams.setMargins(5, 2, 5, 2);
            lineImgVereador.setGravity(Gravity.CENTER);
            lineImgVereador.setLayoutParams(imgLayoutParams);

            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            Drawable imgDrawable = getResources().getDrawable(getResources().getIdentifier(vereador.getVereadorFoto().replace(".png", ""), "drawable", getActivity().getPackageName()));
            imageView.setImageDrawable(imgDrawable);
            lineImgVereador.addView(imageView);

            LinearLayout lineTitulo = new LinearLayout(getActivity());
            LinearLayout.LayoutParams tituloLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 6);
            tituloLayoutParams.setMargins(5, 2, 5, 2);
            tituloLayoutParams.gravity = Gravity.CENTER;
            lineTitulo.setOrientation(LinearLayout.VERTICAL);
            lineTitulo.setLayoutParams(tituloLayoutParams);

            TextView textTitulo = new TextView(getActivity());
            textTitulo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textTitulo.setText(vereador.getVereadorNome());
            textTitulo.setTextColor(Color.parseColor("#024da1"));
            textTitulo.setTextSize(18.0f);
            textTitulo.setTypeface(null, Typeface.BOLD);
            textTitulo.setGravity(Gravity.CENTER);
            lineTitulo.addView(textTitulo);

            LinearLayout lineImgBalao = new LinearLayout(getActivity());
            LinearLayout.LayoutParams imgBalaoLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
            imgBalaoLayoutParams.gravity = Gravity.CENTER;
            imgBalaoLayoutParams.setMargins(5, 2, 5, 2);
            lineImgBalao.setGravity(Gravity.CENTER);
            lineImgBalao.setLayoutParams(imgBalaoLayoutParams);

            ImageView imgBalao = new ImageView(getActivity());
            imgBalao.setLayoutParams(imgBalaoLayoutParams);
            imgBalao.setImageDrawable(getResources().getDrawable(br.gov.ms.camara.R.drawable.img_balao_solicitacao));
            lineImgBalao.addView(imgBalao);

            lineVereador.removeAllViews();

            lineVereador.addView(lineImgVereador);
            lineVereador.addView(lineTitulo);
            lineVereador.addView(lineImgBalao);

            lineVereadores.addView(lineVereador);
        }
    }

    @Override
    public void updateData() {
        findVereadores();
    }
}



