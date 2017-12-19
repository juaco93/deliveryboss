package com.jadevelopment.deliveryboss1.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pools;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
//import android.view.ActionMode;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.DeliverybossApi;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponse;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseDirecciones;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseProductos;
import com.jadevelopment.deliveryboss1.data.api.model.EmpresasBody;
import com.jadevelopment.deliveryboss1.data.api.model.MessageEvent;
import com.jadevelopment.deliveryboss1.data.api.model.Orden;
import com.jadevelopment.deliveryboss1.data.api.model.Orden_detalle;
import com.jadevelopment.deliveryboss1.data.api.model.Producto;

import java.io.IOException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.jadevelopment.deliveryboss1.data.api.model.Usuario_direccion;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarritoActivity extends AppCompatActivity {

    RecyclerView mListaCarrito;
    CarritoAdapter mCarritoAdapter;
    List<Orden_detalle> ordenesDetalleLocal;
    Orden_detalle detalleMod;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;
    List<Usuario_direccion> serverDirecciones;
    String authorization;

    // Variables multiselect
    private ActionMode mActionMode;
    Menu context_menu;
    boolean isMultiSelect = false;

    Spinner spTipoEntrega;
    Spinner spDireccion;
    TextView txtTotal;
    TextView txtCarritoImporteDelivery;
    TextView nombreEmpresa;
    EditText nota;
    EditText pagaCon;
    Button btnConfirmarOrden;
    Button btnAgregarDireccion;
    EmpresasBody empresa;
    Float importeTotal;
    String tipoEnvio = "1";
    String idDireccionUsuario = "";
    int c;
    int[] elem_a_borrar;

    private static final int FRAGMENTO_AGREGAR_DIRECCION = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCarrito);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spTipoEntrega = (Spinner) findViewById(R.id.spTipoEntrega);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipo_entrega, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoEntrega.setAdapter(adapter);
        spDireccion = (Spinner) findViewById(R.id.spDireccion);
        btnAgregarDireccion = (Button) findViewById(R.id.btnCarritoAgregarDireccion);
        btnAgregarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("");
            }
        });

        txtCarritoImporteDelivery = (TextView) findViewById(R.id.txtCarritoImporteDelivery);
        txtTotal = (TextView) findViewById(R.id.txtCarritoImporteTotal);
        nombreEmpresa = (TextView) findViewById(R.id.lbCarritoNombreEmpresa);
        nota = (EditText) findViewById(R.id.txtCarritoNota);
        pagaCon = (EditText) findViewById(R.id.txtCarritoPagaCon);
        btnConfirmarOrden = (Button) findViewById(R.id.btnConfirmarOrden);
        mListaCarrito = (RecyclerView) findViewById(R.id.listaCarrito);
        txtEmptyContainer = (TextView) findViewById(R.id.txtEmptyContainerCarrito);
        mEmptyStateContainer = findViewById(R.id.empty_state_containerCarrito);
        mCarritoAdapter = new CarritoAdapter(this, new ArrayList<Orden_detalle>(0));

        ///// INICIALIZACION DE VARIABLES
        String listaOrdenes = getIntent().getExtras().getString("ordenes_detalle");
        String empresaJSON = getIntent().getExtras().getString("empresa");
        empresa = new Gson().fromJson(empresaJSON, EmpresasBody.class);
        Type listType = new TypeToken<ArrayList<Orden_detalle>>(){}.getType();
        ordenesDetalleLocal = new Gson().fromJson(listaOrdenes, listType);
        importeTotal = sumarTotal(0.00f);
        nombreEmpresa.setText(empresa.getNombre_fantasia());

        //// RETROFIT
        // Interceptor para log del Request
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Inicializar GSON
        Gson gson =
                new GsonBuilder()
                        .create();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(DeliverybossApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        // Crear conexión a la API de Deliveryboss
        mDeliverybossApi = mRestAdapter.create(DeliverybossApi.class);


        //// SUMA DEL DELIVERY (SI APLICA)
        spTipoEntrega.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //DELIVERY
                if(position==0){
                    Float precioDelivery = Float.valueOf(empresa.getPrecio_delivery());
                    txtCarritoImporteDelivery.setText("$" + String.format("%.2f", precioDelivery));
                    importeTotal = sumarTotal(Float.valueOf(empresa.getPrecio_delivery()));
                    String subtotSt = String.format("%.2f", importeTotal);
                    txtTotal.setText("$"+String.valueOf(subtotSt));
                    tipoEnvio = "1";
                    spDireccion.setEnabled(true);
                }
                //RETIRO EN LOCAL
                if(position==1){
                    Float precioDelivery = 0.00f;
                    txtCarritoImporteDelivery.setText("$" + String.format("%.2f", precioDelivery));
                    btnConfirmarOrden.setEnabled(true);
                    importeTotal = sumarTotal(0.00f);
                    String subtotSt = String.format("%.2f", importeTotal);
                    txtTotal.setText("$"+String.valueOf(subtotSt));
                    tipoEnvio = "2";
                    idDireccionUsuario = null;
                    spDireccion.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spDireccion.setEnabled(false);
            }
        });

        /// SPINNER DE DIRECCIONES
        spDireccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(serverDirecciones!=null && serverDirecciones.size()>0)idDireccionUsuario = serverDirecciones.get(position).getIdusuario_direccion();
                Log.d("direcciones", "id de direccion elegida: " + idDireccionUsuario);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String subtotSt = String.format("%.2f", importeTotal);
        txtTotal.setText("$"+String.valueOf(subtotSt));

        // LISTA DE PRODUCTOS
        ////////////////////// FUNCIONES PARA MODIFICAR CANTIDADES ETC DEL CARRITO  //////////////////////////////
        // click normal //
        mCarritoAdapter.setOnItemClickListener(new CarritoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Orden_detalle clickedItemCarrito) {
                //Log.d("carrito","click en un item del carrito: ");
                showDialog("orden_detalle",(new Gson()).toJson(clickedItemCarrito));
            }
        });

        // click en el boton EDIT //
        mCarritoAdapter.setOnBtnEditClickListener(new CarritoAdapter.btnEditClickListener() {
            @Override
            public void onBtnEditClick(Orden_detalle clickedItemCarrito) {
                Log.d("carrito","click EDIT en un item del carrito: ");
                showDialog("orden_detalle",(new Gson()).toJson(clickedItemCarrito));
            }
        });

        // escuchamos los eventos LongClick en la lista de productos
        mCarritoAdapter.setOnLongItemClickListener(new CarritoAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(Orden_detalle clickedItemCarrito) {
                if (mActionMode == null) {
                    // Start the CAB using the ActionMode.Callback defined above
                    mActionMode = startSupportActionMode(mActionModeCallback);
                }

                if(clickedItemCarrito.getSelected()){
                    clickedItemCarrito.setSelected(false);
                    c--;
                } else {
                    clickedItemCarrito.setSelected(true);
                    c++;
                }
                Log.d("carrito1","Cant select->"+String.valueOf(c));
                elem_a_borrar= new int[ordenesDetalleLocal.size()];
                int f=0;
                for (int i=0; i<c; i++) {
                    if(ordenesDetalleLocal.get(i).getSelected()){
                        elem_a_borrar[f]=i;
                        f++;
                    }
                }

                Log.d("carritoLongClick","Click largo en item: "+clickedItemCarrito.getProducto_nombre() + "--> Seteado a: " + clickedItemCarrito.getSelected().toString());
            }
        });


        if(ordenesDetalleLocal.size()<1){
            mostrarCarritoEmpty();
        }else{
            mostrarOrdenesDetalle(ordenesDetalleLocal);
        }
        pagaCon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){

                    pagaCon.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");
                    BigDecimal parsed = new BigDecimal(cleanString).setScale(2,BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100),BigDecimal.ROUND_FLOOR);
                    String formato = "$"+String.valueOf(parsed);

                    current = formato;
                    pagaCon.setText(formato);
                    pagaCon.setSelection(formato.length());

                    pagaCon.addTextChangedListener(this);
                    chequearPagaCon();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        chequearCompraMinima();

        mListaCarrito.setAdapter(mCarritoAdapter);
        btnConfirmarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Primero chequeamos que alcance el monto de compra minima
                if(chequearCompraMinima()){
                    // Luego chequeamos si eligio Delivery, que tenga direccion para el mismo
                    if(spDireccion.isEnabled()) {
                        if (chequearDireccion()){
                            if(chequearPagaCon()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                            builder.setTitle("¿Deseas enviar tu orden?");
                            builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    enviarOrden();
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            }else{
                                showError("Por favor, ingresá el monto con el que vas a abonar");
                            }
                        }
                    }
                    // Si eligió Retiro en el local, no comprobamos la direccion
                    if(spTipoEntrega.getSelectedItem().toString().equals("Retiro en el local")){
                        if(chequearPagaCon()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                            builder.setTitle("¿Deseas enviar tu orden?");
                            builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    enviarOrden();
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else{
                            showError("Por favor, ingresá el monto con el que vas a abonar");
                        }
                    }
                }else if(!chequearCompraMinima()){
                    showError("No llegas al mínimo. Agregá mas productos al carrito!");
                }
                if(spDireccion.isEnabled() && !chequearDireccion()){
                    showError("No tenés ninguna dirección de envío! Agregá una con '+'");
                }
            }
        });
        obtenerDirecciones();
    }



    private void mostrarOrdenesDetalle(List<Orden_detalle> ordenes) {
        mCarritoAdapter.swapItems(ordenes);
        mListaCarrito.setVisibility(View.VISIBLE);
    }

    private float sumarTotal(Float delivery){
        Float total = 0.00f;
        for (int i=0; i<ordenesDetalleLocal.size(); i++) {
            total += Float.valueOf(ordenesDetalleLocal.get(i).getOrden_detalle_subtotal());
        }
        if(delivery!=0.00f){
            total += delivery;
        }

        return total;
    }


    private void enviarOrden() {
        // Variables del Objeto "Orden"
        String authorization = SessionPrefs.get(this).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(this).getPrefUsuarioIdUsuario();
        String idempresa = empresa.getIdempresa();
        String nombreEmpresa = empresa.getNombre_fantasia();

        // Tratamiento especial del "Paga con"
        String cleanString = pagaCon.getText().toString().replaceAll("[$,.]", "");
        BigDecimal parsed = new BigDecimal(cleanString).setScale(2,BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100),BigDecimal.ROUND_FLOOR);
        String usuarioPagaCon = String.valueOf(parsed);
        Log.d("pagacom","Paga con: "+usuarioPagaCon);

        String textoNota = nota.getText().toString();
        String total = String.valueOf(importeTotal);

        // Creacion del Objeto "Orden"
        Orden orden = new Orden("","","","",idusuario,idempresa,idDireccionUsuario,total,usuarioPagaCon,textoNota,tipoEnvio,"1",nombreEmpresa,"","",ordenesDetalleLocal);


        // Realizar petición HTTP
        Call<ApiResponse> call = mDeliverybossApi.insertarOrden(authorization,idempresa,idusuario,orden);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call,
                                   Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    String error="";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("application/json")) {
                        try {
                            Log.d("insertarOrden", "se recibio respuesta json (con error): " + response.errorBody().string());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Gson gson = new Gson();
                        ApiResponse mensaje = null;
                        try {
                            mensaje = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(mensaje.getEstado().equals("3"))error = "Su orden no ha podido ser enviada, revise su conexión a internet";
                    }
                    showError(error);
                    return;
                }

                Log.d("insertarOrden", "RAW: " + response.raw().toString());
                showError(response.body().getMensaje());
                Intent intent = new Intent(CarritoActivity.this,PrincipalActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("gson", "Petición rechazada:" + t.getMessage());
            }
        });
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void mostrarCarritoEmpty() {
        mListaCarrito.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        txtEmptyContainer.setText("¡Aún no agregaste nada a tu orden!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(CarritoActivity.this,DetalleEmpresa.class);
                intent.putExtra("carrito",(new Gson()).toJson(ordenesDetalleLocal));
                intent.putExtra("empresaJson",(new Gson()).toJson(empresa));
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean chequearCompraMinima(){
        if(importeTotal>= Float.valueOf(empresa.getCompra_minima())){
            btnConfirmarOrden.setEnabled(true);
            btnConfirmarOrden.setText("Enviar Orden");
            return true;
        }else {
            btnConfirmarOrden.setEnabled(false);
            btnConfirmarOrden.setText("¡Aún no llegas al mínimo!");
            return false;
        }
    }

    private boolean chequearDireccion(){
     if(spDireccion.getSelectedItem().toString().equals("Agrega tu dirección de envío")){
         btnConfirmarOrden.setEnabled(false);
         btnConfirmarOrden.setText("¡No tenés dirección para tu envío!");
         return false;
     }  else {
         if(spTipoEntrega.getSelectedItem().toString().equals("Retiro en el local")){
             btnConfirmarOrden.setEnabled(true);
             btnConfirmarOrden.setText("Enviar Orden");
             return true;
         }
         else {
             btnConfirmarOrden.setEnabled(true);
             btnConfirmarOrden.setText("Enviar Orden");
             return true;
         }
     }
    }
    private boolean chequearPagaCon(){
        if(pagaCon.getText()!=null && !TextUtils.isEmpty(pagaCon.getText())){
            String cleanString = pagaCon.getText().toString().replaceAll("[$,.]", "");
            double parsed = Double.parseDouble(cleanString);
            if(importeTotal<= parsed){
            btnConfirmarOrden.setEnabled(true);
            btnConfirmarOrden.setText("Enviar Orden");
            return true;
            }else{
                btnConfirmarOrden.setEnabled(false);
                btnConfirmarOrden.setText("El monto es menor al importe total");
                return false;
            }
        }else {
            btnConfirmarOrden.setEnabled(false);
            btnConfirmarOrden.setText("Falta el monto con el que pagás");
            return false;
        }
    }


    private void obtenerDirecciones(){
        authorization = SessionPrefs.get(getApplicationContext()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getApplicationContext()).getPrefUsuarioIdUsuario();
        Log.d("direcciones", "Recuperando Direcciones desde el Server");

        // Realizar petición HTTP
        Call<ApiResponseDirecciones> call2 = mDeliverybossApi.obtenerDireccionesUsuario(authorization,idusuario);
        call2.enqueue(new Callback<ApiResponseDirecciones>() {
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

                        Log.d("direcciones", response.errorBody().toString());
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        Log.d("direcciones", response.errorBody().toString());
                        /*try {
                            // Reportar causas de error no relacionado con la API
                            Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                    //showLoadingIndicator(false);
                    //showErrorMessage(error);
                    Log.d("direcciones", response.message());
                    Log.d("direcciones", response.raw().toString());
                    return;
                }

                serverDirecciones = response.body().getDatos();
                Log.d("direcciones", "todo bien, recibido: " + response.body().getDatos().toString());
                if (serverDirecciones.size() > 0) {
                    // Mostrar lista de ordenes
                    mostrarDirecciones(serverDirecciones);
                    Log.d("direcciones","obtuvimos nueva direccion del fragment, pasamos a habilitar el boton");
                    chequearDireccion();
                } else {
                    // Mostrar empty state
                    mostrarDireccionesEmpty();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDirecciones> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("direcciones", "Petición rechazada:" + t.getMessage());
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void mostrarDirecciones(List<Usuario_direccion> direccionesServer) {
        Log.d("direcciones", "Entramos a mostrar direcciones ");
        //String array to store all the book names
        String[] items = new String[direccionesServer.size()];

        //Traversing through the whole list to get all the names
        for(int i=0; i<direccionesServer.size(); i++){
            //Storing names to string array
            items[i] = direccionesServer.get(i).getCalle() + " " + direccionesServer.get(i).getNumero();
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter to spinner
        spDireccion.setAdapter(adapter);

    }

    private void mostrarDireccionesEmpty() {
        String[] items = new String[1];

        //Traversing through the whole list to get all the names
        for(int i=0; i<items.length; i++){
            //Storing names to string array
            items[i] =  "Agrega tu dirección de envío";
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting adapter to spinner
        spDireccion.setAdapter(adapter);
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


    // TODO: Al parecer, no se esta usando la funcion onactivityresult, chequear eventos de EventBus
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int posicion = 0;
        switch (requestCode) {
            case FRAGMENTO_AGREGAR_DIRECCION:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    obtenerDirecciones();

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //Si cierra el dialogo sin agregar nada...
                }
                break;
        }
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

            // EVENTO DE MODIFICACION DE ITEM CARRITO
        }else if(event.getIdevento().equals("5")){
            alModificarItemCarrito(event.getDescripcion());
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


    public void showDialog(String id, String orden_detalle) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        AgregarProductoFragment newFragment = new AgregarProductoFragment();

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("orden_detalle", orden_detalle);
        newFragment.setArguments(args);

        // setup link back to use and display
        //newFragment.setTargetFragment(this,FRAGMENTO_MODIFICAR_CARRITO);
        newFragment.show(fragmentManager.beginTransaction(), "Modificar");
    }

    public void alModificarItemCarrito(String detalle_a_mod){
        int posicion = 0;
                    detalleMod = (new Gson()).fromJson(detalle_a_mod,Orden_detalle.class);

                    for (int i=0; i<ordenesDetalleLocal.size(); i++) {
                        if(ordenesDetalleLocal.get(i).getProducto_idproducto().equals(detalleMod.getProducto_idproducto())){
                            Log.d("eventbus","Encontrado en carrito, posicion-->" + i);
                            posicion = i;
                        }
                    }

                        // SI LA CANTIDAD SETEADA ES '0' LO QUITAMOS DE LA LISTA
                        if(detalleMod.getCantidad().equals("0")){
                            Log.d("eventbus","Borrando...");
                            ordenesDetalleLocal.remove(posicion);
                        }else{
                            Log.d("eventbus","Modificando...");
                            // SI NO, MODIFICAR
                            ordenesDetalleLocal.set(posicion,detalleMod);
                        }
                        refreshOrdenesDetalle();

        // RECALCULO DEL TOTAL
        if(tipoEnvio.equals("1")){
            importeTotal = sumarTotal(Float.valueOf(empresa.getPrecio_delivery()));
            String subtotSt = String.format("%.2f", importeTotal);
            txtTotal.setText("$"+String.valueOf(subtotSt));
        }else if (tipoEnvio.equals("2")){
            importeTotal = sumarTotal(0.00f);
            String subtotSt = String.format("%.2f", importeTotal);
            txtTotal.setText("$"+String.valueOf(subtotSt));
        }
    }


    // MENU CONTEXTUAL PARA ELIMINAR ITEMS DEL CARRITO
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.carrito_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            String titulo = String.valueOf(c)+" seleccionados";
            mode.setTitle(titulo);
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    for(int i=0; i<c;i++){
                        Log.d("carrito1","BORRAR-->"+ordenesDetalleLocal.get(elem_a_borrar[i]).getProducto_nombre()+" selected-->"+ordenesDetalleLocal.get(elem_a_borrar[i]).getSelected());
                        ordenesDetalleLocal.remove(elem_a_borrar[i]);
                    }
                    mode.finish(); // Action picked, so close the CAB
                    refreshOrdenesDetalle();
                    c=0;
                    return true;
                default:
                    return false;
            }
        }


        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            for (int i=0; i<ordenesDetalleLocal.size(); i++) {
                if(ordenesDetalleLocal.get(i).getSelected()){
                    ordenesDetalleLocal.get(i).setSelected(false);
                }
            }
            refreshOrdenesDetalle();

        }
    };

    public void refreshOrdenesDetalle(){
        if(ordenesDetalleLocal.size()>0) mostrarOrdenesDetalle(ordenesDetalleLocal);
        if(ordenesDetalleLocal.size()==0) mostrarCarritoEmpty();
    }
}

// cambio en comentario
//MUY BIEN
