package br.gov.ms.camara.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.gov.ms.camara.R;
import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.UsersController;
import br.gov.ms.camara.model.User;
import br.gov.ms.camara.util.CustomToast;
import br.gov.ms.camara.util.FormUtil;
import br.gov.ms.camara.util.FormValidate;

/**
 * Created by rodolfoortale on 6/17/16.
 */
public class EsqueceuSenhaDialog extends Dialog {
    private static final String TAG = "EsqueceuSenhaDialog";

    private Activity activity;

    private Dialog dialog;

    private ProgressDialog progressDialog;

    private EditText edtEmail;
    private LinearLayout lineEmail;
    private ImageButton btnClose;
    private Button btnDialogOk;

    private UsersController usersController;

    public EsqueceuSenhaDialog(Context context, int theme) {
        super(context, theme);
    }

    public EsqueceuSenhaDialog(Activity activity) {
        super(activity);
        this.activity = activity;

        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(FormUtil.debug);

        dialog = FormUtil.getDefaultDialogProperties(this, R.layout.dialog_esqueceu_senha);

        edtEmail  = (EditText) dialog.findViewById(R.id.edtEmail);
        lineEmail = (LinearLayout) dialog.findViewById(R.id.lineEmail);
        btnClose  = (ImageButton) dialog.findViewById(R.id.btnClose);
        btnDialogOk = (Button) dialog.findViewById(R.id.btnDialogOk);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFormEmail()) {
                    sendEmail();
                }
            }
        });

        dialog.show();
    }

    private void sendEmail() {
        if (validateFormEmail()) {
            usersController = new UsersController(JSONCons.getBaseURL()) {
                @Override
                protected void onInit() {
                    super.onInit();
                    iniciaConsulta("Aguarde enquanto enviamos um e-mail com o link para alteração de senha");
                }

                @Override
                protected void result(JSONObject jsonObject) {
                    super.result(jsonObject);

                    if (jsonObject != null) {
                        try {
                            Boolean success = jsonObject.getBoolean(JSONCons.keySuccess);

                            finalizaConsulta();

                            if (success) {
                                CustomToast.show(activity, "E-mail com o link para alteração de senha foi enviado para " + edtEmail.getText().toString(), CustomToast.TOAST_WARNING, Toast.LENGTH_LONG);
                                dialog.dismiss();
                            }

                            else {
                                CustomToast.show(activity, "Verifique se este e-mail informado está realmente cadastrado em nosso sistema.", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    else {
                        CustomToast.show(activity, "Houve um erro ao tentar enviar o e-mail.", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                    }

                    finalizaConsulta();
                }

                @Override
                protected void timeoutUsersCallback() {
                    super.timeoutUsersCallback();
                }
            };
            usersController.alterarSenha(edtEmail.getText().toString());
        }
    }

    private boolean validateFormEmail() {
        if (edtEmail.getText().toString().isEmpty()) {
            CustomToast.show(activity, "Informe seu e-mail de cadastro.", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
            return false;
        }

        else if (!FormValidate.isValidEmail(edtEmail.getText().toString())) {
            CustomToast.show(activity, "E-mail inválido.", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    private void iniciaConsulta(String message) {
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }

    private void finalizaConsulta() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
