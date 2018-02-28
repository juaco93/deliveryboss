package com.jadevelopment.deliveryboss1.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.ArcMotion;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.DeliverybossApi;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseProductos;
import com.jadevelopment.deliveryboss1.data.api.model.EmpresasBody;
import com.jadevelopment.deliveryboss1.data.api.model.Orden_detalle;
import com.jadevelopment.deliveryboss1.data.api.model.Producto;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.SEARCH_SERVICE;


public class InfoMenuFragment extends Fragment {
    EmpresasBody empresa;
    private RecyclerView mListaProductos;
    private ProductosAdapter mProductosAdapter;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;
    private static final int FRAGMENTO_AGREGAR_PRODUCTO = 1;
    List<Producto> serverProductos;
    String authorization;
    Context context;
    Orden_detalle detalle;
    List<Orden_detalle> ordenesDetalleLocal = new ArrayList<>();
    Boolean abierto= false;

    private FloatingActionButton mSharedFab;
    boolean visible;
    ViewGroup transitionsContainer;

    public InfoMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_info_menu, container, false);

        transitionsContainer = (ViewGroup) v.findViewById(R.id.contenedor_transiciones);


        txtEmptyContainer = (TextView) v.findViewById(R.id.txtEmptyContainer);

        mListaProductos = (RecyclerView) v.findViewById(R.id.list_productos);
        mProductosAdapter = new ProductosAdapter(context, new ArrayList<Producto>(0));
        mProductosAdapter.setOnItemClickListener(new ProductosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Producto clickedProducto) {
                //showDialog(clickedProducto.getIdproducto(),clickedProducto.getProducto());
                showDialog("producto",(new Gson()).toJson(clickedProducto));
                //fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.cajita_open));
            }

        });

        mListaProductos.setAdapter(mProductosAdapter);
        mEmptyStateContainer = v.findViewById(R.id.empty_state_containerMenu);

        // Obtener intent en caso de que regrese de la actividad Carrito (para no perder lo que estaba en el carrito)
        String carrito = getActivity().getIntent().getExtras().getString("carrito");
        if(carrito!=null){
        Type listType = new TypeToken<ArrayList<Orden_detalle>>(){}.getType();
        ordenesDetalleLocal = new Gson().fromJson(carrito, listType);
        }

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
        Log.d("gson",empresa.getNombre_empresa());

        obtenerProductos("");


        // Chequeamos si el local esta abierto para permitir el uso del carrito (o no)
        chequearLocalAbierto();

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerProductos("");
    }

    private void obtenerProductos(String rawStatus) {
        authorization = SessionPrefs.get(getContext()).getPrefUsuarioToken();
        Log.d("gson", "Recuperando Productos desde el Server");
        Log.d("gson", "Token Header: " + authorization);

        // Realizar petición HTTP
        Call<ApiResponseProductos> call = mDeliverybossApi.obtenerProductos(authorization,empresa.getIdempresa());
        call.enqueue(new Callback<ApiResponseProductos>() {
            @Override
            public void onResponse(Call<ApiResponseProductos> call,
                                   Response<ApiResponseProductos> response) {
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

                serverProductos = response.body().getDatos();
                Log.d("gson", "todo bien, recibido: " + response.body().getDatos().toString());
                if (serverProductos.size() > 0) {
                    Log.d("gson", "nombre producto: " + serverProductos.get(0).getProducto());
                    // Mostrar lista de empresas
                    mostrarProductos(serverProductos);
                } else {
                    // Mostrar empty state
                    mostrarProductosEmpty();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseProductos> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("gson", "Petición rechazada:" + t.getMessage());
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void mostrarProductos(List<Producto> productosServer) {
        Log.d("gson", "Entramos a mostrar productos " + productosServer.get(0).getProducto());
        txtEmptyContainer.setText(productosServer.get(0).getProducto());
        mProductosAdapter.swapItems(productosServer);
        mListaProductos.setVisibility(View.VISIBLE);
        mEmptyStateContainer.setVisibility(View.GONE);
    }

    private void mostrarProductosEmpty() {
        mListaProductos.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        txtEmptyContainer.setText("¡El restaurante aún no cargó su menú!");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_empresas, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Buscar producto...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (TextUtils.isEmpty(newText)) {
                    obtenerProductos("");
                } else {
                    buscar(newText);
                }
                return false;
            }
        });
        }

    public void buscar(String query){
        query = query.toString().toLowerCase();
        final List<Producto> filteredList = new ArrayList<>();

        for (int i = 0; i < serverProductos.size(); i++) {

            final String nombre = serverProductos.get(i).getProducto().toLowerCase();
            if (nombre.contains(query)) {
                filteredList.add(serverProductos.get(i));
            }
        }
        mProductosAdapter.swapItems(filteredList);

    }

    public void showDialog(String id, String producto) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        AgregarProductoFragment newFragment = new AgregarProductoFragment();

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("producto", producto);

        newFragment.setArguments(args);

        // setup link back to use and display
        newFragment.setTargetFragment(this,FRAGMENTO_AGREGAR_PRODUCTO);
        newFragment.show(fragmentManager.beginTransaction(), "Agregar producto");

        //newFragment.show(fragmentManager, "Agregar producto");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean yaExisteProductoEnCarrito = false;
        int posicion = 0;
        switch (requestCode) {
            case FRAGMENTO_AGREGAR_PRODUCTO:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();

                    detalle = (new Gson()).fromJson((bundle.getString("detalle")),Orden_detalle.class);

                    for (int i=0; i<ordenesDetalleLocal.size(); i++) {
                        if(ordenesDetalleLocal.get(i).getProducto_idproducto().equals(detalle.getProducto_idproducto())){
                            yaExisteProductoEnCarrito = true;
                            posicion = i;
                        }else{
                            yaExisteProductoEnCarrito = false;
                        }
                    }

                    // SI EXISTE EL PRODUCTO EN EL CARRITO NO LO AGREGAMOS
                    if(yaExisteProductoEnCarrito){
                        ordenesDetalleLocal.set(posicion,detalle);
                        Toast.makeText(context,"Cambiaste la cantidad de: " + ordenesDetalleLocal.get(posicion).getProducto_nombre()+"\nCantidad nueva: "+ordenesDetalleLocal.get(posicion).getCantidad(),Toast.LENGTH_LONG).show();
                    }else{
                        ordenesDetalleLocal.add(detalle);
                        Toast.makeText(context,"Agregaste: " + detalle.getCantidad()+" "+detalle.getProducto_nombre()+" a tu orden!",Toast.LENGTH_LONG).show();
                    }

                    // Animacion del Fab del Carrito
                    /*
                    TransitionManager.beginDelayedTransition(transitionsContainer);
                    visible = !visible;
                    mSharedFab.setVisibility(visible ? View.VISIBLE : View.GONE);
                    */

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //Si cierra el dialogo sin agregar nada...
                }
                break;


        }
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
            mSharedFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),CarritoActivity.class);
                    intent.putExtra("ordenes_detalle",(new Gson()).toJson(ordenesDetalleLocal));
                    intent.putExtra("empresa", (new Gson()).toJson(empresa));
                    intent.putExtra("abierto_hoy",abierto);
                    startActivity(intent);
                }
            });
        }
    }

    public void chequearLocalAbierto(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String diaActual = sdf.format(new Date());

        Log.d("dia", diaActual);

        abierto=false;

        String turno1 = "";
        String turno2 = "";

        if(empresa.getHorario()!=null) {
            String[] dias = empresa.getHorario().split(",");
            for (String dia : dias) {
                if (dia != null) {
                    String[] secciones = dia.split("-");

                    String iddia = secciones[0];
                    if(secciones[1]!=null) turno1 = secciones[1];
                    if(secciones[2]!=null) turno2 = secciones[2];

                    Log.d("dia", "turno1: " + turno1 + "turno2" + turno2);

                    if(diaActual.equals(iddia)){
                        //if(!turno1.equals("") && !turno2.equals("")) holder.horarios.setText("HOY "+turno1 +" Y "+turno2);
                        //if(turno2.equals("0:01 a 0:01")) holder.horarios.setText("HOY "+turno1);
                        //if(turno1.equals("0:00 a 0:00")) holder.horarios.setText("HOY ABIERTO LAS 24 HS");
                        abierto=true;
                    }
                }
            }
        }
    }

}
