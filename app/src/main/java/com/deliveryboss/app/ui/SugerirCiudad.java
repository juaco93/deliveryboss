package com.deliveryboss.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.VinosYBodegasApi;
import com.deliveryboss.app.data.api.model.ApiResponse;
import com.deliveryboss.app.data.api.model.CiudadSugerida;
import com.deliveryboss.app.data.prefs.SessionPrefs;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SugerirCiudad extends AppCompatActivity {

    TextView Ciudad;
    TextView Comentario;
    TextView Email;
    Button btnEnviarSugerencia;

    String authorization;
    private Retrofit mRestAdapter;
    private VinosYBodegasApi mVinosYBodegasApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerir_ciudad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Remover título de la action bar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Ciudad = (TextView) findViewById(R.id.txtSCCiudad);
        Comentario = (TextView) findViewById(R.id.txtSCComentario);
        Email = (TextView) findViewById(R.id.txtSCEmail);
        btnEnviarSugerencia = (Button) findViewById(R.id.btnSCEnviarSugerencia);
        btnEnviarSugerencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sugerirCiudad();
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
                .baseUrl(VinosYBodegasApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        // Crear conexión a la API de Deliveryboss
        mVinosYBodegasApi = mRestAdapter.create(VinosYBodegasApi.class);

    }

    private void sugerirCiudad() {
        authorization = SessionPrefs.get(getApplicationContext()).getPrefUsuarioToken();
        // Store values at the time of the login attempt.
        String ciudad = Ciudad.getText().toString();
        String comentario = Comentario.getText().toString();
        String email = Email.getText().toString();

        Call<ApiResponse> call = mVinosYBodegasApi.sugerirCiudad(authorization,new CiudadSugerida(ciudad,comentario,email));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Ocultar progreso
                // Procesar errores
                if (!response.isSuccessful()) {
                    String error="";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("application/json")) {
                        //Log.d("sugerir", "se recibio respuesta json (con error): " + response.errorBody().string());

                    } else {
                        Gson gson = new Gson();
                        ApiResponse mensaje = null;
                        try {
                            mensaje = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //if(mensaje.getEstado().equals("3"))error = "La empresa ya se encuentra registrada";
                    }
                    //showLoginError(error);
                    return;
                }
                //Log.d("sugerir", "RAW: " + response.raw().toString());
                showErrorMessage(response.body().getMensaje());

                // Ir a la pantalla principal
                startActivity(new Intent(SugerirCiudad.this, CiudadNoDisponible.class));
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // showLoginError(t.getMessage());
            }
        });

    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(this,SeleccionarCiudad.class);
                //intent.putExtra("idCiudad",ciudadIntent);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SeleccionarCiudad.class);
        startActivity(intent);
    }

}
