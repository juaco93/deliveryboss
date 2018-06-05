package com.deliveryboss.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.deliveryboss.app.data.api.model.EmpresasBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.DeliverybossApi;
import com.deliveryboss.app.data.api.model.ApiResponseDirecciones;
import com.deliveryboss.app.data.api.model.MessageEvent;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.data.prefs.SessionPrefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MisDireccionesActivity extends AppCompatActivity {

    private RecyclerView mListaDirecciones;
    private DireccionesAdapter mDireccionesAdapter;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;
    private static final int FRAGMENTO_AGREGAR_DIRECCION = 3;
    private SwipeRefreshLayout swipeRefreshLayout;


    List<Usuario_direccion> serverDirecciones;
    Usuario_direccion direccionGuardada;
    String authorization;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_direcciones);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("");
            }
        });

        obtenerDireccionGuardada();

        mListaDirecciones = (RecyclerView) findViewById(R.id.list_direcciones);
        mDireccionesAdapter = new DireccionesAdapter(this, new ArrayList<Usuario_direccion>(0),direccionGuardada);

        mListaDirecciones.setAdapter(mDireccionesAdapter);
        mEmptyStateContainer = findViewById(R.id.empty_state_containerDirecciones);
        txtEmptyContainer = (TextView) findViewById(R.id.txtOrdenesEmptyContainer);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_content_direcciones);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Pedir al servidor información reciente
                obtenerDirecciones();
            }
        });

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

        obtenerDirecciones();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(this,PrincipalActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void obtenerDirecciones(){
        authorization = SessionPrefs.get(getApplicationContext()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getApplicationContext()).getPrefUsuarioIdUsuario();
        Log.d("gson", "Recuperando Direcciones desde el Server");

        // Realizar petición HTTP
        Call<ApiResponseDirecciones> call = mDeliverybossApi.obtenerDireccionesUsuario(authorization,idusuario);
        call.enqueue(new Callback<ApiResponseDirecciones>() {
            @Override
            public void onResponse(Call<ApiResponseDirecciones> call,
                                   Response<ApiResponseDirecciones> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {

                        Log.d("gson", response.errorBody().toString());
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        Log.d("gson", response.errorBody().toString());
                        /*try {
                            // Reportar causas de error no relacionado con la API
                            Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }

                    return;
                }

                serverDirecciones = response.body().getDatos();
                //Log.d("gson", "tod bien, recibido: " + response.body().getDatos().toString());
                if (serverDirecciones.size() > 0) {
                    // Mostrar lista de ordenes
                    mostrarDirecciones(serverDirecciones, direccionGuardada);
                    showLoadingIndicator(false);
                } else {
                    // Mostrar empty state
                    mostrarDireccionesEmpty();
                    showLoadingIndicator(false);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDirecciones> call, Throwable t) {
                //showLoadingIndicator(false);
                //Log.d("gson", "Petición rechazada:" + t.getMessage());
                showLoadingIndicator(false);
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void mostrarDirecciones(List<Usuario_direccion> direccionesServer, Usuario_direccion mDireccionGuardada) {
        //Log.d("gson", "Entramos a mostrar ordenes ");
        mDireccionesAdapter.swapItems(direccionesServer,mDireccionGuardada);
        mListaDirecciones.setVisibility(View.VISIBLE);
        mEmptyStateContainer.setVisibility(View.GONE);
    }

    private void mostrarDireccionesEmpty() {
        mListaDirecciones.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        txtEmptyContainer.setText(getResources().getString(R.string.mensajeSinDirecciones));
    }


    public void showDialog(String direccion) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        ModificarDireccionFragment newFragment = new ModificarDireccionFragment();

        Bundle args = new Bundle();
        if(!direccion.isEmpty() && !direccion.equals(""))args.putString("direccion", direccion);
        newFragment.setArguments(args);

        // setup link back to use and display
        //newFragment.setTargetActivity(this,FRAGMENTO_AGREGAR_DIRECCION);
        newFragment.show(fragmentManager.beginTransaction(), "Agregar direccion");

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.d("eventbus","evento recibido, descripcion: " + event.getDescripcion());
        // Si es 2, es insercion de nueva direccion
        if(event.getIdevento().equals("2")){
            obtenerDirecciones();
        // Si es 3, es modificacion de direccion existente
        }else if(event.getIdevento().equals("3")){
            obtenerDirecciones();
        // Si es 4, es eliminacion de direccion existente
        }else if(event.getIdevento().equals("4")){
            obtenerDirecciones();
        // Si es 5, es cambio de direccion por defecto
        }else if(event.getIdevento().equals("5")){
            obtenerDireccionGuardada();
            obtenerDirecciones();
    }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void showLoadingIndicator(final boolean show) {
        swipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }

    private void obtenerDireccionGuardada(){
        String idDireccion = SessionPrefs.get(this).getPrefUsuarioIdDireccion();
        String idUsuario = SessionPrefs.get(this).getPrefUsuarioIdUsuario();
        String idCiudad = SessionPrefs.get(this).getPrefUsuarioIdCiudad();
        String ciudad = SessionPrefs.get(this).getPrefUsuarioCiudad();
        String calle = SessionPrefs.get(this).getPrefUsuarioDireccionCalle();
        String numero = SessionPrefs.get(this).getPrefUsuarioDireccionNumero();

        direccionGuardada = new Usuario_direccion(idDireccion,idUsuario,idCiudad,ciudad,calle,numero,"","","","","");
    }


}
