package br.gov.ms.camara.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.gov.ms.camara.LoginActivity;
import br.gov.ms.camara.MainActivity;
import br.gov.ms.camara.R;
import br.gov.ms.camara.asynctask.EmailSender;
import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.AldermenController;
import br.gov.ms.camara.controller.RequestsContoller;
import br.gov.ms.camara.controller.UsersController;
import br.gov.ms.camara.enums.EnumCameraOptions;
import br.gov.ms.camara.enums.EnumFragmentScreens;
import br.gov.ms.camara.model.User;
import br.gov.ms.camara.model.solicitacao.RequestStatus;
import br.gov.ms.camara.model.solicitacao.RequestType;
import br.gov.ms.camara.model.solicitacao.Solicitacao;
import br.gov.ms.camara.model.vereador.Vereador;
import br.gov.ms.camara.model.vereador.VereadorTelefone;
import br.gov.ms.camara.util.CustomToast;
import br.gov.ms.camara.util.Mail;
import br.gov.ms.camara.util.Utilities;

/**
 * Created by rodolfoortale on 03/03/16.
 */
public class SolicitacaoConsultadaFragment extends Fragment implements LocationListener {
    private static final long MIN_TIME_BW_UPDATES = 100;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1.0f;

    private TextView lbNomeSolicitacao;
    private TextView txtEnderecoSolicitacao;
    private ImageView imgTirarFoto;
    private Button btnLocalizarEndereco;
    private Spinner spnVereadores;
    private EditText txtDescricao;
    private Button btnEnviarSolicitacao;

    private RequestType requestType;
    private Vereador vereadorSelecionado;

    private Address address;
    public static Location location;

    private boolean canGetLocation;

    static final int GET_GPS_ENABLED = 1;
    static final int RESULT_OK       = 0;

    private String DIRETORIOROOTAPP = Environment.getExternalStorageDirectory().toString();
    private String DIRETORIOFOTOS = "/Solicitacao/Fotos";
    private String imageFilePath = "";
    private Bitmap bitmapFotoSolicitacao = null;
    private ProgressDialog progressDialog;
    private ArrayList<Vereador> vereadoresList;
    private AldermenController aldermenController;

    private UsersController usersController;
    private User user;

    private String latitude;
    private String longitude;

    private Mail mail;

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vInflater = inflater.inflate(R.layout.fragment_solicitacao_consultada, container, false);

        requestType = Utilities.requestTypeConsultada;

        lbNomeSolicitacao = (TextView) vInflater.findViewById(R.id.lbNomeSolicitacao);
        lbNomeSolicitacao.setText(requestType.getNomeSolicitacao());

        btnEnviarSolicitacao = (Button) vInflater.findViewById(R.id.btnEnviarSolicitacao);
        btnEnviarSolicitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (formValidate()) {
                    sendRequest();
                }
            }
        });

        txtEnderecoSolicitacao = (TextView) vInflater.findViewById(R.id.txtEnderecoSolicitacao);
        txtEnderecoSolicitacao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                latitude = "";
                longitude = "";
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtDescricao = (EditText) vInflater.findViewById(R.id.txtDescricao);

        spnVereadores = (Spinner) vInflater.findViewById(R.id.spnVereadores);
        spnVereadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vereadorSelecionado = (Vereador) spnVereadores.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imgTirarFoto = (ImageView) vInflater.findViewById(R.id.imgTirarFoto);
        imgTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_photo_choice);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

                final RadioGroup radGroupOpcaoFoto = (RadioGroup) dialog.findViewById(R.id.radGroupOpcaoFoto);

                RadioButton radioTakePhoto = (RadioButton) dialog.findViewById(R.id.radioTakePhoto);
                radioTakePhoto.setTag(EnumCameraOptions.TAKEPHOTO);

                RadioButton radioChoosePhoto = (RadioButton) dialog.findViewById(R.id.radioChoosePhoto);
                radioChoosePhoto.setTag(EnumCameraOptions.CHOOSEFROMGALLERY);

                Button btnDialogOk = (Button) dialog.findViewById(R.id.btnDialogOk);
                btnDialogOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        // get selected radio button from radioGroup
                        int selectedId = radGroupOpcaoFoto.getCheckedRadioButtonId();

                        // find the radiobutton by returned id
                        RadioButton radioOpcaoFoto = (RadioButton) dialog.findViewById(selectedId);

                        EnumCameraOptions idOpcaoFoto = (EnumCameraOptions) radioOpcaoFoto.getTag();

                        openCamera(idOpcaoFoto);
                    }
                });

                dialog.show();
            }
        });

        btnLocalizarEndereco = (Button) vInflater.findViewById(R.id.btnLocalizarEndereco);
        btnLocalizarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuaBuscaEndereco();
            }
        });

        findVereadores();

        return vInflater;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    private void openCamera(EnumCameraOptions idOpcaoFoto) {
        Intent intent;
        File photoFile = null;
        switch (idOpcaoFoto) {
            case TAKEPHOTO:
                /*
                if (criarArquivo() != null) {
                    photoFile = criarArquivo(); // createImageFile();

                    // Continue only if the File was successfully created
                    imageFilePath = photoFile.getAbsolutePath();
                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(intent, EnumCameraOptions.TAKEPHOTO.getId());
                }
                */

                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    if (criarArquivo() != null) {
                        photoFile = criarArquivo(); // createImageFile();
                        // Continue only if the File was successfully created
                        imageFilePath = photoFile.getAbsolutePath();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(intent, EnumCameraOptions.TAKEPHOTO.getId());
                    }
                }
                break;

            case CHOOSEFROMGALLERY:
                //intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivityForResult(Intent.createChooser(intent, "Select Picture"), EnumCameraOptions.CHOOSEFROMGALLERY.getId());

                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, EnumCameraOptions.CHOOSEFROMGALLERY.getId());

                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EnumCameraOptions.TAKEPHOTO.getId() && resultCode != 0) {
            bitmapFotoSolicitacao = Utilities.decodeSampledBitmapFromFile(imageFilePath, 300, 300);
            imgTirarFoto.setImageBitmap(bitmapFotoSolicitacao);
        }

        else if (data != null) {
            Uri selectedImageUri = data.getData();
            imageFilePath = getPath(selectedImageUri);


            //Uri _uri = data.getData();

            //User had pick an image.
            //Cursor cursor = activity.getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            //cursor.moveToFirst();

            //Link to the image
            //imageFilePath = cursor.getString(0);

            bitmapFotoSolicitacao = Utilities.decodeSampledBitmapFromFile(imageFilePath, 300, 300);

            if (bitmapFotoSolicitacao != null) {
                imgTirarFoto.setImageBitmap(bitmapFotoSolicitacao);
            }

            //cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

public void iniciaConsulta(String msg) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Processando...");
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public void finalizaConsulta() {
        progressDialog.dismiss();
    }

    private void findVereadores() {
        aldermenController = new AldermenController(JSONCons.getURLFindVereadores()) {
            @Override
            protected void onInit() {
                super.onInit();
                iniciaConsulta("Carregando lista de vereadores");
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

                carregaSpinner();
            }

            @Override
            protected void timeoutUsersCallback() {
                super.timeoutUsersCallback();
            }
        };
        aldermenController.getAldermen();
    }

    public File criarArquivo(){
        Date dataCriacao = new Date();

        String formatoSave = "ddMMyyyyHHmmss";

        File hashObjetoDiretorio = new File(DIRETORIOROOTAPP + DIRETORIOFOTOS);
        File hashObjetoArquivo = new File(hashObjetoDiretorio, File.separator + new SimpleDateFormat(formatoSave).format(dataCriacao) + ".jpg");

        try {
            if(!hashObjetoDiretorio.exists()){
                hashObjetoDiretorio.mkdirs();
            }

            if(!hashObjetoArquivo.exists()){
                hashObjetoArquivo.createNewFile();
            }

            return hashObjetoArquivo;
        } catch (FileNotFoundException e) {
            //Log.e("//Log CRIACAO ARQUIVO", "Problemas ao encontrar objeto: " + e.getMessage());
        } catch (IOException e) {
            //Log.e("//Log CRIACAO ARQUIVO", "Problemas na escrita do arquivo: " + e.getMessage());
        }

        return null;
    }

    public void efetuaBuscaEndereco() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            Toast.makeText(activity, "Habilite sua localização para melhor precisão de sua localização.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        location = getLocation();
        if (location != null) {
            try {
                address = Utilities.getAddressForLocation(activity, location);
                txtEnderecoSolicitacao.setText(address.getThoroughfare() + ", " + address.getSubThoroughfare());
                //Log.v("SolicitacaoConsultadaFragment", "rua: " + address.getThoroughfare());
            } catch (IOException ioExc) {
                Toast.makeText(activity, "Erro ao consultar localização.", Toast.LENGTH_LONG).show();
            }
        }

        else {
            Toast.makeText(activity, "Não foi possível obter sua localização. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
        }
    }

    private void carregaSpinner() {
        ArrayAdapter<Vereador> adapter = new ArrayAdapter<>(activity, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        Vereador vereador = new Vereador();
        vereador.setId(0);
        vereador.setVereadorNome(getString(R.string.lbSelecionarUmVereador));
        adapter.add(vereador);
        for (Vereador vereadorAux: vereadoresList) {
            adapter.add(vereadorAux);
        }
        spnVereadores.setAdapter(adapter);
    }

    public Location getLocation() {
        try {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    //Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //Log.v("SolicitacaoConsultadaFragment", "location: " + location.getLatitude());

                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        //Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                //Log.v("SolicitacaoConsultadaFragment", "location: " + location.getLatitude());

                                latitude = String.valueOf(location.getLatitude());
                                longitude = String.valueOf(location.getLongitude());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        SolicitacaoConsultadaFragment.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private boolean formValidate() {
        if (txtEnderecoSolicitacao.getText().toString().isEmpty()) {
            CustomToast.show(activity, "Por favor, preencha um endereço.", CustomToast.TOAST_WARNING, Toast.LENGTH_LONG);
            return false;
        }

        return true;
    }

    private void sendRequest() {
        usersController = new UsersController() {};
        user = usersController.getUserFromSharedPreferences(activity);

        RequestsContoller requestsContoller = new RequestsContoller(JSONCons.getBaseURL()) {
            @Override
            protected void onInit() {
                super.onInit();
                iniciaConsulta("Enviando requisição");
            }

            @Override
            protected void result(JSONObject jsonObject) {
                super.result(jsonObject);

                finalizaConsulta();

                if (jsonObject != null) {
                    try {
                        Boolean success = jsonObject.getBoolean(JSONCons.keySuccess);

                        if (success) {
                            new AlertDialog.Builder(activity)
                                    //new AlertDialog.Builder(activity)
                                    .setTitle("Solicitação enviada")
                                    .setMessage("Sua solicitação foi enviada.")
                                    .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            sendEmail();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            ((MainActivity) activity).setFragment(EnumFragmentScreens.SOLICITACOES, null, null);
                        }

                        else {
                            CustomToast.show(activity, "Houve um erro ao enviar sua solicitação", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    CustomToast.show(activity, "Houve um erro ao enviar sua solicitação", CustomToast.TOAST_WARNING, Toast.LENGTH_SHORT);
                }
            }

            @Override
            protected void timeoutUsersCallback() {
                super.timeoutUsersCallback();
                finalizaConsulta();
            }
        };

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setLatitude(latitude != null ? latitude : "");
        solicitacao.setLongitude(longitude != null ? longitude : "");
        solicitacao.setEndereco(txtEnderecoSolicitacao.getText().toString());

        RequestStatus requestStatus = new RequestStatus();
        requestStatus.setId(2);
        solicitacao.setSituacao(requestStatus);

        solicitacao.setRequestType(requestType);
        solicitacao.setUser(user);

        requestsContoller.saveRequest(solicitacao, bitmapFotoSolicitacao);
    }

    private void sendEmail() {
        String name = user.getName();
        String email = user.getUsername();
        String telefone = user.getTelefone();

        // Dados do usuário a serem enviados no email
        String auxDadosUsuario = "Nome: " + name +
                "\nEmail: " + email +
                "\nTelefone: " + telefone + "\n\n";

        String auxTxtEndereco  = txtEnderecoSolicitacao.getText() != null ? txtEnderecoSolicitacao.getText().toString() : "";
        String auxTxtDescricao = txtDescricao.getText() != null ? txtDescricao.getText().toString() : "";

        String textBody = auxDadosUsuario +
                "Endereço da solicitação: " + auxTxtEndereco +
                "\nDescrição da solicitação: " + auxTxtDescricao;

        String vereadorEmail = "";
        String[] emailTo = null;

        if (vereadorSelecionado != null && vereadorSelecionado.getVereadorEmail() != null) {
            vereadorEmail = vereadorSelecionado.getVereadorEmail();
            emailTo = new String[2];
            emailTo[0] = "camaraativacg@gmail.com";
            emailTo[1] = vereadorEmail;
        }

        else {
            emailTo = new String[1];
            //emailTo[0] = "ortale22@gmail.com";
            emailTo[0] = "camaraativacg@gmail.com";
        }

        new EmailSender(activity, textBody, email, requestType.getNomeSolicitacao(), imageFilePath, emailTo) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                /*
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
                */
            }

            @Override
            protected void timeoutCallback() {
                super.timeoutCallback();
                finalizaConsulta();

                //CustomToast.show(activity, "Verifique sua conexão com a internet", CustomToast.TOAST_ERROR, Toast.LENGTH_SHORT);
            }
        }.execute();
    }
}
