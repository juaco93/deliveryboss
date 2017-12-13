package com.jadevelopment.deliveryboss1.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.DeliverybossApi;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseCalificaciones;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseProductos;
import com.jadevelopment.deliveryboss1.data.api.model.Calificacion;
import com.jadevelopment.deliveryboss1.data.api.model.EmpresasBody;
import com.jadevelopment.deliveryboss1.data.api.model.Producto;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoCalificacionesFragment extends Fragment {
    EmpresasBody empresa;
    private RecyclerView mListaCalificaciones;
    private CalificacionesAdapter mCalificacionesAdapter;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;
    List<Calificacion> serverCalificaciones;
    String authorization;
    Context context;
    private FloatingActionButton mSharedFab;

    public InfoCalificacionesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info_menu, container, false);

        txtEmptyContainer = (TextView) v.findViewById(R.id.txtEmptyContainer);

        mListaCalificaciones = (RecyclerView) v.findViewById(R.id.list_productos);
        mCalificacionesAdapter = new CalificacionesAdapter(context, new ArrayList<Calificacion>(0));
        mListaCalificaciones.setAdapter(mCalificacionesAdapter);
        mEmptyStateContainer = v.findViewById(R.id.empty_state_containerMenu);
        mCalificacionesAdapter.setOnItemClickListener(new CalificacionesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Calificacion clickedCalificacion) {

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

        Intent intentRecibido = getActivity().getIntent();
        empresa = (new Gson()).fromJson((intentRecibido.getStringExtra("empresaJson")),EmpresasBody.class);
        Log.d("gson",empresa.getIdempresa());
        Log.d("gson",empresa.getNombre_fantasia());

        obtenerCalificaciones("");

        // Inflate the layout for this fragment
        return v;
    }


    private void obtenerCalificaciones(String rawStatus) {
        authorization = SessionPrefs.get(getContext()).getPrefUsuarioToken();
        Log.d("gson", "Recuperando Productos desde el Server");
        Log.d("gson", "Token Header: " + authorization);

        // Realizar petición HTTP
        Call<ApiResponseCalificaciones> call = mDeliverybossApi.obtenerCalificacionUsuario(authorization,empresa.getIdempresa(),"");
        call.enqueue(new Callback<ApiResponseCalificaciones>() {
            @Override
            public void onResponse(Call<ApiResponseCalificaciones> call,
                                   Response<ApiResponseCalificaciones> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {

                        Log.d("gson", response.errorBody().toString());
                    } else {
                        Log.d("gson", response.errorBody().toString());
                    }
                    Log.d("gson", response.message());
                    Log.d("gson", response.raw().toString());
                    return;
                }

                serverCalificaciones = response.body().getDatos();
                Log.d("gson", "todo bien, recibido: " + response.body().getDatos().toString());
                if (serverCalificaciones.size() > 0) {
                    // Mostrar lista de calificaciones
                    mostrarCalificaciones(serverCalificaciones);
                } else {
                    // Mostrar empty state
                    mostrarCalificacionesEmpty();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseCalificaciones> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("gson", "Petición rechazada:" + t.getMessage());
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void mostrarCalificaciones(List<Calificacion> calificacionesServer) {
        Log.d("gson", "Entramos a mostrar productos " + calificacionesServer.get(0).getUsuario());
        txtEmptyContainer.setText(calificacionesServer.get(0).getUsuario());
        mCalificacionesAdapter.swapItems(calificacionesServer);
        mListaCalificaciones.setVisibility(View.VISIBLE);
        mEmptyStateContainer.setVisibility(View.GONE);
    }

    private void mostrarCalificacionesEmpty() {
        mListaCalificaciones.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        txtEmptyContainer.setText("¡El restaurante aún no tiene calificaciones!");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedFab = null; // To avoid keeping/leaking the reference of the FAB
    }

    public void shareFab(FloatingActionButton fab) {
        if (fab == null) { // When the FAB is shared to another Fragment
            if (mSharedFab != null) {
                mSharedFab.setOnClickListener(null);
            }
            mSharedFab = null;
        }
        else {
            mSharedFab = fab;
            mSharedFab.setImageResource(R.drawable.cajita_sola);
        }
    }
}
