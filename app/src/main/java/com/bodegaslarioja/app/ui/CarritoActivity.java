package com.bodegaslarioja.app.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
//import android.view.ActionMode;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bodegaslarioja.app.data.api.VinosYBodegasApi;
import com.bodegaslarioja.app.data.api.model.BodegasBody;
import com.bodegaslarioja.app.data.api.model.DeliveryRequest;
import com.bodegaslarioja.app.data.api.model.Venta;
import com.bodegaslarioja.app.data.api.model.Venta_detalle;
import com.bodegaslarioja.app.data.api.model.empresa_delivery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bodegaslarioja.app.R;
import com.bodegaslarioja.app.data.api.model.ApiResponse;
import com.bodegaslarioja.app.data.api.model.MessageEvent;

import java.io.IOException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.bodegaslarioja.app.data.api.model.Usuario_direccion;
import com.bodegaslarioja.app.data.prefs.SessionPrefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
    List<Venta_detalle> ordenesDetalleLocal;
    Venta_detalle detalleMod;
    private Retrofit mRestAdapter;
    private VinosYBodegasApi mVinosYBodegasApi;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;
    // TODO: cambiar el request al server, puesto que ya tenemos la direccion del usuario
    List<Usuario_direccion> serverDirecciones;
    Usuario_direccion direccionUsuario;
    //
    List<empresa_delivery> serverDelivery;
    String authorization;
    String stDireccion;
    Boolean abierto_hoy = false;
    Boolean soloRetiroEnLocal = false;

    // Variables multiselect
    private ActionMode mActionMode;

    Spinner spTipoEntrega;
    Spinner spDireccion;
    TextView txtTotal;
    TextView txtCarritoImporteDelivery;
    TextView nombreEmpresa;
    EditText nota;
    EditText pagaCon;
    Button btnConfirmarOrden;
    //Button btnAgregarDireccion;
    BodegasBody empresa;
    Float importeTotal;
    String tipoEnvio = "1";
    String idDireccionUsuario = "";
    //Float precioDelivery;
    String precioDelivery = "";
    int c;
    int[] elem_a_borrar;
    CardView cvDireccion;

    DeliveryRequest obtenerPrecioDelivery;

    private static final int FRAGMENTO_AGREGAR_DIRECCION = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCarrito);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spDireccion = (Spinner) findViewById(R.id.spDireccion);
        //btnAgregarDireccion = (Button) findViewById(R.id.btnCarritoAgregarDireccion);
        /*btnAgregarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("");
            }
        });*/

        txtCarritoImporteDelivery = (TextView) findViewById(R.id.txtCarritoImporteDelivery);
        txtTotal = (TextView) findViewById(R.id.txtCarritoImporteTotal);
        nombreEmpresa = (TextView) findViewById(R.id.lbCarritoNombreEmpresa);
        nota = (EditText) findViewById(R.id.txtCarritoNota);
        //pagaCon = (EditText) findViewById(R.id.txtCarritoPagaCon);
        btnConfirmarOrden = (Button) findViewById(R.id.btnConfirmarOrden);
        mListaCarrito = (RecyclerView) findViewById(R.id.listaCarrito);
        txtEmptyContainer = (TextView) findViewById(R.id.txtEmptyContainerCarrito);
        mEmptyStateContainer = findViewById(R.id.empty_state_containerCarrito);
        mCarritoAdapter = new CarritoAdapter(this, new ArrayList<Venta_detalle>(0));
        spTipoEntrega = (Spinner) findViewById(R.id.spTipoEntrega);
        cvDireccion = (CardView) findViewById(R.id.cvDireccion);

        ///// INICIALIZACION DE VARIABLES
        String listaOrdenes = getIntent().getExtras().getString("ordenes_detalle");
        String empresaJSON = getIntent().getExtras().getString("empresa");
        String direccionJSON = getIntent().getExtras().getString("direccionJson");
        abierto_hoy = getIntent().getExtras().getBoolean("abierto_hoy");
        empresa = new Gson().fromJson(empresaJSON, BodegasBody.class);
        String idUsuario = SessionPrefs.get(this).getPrefUsuarioIdUsuario();
        String usuarioIdciudad = SessionPrefs.get(this).getPrefUsuarioDireccionIdCiudad();
        String usuarioIddireccion = SessionPrefs.get(this).getPrefUsuarioIdDireccion();
        String usuarioCiudad = SessionPrefs.get(this).getPrefUsuarioCiudad();
        String usuarioCalle = SessionPrefs.get(this).getPrefUsuarioDireccionCalle();
        String usuarioNumero = SessionPrefs.get(this).getPrefUsuarioDireccionNumero();
        String usuarioLatitud = SessionPrefs.get(this).getPrefUsuarioDireccionLatitud();
        String usuarioLongitud = SessionPrefs.get(this).getPrefUsuarioDireccionLongitud();
        direccionUsuario = new Usuario_direccion(usuarioIddireccion,idUsuario,usuarioIdciudad,usuarioCiudad,usuarioCalle,usuarioNumero,"","","",usuarioLatitud,usuarioLongitud);
        //Log.d("chequeo","Direccion usuario -->"+direccionUsuario.getLatitud()+","+direccionUsuario.getLongitud());


        Type listType = new TypeToken<ArrayList<Venta_detalle>>(){}.getType();
        ordenesDetalleLocal = new Gson().fromJson(listaOrdenes, listType);
        importeTotal = sumarTotal();
        nombreEmpresa.setText(empresa.getNombre());

        //// RETROFIT
        // Interceptor para log del Request
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Inicializar GSON
        Gson gson =
                new GsonBuilder()
                        .create();

        // Crear conexi??n al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(VinosYBodegasApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        // Crear conexi??n a la API de Deliveryboss
        mVinosYBodegasApi = mRestAdapter.create(VinosYBodegasApi.class);

        // Variables de delivery
        //obtenerPrecioDelivery = Utilidades.calcularPrecioDelivery(direccionUsuario,empresa);

        // TIPO 1 ----- SOLO DELIVERY
        if(empresa.getTipo_entrega().equals("1")){
                soloRetiroEnLocal = false;
                //precioDelivery = obtenerPrecioDelivery.getDatos().getPrecio();
                precioDelivery = "Acordar con el vendedor";
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.tipo_entrega_solo_delivery, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spTipoEntrega.setAdapter(adapter);

                // TIPO 2 ----- SOLO RETIRO EN LOCAL
            }else if(empresa.getTipo_entrega().equals("2")){
                soloRetiroEnLocal = true;
                //precioDelivery = 0.0f;
            precioDelivery = "Acordar con el vendedor";
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.tipo_entrega_solo_retiro, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spTipoEntrega.setAdapter(adapter);
            }else{
                precioDelivery = "Acordar con el vendedor";
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.tipo_entrega, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spTipoEntrega.setAdapter(adapter);
        }
        chequearSoloRetiro();

        //// SUMA DEL DELIVERY (SI APLICA)
        spTipoEntrega.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // Opcion "Eleg?? el tipo de entrega"
                if(position==0){
                    cvDireccion.setVisibility(View.GONE);
                    if(spTipoEntrega.getSelectedItem().equals("Eleg?? el tipo de entrega")) {
                        chequearTipoEntrega();
                        importeTotal = sumarTotal();
                    }
                    if(spTipoEntrega.getSelectedItem().equals("Retiro en el local")){
                        //precioDelivery = 0.00f;
                        txtCarritoImporteDelivery.setText("$" + String.format("%.2f", precioDelivery));
                        btnConfirmarOrden.setEnabled(true);
                        importeTotal = sumarTotal();
                        String subtotSt = String.format("%.2f", importeTotal);
                        txtTotal.setText("$"+String.valueOf(subtotSt));
                        tipoEnvio = "1";
                        idDireccionUsuario = null;
                        spDireccion.setEnabled(false);
                        chequearDireccion();
                    }
                }
                //DELIVERY
                if(position==1){
                    //precioDelivery = Float.valueOf(empresa.getPrecio_delivery());
                    cvDireccion.setVisibility(View.VISIBLE);
                    //Log.d("chequeo","Precio calculado en carrito--> $"+precioDelivery.toString());
                    txtCarritoImporteDelivery.setText(precioDelivery);
                    importeTotal = sumarTotal();
                    String subtotSt = String.format("%.2f", importeTotal);
                    txtTotal.setText("$"+String.valueOf(subtotSt));
                    tipoEnvio = "2";
                    spDireccion.setEnabled(true);

                    chequearDireccion();
                }
                //RETIRO EN LOCAL
                if(position==2){
                    cvDireccion.setVisibility(View.GONE);
                    //precioDelivery = 0.00f;
                    txtCarritoImporteDelivery.setText(precioDelivery);
                    btnConfirmarOrden.setEnabled(true);
                    importeTotal = sumarTotal();
                    String subtotSt = String.format("%.2f", importeTotal);
                    txtTotal.setText("$"+String.valueOf(subtotSt));
                    tipoEnvio = "1";
                    idDireccionUsuario = null;
                    spDireccion.setEnabled(false);

                    chequearDireccion();
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
               // if(serverDirecciones!=null && serverDirecciones.size()>0)idDireccionUsuario = serverDirecciones.get(position-1).getIdusuario_direccion();
                //Log.d("direcciones", "id de direccion elegida: " + idDireccionUsuario);
                if(position>0)stDireccion = serverDirecciones.get(position-1).getIdusuario_direccion();
                chequearDireccion();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String subtotSt = String.format("%.2f", importeTotal);
        txtTotal.setText("$"+String.valueOf(subtotSt));

        // LISTA DE PRODUCTOS
        ////////////////////// FUNCIONES PARA MODIFICAR CANTIDADES DEL CARRITO  //////////////////////////////
        // click normal //
        mCarritoAdapter.setOnItemClickListener(new CarritoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Venta_detalle clickedItemCarrito) {
                if(c>0){
                    if(clickedItemCarrito.getSelected()){
                        clickedItemCarrito.setSelected(false);
                        c--;
                    } else {
                        clickedItemCarrito.setSelected(true);
                        c++;
                    }
                    String titulo = String.valueOf(c)+" seleccionados";
                    mActionMode.setTitle(titulo);
                }else{
                    //Log.d("carrito","click en un item del carrito: ");
                    showDialog("orden_detalle",(new Gson()).toJson(clickedItemCarrito));
                }
            }
        });

        // click en el boton EDIT //
        mCarritoAdapter.setOnBtnEditClickListener(new CarritoAdapter.btnEditClickListener() {
            @Override
            public void onBtnEditClick(Venta_detalle clickedItemCarrito) {
                //Log.d("carrito","click EDIT en un item del carrito: ");
                showDialog("orden_detalle",(new Gson()).toJson(clickedItemCarrito));
            }
        });

        // escuchamos los eventos LongClick en la lista de productos
        mCarritoAdapter.setOnLongItemClickListener(new CarritoAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(Venta_detalle clickedItemCarrito) {
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
                String titulo = String.valueOf(c)+" seleccionados";
                mActionMode.setTitle(titulo);

                //Log.d("carritoLongClick","Click largo en item: "+clickedItemCarrito.getProducto_nombre() + "--> Seteado a: " + clickedItemCarrito.getSelected().toString());
            }
        });


        if(ordenesDetalleLocal.size()<1){
            mostrarCarritoEmpty();
        }else{
            mostrarOrdenesDetalle(ordenesDetalleLocal);
        }
        /*
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
        */

        //chequearCompraMinima();

        mListaCarrito.setAdapter(mCarritoAdapter);
        btnConfirmarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chequearTipoEntrega()){
                // Primero chequeamos que alcance el monto de compra minima
                if(chequearCompraMinima()){
                    // Luego chequeamos si eligio Delivery, que tenga direccion para el mismo
                    if(spDireccion.isEnabled()) {
                        if (chequearDireccion()){
                            if(chequearPagaCon()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                            builder.setTitle("??Deseas enviar tu pedido?");
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
                                showError("Ingres?? el monto con el que vas a pagar");
                            }
                        }
                    }
                    // Si eligi?? Retiro en el local, no comprobamos la direccion
                    if(spTipoEntrega.getSelectedItem().toString().equals("Retiro en el local")){
                        if(chequearPagaCon()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                            builder.setTitle("??Deseas enviar tu pedido?");
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
                            showError("Ingres?? el monto con el que vas a pagar");
                        }
                    }

                } // FIN IF chequearCompraMinima
                else if(!chequearCompraMinima()){
                    showError("No llegas al m??nimo de compra. Agreg?? mas productos al carrito");

                } //Fin IF chequearDireccion
                if(spDireccion.isEnabled() && !chequearDireccion()){
                    showError("No ten??s ninguna direcci??n de env??o! Agreg?? una con '+'");
                }

                } // Fin IF chequearTipoEntrega
                else{
                    showError("Eleg?? el tipo entrega");
                }
            }
        });

        // Obtenemos las direcciones del usuario para el delivery
        obtenerDirecciones();

        // Chequeamos si el local esta abierto para permitir el envio de ordenes
        //chequearLocalAbiertoHoy();

        // Chequeamos que haya seleccionado el tipo de entrega
        chequearTipoEntrega();


    }



    private void mostrarOrdenesDetalle(List<Venta_detalle> ordenes) {
        mCarritoAdapter.swapItems(ordenes);
        mListaCarrito.setVisibility(View.VISIBLE);
    }

    private float sumarTotal(){
        Float total = 0.00f;
        for (int i=0; i<ordenesDetalleLocal.size(); i++) {
            if(ordenesDetalleLocal.get(i).getImporte_subtotal()!=null){
                total += Float.valueOf(ordenesDetalleLocal.get(i).getImporte_subtotal());
            }
        }

        //if(precioDelivery!=null)total+=precioDelivery;

        return total;
    }


    private void enviarOrden() {
        // Mostramos el toast de "enviando orden"
        showError(getResources().getString(R.string.toastCarritoEnviandoOrden));
        // Variables del Objeto "Orden"
        String authorization = SessionPrefs.get(this).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(this).getPrefUsuarioIdUsuario();
        String idempresa = empresa.getIdbodega();
        String nombreEmpresa = empresa.getNombre();
        String stPrecioDelivery = precioDelivery.toString();
        String direccionLocal = "";

        //Retiro en local
        if(tipoEnvio.equals("1")){
            direccionLocal = null;

        }else{
        //Delivery
            direccionLocal = direccionUsuario.getIdusuario_direccion();
        }


        // Tratamiento especial del "Paga con"
        //String cleanString = pagaCon.getText().toString().replaceAll("[$,.]", "");
        //BigDecimal parsed = new BigDecimal(cleanString).setScale(2,BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100),BigDecimal.ROUND_FLOOR);
        //String usuarioPagaCon = String.valueOf(parsed);
        //Log.d("pagacom","Paga con: "+usuarioPagaCon);

        String textoNota = nota.getText().toString();
        String total = String.valueOf(importeTotal);

        // Creacion del Objeto "Orden"
        //Orden orden = new Orden("","","","","",idusuario,idempresa,direccionLocal,stPrecioDelivery,total,usuarioPagaCon,textoNota,tipoEnvio,"1",nombreEmpresa,"","",ordenesDetalleLocal,"0","");
        Venta venta = new Venta("","",empresa.getIdbodega(),idusuario,String.valueOf(importeTotal),textoNota,"","2","1",tipoEnvio,direccionLocal,ordenesDetalleLocal);

        Log.d("venta",new Gson().toJson(venta));
        Log.d("venta",new Gson().toJson(ordenesDetalleLocal));


        // Realizar petici??n HTTP
        Call<ApiResponse> call = mVinosYBodegasApi.insertarVenta(authorization,idempresa,idusuario,venta);
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
                        Log.d("insertarVenta", "se recibio respuesta json (con error): " + response.errorBody().toString());

                    } else {
                        Gson gson = new Gson();
                        ApiResponse mensaje = null;
                        try {
                            mensaje = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(mensaje.getEstado().equals("3"))error = "Tu pedido no fu?? enviado. Revis?? su conexi??n a Internet";
                    }
                    showError(error);
                    return;
                }

                Log.d("invertarVenta", "RAW: " + response.raw().toString());
                showError(response.body().getMensaje());
                Intent intent = new Intent(CarritoActivity.this,PrincipalActivity.class);
                intent.putExtra("estado","1");
                intent.putExtra("mensaje","Pedido enviado correctamente!");
                startActivity(intent);
                EventBus.getDefault().post(new MessageEvent("10", "Se envio el pedido exitosamente."));

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Log.d("gson", "Petici??n rechazada:" + t.getMessage());
            }
        });
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void mostrarCarritoEmpty() {
        mListaCarrito.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        txtEmptyContainer.setText("??Tu carrito est?? vac??o!");
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
        /*
        if(importeTotal>= Float.valueOf(empresa.getCompra_minima())){
            btnConfirmarOrden.setEnabled(true);
            btnConfirmarOrden.setText("Enviar Orden");
            return true;
        }else {
            btnConfirmarOrden.setEnabled(false);
            btnConfirmarOrden.setText("??A??n no llegas al m??nimo!");
            return false;
        }*/
        return  true;
    }

    private boolean chequearTipoEntrega() {
        if (spTipoEntrega.getSelectedItem() != null) {
            if (!spTipoEntrega.getSelectedItem().equals("Eleg?? el tipo de entrega")) {
                btnConfirmarOrden.setEnabled(true);
                btnConfirmarOrden.setText("Enviar Pedido");
                return true;
            } else {
                btnConfirmarOrden.setEnabled(false);
                btnConfirmarOrden.setText("??Eleg?? el tipo de entrega!");
                return false;
            }
        }else return false;
    }


    private boolean chequearDireccion() {
        if(spDireccion.getSelectedItem()!=null){
        if (spDireccion.getSelectedItem().toString().equals("Agrega tu direcci??n de env??o")) {
            btnConfirmarOrden.setEnabled(false);
            btnConfirmarOrden.setText("??No ten??s direcci??n para tu env??o!");
            return false;
        } else {
            if (spDireccion.getSelectedItem().toString().equals("Eleg?? tu direcci??n") && spTipoEntrega.getSelectedItem().toString().equals("Delivery")) {
                btnConfirmarOrden.setEnabled(false);
                btnConfirmarOrden.setText("??Eleg?? la direcci??n de env??o!");
                return false;
            }
            else {
                    if (spTipoEntrega.getSelectedItem().toString().equals("Retiro en el local")) {
                        btnConfirmarOrden.setEnabled(true);
                        btnConfirmarOrden.setText("Enviar Pedido");
                        return true;
                    } else {

                        btnConfirmarOrden.setEnabled(true);
                        btnConfirmarOrden.setText("Enviar Pedido");
                        return true;
                    }
            }
          }

        }
        // Si el spDireccion es igual a NULL
        else{
            return false;
        }

        }

    private boolean chequearPagaCon(){
        /*
        if(pagaCon.getText()!=null && !TextUtils.isEmpty(pagaCon.getText())){
            String cleanString = pagaCon.getText().toString().replaceAll("[$,.]", "");
            double parsed = Double.parseDouble(cleanString);
            double montoSinDecimales = parsed / 100;
            //Log.d("carrito","Monto parseado->"+String.valueOf(parsed));
            //Log.d("carrito","Monto corregido->"+String.valueOf(montoSinDecimales));
            if(importeTotal<= montoSinDecimales){
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
            btnConfirmarOrden.setText("Falta el monto con el que pag??s");
            return false;
        }*/
        return true;
    }


    private void obtenerDirecciones(){
        /*
        authorization = SessionPrefs.get(getApplicationContext()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getApplicationContext()).getPrefUsuarioIdUsuario();
        Log.d("direcciones", "Recuperando Direcciones desde el Server");

        // Realizar petici??n HTTP
        Call<ApiResponseDirecciones> call2 = mVinosYBodegasApi.obtenerDireccionesUsuario(authorization,idusuario);
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
                    } else {
                        Log.d("direcciones", response.errorBody().toString());
                    }

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
                Log.d("direcciones", "Petici??n rechazada:" + t.getMessage());
                //showErrorMessage("Error de comunicaci??n");
            }
        });
        */
        mostrarDirecciones(direccionUsuario);
    }

    private void mostrarDirecciones(Usuario_direccion direccion) {
        //Log.d("direcciones", "Entramos a mostrar direcciones ");

        String[] items = new String[1];
        items[0] = direccion.getCalle() + " " + direccion.getNumero();

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter to spinner
        spDireccion.setAdapter(adapter);
        spDireccion.setEnabled(false);

    }

    private void mostrarDireccionesEmpty() {
        String[] items = new String[1];

        //Traversing through the whole list to get all the names
        for(int i=0; i<items.length; i++){
            //Storing names to string array
            items[i] =  "Agrega tu direcci??n de env??o";
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting adapter to spinner
        spDireccion.setAdapter(adapter);
    }


/*
    public void showDialog(String direccion) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        ModificarDireccionFragment newFragment = new ModificarDireccionFragment();

        Bundle args = new Bundle();
        if(!direccion.isEmpty() && !direccion.equals(""))args.putString("direccion", direccion);
        newFragment.setArguments(args);

        // setup link back to use and display
        //newFragment.setTargetActivity(this,FRAGMENTO_AGREGAR_DIRECCION);
        newFragment.show(fragmentManager.beginTransaction(), "Agregar direccion");

    }*/


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
        //Log.d("eventbus","evento recibido, descripcion: " + event.getDescripcion());
        if(event.getIdevento().equals("2")){
            obtenerDirecciones();
        }else if(event.getIdevento().equals("3")){
            obtenerDirecciones();
        }else if(event.getIdevento().equals("4")){
            obtenerDirecciones();

            // EVENTO DE MODIFICACION DE ITEM CARRITO
        }else if(event.getIdevento().equals("5")){
            Log.d("joaco_prod","evento de modificacion de item en carrito");
            Log.d("joaco_prod",event.getIdevento());
            Log.d("joaco_prod",event.getDescripcion());
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
        detalleMod = (new Gson()).fromJson(detalle_a_mod,Venta_detalle.class);
        Log.d("joaco_prod",detalle_a_mod);


                    for (int i=0; i<ordenesDetalleLocal.size(); i++) {
                        if(ordenesDetalleLocal.get(i).getIdproducto().equals(detalleMod.getIdproducto())){
                            //Log.d("eventbus","Encontrado en carrito, posicion-->" + i);
                            posicion = i;
                        }
                    }

                        // SI LA CANTIDAD SETEADA ES '0' LO QUITAMOS DE LA LISTA
                        if(detalleMod.getCantidad().equals("0")){
                            //Log.d("eventbus","Borrando...");
                            ordenesDetalleLocal.remove(posicion);
                        }else{
                            //Log.d("eventbus","Modificando...");
                            // SI NO, MODIFICAR
                            ordenesDetalleLocal.set(posicion,detalleMod);
                        }
                        refreshOrdenesDetalle();

        // RECALCULO DEL TOTAL
        if(tipoEnvio.equals("1")){
            importeTotal = sumarTotal();
            String subtotSt = String.format("%.2f", importeTotal);
            txtTotal.setText("$"+String.valueOf(subtotSt));
        }else if (tipoEnvio.equals("2")){
            importeTotal = sumarTotal();
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
                    // Primero guardamos los elementos a borrar en un array
                    elem_a_borrar= new int[c];
                    int f=0;
                    for (int i=0; i<ordenesDetalleLocal.size(); i++) {
                        if(ordenesDetalleLocal.get(i).getSelected()){
                            elem_a_borrar[f]=i;
                            f++;
                        }
                    }
                    //Log.d("carrito1","A BORRAR-->"+Arrays.toString(elem_a_borrar));
                    // Caso seguido los borramos uno por uno del array original (array original= ordenesDetalleLocal)
                    // Lo borramos en orden invertido para que no afecte la posicion de los items que van quedando
                    for(int i=c; i>0;i--){
                        //Log.d("carrito1","borrando-->"+String.valueOf(elem_a_borrar[i-1]));
                        ordenesDetalleLocal.remove(elem_a_borrar[i-1]);
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
            c=0;
        }
    };

    public void refreshOrdenesDetalle(){
        if(ordenesDetalleLocal.size()>0) mostrarOrdenesDetalle(ordenesDetalleLocal);
        if(ordenesDetalleLocal.size()==0) mostrarCarritoEmpty();
    }

    public void chequearLocalAbiertoHoy(){
        if(abierto_hoy){
            btnConfirmarOrden.setEnabled(true);
            btnConfirmarOrden.setText("Enviar Pedido");
        }else{
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new android.support.v7.app.AlertDialog.Builder(this);
            } else {
                builder = new AlertDialog.Builder(getBaseContext());
            }
            builder.setTitle(R.string.alertLocalAbiertoTitle)
                    .setMessage(R.string.alertLocalAbiertoMsg)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Volvemos a la activity detalle empresa
                            Intent intent = new Intent(CarritoActivity.this,DetalleEmpresa.class);
                            intent.putExtra("carrito",(new Gson()).toJson(ordenesDetalleLocal));
                            intent.putExtra("empresaJson",(new Gson()).toJson(empresa));
                            startActivity(intent);
                        }
                    })
                    .setIcon(R.drawable.ic_info)
                    .setCancelable(false)
                    .show();
        }
    }

    public void chequearSoloRetiro(){
        if(soloRetiroEnLocal){
            cvDireccion.setVisibility(View.GONE);
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new android.support.v7.app.AlertDialog.Builder(this);
            } else {
                builder = new AlertDialog.Builder(getBaseContext());
            }
            builder.setTitle(R.string.alertRetiroLocalTitle)
                    .setMessage(R.string.alertRetiroLocalMsg)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(R.drawable.ic_location_dark)
                    .setCancelable(false)
                    .show();
        }else{
            cvDireccion.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DetalleEmpresa.class);
        intent.putExtra("carrito",(new Gson()).toJson(ordenesDetalleLocal));
        intent.putExtra("empresaJson",(new Gson()).toJson(empresa));
        startActivity(intent);
    }


}
