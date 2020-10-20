package com.bodegaslarioja.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bodegaslarioja.app.R;
import com.bodegaslarioja.app.data.api.VinosYBodegasApi;
import com.bodegaslarioja.app.data.api.model.ApiResponseCiudades;
import com.bodegaslarioja.app.data.api.model.Ciudad;
import com.bodegaslarioja.app.data.prefs.SessionPrefs;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeleccionarCiudad extends AppCompatActivity {

    Spinner spCiudades;
    CardView cvVerRestaurantes;
    private Retrofit mRestAdapter;
    private VinosYBodegasApi mVinosYBodegasApi;
    List<Ciudad> serverCiudades;

    String stCiudad;
    Button btnSelCiudadSugerir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_ciudad);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        spCiudades = (Spinner) findViewById(R.id.spSeleccionarCiudad);
        cvVerRestaurantes = (CardView) findViewById(R.id.cvVerRestaurantes);
        cvVerRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spCiudades.getSelectedItem().toString()!="Seleccioná tu ciudad"){
                    Intent intent = new Intent(SeleccionarCiudad.this, PrincipalActivity.class);
                    //intent.putExtra("idCiudad",stCiudad);
                    SessionPrefs.get(SeleccionarCiudad.this).saveCiudad(stCiudad, spCiudades.getSelectedItem().toString());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(SeleccionarCiudad.this, "Por favor, seleccioná tu ciudad antes de continuar", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnSelCiudadSugerir = (Button) findViewById(R.id.btnSelCiudadSugerir);
        btnSelCiudadSugerir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeleccionarCiudad.this, SugerirCiudad.class));
            }
        });

        /// SPINNER DE CIUDADES
        spCiudades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)stCiudad = serverCiudades.get(position-1).getIdciudad();
                Log.d("direcciones", "id de ciudad elegida: " + stCiudad);
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
                .baseUrl(VinosYBodegasApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        // Crear conexión a la API de Deliveryboss
        mVinosYBodegasApi = mRestAdapter.create(VinosYBodegasApi.class);


        obtenerCiudades();

    }

    private void obtenerCiudades(){
        String authorization = SessionPrefs.get(this).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(this).getPrefUsuarioIdUsuario();
        Log.d("direcciones", "Recuperando Ciudades desde el Server");

        // Realizar petición HTTP
        Call<ApiResponseCiudades> call2 = mVinosYBodegasApi.obtenerCiudades(authorization);
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
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void mostrarCiudades(List<Ciudad> ciudadesServer) {
        //String array to store all the book names
        String[] items = new String[ciudadesServer.size()+1];

        int c=1;
        items[0] = "Seleccioná tu ciudad";
        //Traversing through the whole list to get all the names
        for(int i=0; i<ciudadesServer.size(); i++){
            //Storing names to string array
            items[c] = ciudadesServer.get(i).getCiudad();
            c++;
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.dropdown_item_layoutpropio);

        //setting adapter to spinner
        spCiudades.setAdapter(adapter);

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
        adapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting adapter to spinner
        spCiudades.setAdapter(adapter);
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
