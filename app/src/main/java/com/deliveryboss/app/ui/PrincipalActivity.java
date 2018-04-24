package com.deliveryboss.app.ui;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deliveryboss.app.data.api.model.MessageEvent;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.data.util.Utilidades;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.CircleTransform;
import com.deliveryboss.app.data.api.model.ApiResponseMantenimiento;
import com.deliveryboss.app.data.api.model.EmpresasBody;
import com.deliveryboss.app.data.api.model.Mantenimiento;
import com.deliveryboss.app.data.app.Config;
import com.deliveryboss.app.data.prefs.SessionPrefs;
import com.deliveryboss.app.data.api.model.ApiResponseEmpresas;
import com.deliveryboss.app.data.api.DeliverybossApi;
import com.deliveryboss.app.data.util.NotificationUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PrincipalActivity extends AppCompatActivity {
    private static final int STATUS_FILTER_DEFAULT_VALUE = 0;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    String authorization;
    private RecyclerView mListaEmpresas;
    private View mEmptyStateContainer;
    private TextView txtEmptyStateEmpresas;
    private EmpresasAdapter mEmpresasAdapter;
    List<EmpresasBody> serverEmpresas;
    List<Mantenimiento> mantenimientos;
    Boolean mantenimientoActivo=false;
    //String ciudadIntent;
    String rubroIntent;
    String nombreApp = "deliveryboss en ";
    SearchView searchView;
    //Usuario_direccion direccionUsuario;
    Usuario_direccion usuarioDireccion;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout layoutContent;
    private RelativeLayout layoutButtons;
    private boolean isOpen = false;


    // CODIGO DEL NAV DRAWER
    private DrawerLayout drawerLayout;
    private String drawerTitle;
    ImageView fotoPerfil;
    TextView userName;
    TextView userEmail;
    View rootView;
    private BroadcastReceiver mRegistrationBroadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_principal);
        //View contenido_principal = findViewById( R.id.content_principal ); // root View id from that link

        String ciudadNombre = SessionPrefs.get(this).getPrefUsuarioCiudad();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // NAVIGATION DRAWER
        rootView = getLayoutInflater().inflate(R.layout.activity_principal,
                null);
        setContentView(rootView);

        checkUserSession();
        setToolbar(); // Setear Toolbar como action bar

        layoutContent = (RelativeLayout) findViewById(R.id.contenido_principal);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        fotoPerfil = (ImageView) headerView.findViewById(R.id.circle_image);
        userName = (TextView) headerView.findViewById(R.id.userName);
        userEmail = (TextView) headerView.findViewById(R.id.userEmail);

        // Chequeo de variables provenientes de CarritoActivity
        String estado = getIntent().getStringExtra("estado");
        String mensaje = getIntent().getStringExtra("mensaje");
        if(estado!=null){
            if(estado.equals("1")){
                Log.d("ordenEnviada","Mostramos el dialog de orden enviada");
                showOrdenEnviadaDialog();
            }
        }

        // Intent proveniente de FiltroEmpresasActivity
        Intent intentFiltros = getIntent();
        if(intentFiltros.getStringExtra("Delivery en mi zona")!=null){
            filtrarListaPorParametro("",intentFiltros.getStringExtra("Delivery en mi zona"));
        }

        if (navigationView != null) {
            setupDrawerContent(navigationView);
            // Añadir carácteristicas

            String nombre = SessionPrefs.get(this).getPrefUsuarioNombreyApellido();
            String email = SessionPrefs.get(this).getPrefUsuarioEmail();
            String imagen = SessionPrefs.get(this).getPrefUsuarioImagen();

            if(imagen!=null){
                if(!imagen.isEmpty()) {
                    Picasso.with(this).setLoggingEnabled(true);
                    Picasso
                            .with(this)
                            .load(imagen)
                            .fit()
                            .transform(new CircleTransform())
                            .into(fotoPerfil);
                }}
            userName.setText(nombre);
            userEmail.setText(email);

        }

        drawerTitle = getResources().getString(R.string.home_item);
        if (savedInstanceState == null) {
            // Seleccionar item
        }
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrincipalActivity.this, MiPerfilActivity.class));
            }
        });
        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrincipalActivity.this, MiPerfilActivity.class));
            }
        });

        rubroIntent = "1";

        if(rubroIntent!=null) {
            if (rubroIntent.equals("1")) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorComida)));
                getSupportActionBar().setTitle(nombreApp + ciudadNombre);
                setearColorToolbar(getResources().getColor(R.color.colorComida));
            }
            if (rubroIntent.equals("2")) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorFarmacia)));
                getSupportActionBar().setTitle("Farmacia");
                setearColorToolbar(getResources().getColor(R.color.colorFarmacia));
            }
            if (rubroIntent.equals("3")) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMercado)));
                getSupportActionBar().setTitle(nombreApp + ciudadNombre);
                setearColorToolbar(getResources().getColor(R.color.colorMercado));
            }
            if (rubroIntent.equals("4")) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTaxi)));
                getSupportActionBar().setTitle("Taxi");
                setearColorToolbar(getResources().getColor(R.color.colorTaxi));
            }
        }

        checkUserSession();



        mListaEmpresas = (RecyclerView) findViewById(R.id.list_empresas);
        mEmpresasAdapter = new EmpresasAdapter(this, new ArrayList<EmpresasBody>(0),usuarioDireccion);
        mEmpresasAdapter.setOnItemClickListener(new EmpresasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(EmpresasBody clickedEmpresa) {
                Intent intent2 = new Intent(PrincipalActivity.this, DetalleEmpresa.class);
                intent2.putExtra("empresaJson",(new Gson()).toJson(clickedEmpresa));
                startActivity(intent2);
            }

        });
        mListaEmpresas.setAdapter(mEmpresasAdapter);
        mEmptyStateContainer = findViewById(R.id.empty_state_container);
        txtEmptyStateEmpresas = (TextView) findViewById(R.id.txt_empty_state);

        // Inicializar GSON
        Gson gson =
                new GsonBuilder()
                        .create();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(DeliverybossApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // Crear conexión a la API de Deliveryboss
        mDeliverybossApi = mRestAdapter.create(DeliverybossApi.class);



        // Chequeamos si estamos en mantenimiento. De no ser asi obtenemos las empresas.
        obtenerMantenimiento();
        if(!mantenimientoActivo)obtenerEmpresas(rubroIntent);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_content);
        Log.d("swipe", "swipe valor: " + swipeRefreshLayout.toString());
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Pedir al servidor información reciente
                    if(!mantenimientoActivo)obtenerEmpresas(rubroIntent);
                }
            });



        //// PARTE DE MENSAJERIA VIA FCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    //displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                }
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.menu_empresas, menu);
            //return true;
        }
        if(MenuItemCompat.getActionView(menu.findItem(R.id.action_search))!=null)searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        String nombreRubro ="";
        if(rubroIntent!=null) {
            switch (rubroIntent){
                case "1":
                    nombreRubro = "Comida";
                    break;
                case "2":
                    nombreRubro = "Farmacia";
                    break;
                case "3":
                    nombreRubro = "Mercado";
                    break;
                case "4":
                    nombreRubro = "Taxi";
                    break;
            }
            searchView.setQueryHint("Buscar comidas, locales...");
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (TextUtils.isEmpty(newText)) {
                    obtenerEmpresas(rubroIntent);
                } else {
                    buscar(newText);
                }
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                //Intent intent = new Intent(this,SeleccionRubro.class);
                //startActivity(intent);
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_sort:
                //setearAnimaciones();
                abrirFiltro();
        }

        return super.onOptionsItemSelected(item);
    }


    private void obtenerEmpresas(String rubro) {
        authorization = SessionPrefs.get(this).getPrefUsuarioToken();
        String ciudad = SessionPrefs.get(this).getPrefUsuarioIdCiudad();
        String nombreCiudad = SessionPrefs.get(this).getPrefUsuarioCiudad();

        String idUsuario = SessionPrefs.get(this).getPrefUsuarioIdUsuario();
        String usuarioIdciudad = SessionPrefs.get(this).getPrefUsuarioDireccionIdCiudad();
        String usuarioIddireccion = SessionPrefs.get(this).getPrefUsuarioIdDireccion();
        String usuarioLatitud = SessionPrefs.get(this).getPrefUsuarioDireccionLatitud();
        String usuarioLongitud = SessionPrefs.get(this).getPrefUsuarioDireccionLongitud();

        usuarioDireccion = new Usuario_direccion(usuarioIddireccion,idUsuario,usuarioIdciudad,"","","","","","",usuarioLatitud,usuarioLongitud);

        //Log.d("logindb", "Recuperando Empresas de: " + nombreCiudad);
        Log.d("logindb", "Direccion Usuario: " + (new Gson()).toJson(usuarioDireccion));

        // Realizar petición HTTP
        Call<ApiResponseEmpresas> call = mDeliverybossApi.obtenerEmpresasPorRubro(authorization,usuarioIdciudad, rubro);
        call.enqueue(new Callback<ApiResponseEmpresas>() {
            @Override
            public void onResponse(Call<ApiResponseEmpresas> call,
                                   Response<ApiResponseEmpresas> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        /*try {
                            // Reportar causas de error no relacionado con la API
                            //Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                    showLoadingIndicator(false);
                    showErrorMessage(error);
                    return;
                }

                serverEmpresas = response.body().getDatos();

                if (serverEmpresas.size() > 0) {
                    // Mostrar lista de empresas
                    mostrarEmpresas(serverEmpresas);
                    showLoadingIndicator(false);
                } else {
                    // Mostrar empty state
                    mostrarEmpresasEmpty();
                    showLoadingIndicator(false);
                }


            }

            @Override
            public void onFailure(Call<ApiResponseEmpresas> call, Throwable t) {
                showLoadingIndicator(false);
                Log.d("logindb", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Comprueba tu conexión a Internet");
            }
        });
    }

    private void obtenerMantenimiento() {
        authorization = SessionPrefs.get(this).getPrefUsuarioToken();

        // Realizar petición HTTP
        Call<ApiResponseMantenimiento> call = mDeliverybossApi.obtenerMantenimiento(authorization);
        call.enqueue(new Callback<ApiResponseMantenimiento>() {
            @Override
            public void onResponse(Call<ApiResponseMantenimiento> call,
                                   Response<ApiResponseMantenimiento> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        /*try {
                            // Reportar causas de error no relacionado con la API
                            //Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                    showErrorMessage(error);
                    return;
                }

                mantenimientos = response.body().getDatos();

                if (mantenimientos.size() > 0) {
                    Log.d("mantenimiento",mantenimientos.get(0).getTitulo()+": "+ mantenimientos.get(0).getMensaje());
                    Log.d("mantenimiento","Estado Mantenimiento: "+mantenimientos.get(0).getEstado());
                    chequearMantenimiento(mantenimientos);
                } else {
                    // Si por alguna razon no podemos obtener el registro de mantenimiento, activamos el modo normal.
                    mantenimientoActivo=false;
                    }


            }

            @Override
            public void onFailure(Call<ApiResponseMantenimiento> call, Throwable t) {
                showLoadingIndicator(false);
                Log.d("logindb", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Comprueba tu conexión a Internet");
            }
        });
    }

    private void chequearMantenimiento(List<Mantenimiento> mantenimientosServer) {
        // Si estado es TRUE significa que estamos en mantenimiento
        if(mantenimientosServer.get(0).getEstado().equals("1")){
            mantenimientoActivo=true;
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new android.support.v7.app.AlertDialog.Builder(this);
            } else {
                builder = new AlertDialog.Builder(getBaseContext());
            }
            builder.setTitle(mantenimientosServer.get(0).getTitulo())
                    .setMessage(mantenimientosServer.get(0).getMensaje())
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(R.drawable.ic_info)
                    .setCancelable(false)
                    .show();
        }
        // Sino, estamos en modo normal
        else{
            mantenimientoActivo=false;
        }
    }

    private void mostrarEmpresas(List<EmpresasBody> empresasServer) {
        mEmpresasAdapter.swapItems(empresasServer,usuarioDireccion);
        mListaEmpresas.setVisibility(View.VISIBLE);
        mEmptyStateContainer.setVisibility(View.GONE);

        //ordenarListaPorParametro("calificacion");
    }

    private void mostrarEmpresasEmpty() {
        mListaEmpresas.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        String tipo="restaurantes";
        if(rubroIntent.equals("1"))tipo="restaurantes";
        if(rubroIntent.equals("3"))tipo="maxikioscos";

        txtEmptyStateEmpresas.setText("¡No hay "+tipo+" para tu ciudad!");
    }

    private void showLoadingIndicator(final boolean show) {
        swipeRefreshLayout.setRefreshing(show);
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }


    public void buscar(String query){
        query = query.toString().toLowerCase();

        final List<EmpresasBody> filteredList = new ArrayList<>();
        String rubrosConcat ="";

        for (int i = 0; i < serverEmpresas.size(); i++) {

            if(serverEmpresas.get(i).getEmpresa_subrubro()!=null){
                if(serverEmpresas.get(i).getEmpresa_subrubro().get(0).getSubrubro1()!=null){
                    rubrosConcat= (serverEmpresas.get(i).getEmpresa_subrubro().get(0).getSubrubro1());
                }
                if(serverEmpresas.get(i).getEmpresa_subrubro().get(0).getSubrubro2()!=null){
                    rubrosConcat +=(", "+serverEmpresas.get(i).getEmpresa_subrubro().get(0).getSubrubro2());
                }
                if(serverEmpresas.get(i).getEmpresa_subrubro().get(0).getSubrubro3()!=null){
                    rubrosConcat +=(", "+serverEmpresas.get(i).getEmpresa_subrubro().get(0).getSubrubro3());
                }
            }

            final String nombre = serverEmpresas.get(i).getNombre_empresa().toLowerCase();
            final String rubro = rubrosConcat.toLowerCase();
            if (nombre.contains(query)|| rubro.contains(query)) {

                filteredList.add(serverEmpresas.get(i));
            }
        }
        mEmpresasAdapter.swapItems(filteredList,usuarioDireccion);

    }

    public void setearColorToolbar(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    // A PARTIR DE ACA EMPIEZA CODIGO RELATIVO AL NAV DRAWER
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void checkUserSession(){
        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);

                        //Hacer lo que tenga que hacer
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_ordenes:
                                // launch new intent instead of loading fragment
                                startActivity(new Intent(PrincipalActivity.this, MisOrdenesActivity.class));
                                break;
                            case R.id.nav_direcciones:
                                startActivity(new Intent(PrincipalActivity.this, MisDireccionesActivity.class));
                                break;
                            case R.id.nav_cambiar_direccion:
                                startActivity(new Intent(PrincipalActivity.this, SeleccionarDireccion.class));
                                break;
                            case R.id.nav_sugerirempresa:
                                startActivity(new Intent(PrincipalActivity.this, SugerirEmpresa.class));
                                break;
                            case R.id.nav_compartir:
                                compartirApp();
                                break;
                            /*case R.id.nav_log_out:
                                SessionPrefs.get(getApplicationContext()).logOut();
                                checkUserSession();
                                break;*/

                        }

                        // Desmarcar item presionado
                        menuItem.setChecked(false);
                        // Cerrar drawer
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    protected void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(rootView);
        rootView = null;
        System.gc();
    }

    public void compartirApp(){
        Intent sendText = new Intent();
        sendText.setAction(Intent.ACTION_SEND);
        sendText.putExtra(Intent.EXTRA_TEXT, "Hola descarga deliveryboss, es muy recomendable --> https://play.google.com/store/apps/details?id=com.deliveryboss.app");
        sendText.setType("text/plain");
        startActivity(sendText);
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;
    @Override
    public void onBackPressed(){
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            finishAffinity();
        }else {
            Toast.makeText(this, "Presioná atrás de nuevo para salir", Toast.LENGTH_LONG).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }


    public void showOrdenEnviadaDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        OrdenEnviadaFragment newFragment = new OrdenEnviadaFragment();
        newFragment.show(fragmentManager.beginTransaction(), "¡Orden enviada!");
    }


    /*private void ordenarListaPorParametro(String param){
        switch (param){
            case "nombre":
                    serverEmpresas.sort(new Comparator<EmpresasBody>() {
                        @Override
                        public int compare(EmpresasBody lhs, EmpresasBody rhs) {
                            char l = Character.toUpperCase(lhs.getNombre_empresa().charAt(0));

                            if (l < 'A' || l > 'Z')
                                l += 'Z';
                            char r = Character.toUpperCase(rhs.getNombre_empresa().charAt(0));
                            if (r < 'A' || r > 'Z')
                                r += 'Z';
                            String s1 = l + lhs.getNombre_empresa().substring(1);
                            String s2 = r + rhs.getNombre_empresa().substring(1);
                            return s1.compareTo(s2);
                        }
                    });
            case "calificacion":
                serverEmpresas.sort(new Comparator<EmpresasBody>() {
                    @Override
                    public int compare(EmpresasBody lhs, EmpresasBody rhs) {
                        String s1 = lhs.getCalificacion_general();
                        String s2 = rhs.getCalificacion_general();
                        return s1.compareTo(s2);
                    }
                });
        }
    }*/

    public void filtrarListaPorParametro(String query, String filtro){
        query = query.toString().toLowerCase();

        final List<EmpresasBody> filteredList = new ArrayList<>();

        for (int i = 0; i < serverEmpresas.size(); i++) {


            //final String nombre = Utilidades.ChequearLocalAbiertoHoy(serverEmpresas.get(i));
            //final String rubro = serverEmpresas.get(i).getSubrubro().toLowerCase();
            if (Utilidades.ChequearLocalAbiertoHoy(serverEmpresas.get(i))) {

                filteredList.add(serverEmpresas.get(i));
            }
        }
        mEmpresasAdapter.swapItems(filteredList,usuarioDireccion);

    }

    private void abrirFiltro(){
        View view = findViewById(R.id.action_sort);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");


        int revealX = (int) (view.getRight()+view.getLeft()+view.getWidth()+view.getTop());
        int revealY = (int) (view.getTop() + view.getBottom()) / 2;

        //Log.d("juaco1993","View x:"+ revealX+" y:"+revealY);

        Intent intent = new Intent(this, FiltroEmpresasActivity.class);
        intent.putExtra(FiltroEmpresasActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(FiltroEmpresasActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());

    }
}
