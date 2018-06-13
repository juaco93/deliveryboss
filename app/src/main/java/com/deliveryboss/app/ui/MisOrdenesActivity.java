package com.deliveryboss.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.DeliverybossApi;
import com.deliveryboss.app.data.api.model.ApiResponseOrdenes;
import com.deliveryboss.app.data.api.model.MessageEvent;
import com.deliveryboss.app.data.api.model.Orden;
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

public class MisOrdenesActivity extends AppCompatActivity {
    private RecyclerView mListaOrdenes;
    private OrdenesAdapter mOrdenesAdapter;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;
    private SwipeRefreshLayout swipeRefreshLayout;

    List<Orden> serverOrdenes;
    String authorization;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_ordenes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mListaOrdenes = (RecyclerView) findViewById(R.id.list_ordenes);
        mOrdenesAdapter = new OrdenesAdapter(this, new ArrayList<Orden>(0));
        mListaOrdenes.setAdapter(mOrdenesAdapter);
        mOrdenesAdapter.setOnItemClickListener(new OrdenesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Orden clickedOrden) {
                showInfoEstadoOrden((new Gson()).toJson(clickedOrden));
            }
        });
        mEmptyStateContainer = findViewById(R.id.empty_state_containerOrdenes);
        txtEmptyContainer = (TextView) findViewById(R.id.txtOrdenesEmptyContainer);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_content_ordenes);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Pedir al servidor información reciente
                obtenerOrdenes();
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

        obtenerOrdenes();
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

    private void obtenerOrdenes(){
        authorization = SessionPrefs.get(getApplicationContext()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getApplicationContext()).getPrefUsuarioIdUsuario();
        //Log.d("gson", "Recuperando Ordenes desde el Server");

        // Realizar petición HTTP
        Call<ApiResponseOrdenes> call = mDeliverybossApi.obtenerOrdenesUsuario(authorization,"0",idusuario);
        call.enqueue(new Callback<ApiResponseOrdenes>() {
            @Override
            public void onResponse(Call<ApiResponseOrdenes> call,
                                   Response<ApiResponseOrdenes> response) {
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
                    // Mostrar empty state
                    mostrarOrdenesEmpty();
                    return;
                }

                serverOrdenes = response.body().getDatos();
                Log.d("gson", "todo bien, recibido: " + response.body().getDatos().toString());
                if (serverOrdenes.size() > 0) {
                    // Mostrar lista de ordenes
                    mostrarOrdenes(serverOrdenes);
                    showLoadingIndicator(false);
                } else {
                    // Mostrar empty state
                    mostrarOrdenesEmpty();
                    showLoadingIndicator(false);
                }
                if(getIntent()!=null){
                    Log.d("notinoti","Recibimos notificacion, ingresando a orden");
                    if(getIntent().getStringExtra("idorden")!=null){
                        int cant = serverOrdenes.size();
                        for(int i=0;i<cant;i++){
                            // Chequeamos el idorden para ver si esta en las listadas, y si está actuamos según el estado de la orden
                            // Si estado='confirmada' o estado='cancelada' mostramos el estado de la orden
                            // Si estado='entregada' entonces mostramos el dialogo para calificar la orden
                            if(serverOrdenes.get(i).getIdorden().equals(getIntent().getStringExtra("idorden"))){
                                if(getIntent().getStringExtra("estado").equals("entregada")){
                                    if(serverOrdenes.get(i).getCalificado()==null)showDialogCalificar((new Gson()).toJson(serverOrdenes.get(i)));
                                }
                                if(getIntent().getStringExtra("estado").equals("confirmada")||getIntent().getStringExtra("estado").equals("cancelada")||getIntent().getStringExtra("estado").equals("anulada")||getIntent().getStringExtra("estado").equals("enviada")){
                                    showInfoEstadoOrden((new Gson()).toJson(serverOrdenes.get(i)));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseOrdenes> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("gson", "Petición rechazada:" + t.getMessage());
                showLoadingIndicator(false);
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void mostrarOrdenes(List<Orden> ordenesServer) {
        //Log.d("gson", "Entramos a mostrar ordenes ");
        mOrdenesAdapter.swapItems(ordenesServer);
        mListaOrdenes.setVisibility(View.VISIBLE);
        mEmptyStateContainer.setVisibility(View.GONE);
    }

    private void mostrarOrdenesEmpty() {
        mListaOrdenes.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        txtEmptyContainer.setText(getResources().getString(R.string.mensajeSinOrdenes));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //Log.d("eventbus","evento recibido, descripcion: " + event.getDescripcion());
        if(event.getIdevento().equals("1")){
            obtenerOrdenes();
            Intent intent = new Intent(this,PrincipalActivity.class);
            startActivity(intent);
        }
        if(event.getIdevento().equals("2")){

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

    public void showInfoEstadoOrden(String orden) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        OrdenInfoEstadoFragment newFragment = new OrdenInfoEstadoFragment();

        Bundle args = new Bundle();
        if(!orden.isEmpty() && !orden.equals(""))args.putString("orden", orden);
        newFragment.setArguments(args);

        newFragment.show(fragmentManager.beginTransaction(), "Estado de tu orden");

    }

    public void showDialogCalificar(String orden) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CalificacionDialogFragment newFragment = new CalificacionDialogFragment();

        Bundle args = new Bundle();
        if(!orden.isEmpty() && !orden.equals(""))args.putString("orden", orden);
        newFragment.setArguments(args);

        newFragment.show(fragmentManager.beginTransaction(), "Calificá tu orden");

    }

    private void showLoadingIndicator(final boolean show) {
        swipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

}
