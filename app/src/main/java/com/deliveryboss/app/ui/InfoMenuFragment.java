package com.deliveryboss.app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.deliveryboss.app.data.api.model.BadgeDrawable;
import com.deliveryboss.app.data.util.Utilidades;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.DeliverybossApi;
import com.deliveryboss.app.data.api.model.ApiResponseProductos;
import com.deliveryboss.app.data.api.model.EmpresasBody;
import com.deliveryboss.app.data.api.model.Orden_detalle;
import com.deliveryboss.app.data.api.model.Producto;
import com.deliveryboss.app.data.prefs.SessionPrefs;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

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

    // FAB //
    FloatingActionButton button;
    FloatingActionButton buttonA;
    FloatingActionsMenu menuMultipleActions;

    private FloatingActionsMenu mSharedFab;
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

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_info_menu, container, false);
        View vDeta = inflater.inflate(R.layout.activity_detalle_empresa,container,false);

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

        /////// FAB CARRITO ///////////
        button = v.findViewById(R.id.action_b);
        button.setSize(FloatingActionButton.SIZE_NORMAL);
        button.setColorNormalResId(R.color.colorComida);
        button.setColorPressedResId(R.color.colorAccent);
        button.setIcon(R.drawable.cajita_sola);
        button.setStrokeVisible(false);
        button.setTitle("Ir al carrito");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CarritoActivity.class);
                intent.putExtra("ordenes_detalle",(new Gson()).toJson(ordenesDetalleLocal));
                intent.putExtra("empresa", (new Gson()).toJson(empresa));
                intent.putExtra("abierto_hoy",abierto);
                startActivity(intent);
            }
        });

        buttonA = v.findViewById(R.id.action_a);
        buttonA.setSize(FloatingActionButton.SIZE_NORMAL);
        buttonA.setColorNormalResId(R.color.colorComida);
        buttonA.setColorPressedResId(R.color.colorAccent);
        buttonA.setIcon(R.drawable.ic_money);
        buttonA.setStrokeVisible(false);
        buttonA.setTitle("Monto");

        menuMultipleActions = v.findViewById(R.id.multiple_actions);
       //menuMultipleActions.setIcon(getResources().getDrawable(R.drawable.icono_carrito_items));


        LayerDrawable localLayerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.icono_carrito_items);

        Drawable cartBadgeDrawable = localLayerDrawable.findDrawableByLayerId(R.id.ic_badge);
        setBadgeCount(getContext(),localLayerDrawable,"3");

        /////// FAB CARRITO ///////////


        // Obtener intent en caso de que regrese de la actividad Carrito (para no perder lo que estaba en el carrito)
        String carrito = getActivity().getIntent().getExtras().getString("carrito");
        if(carrito!=null){
        Type listType = new TypeToken<ArrayList<Orden_detalle>>(){}.getType();
        ordenesDetalleLocal = new Gson().fromJson(carrito, listType);
        buttonA.setTitle("$"+String.valueOf(sumarTotal()));
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

        //Log.d("gson",empresa.getIdempresa());
        //Log.d("gson",empresa.getNombre_empresa());

        obtenerProductos("");


        // Chequeamos si el local esta abierto para permitir el uso del carrito (o no)
        abierto = Utilidades.ChequearLocalAbiertoHoy(empresa);

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


                        buttonA.setTitle("$"+String.valueOf(sumarTotal()));
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

    public void shareFab(FloatingActionsMenu fab) {
        if (fab == null) { // When the FAB is shared to another Fragment
            if (mSharedFab != null) {
                mSharedFab.setOnClickListener(null);
            }
            mSharedFab = null;
        }
        else {
            mSharedFab = fab;
            //mSharedFab.setImageResource(R.drawable.cajita_sola);

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


    private float sumarTotal(){
        Float total = 0.00f;
        int cantidad= 0;
        cantidad = ordenesDetalleLocal.size();
        for (int i=0; i<cantidad; i++) {
            total += Float.valueOf(ordenesDetalleLocal.get(i).getOrden_detalle_subtotal());
        }

        createCartBadge(cantidad);
       return total;
    }

    private void createCartBadge(int paramInt) {
        if (Build.VERSION.SDK_INT <= 15) {
            return;
        }
        LayerDrawable localLayerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.icono_carrito_items);
        Drawable cartBadgeDrawable = localLayerDrawable
                .findDrawableByLayerId(R.id.ic_badge);
        BadgeDrawable badgeDrawable;
        if ((cartBadgeDrawable != null)
                && ((cartBadgeDrawable instanceof BadgeDrawable))
                && (paramInt < 10)) {
            badgeDrawable = (BadgeDrawable) cartBadgeDrawable;
        } else {
            badgeDrawable = new BadgeDrawable(getContext());
        }
        badgeDrawable.setCount(String.valueOf(paramInt));
        localLayerDrawable.mutate();
        localLayerDrawable.setDrawableByLayerId(R.id.ic_badge, badgeDrawable);
        menuMultipleActions.setIcon(localLayerDrawable);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }


}
