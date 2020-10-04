package com.deliveryboss.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.VinosYBodegasApi;
import com.deliveryboss.app.data.api.model.ApiResponse;
import com.deliveryboss.app.data.api.model.EmpresaSugerida;
import com.deliveryboss.app.data.prefs.SessionPrefs;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SugerirEmpresa extends AppCompatActivity {
    TextView Nombre;
    TextView Apellido;
    TextView Email;
    TextView Telefono;
    TextView NombreEmpresa;
    TextView DireccionEmpresa;
    TextView Ciudad;
    Spinner spTipoSugerencia;

    String authorization;

    Button btnSugerirEmpresa;

    private Retrofit mRestAdapter;
    private VinosYBodegasApi mVinosYBodegasApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerir_empresa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Remover título de la action bar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Nombre = (TextView) findViewById(R.id.txtSENombre);
        Apellido = (TextView) findViewById(R.id.txtSEApellido);
        Email = (TextView) findViewById(R.id.txtSEEmail);
        Telefono = (TextView) findViewById(R.id.txtSETelefono);
        NombreEmpresa = (TextView) findViewById(R.id.txtSENombreEmpresa);
        DireccionEmpresa = (TextView) findViewById(R.id.txtSEDireccionEmpresa);
        Ciudad = (TextView) findViewById(R.id.txtSECiudad);
        spTipoSugerencia = (Spinner) findViewById(R.id.spSETipoSugerencia);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipo_sugerencia, R.layout.spinner_item_registro);
        adapter.setDropDownViewResource(R.layout.dropdown_item_layoutpropio);
        spTipoSugerencia.setAdapter(adapter);
        btnSugerirEmpresa = (Button) findViewById(R.id.btnSugerirEmpresa);
        btnSugerirEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sugerirEmpresa();
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

    private void sugerirEmpresa() {
        authorization = SessionPrefs.get(getApplicationContext()).getPrefUsuarioToken();
        // Store values at the time of the login attempt.
        String tipoSugerencia = spTipoSugerencia.getSelectedItem().toString();
        String nombre = Nombre.getText().toString();
        String apellido = Apellido.getText().toString();
        String telefono = Telefono.getText().toString();
        String email = Email.getText().toString();
        String nombreEmpresa = NombreEmpresa.getText().toString();
        String direccionEmpresa = DireccionEmpresa.getText().toString();
        String ciudadEmpresa = Ciudad.getText().toString();

            Call<ApiResponse> call = mVinosYBodegasApi.sugerirEmpresa(authorization,new EmpresaSugerida(tipoSugerencia,nombre,apellido,telefono,email,nombreEmpresa,direccionEmpresa,ciudadEmpresa));
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
                            if(mensaje.getEstado().equals("3"))error = "La empresa ya se encuentra registrada";
                        }
                        //showLoginError(error);
                        return;
                    }
                    //Log.d("sugerir", "RAW: " + response.raw().toString());
                    showErrorMessage(response.body().getMensaje());

                    // Ir a la pantalla principal
                    startActivity(new Intent(SugerirEmpresa.this, PrincipalActivity.class));
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
                Intent intent = new Intent(this,PrincipalActivity.class);
                //intent.putExtra("idCiudad",ciudadIntent);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
