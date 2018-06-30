package com.deliveryboss.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.DeliverybossApi;
import com.deliveryboss.app.data.api.model.ApiResponseCiudades;
import com.deliveryboss.app.data.api.model.ApiResponseDirecciones;
import com.deliveryboss.app.data.api.model.Ciudad;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.data.prefs.SessionPrefs;
import com.deliveryboss.app.data.util.Utilidades;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeleccionarDireccion extends AppCompatActivity {

    Spinner spDirecciones;
    CardView btnContinuar;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    List<Usuario_direccion> serverDirecciones;
    Usuario_direccion direccionElegida;

    String stDireccion;
    String authorization;
    //Button btnAgregarDireccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_direccion);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        spDirecciones = (Spinner) findViewById(R.id.spSeleccionarDireccion);
        //btnAgregarDireccion = (Button) findViewById(R.id.btnSeleccionarDireccionAgregar);
        /*btnAgregarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeleccionarDireccion.this,MisDireccionesActivity.class);
                startActivity(intent);
            }
        });*/
        btnContinuar = (CardView) findViewById(R.id.btnSeleccionarDireccionContinuar);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spDirecciones.getSelectedItem().equals("Seleccioná tu dirección")){
                    if(spDirecciones.getSelectedItem().equals("No tenés direcciones guardadas")){
                        Intent intent = new Intent(SeleccionarDireccion.this, MisDireccionesActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(SeleccionarDireccion.this, PrincipalActivity.class);

                        // Guardar usuario en preferencias
                        //SessionPrefs.get(SeleccionarDireccion.this).saveDireccion(direccionElegida.getIdusuario_direccion(), direccionElegida.getCiudad_idciudad(), direccionElegida.getCiudad(), direccionElegida.getCalle(), direccionElegida.getNumero(), direccionElegida.getLatitud(), direccionElegida.getLongitud());
                        Utilidades.setearDireccionPorDefecto(SeleccionarDireccion.this,(new Gson()).toJson(direccionElegida));
                        startActivity(intent);
                        finish();
                    }
                }else{
                        Toast.makeText(SeleccionarDireccion.this, "Seleccioná tu dirección para continuar", Toast.LENGTH_LONG).show();
                }
            }
        });

        /// SPINNER DE CIUDADES
        spDirecciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    direccionElegida = serverDirecciones.get(position-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkUserSession();

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


        obtenerDirecciones();

    }

    private void obtenerDirecciones(){
        authorization = SessionPrefs.get(getApplicationContext()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getApplicationContext()).getPrefUsuarioIdUsuario();
        //Log.d("direcciones", "Recuperando Direcciones desde el Server");

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

                        //Log.d("direcciones", response.errorBody().toString());
                    } else {
                        //Log.d("direcciones", response.errorBody().toString());
                    }

                    //Log.d("direcciones", response.message());
                    //Log.d("direcciones", response.raw().toString());
                    return;
                }

                serverDirecciones = response.body().getDatos();
                //Log.d("direcciones", "todio bien, recibido: " + response.body().getDatos().toString());
                if (serverDirecciones.size() > 0) {
                    // Mostrar lista de ordenes
                    mostrarDirecciones(serverDirecciones);
                    //Log.d("direcciones","obtuvimos nueva direccion del fragment, pasamos a habilitar el boton");
                } else {
                    // Mostrar empty state
                    mostrarDireccionesEmpty();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDirecciones> call, Throwable t) {
                //showLoadingIndicator(false);
                //Log.d("direcciones", "Petición rechazada:" + t.getMessage());
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void mostrarDirecciones(List<Usuario_direccion> direccionesServer) {
        //Log.d("direcciones", "Entramos a mostrar direcciones ");

        // String[] items = new String[direccionesServer.size()];
        String[] items = new String[direccionesServer.size()+1];


        int c=1;
        items[0] = "Seleccioná tu dirección";
        //Traversing through the whole list to get all the names
        for(int i=0; i<direccionesServer.size(); i++){
            //Storing names to string array

            items[c] = direccionesServer.get(i).getCalle() + " " + direccionesServer.get(i).getNumero();
            c++;

            //items[i] = direccionesServer.get(i).getCalle() + " " + direccionesServer.get(i).getNumero();
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter to spinner
        spDirecciones.setAdapter(adapter);

    }

    private void mostrarDireccionesEmpty() {
        String[] items = new String[1];

        //Traversing through the whole list to get all the names
        for(int i=0; i<items.length; i++){
            //Storing names to string array
            items[i] =  "No tenés direcciones guardadas";
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting adapter to spinner
        spDirecciones.setAdapter(adapter);
    }

    private void checkUserSession(){
        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
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

}
