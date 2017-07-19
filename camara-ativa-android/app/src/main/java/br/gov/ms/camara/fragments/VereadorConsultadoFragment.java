package br.gov.ms.camara.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.gov.ms.camara.MainActivity;
import br.gov.ms.camara.asynctask.EmailSender;
import br.gov.ms.camara.controller.UsersController;
import br.gov.ms.camara.dialogs.AldermanBioDialog;
import br.gov.ms.camara.enums.EnumFragmentScreens;
import br.gov.ms.camara.model.User;
import br.gov.ms.camara.model.vereador.Vereador;
import br.gov.ms.camara.util.CustomToast;
import br.gov.ms.camara.util.Utilities;

/**
 * Created by rodolfoortale on 03/03/16.
 */
public class VereadorConsultadoFragment extends Fragment {
    private Vereador vereador;

    private TextView lbNomeVereador;
    private TextView txtTelVereador;
    private TextView txtEmailVereador;
    private TextView txtWebsiteVereador;
    private EditText txtDescricao;
    private ImageView imgVereador;
    private Button btnMostrarBiografia;
    private Button btnSalvarForm;

    private Activity activity;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vInflater = inflater.inflate(br.gov.ms.camara.R.layout.fragment_vereador_consultado, container, false);

        vereador = Utilities.vereadorConsultado;

        lbNomeVereador = (TextView) vInflater.findViewById(br.gov.ms.camara.R.id.lbNomeVereador);
        txtTelVereador = (TextView) vInflater.findViewById(br.gov.ms.camara.R.id.txtTelVereador);
        txtEmailVereador = (TextView) vInflater.findViewById(br.gov.ms.camara.R.id.txtEmailVereador);
        txtWebsiteVereador = (TextView) vInflater.findViewById(br.gov.ms.camara.R.id.txtWebsiteVereador);
        txtDescricao = (EditText) vInflater.findViewById(br.gov.ms.camara.R.id.txtDescricao);

        lbNomeVereador.setText(vereador.getVereadorNome());

        String auxTelefone = "";
        for (int i = 0; i < vereador.getVereadorTelefone().size(); i++) {
            auxTelefone += vereador.getVereadorTelefone().get(i).getTelefone();
            //Log.v("VereadorConsultadoFragment", "telefone: " + auxTelefone);

            if (i < vereador.getVereadorTelefone().size()-1)
                auxTelefone += ", ";
        }
        txtTelVereador.setText(auxTelefone);

        txtEmailVereador.setText(vereador.getVereadorEmail());
        txtWebsiteVereador.setText(vereador.getVereadorSite());

        btnMostrarBiografia = (Button) vInflater.findViewById(br.gov.ms.camara.R.id.btnMostrarBiografia);
        btnMostrarBiografia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AldermanBioDialog(activity, vereador);
            }
        });

        btnSalvarForm = (Button) vInflater.findViewById(br.gov.ms.camara.R.id.btnSalvarForm);
        btnSalvarForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        imgVereador = (ImageView) vInflater.findViewById(br.gov.ms.camara.R.id.imgVereador);
        Drawable imgDrawable = getResources().getDrawable(getResources().getIdentifier(vereador.getVereadorFoto().replace(".png", ""), "drawable", getActivity().getPackageName()));
        imgVereador.setImageDrawable(imgDrawable);

        return vInflater;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    private void sendEmail() {
        UsersController usersController = new UsersController() {};
        User user = usersController.getUserFromSharedPreferences(activity);

        String name = user.getName();
        String email = user.getUsername();
        String telefone = user.getTelefone();

        // Dados do usuário a serem enviados no email
        String auxDadosUsuario = "Nome: " + name +
                "\nEmail: " + email +
                "\nTelefone: " + telefone + "\n\n";
        String auxTxtDescricao = txtDescricao.getText() != null ? txtDescricao.getText().toString() : "";

        String textBody = auxDadosUsuario +
                "\nDescrição: " + auxTxtDescricao;

        String[] emailTo = { vereador.getVereadorEmail() };

        new EmailSender(activity, textBody, email, "Fale com seu vereador", null, emailTo) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                iniciaConsulta("Enviando email para o vereador selecionado");
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                finalizaConsulta();

                if (aBoolean != null) {
                    if (aBoolean) {
                        CustomToast.show(activity, "Email enviado com sucesso", CustomToast.TOAST_SUCCESS, Toast.LENGTH_SHORT);
                        ((MainActivity) activity).setFragment(EnumFragmentScreens.VEREADORES, null, null);
                    }

                    else {
                        CustomToast.show(activity, "Houve um erro ao tentar enviar o email.", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                    }
                }

                else {
                    CustomToast.show(activity, "Houve um erro ao tentar enviar o email.", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                }
            }

            @Override
            protected void timeoutCallback() {
                super.timeoutCallback();
                finalizaConsulta();

                CustomToast.show(activity, "Verifique sua conexão com a internet", CustomToast.TOAST_ERROR, Toast.LENGTH_SHORT);
            }
        }.execute();
    }

    public void iniciaConsulta(String msg) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Processando...");
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public void finalizaConsulta() {
        progressDialog.dismiss();
    }
}
