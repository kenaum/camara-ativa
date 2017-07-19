package br.gov.ms.camara;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.UsersController;
import br.gov.ms.camara.model.User;
import br.gov.ms.camara.util.CustomToast;
import br.gov.ms.camara.util.FormUtil;
import br.gov.ms.camara.util.FormValidate;

/**
 * Created by rodolfoortale on 12/03/16.
 */
public class StoreActivity extends Activity {

    private Button loginButton;
    private Button btnStore;
    private EditText txtUser;
    private EditText txtPassword;
    private EditText txtNome;
    private EditText txtTelefone;

    CallbackManager callbackManager;

    private UsersController usersController;
    private User user;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_store);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(FormUtil.debug);

        loginButton = (Button) findViewById(R.id.btnLoginFace);
        btnStore = (Button) findViewById(R.id.btnStore);
        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtNome = (EditText) findViewById(R.id.txtNome);
        txtTelefone = (EditText) findViewById(R.id.txtTelefone);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFblogin();
            }
        });

        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    usersController = new UsersController(JSONCons.getBaseURL()) {
                        @Override
                        protected void onInit() {
                            super.onInit();
                            iniciaConsulta("Aguarde enquanto seus dados estão sendo enviados.");
                        }

                        @Override
                        protected void result(JSONObject jsonObject) {
                            super.result(jsonObject);

                            if (jsonObject != null) {
                                try {
                                    Boolean success = jsonObject.getBoolean(JSONCons.keySuccess);
                                    Integer id = jsonObject.getInt(JSONCons.keyIdUser);

                                    if (success) {
                                        user = getUserObj();
                                        user.setId(id);
                                        user.setLogged(true);
                                        usersController = new UsersController() {};
                                        usersController.saveUserToSharedPreferences(StoreActivity.this, user);

                                        Intent intent = new Intent(StoreActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    else {
                                        CustomToast.show(StoreActivity.this, "Houve um erro ao cadastrar o usuário", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            else {
                                CustomToast.show(StoreActivity.this, "Houve um erro ao cadastrar o usuário", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                            }

                            finalizaConsulta();
                        }

                        @Override
                        protected void timeoutUsersCallback() {
                            super.timeoutUsersCallback();
                        }
                    };
                    user = getUserObj();
                    usersController.save(user);
                }
            }
        });

        setValues();

        /*
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });

                                                String email = json.getString("name");
                                                String name = json.getString("email");

                                                txtUser.setText(email);
                                                txtNome.setText(name);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setValues() {
        if (FormUtil.debug) {
            txtUser.setText("ortale_@gmail.com");
            txtPassword.setText("teste1234");
            txtNome.setText("Rodolfo");
            txtTelefone.setText("(67) 9273-1220");
        }
    }

    // Private method to handle Facebook login and callback
    private void onFblogin() {
        callbackManager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email, user_birthday"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");

                                    txtUser.setText(email);
                                    txtNome.setText(name);

                                    LoginManager.getInstance().logOut();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                //Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                //Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }

    private User getUserObj() {
        user = new User();
        user.setUsername(txtUser.getText().toString());
        user.setPassword(txtPassword.getText().toString());
        user.setName(txtNome.getText().toString());
        user.setTelefone(txtTelefone.getText().toString());
        user.setIsBlocked(false);

        return user;
    }

    private Boolean validateForm() {
        if (!FormValidate.isValidEmail(txtUser.getText().toString()) || !FormValidate.isEmailInRange(txtUser.getText().toString())) {
            CustomToast.show(this, getString(R.string.errorUsuarioInvalido), CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
            return false;
        }

        else if(!FormValidate.isPasswordInRange(txtPassword.getText().toString())) {
            CustomToast.show(this, getString(R.string.errorSenhaInvalida), CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
            return false;
        }

        else if(txtNome.getText().toString().isEmpty()) {
            CustomToast.show(this, "Preencha o campo de nome", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
            return false;
        }

        else if(txtTelefone.getText().toString().isEmpty()) {
            CustomToast.show(this, "Preencha o campo de telefone", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
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