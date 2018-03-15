package com.jadevelopment.deliveryboss1.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.CircleTransform;
import com.jadevelopment.deliveryboss1.data.api.DeliverybossApi;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponse;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseEmpresas;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseUsuario;
import com.jadevelopment.deliveryboss1.data.api.model.EmpresasBody;
import com.jadevelopment.deliveryboss1.data.api.model.Usuario;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MiPerfilActivity extends AppCompatActivity {
    ImageView perfilFoto;
    String urlFoto;
    String sexo;
    EditText perfilNombre;
    EditText perfilApellido;
    EditText perfilTelefono;
    EditText perfilEmail;
    EditText perfilFechaNacimiento;
    Button cvCerrarSesion;

    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    ApiResponseUsuario serverUsuario;

    Calendar myCalendar = Calendar.getInstance();

    boolean modificar=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMiPerfil);
        setSupportActionBar(toolbar);
        // Remover título de la action bar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        modificar=false;

        perfilFoto = (ImageView) findViewById(R.id.imgPerfilFoto);
        perfilNombre = (EditText) findViewById(R.id.txtPerfilNombre);
        perfilApellido = (EditText) findViewById(R.id.txtPerfilApellido);
        perfilTelefono = (EditText) findViewById(R.id.txtPerfilTelefono) ;
        perfilEmail = (EditText) findViewById(R.id.txtPerfilEmail);
        perfilFechaNacimiento = (EditText) findViewById(R.id.txtPerfilFechaNacimiento);
        cvCerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        perfilFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MiPerfilActivity.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        cvCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionPrefs.get(getApplicationContext()).logOut();
                checkUserSession();
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

        obtenerUsuario();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_miperfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(this,PrincipalActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_modificar_datos:
                if(!modificar) {
                    modificar=true;
                    item.setIcon(R.drawable.ic_action_okay);
                    perfilNombre.setEnabled(true);
                    perfilApellido.setEnabled(true);
                    perfilTelefono.setEnabled(true);
                    //perfilEmail.setEnabled(true);
                    perfilFechaNacimiento.setEnabled(true);

                }else{
                    item.setIcon(R.drawable.ic_action_edit_2);
                    perfilNombre.setEnabled(false);
                    perfilApellido.setEnabled(false);
                    perfilTelefono.setEnabled(false);
                    //perfilEmail.setEnabled(false);
                    perfilFechaNacimiento.setEnabled(false);
                    modificar=false;
                    modificarUsuario();
                }

        }

        return super.onOptionsItemSelected(item);
    }

    private void checkUserSession(){
        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }


    private void obtenerUsuario() {
        String authorization = SessionPrefs.get(this).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(this).getPrefUsuarioIdUsuario();

        // Realizar petición HTTP
        Call<ApiResponseUsuario> call = mDeliverybossApi.obtenerUsuarioPorId(authorization,idusuario);
        call.enqueue(new Callback<ApiResponseUsuario>() {
            @Override
            public void onResponse(Call<ApiResponseUsuario> call,
                                   Response<ApiResponseUsuario> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                    } else {
                    }
                    showErrorMessage(error);
                    return;
                }

                serverUsuario = response.body();

                if (serverUsuario!=null) {
                    // Mostrar usuario
                    mostrarUsuario(serverUsuario);


                } else {
                    // Mostrar empty state

                }


            }

            @Override
            public void onFailure(Call<ApiResponseUsuario> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("logindb", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Comprueba tu conexión a Internet");
            }
        });
    }

    private void modificarUsuario() {
        String authorization = SessionPrefs.get(this).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(this).getPrefUsuarioIdUsuario();

        String imagen = urlFoto;
        String nombre = perfilNombre.getText().toString();
        String apellido = perfilApellido.getText().toString();
        String telefono = perfilTelefono.getText().toString();
        String e_mail = perfilEmail.getText().toString();
        String sexo_idsexo =sexo;
        String fecha_nacimiento = perfilFechaNacimiento.getText().toString();

        final Usuario usuarioMod = new Usuario("","",imagen,nombre,apellido,telefono,e_mail,sexo_idsexo,fecha_nacimiento);

        Gson gson = new Gson();
        String jsonInString = gson.toJson(usuarioMod);

        Log.d("logindb","Modificando usuario>"+jsonInString);

        // Realizar petición HTTP
        Call<ApiResponse> call = mDeliverybossApi.modificarUsuario(authorization,usuarioMod,idusuario);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call,
                                   Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                    } else {

                    }


                }
                String error="";
                if(response.body().getMensaje()!=null) {
                    error = response.body().getMensaje();
                    showErrorMessage(error);

                    SessionPrefs.get(MiPerfilActivity.this).modificarUsuario(usuarioMod);
                }else{
                    showErrorMessage(error);
                    return;
                }



            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("logindb", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Comprueba tu conexión a Internet");
            }
        });
    }

    private void mostrarUsuario(ApiResponseUsuario usuarioServer) {
        ApiResponseUsuario user = usuarioServer;

        perfilNombre.setText(user.getNombre());
        perfilApellido.setText(user.getApellido());
        perfilTelefono.setText(user.getTelefono());
        perfilEmail.setText(user.getE_mail());
        perfilFechaNacimiento.setText(user.getFecha_nacimiento());

        urlFoto = user.getImagen();
        sexo = user.getSexo_idsexo();

        if(user.getImagen()!=null){
            if(!user.getImagen().isEmpty()) {
                Picasso.with(this).setLoggingEnabled(true);
                Picasso
                        .with(this)
                        .load(user.getImagen())
                        .fit()
                        .transform(new CircleTransform())
                        .into(perfilFoto);
            }}

    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void updateLabel() {
        Log.d("logindb","UPDATE LABEL");
        String myFormat = "yyyy-MM-dd";
        java.text.SimpleDateFormat sdf = null;

        sdf = new java.text.SimpleDateFormat(myFormat, Locale.US);

        Log.d("logindb","Fecha sin formato: "+myCalendar.getTime().toString());
        assert sdf != null;
        Log.d("logindb","Fecha con formato: "+ sdf.format(myCalendar.getTime()));
        perfilFechaNacimiento.setText(sdf.format(myCalendar.getTime()));
    }


}
