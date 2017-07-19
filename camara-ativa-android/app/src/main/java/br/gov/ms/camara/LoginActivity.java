package br.gov.ms.camara;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.UsersController;
import br.gov.ms.camara.dialogs.EsqueceuSenhaDialog;
import br.gov.ms.camara.model.User;
import br.gov.ms.camara.util.CustomToast;
import br.gov.ms.camara.util.FormUtil;
import br.gov.ms.camara.util.FormValidate;

/**
 * Created by rodolfoortale on 03/03/16.
 */
public class LoginActivity extends Activity {
    private EditText txtUser;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnCadastro;
    private Button btnEsqueciSenha;

    private UsersController usersController;
    private User user;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(FormUtil.debug);

        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCadastro = (Button) findViewById(R.id.btnCadastro);
        btnEsqueciSenha = (Button) findViewById(R.id.btnEsqueciSenha);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    usersController = new UsersController(JSONCons.getBaseURL()) {
                        @Override
                        protected void onInit() {
                            super.onInit();
                            iniciaConsulta("Carregando dados de usuário");
                        }

                        @Override
                        protected void result(JSONObject jsonObject) {
                            super.result(jsonObject);

                            if (jsonObject != null) {
                                try {
                                    Boolean success = jsonObject.getBoolean(JSONCons.keySuccess);

                                    if (success) {
                                        JSONObject objUser = jsonObject.getJSONObject(JSONCons.keyUser);

                                        Integer id = objUser.getInt(JSONCons.keyId);
                                        String userName = objUser.getString(JSONCons.keyUser);
                                        String name = objUser.getString(JSONCons.keyNome);
                                        Boolean isBlocked = objUser.getBoolean(JSONCons.keyIsBlocked);

                                        User user = new User();
                                        user.setId(id);
                                        user.setUsername(userName);
                                        user.setName(name);
                                        user.setLogged(true);

                                        if (!isBlocked) {
                                            usersController = new UsersController() {};
                                            usersController.saveUserToSharedPreferences(LoginActivity.this, user);

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        else {
                                            CustomToast.show(LoginActivity.this, "Sua conta está bloqueada", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                                        }
                                    }

                                    else {
                                        CustomToast.show(LoginActivity.this, "Usuário ou senha inválidos", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                                    }

                                    finalizaConsulta();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            else {
                                CustomToast.show(LoginActivity.this, "Houve um erro ao tentar efetuar o login", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                            }

                            finalizaConsulta();
                        }

                        @Override
                        protected void timeoutUsersCallback() {
                            super.timeoutUsersCallback();
                        }
                    };
                    usersController.login(txtUser.getText().toString(), txtPassword.getText().toString());
                }
            }
        });

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, StoreActivity.class);
                startActivity(intent);
            }
        });

        btnEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EsqueceuSenhaDialog(LoginActivity.this);
            }
        });

        setValues();
    }

    private Boolean validateForm() {
        if (!FormValidate.isValidEmail(txtUser.getText().toString()) || !FormValidate.isEmailInRange(txtUser.getText().toString())) {
            CustomToast.show(this, getString(R.string.errorUsuarioInvalido), CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    private void setValues() {
        if (FormUtil.debug) {
            txtUser.setText("ortale22@gmail.com");
            txtPassword.setText("teste1234");
        }
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