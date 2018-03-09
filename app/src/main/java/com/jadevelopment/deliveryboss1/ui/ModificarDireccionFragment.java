package com.jadevelopment.deliveryboss1.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.DeliverybossApi;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponse;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseCiudades;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseDirecciones;
import com.jadevelopment.deliveryboss1.data.api.model.Ciudad;
import com.jadevelopment.deliveryboss1.data.api.model.MessageEvent;
import com.jadevelopment.deliveryboss1.data.api.model.Orden;
import com.jadevelopment.deliveryboss1.data.api.model.Orden_detalle;
import com.jadevelopment.deliveryboss1.data.api.model.Producto;
import com.jadevelopment.deliveryboss1.data.api.model.Usuario_direccion;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModificarDireccionFragment extends DialogFragment {

    private static final int ABRIR_MAPA_REQUEST_CODE = 1;

    //EditText ciudad;
    EditText calle;
    EditText numero;
    EditText habitacion;
    EditText barrio;
    EditText telefono;
    EditText referencia;
    ImageButton ubicacion;
    EditText latitudLongitud;
    Spinner ciudad;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    List<Ciudad> serverCiudades;
    private TextInputLayout mFloatLabelCalle;
    private TextInputLayout mFloatLabelNumero;
    private TextInputLayout mFloatLabelHabitacion;
    private TextInputLayout mFloatLabelBarrio;
    private TextInputLayout mFloatLabelTelefono;
    private TextInputLayout mFloatLabelReferencia;
    View focusView = null;

    Usuario_direccion direccion;
    Button btnAceptar;
    Button btnCancelar;

    Boolean esAlta = false;
    String iddireccion;
    String stCiudad;
    String stCalle;
    String stNumero;
    String stHabitacion;
    String stBarrio;
    String stTelefono;
    String stReferencia;

    String latRecibida = "";
    String longRecibida = "";
    String latEnviada = "";
    String longEnviada = "";

    MapView mMapView;
    private GoogleMap googleMap;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_modificar_direccion, null);

        // Get your views by using view.findViewById() here and do your listeners.
        //ciudad = (EditText) view.findViewById(R.id.txtDireccionEditCiudad);
        ciudad = (Spinner) view.findViewById(R.id.spDireccionEditCiudad);
        calle = (EditText) view.findViewById(R.id.txtDireccionEditCalle);
        numero = (EditText) view.findViewById(R.id.txtDireccionEditNumero);
        habitacion = (EditText) view.findViewById(R.id.txtDireccionEditHabitacion);
        barrio = (EditText) view.findViewById(R.id.txtDireccionEditBarrio);
        telefono = (EditText) view.findViewById(R.id.txtDireccionEditTelefono);
        referencia = (EditText) view.findViewById(R.id.txtDireccionEditReferencia);
        ubicacion = (ImageButton) view.findViewById(R.id.btnAgregarUbicacion);
        latitudLongitud = (EditText) view.findViewById(R.id.txtDireccionEditUbicacion);

        //FloatLabels
        mFloatLabelCalle = (TextInputLayout) view.findViewById(R.id.lbDireccionEditCalle);
        mFloatLabelNumero = (TextInputLayout) view.findViewById(R.id.lbDireccionEditNumero);
        mFloatLabelHabitacion = (TextInputLayout) view.findViewById(R.id.lbDireccionEditHabitacion);
        mFloatLabelBarrio = (TextInputLayout) view.findViewById(R.id.lbDireccionEditBarrio);
        mFloatLabelTelefono = (TextInputLayout) view.findViewById(R.id.lbDireccionEditTelefono);
        mFloatLabelReferencia = (TextInputLayout) view.findViewById(R.id.lbDireccionEditReferencia);

        /// SPINNER DE CIUDADES
        ciudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stCiudad = serverCiudades.get(position).getIdciudad();
                Log.d("direcciones", "id de ciudad elegida: " + stCiudad);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAceptar = (Button) view.findViewById(R.id.btnAgregarDireccion);

//        Log.d("direccionJSON",getArguments().getString("direccion"));

        // MAPA PARA AGREGAR MARCADOR

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Mantené presionado en el mapa para marcar tu ubicación",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),MapsActivity.class);
                if((latEnviada!=null && !latEnviada.equals("")) && (longEnviada!=null && !longEnviada.equals("")) ){
                    Log.d("ubicacion","Ubicacion enviada: "+latEnviada+","+longEnviada);
                    intent.putExtra("latitudLongitud",latEnviada+","+longEnviada);
                }

                startActivityForResult(intent,ABRIR_MAPA_REQUEST_CODE);
            }
        });



        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(esAlta){
                    insertarDireccion();
                }else{
                    modificarDireccion();
                }
            }
        });

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

        // Cargar spinner de ciudades
        obtenerCiudades();

        // Cargar los campos iniciales solo si es modificacion
        if((getArguments().getString("direccion")!=null)){
            direccion = (new Gson()).fromJson((getArguments().getString("direccion")),Usuario_direccion.class);
            //ciudad.setText(direccion.getCiudad_idciudad());
            calle.setText(direccion.getCalle());
            numero.setText(direccion.getNumero());
            habitacion.setText(direccion.getHabitacion());
            barrio.setText(direccion.getBarrio());
            telefono.setText(direccion.getTelefono());
            referencia.setText(direccion.getIndicaciones());
            if((direccion.getLatitud()!=null && !direccion.getLatitud().isEmpty()) && (direccion.getLongitud()!=null && !direccion.getLongitud().isEmpty())){
                latitudLongitud.setText("¡Ubicación registrada!");
                latEnviada= direccion.getLatitud();
                longEnviada=direccion.getLongitud();
            }else{
                latitudLongitud.setText("¡Agregá tu ubicación!");
            }

            esAlta = false;
        }else{
            //ciudad.setText("");
            calle.setText("");
            numero.setText("");
            habitacion.setText("");
            barrio.setText("");
            telefono.setText("");
            referencia.setText("");
            esAlta = true;
        }

        if(!esAlta)btnAceptar.setText("Modificar dirección");
        if(esAlta)btnAceptar.setText("Agregar dirección");

        // Set the dialog layout
        builder.setView(view);
        return builder.create();
    }

    private void insertarDireccion() {
        // Variables del Objeto "Direccion"
        String authorization = SessionPrefs.get(getActivity()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getActivity()).getPrefUsuarioIdUsuario();
        //String stCiudad = ciudad.getText().toString();
        stCalle = calle.getText().toString();
        stNumero = numero.getText().toString();
        stHabitacion = habitacion.getText().toString();
        stBarrio = barrio.getText().toString();
        stTelefono = telefono.getText().toString();
        stReferencia = referencia.getText().toString();

        boolean cancelar = camposFaltantes();
        if (cancelar) {
            focusView.requestFocus();
        }else {

            // Creacion del Objeto "Direccion"
            Usuario_direccion nuevaDireccion = new Usuario_direccion("", idusuario, stCiudad, "", stCalle, stNumero, stHabitacion,  stBarrio, stTelefono, stReferencia, latRecibida, longRecibida);

            // Realizar petición HTTP
            Call<ApiResponse> call = mDeliverybossApi.insertarDireccionUsuario(authorization, idusuario, nuevaDireccion);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call,
                                       Response<ApiResponse> response) {
                    if (!response.isSuccessful()) {
                        String error = "";
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
                            if (mensaje.getEstado().equals("3"))
                                error = "Su orden no ha podido ser enviada, revise su conexión a internet";
                        }
                        showError(error);
                        return;
                    }

                    Log.d("insertarDireccion", "RAW: " + response.raw().toString());
                    showError(response.body().getMensaje());
                    dismiss();
                    EventBus.getDefault().post(new MessageEvent("2", "Se inserto nueva direccion."));

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d("gson", "Petición rechazada:" + t.getMessage());
                    dismiss();
                }
            });
        }
    }

    private void modificarDireccion() {

        // Variables del Objeto "Direccion"
        String authorization = SessionPrefs.get(getActivity()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getActivity()).getPrefUsuarioIdUsuario();
        iddireccion = direccion.getIdusuario_direccion();
        //stCiudad = ciudad.getText().toString();
        stCalle = calle.getText().toString();
        stNumero = numero.getText().toString();
        if(habitacion.getText()!=null && TextUtils.isEmpty(habitacion.getText().toString())) {
            stHabitacion = habitacion.getText().toString();
        }else{
            stHabitacion = null;
        }
        stBarrio = barrio.getText().toString();
        stTelefono = telefono.getText().toString();
        stReferencia = referencia.getText().toString();

        boolean cancelar = camposFaltantes();
        if (cancelar) {
            focusView.requestFocus();
        }else {

            // Creacion del Objeto "Direccion"
            Usuario_direccion nuevaDireccion = new Usuario_direccion("", idusuario, stCiudad, "", stCalle, stNumero, stHabitacion, stBarrio, stTelefono, stReferencia, latRecibida, longRecibida);

            Gson gson = new Gson();
            String dire = gson.toJson(nuevaDireccion);
            Log.d("ubicacion", "Objeto Direccion enviada a sv-->"+ dire );

            // Realizar petición HTTP
            Call<ApiResponse> call = mDeliverybossApi.modificarDireccionUsuario(authorization, idusuario, iddireccion, nuevaDireccion);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call,
                                       Response<ApiResponse> response) {
                    if (!response.isSuccessful()) {
                        String error = "";
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
                            if (mensaje.getEstado().equals("3"))
                                error = "Su orden no ha podido ser enviada, revise su conexión a internet";
                        }
                        showError(error);
                        return;
                    }

                    Log.d("insertarDireccion", "RAW: " + response.raw().toString());
                    showError(response.body().getMensaje());
                    EventBus.getDefault().post(new MessageEvent("3", "Se modifico una direccion."));
                    dismiss();

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d("gson", "Petición rechazada:" + t.getMessage());
                    dismiss();
                }
            });
        }
    }


    private void obtenerCiudades(){
        String authorization = SessionPrefs.get(getContext()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getContext()).getPrefUsuarioIdUsuario();
        Log.d("direcciones", "Recuperando Ciudades desde el Server");

        // Realizar petición HTTP
        Call<ApiResponseCiudades> call2 = mDeliverybossApi.obtenerCiudades(authorization);
        call2.enqueue(new Callback<ApiResponseCiudades>() {
            @Override
            public void onResponse(Call<ApiResponseCiudades> call,
                                   Response<ApiResponseCiudades> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {

                        Log.d("ciudades", response.errorBody().toString());
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        Log.d("ciudades", response.errorBody().toString());
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

                serverCiudades = response.body().getDatos();
                Log.d("direcciones", "todo bien, recibido: " + response.body().getDatos().toString());
                if (serverCiudades.size() > 0) {
                    // Mostrar lista de ordenes
                    mostrarCiudades(serverCiudades);
                } else {
                    // Mostrar empty state
                    mostrarCiudadesEmpty();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseCiudades> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("direcciones", "Petición rechazada:" + t.getMessage());
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    private void mostrarCiudades(List<Ciudad> ciudadesServer) {
        //String array to store all the book names
        String[] items = new String[ciudadesServer.size()];

        //Traversing through the whole list to get all the names
        for(int i=0; i<ciudadesServer.size(); i++){
            //Storing names to string array
            items[i] = ciudadesServer.get(i).getCiudad();
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.dropdown_item_layoutpropio);

        //setting adapter to spinner
        ciudad.setAdapter(adapter);

        if(direccion!=null) {
            if (!direccion.getCiudad().equals(null) && !esAlta) {
                int spinnerPosition = adapter.getPosition(direccion.getCiudad());
                ciudad.setSelection(spinnerPosition);
            }
        }
        //Creating an array adapter for list view
    }

    private void mostrarCiudadesEmpty() {
        String[] items = new String[1];

        //Traversing through the whole list to get all the names
        for(int i=0; i<items.length; i++){
            //Storing names to string array
            items[i] =  "No existen ciudades para mostrar";
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting adapter to spinner
        ciudad.setAdapter(adapter);
    }


    public boolean camposFaltantes(){
        boolean cancel = false;
        focusView = null;

        calle.setError(null);
        numero.setError(null);
        //habitacion.setError(null);
        barrio.setError(null);
        telefono.setError(null);

        mFloatLabelCalle.setError(null);
        mFloatLabelNumero.setError(null);
        mFloatLabelHabitacion.setError(null);
        mFloatLabelBarrio.setError(null);
        mFloatLabelTelefono.setError(null);

        // Calle
        if (TextUtils.isEmpty(stCalle)) {
            Log.d("direccionMod","Esta vacio calle: "+ stCalle+" <-");
            calle.setError(getString(R.string.error_field_required));
            mFloatLabelCalle.setError(getString(R.string.error_field_required));
            focusView = calle;
            cancel = true;
        }
        // Numero
        if (TextUtils.isEmpty(stNumero)) {
            Log.d("direccionMod","Esta vacio numero: "+ stNumero+" <-");
            numero.setError(getString(R.string.error_field_required));
            mFloatLabelNumero.setError(getString(R.string.error_field_required));
            focusView = numero;
            cancel = true;
        }
        // Habitacion es opcional
        /*
        if (TextUtils.isEmpty(stHabitacion)) {
            Log.d("direccionMod","Esta vacio hab: "+ stHabitacion+" <-");
            habitacion.setError(getString(R.string.error_field_required));
            mFloatLabelHabitacion.setError(getString(R.string.error_field_required));
            focusView = habitacion;
            cancel = true;
        }*/

        // Barrio
        if (TextUtils.isEmpty(stBarrio)) {
            Log.d("direccionMod","Esta vacio barrio: "+ stBarrio+" <-");
            barrio.setError(getString(R.string.error_field_required));
            mFloatLabelBarrio.setError(getString(R.string.error_field_required));
            focusView = barrio;
            cancel = true;
        }
        // Telefono
        if (TextUtils.isEmpty(stTelefono)) {
            Log.d("direccionMod","Esta vacio telefono: "+ stTelefono+" <-");
            telefono.setError(getString(R.string.error_field_required));
            mFloatLabelTelefono.setError(getString(R.string.error_field_required));
            focusView = telefono;
            cancel = true;
        }

        // Ubicacion
        if (latitudLongitud.getText()!=null){
            if(TextUtils.isEmpty(latitudLongitud.getText())) {
                Log.d("direccionMod", "Esta vacio ubicacion: " + stTelefono + " <-");
                latitudLongitud.setError(getString(R.string.error_field_required));
                //mFloatLabelTelefono.setError(getString(R.string.error_field_required));
                focusView = latitudLongitud;
                cancel = true;
            }
        }
        return cancel;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ABRIR_MAPA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                if(latRecibida!=null && longRecibida!=null) {
                    latRecibida = data.getStringExtra("CoordLat");
                    longRecibida = data.getStringExtra("CoordLon");

                    Log.d("ubicacion","Ubicacion recibida: "+latRecibida+","+longRecibida);
                    latitudLongitud.setText("¡Ubicación registrada!");
                }
                // Latitud y longitud en null
                else{
                    Log.d("ubicacion", "Lat y Long desde el mapa no se guardaron");
                }
            }

            // Salio del mapa sin guardar nada y esta la lat y long en NULL (presiono flecha atras)
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                if(direccion.getLatitud()!=null && direccion.getLongitud()!=null) {
                    if(!direccion.getLatitud().isEmpty() && !direccion.getLongitud().isEmpty()) {
                        Log.d("ubicacion", "Usando latitud y longitud ORIGINALES");
                        latRecibida = direccion.getLatitud();
                        longRecibida = direccion.getLongitud();

                        Log.d("ubicacion","Valor de latRecibida--->" + latRecibida);
                        Log.d("ubicacion","Valor de longRecibida--->" + longRecibida);
                    }
                }
            }
        }
    }

}
