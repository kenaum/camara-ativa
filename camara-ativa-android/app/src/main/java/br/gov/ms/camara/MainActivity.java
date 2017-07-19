package br.gov.ms.camara;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.gov.ms.camara.asynctask.JSONCons;
import br.gov.ms.camara.controller.AldermenController;
import br.gov.ms.camara.controller.RequestsContoller;
import br.gov.ms.camara.controller.UsersController;
import br.gov.ms.camara.enums.EnumFragmentScreens;
import br.gov.ms.camara.fragments.ComoFuncionaFragment;
import br.gov.ms.camara.fragments.NavigationDrawerFragment;
import br.gov.ms.camara.fragments.NoticiaConsultadaFragment;
import br.gov.ms.camara.fragments.NoticiasCamaraFragment;
import br.gov.ms.camara.fragments.SolicitacaoConsultadaFragment;
import br.gov.ms.camara.fragments.SolicitacoesFragment;
import br.gov.ms.camara.fragments.VereadorConsultadoFragment;
import br.gov.ms.camara.fragments.VereadoresFragment;
import br.gov.ms.camara.interfaces.IUpdateData;
import br.gov.ms.camara.model.User;
import br.gov.ms.camara.model.solicitacao.RequestType;
import br.gov.ms.camara.model.vereador.Vereador;
import br.gov.ms.camara.model.vereador.VereadorTelefone;
import br.gov.ms.camara.util.CustomToast;
import br.gov.ms.camara.util.ScheduledService;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private final String TAG = "MainActivity";

    public static int REQUEST_CODE;

    private String screenTitle = "Principal";
    private ArrayList<String> previousScreenTitle = new ArrayList<>();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private SolicitacoesFragment solicitacoesFragment;
    private NoticiaConsultadaFragment noticiaConsultadaFragment;
    private NoticiasCamaraFragment noticiasCamaraFragment;
    private SolicitacaoConsultadaFragment solicitacaoConsultadaFragment;
    private VereadorConsultadoFragment vereadorConsultadoFragment;
    private VereadoresFragment vereadoresFragment;
    private ComoFuncionaFragment comoFuncionaFragment;

    private Bundle bundle;

    private ProgressDialog progressDialog;

    private final String tagSolicitacoesFragment = "SolicitacoesFragment";
    private final String tagNoticiaConsultadaFragment = "NoticiaConsultadaFragment";
    private final String tagNoticiasCamaraFragment = "NoticiasCamaraFragment";
    private final String tagSolicitacaoConsultadaFragment = "SolicitacaoConsultadaFragment";
    private final String tagVereadorConsultadoFragment = "VereadorConsultadoFragment";
    private final String tagVereadoresFragment = "VereadoresFragment";
    private final String tagComoFuncionaFragment = "ComoFuncionaFragment";

    private UsersController usersController;
    private User user;

    private AldermenController aldermenController;
    private RequestsContoller requestsContoller;

    private ArrayList<Vereador> vereadoresList;
    private ArrayList<RequestType> solicitacaoList;

    private IUpdateData iUpdateData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);

        usersController = new UsersController() {};
        user = usersController.getUserFromSharedPreferences(this);

        //initAnalytics();

        bundle = savedInstanceState;

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        previousScreenTitle.add(screenTitle);
        setTitle(screenTitle);
    }

    /*
    private void initAnalytics() {
        analytics = GoogleAnalytics.getInstance(this);
        //analytics.setLocalDispatchPeriod(1800);
        analytics.enableAutoActivityReports(getApplication());

        tracker = analytics.newTracker(R.xml.global_tracker);
        //tracker = analytics.newTracker("UA-70134866-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                tracker,                                        // Currently used Tracker.
                Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                MainActivity.this);                                         // Context of the application.

        // Make myHandler the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(myHandler);

        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
    */

    @Override
    protected void onStart() {
        super.onStart();
        //GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreActionBar();

        ScheduledService scheduledService = new ScheduledService(this);
        //scheduledService.startService();

        usersController = new UsersController() {};
        user = usersController.getUserFromSharedPreferences(this);

        usersController = new UsersController(JSONCons.getBaseURL()) {
            @Override
            protected void onInit() {
                super.onInit();
            }

            @Override
            protected void result(JSONObject jsonObject) {
                super.result(jsonObject);

                if (jsonObject != null) {
                    try {
                        Boolean isBlocked = jsonObject.getBoolean("is_blocked");

                        if (isBlocked) {
                            new AlertDialog.Builder(MainActivity.this)
                                    //new AlertDialog.Builder(activity)
                                    .setTitle("Usuário bloqueado")
                                    .setMessage("Este usuário se encontra bloqueado.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void timeoutUsersCallback() {
                super.timeoutUsersCallback();
            }
        };
        usersController.checkUser(user);

        loadData();
    }

    private void loadData() {
        loadRequests();
    }

    private void loadRequests() {
        requestsContoller = new RequestsContoller(JSONCons.getBaseURL()) {
            @Override
            protected void onInit() {
                super.onInit();
                iniciaConsulta("Carregando dados do servidor");
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

                    requestsContoller = new RequestsContoller() {};
                    requestsContoller.saveSolicitacaoToSharedPreferences(MainActivity.this, solicitacaoList);

                    iUpdateData = new IUpdateData() {
                        @Override
                        public void updateData() {

                        }
                    };
                    iUpdateData.updateData();

                    loadAldermen();
                }

                else {
                    CustomToast.show(MainActivity.this, "Houve um erro ao tentar consultar os dados do servidor. Tente novamente mais tarde.", CustomToast.TOAST_ERROR, Toast.LENGTH_SHORT);
                }

            }

            @Override
            protected void timeoutUsersCallback() {
                super.timeoutUsersCallback();

                CustomToast.show(MainActivity.this, "Houve um erro ao tentar consultar os dados do servidor. Tente novamente mais tarde.", CustomToast.TOAST_ERROR, Toast.LENGTH_SHORT);
            }
        };
        requestsContoller.getSolicitacoes();
    }

    private void loadAldermen() {
        aldermenController = new AldermenController(JSONCons.getBaseURL()) {
            @Override
            protected void onInit() {
                super.onInit();
            }

            @Override
            protected void result(JSONObject jsonObject) {
                super.result(jsonObject);

                if (jsonObject != null) {
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

                    aldermenController = new AldermenController() {};
                    aldermenController.saveVereadorToSharedPreferences(MainActivity.this, vereadoresList);

                    iUpdateData = new IUpdateData() {
                        @Override
                        public void updateData() {

                        }
                    };
                    iUpdateData.updateData();
                }

                else {
                    CustomToast.show(MainActivity.this, "Houve um erro ao tentar consultar as solicitações.", CustomToast.TOAST_ERROR, Toast.LENGTH_SHORT);
                }
            }

            @Override
            protected void timeoutUsersCallback() {
                super.timeoutUsersCallback();

                CustomToast.show(MainActivity.this, "Houve um erro ao tentar consultar os dados do servidor. Tente novamente mais tarde.", CustomToast.TOAST_ERROR, Toast.LENGTH_SHORT);
            }
        };
        aldermenController.getAldermen();
    }

    /*
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        FragmentTransaction fragmentTransaction = getFragmentTransaction();

        switch (fragment.getTag()) {
            case tagMainFragment:
                quit();
                break;

            case tagListagemVendasFragment:
                setTitle("Principal");
                mainFragment = new MainFragment();
                fragmentTransaction.replace(R.id.container, mainFragment, tagMainFragment);
                break;

            case tagListagemPagamentoContasFragment:
                setTitle("Principal");
                mainFragment = new MainFragment();
                fragmentTransaction.replace(R.id.container, mainFragment, tagMainFragment);
                break;

            case tagListagemTransferenciasFragment:
                setTitle("Principal");
                mainFragment = new MainFragment();
                fragmentTransaction.replace(R.id.container, mainFragment, tagMainFragment);
                break;

            case tagDetalheVendaFragment:
                setTitle("Principal");
                mainFragment = new MainFragment();
                fragmentTransaction.replace(R.id.container, mainFragment, tagMainFragment);
                break;

            case tagTransferenciaFragment:
                setTitle("Transferências");
                listagemTransferenciasFragment = new ListagemTransferenciasFragment();
                fragmentTransaction.replace(R.id.container, listagemTransferenciasFragment, tagListagemTransferenciasFragment);
                break;

            case tagDadosPagamentoFragment:
                setTitle("Venda");
                detalheVendaFragment = new DetalheVendaFragment();
                fragmentTransaction.replace(R.id.container, detalheVendaFragment, tagDetalheVendaFragment);
                break;

            case tagParcelamentoFragment:
                setTitle("Venda");
                detalheVendaFragment = new DetalheVendaFragment();
                fragmentTransaction.replace(R.id.container, detalheVendaFragment, tagDetalheVendaFragment);
                break;

            case tagInformacoesCobrancaFragment:
                setTitle("Venda");
                detalheVendaFragment = new DetalheVendaFragment();
                fragmentTransaction.replace(R.id.container, detalheVendaFragment, tagDetalheVendaFragment);
                break;

            case tagCobrancaEmailFragment:
                setTitle("Venda");
                detalheVendaFragment = new DetalheVendaFragment();
                fragmentTransaction.replace(R.id.container, detalheVendaFragment, tagDetalheVendaFragment);
                break;

            case tagDestinatarioContasFragment:
                setTitle("Nova Transferência");
                transferenciaFragment = new TransferenciaFragment();
                fragmentTransaction.replace(R.id.container, transferenciaFragment, tagTransferenciaFragment);
                break;

            case tagExtratoFragment:
                setTitle("Principal");
                mainFragment = new MainFragment();
                fragmentTransaction.replace(R.id.container, mainFragment, tagMainFragment);
                break;

            case tagPagamentoContaFragment:
                setTitle("Pagamentos");
                listagemPagamentoContasFragment = new ListagemPagamentoContasFragment();
                fragmentTransaction.replace(R.id.container, listagemPagamentoContasFragment, tagListagemPagamentoContasFragment);
                break;
        }

        fragmentTransaction.commit();
    }
    */

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentTransaction fragmentTransaction = getFragmentTransaction();

        Fragment lastFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1);

        switch (position) {
            case 0:
                this.setFragment(EnumFragmentScreens.SOLICITACOES, null, null);
                break;

            case 1:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.camara.ms.gov.br"));
                startActivity(browserIntent);
                //this.setFragment(EnumFragmentScreens.NOTICIASCAMARA, null, null);
                break;

            case 2:
                this.setFragment(EnumFragmentScreens.VEREADORES, null, null);
                break;

            case 3:
                this.setFragment(EnumFragmentScreens.COMOFUNCIONA, null, null);
                break;
        }

        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finish();
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

    public void setScreenTitle(String screenTitle) {
        this.screenTitle = screenTitle;
    }

    public void setFragment(EnumFragmentScreens enumFragmentScreens, Object parameter, Object[] parameters) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction();

        Fragment lastFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1);

        switch (enumFragmentScreens) {
            case COMOFUNCIONA:
                setTitle("Como Reinvidicar?");
                screenTitle = "Como Reinvidicar?";
                comoFuncionaFragment = new ComoFuncionaFragment();
                fragmentTransaction.replace(R.id.container, comoFuncionaFragment, tagComoFuncionaFragment);

                /*
                if (!(lastFragment instanceof DetalheVendaFragment)) {
                    previousScreenTitle.add("Venda");
                    //fragmentTransaction.addToBackStack(null);
                }
                */

                break;

            case SOLICITACOES:
                setTitle("Solicitações");
                screenTitle = "Solicitações";
                solicitacoesFragment = new SolicitacoesFragment();
                fragmentTransaction.replace(R.id.container, solicitacoesFragment, tagSolicitacoesFragment);

                /*
                if (!(lastFragment instanceof DetalheVendaFragment)) {
                    previousScreenTitle.add("Venda");
                    //fragmentTransaction.addToBackStack(null);
                }
                */

                break;

            case SOLICITACAOCONSULTADA:
                setTitle("Solicitação");
                screenTitle = "Solicitação";
                solicitacaoConsultadaFragment = new SolicitacaoConsultadaFragment();
                fragmentTransaction.replace(R.id.container, solicitacaoConsultadaFragment, tagSolicitacaoConsultadaFragment);

                break;

            case VEREADORES:
                setTitle("Fale com seu vereador");
                screenTitle = "Fale com seu vereador";
                vereadoresFragment = new VereadoresFragment();
                fragmentTransaction.replace(R.id.container, vereadoresFragment, tagVereadoresFragment);

                break;

            case VEREADORCONSULTADO:
                setTitle("Fale com seu vereador");
                screenTitle = "Fale com seu vereador";
                vereadorConsultadoFragment = new VereadorConsultadoFragment();
                fragmentTransaction.replace(R.id.container, vereadorConsultadoFragment, tagVereadorConsultadoFragment);

                break;

            case NOTICIASCAMARA:
                setTitle("Notícias da câmara");
                screenTitle = "Notícias da câmara";
                noticiasCamaraFragment = new NoticiasCamaraFragment();
                fragmentTransaction.replace(R.id.container, noticiasCamaraFragment, tagNoticiasCamaraFragment);

                break;

            case NOTICIACONSULTADA:
                setTitle("Notícias da câmara");
                screenTitle = "Notícias da câmara";

                noticiaConsultadaFragment = new NoticiaConsultadaFragment();
                fragmentTransaction.replace(R.id.container, noticiaConsultadaFragment, tagNoticiaConsultadaFragment);

                break;
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /*
    private void googleAnatyticsTest() {
        CobrancaEmailFragment cobrancaEmailFragment = null;
        cobrancaEmailFragment.getActivity();
    }
    */

    private FragmentTransaction getFragmentTransaction() {
        return getSupportFragmentManager().beginTransaction();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);

        // If your minSdkVersion is 11 or higher, instead use:
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.img_bar_logo));
    }

    public void quit() {
        /*
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.tittleSairDoAplicativo))
                .setContentText(getString(R.string.warningSairDoAplicativo))
                .setConfirmText(getString(R.string.labelSim))
                .setCancelText(getString(R.string.labelNao))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        ContasController contasController = new ContasController() {};
                        Conta conta = contasController.getContaFromSharedPreferences(MainActivity.this);
                        conta.setIsLogged(false);
                        conta.setRealizouConsultaDados(false);
                        contasController.saveContaToSharedPreferences(MainActivity.this, conta);
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .show();
                */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            //setTitle(screenTitle);
            return true;
        }

        else {
            setTitle("Câmara Ativa");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_info_app);
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

            Button btnDialogOk = (Button) dialog.findViewById(R.id.btnDialogOk);
            btnDialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            return true;
        }

        if (id == R.id.action_sair) {
            user.setLogged(false);
            usersController.saveUserToSharedPreferences(this, user);

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
