package com.jadevelopment.deliveryboss1.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.DeliverybossApi;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponse;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseDirecciones;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseOrdenes;
import com.jadevelopment.deliveryboss1.data.api.model.MessageEvent;
import com.jadevelopment.deliveryboss1.data.api.model.Orden;
import com.jadevelopment.deliveryboss1.data.api.model.Usuario_direccion;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;

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

        mListaDirecciones = (RecyclerView) findViewById(R.id.list_direcciones);
        mDireccionesAdapter = new DireccionesAdapter(this, new ArrayList<Usuario_direccion>(0));

        mListaDirecciones.setAdapter(mDireccionesAdapter);
        mEmptyStateContainer = findViewById(R.id.empty_state_containerDirecciones);
        txtEmptyContainer = (TextView) findViewById(R.id.txtOrdenesEmptyContainer);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_content_direcciones);
        Log.d("swipe", "swipe valor: " + swipeRefreshLayout.toString());
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
                    //showLoadingIndicator(false);
                    //showErrorMessage(error);
                    Log.d("gson", response.message());
                    Log.d("gson", response.raw().toString());
                    return;
                }

                serverDirecciones = response.body().getDatos();
                Log.d("gson", "todo bien, recibido: " + response.body().getDatos().toString());
                if (serverDirecciones.size() > 0) {
                    // Mostrar lista de ordenes
                    mostrarDirecciones(serverDirecciones);
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
                Log.d("gson", "Petición rechazada:" + t.getMessage());
                showLoadingIndicator(false);
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void mostrarDirecciones(List<Usuario_direccion> direccionesServer) {
        Log.d("gson", "Entramos a mostrar ordenes ");
        mDireccionesAdapter.swapItems(direccionesServer);
        mListaDirecciones.setVisibility(View.VISIBLE);
        mEmptyStateContainer.setVisibility(View.GONE);
    }

    private void mostrarDireccionesEmpty() {
        mListaDirecciones.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        txtEmptyContainer.setText("¡Todavia no agregaste ninguna dirección!");
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
        if(event.getIdevento().equals("2")){
            obtenerDirecciones();
        }else if(event.getIdevento().equals("3")){
            obtenerDirecciones();
        }else if(event.getIdevento().equals("4")){
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

}
