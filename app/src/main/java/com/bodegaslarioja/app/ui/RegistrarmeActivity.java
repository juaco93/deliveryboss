package com.bodegaslarioja.app.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bodegaslarioja.app.R;
import com.bodegaslarioja.app.data.api.VinosYBodegasApi;
import com.bodegaslarioja.app.data.api.model.ApiResponse;
import com.bodegaslarioja.app.data.api.model.FbLoginBody;
import com.bodegaslarioja.app.data.api.model.FbRegisterBody;
import com.bodegaslarioja.app.data.api.model.LoginBody;
import com.bodegaslarioja.app.data.api.model.MiDeserializador;
import com.bodegaslarioja.app.data.api.model.Usuario;
import com.bodegaslarioja.app.data.api.model.UsuarioRegisterBody;
import com.bodegaslarioja.app.data.api.model.regIdBody;
import com.bodegaslarioja.app.data.app.Config;
import com.bodegaslarioja.app.data.prefs.SessionPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarmeActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout mFloatLabelNombre;
    private TextInputLayout mFloatLabelApellido;
    private TextInputLayout mFloatLabelTelefono;
    private TextInputLayout mFloatLabelEmail;
    private TextInputLayout mFloatLabelContrasena;
    private TextInputLayout mFloatLabelFechaNacimiento;
    private TextInputLayout mFloatLabelSexo;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    LoginButton loginButton;
    CallbackManager callbackManager;

    private EditText txtNombre;
    private EditText txtApellido;
    private EditText txtTelefono;
    private EditText txtEmail;
    private EditText txtContrasena;
    private EditText txtFechaNacimiento;
    private Spinner spSexo;
    Calendar myCalendar = Calendar.getInstance();
    String codSexo;


    private Retrofit mRestAdapter;
    private VinosYBodegasApi mVinosYBodegasApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_registrarme);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRegistrarme);
        setSupportActionBar(toolbar);
        // Remover título de la action bar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registrarme");

        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Usuario.class, new MiDeserializador<Usuario>())
                        .create();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(VinosYBodegasApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // Crear conexión a la API de Deliveryboss
        mVinosYBodegasApi = mRestAdapter.create(VinosYBodegasApi.class);

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtApellido= (EditText) findViewById(R.id.txtApellido);
        txtTelefono = (EditText) findViewById(R.id.txtTelefono);
        txtEmail= (EditText) findViewById(R.id.txtEmail2);
        txtContrasena= (EditText) findViewById(R.id.txtContrasena);
        txtFechaNacimiento= (EditText) findViewById(R.id.txtFechaNacimiento);
        spSexo= (Spinner) findViewById(R.id.spSexo);

        //FloatLabels
        mFloatLabelNombre = (TextInputLayout) findViewById(R.id.lbNombre);
        mFloatLabelApellido = (TextInputLayout) findViewById(R.id.lbApellido);
        mFloatLabelTelefono = (TextInputLayout) findViewById(R.id.lbTelefono);
        mFloatLabelEmail = (TextInputLayout) findViewById(R.id.lbEmail);
        mFloatLabelContrasena = (TextInputLayout) findViewById(R.id.lbContrasena);
        mFloatLabelFechaNacimiento = (TextInputLayout) findViewById(R.id.lbFechaNacimiento);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sexo, R.layout.spinner_item_registro);
        adapter.setDropDownViewResource(R.layout.dropdown_item_layoutpropio);
        spSexo.setAdapter(adapter);
        codSexo="";
        spSexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("logindb","posicion sexo: " + String.valueOf(position));
                codSexo = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                codSexo = null;
            }
        });

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

        txtFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegistrarmeActivity.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtContrasena.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (!isOnline()) {
                        showLoginError(getString(R.string.error_network));
                        return false;
                    }
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()) {
                    showLoginError(getString(R.string.error_network));
                    return;
                }
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        ///////////////////   LOGIN FACEBOOK    ///////////////////////
        loginButton = (LoginButton) findViewById(R.id.connectWithFbButton2);
        loginButton.setReadPermissions("public_profile", "email", "user_birthday", "user_location", "user_gender");
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest   =   GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        try
                        {
                            //Log.d("fblogin", ""+response.getJSONObject().toString());
                            //JSONObject location = object.getJSONObject("location");
                            JSONObject picture = object.getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            String id          =   object.getString("id");
                            String email       =   object.getString("email");
                            String first_name  =   object.optString("first_name");
                            String last_name   =   object.optString("last_name");
                            //String locationName   =   location.getString("name");
                            /*String genero = object.optString("gender");
                            String generoCod = "";
                            if(genero.equals("male")){
                                generoCod = "1";
                            }else if(genero.equals("female")){
                                generoCod = "2";
                            }*/


                            String urlFotoPerfil = "https://graph.facebook.com/" + id + "/picture?type=large";
                            //String FechaNac = object.getString("birthday");

                            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            //String date = format.format(Date.parse(FechaNac));
                            //Log.d("logindb","Fecha Formateada: "+date);
                            registerFacebookLogin(id, email, first_name, last_name, urlFotoPerfil,"","", "3");


                            /*tvEmail.setText(email);
                            tvfirst_name.setText(first_name);
                            tvlast_name.setText(last_name);
                            tvfull_name.setText(name);
                            tvlocation.setText(locationName);*/

                            Log.d("logindb", "URL: "+urlFotoPerfil);
                            //fotoPerfil.setImageDrawable(LoadImageFromWebOperations(urlFotoPerfil));
                            //Picasso.with(LoginActivity.this).load(urlFotoPerfil).into(fotoPerfil);

                            //showPrincipalScreen();
                            //finish();

                            LoginManager.getInstance().logOut();
                        }
                        catch (JSONException e)
                        {
                            Log.d("logindb",e.toString());
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email, picture");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }


    private void attemptRegister() {
        final Boolean[] yaExiste = {false};
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        txtNombre.setError(null);
        txtApellido.setError(null);
        txtTelefono.setError(null);
        txtEmail.setError(null);
        txtContrasena.setError(null);
        txtFechaNacimiento.setError(null);

        mFloatLabelNombre.setError(null);
        mFloatLabelApellido.setError(null);
        mFloatLabelTelefono.setError(null);
        mFloatLabelEmail.setError(null);
        mFloatLabelContrasena.setError(null);
        mFloatLabelFechaNacimiento.setError(null);

        // Store values at the time of the login attempt.
        String nombre = txtNombre.getText().toString();
        String apellido = txtApellido.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String email = txtEmail.getText().toString();
        String contrasenia = txtContrasena.getText().toString();
        String imagen = "";
        String FechaNac = txtFechaNacimiento.getText().toString();
        String usuario_tipo_idusuario_tipo = "1";
        String sexo_idsexo = codSexo;
        String usuario_estado_idusuario_estado = "1";

        final boolean[] cancel = {false};
        View focusView = null;

        // Nombre
        if (TextUtils.isEmpty(nombre)) {
            //Log.d("registro","Esta vacio nombre: "+ nombre+" <-");
            txtNombre.setError(getString(R.string.error_field_required));
            mFloatLabelNombre.setError(getString(R.string.error_field_required));
            focusView = txtNombre;
            cancel[0] = true;

        }
        // Apellido
        if (TextUtils.isEmpty(apellido)) {
            txtApellido.setError(getString(R.string.error_field_required));
            mFloatLabelApellido.setError(getString(R.string.error_field_required));
            focusView = txtApellido;
            cancel[0] = true;
        }

        // Telefono
        if (TextUtils.isEmpty(telefono)) {
            txtTelefono.setError(getString(R.string.error_field_required));
            mFloatLabelTelefono.setError(getString(R.string.error_field_required));
            focusView = txtTelefono;
            cancel[0] = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.error_field_required));
            mFloatLabelEmail.setError(getString(R.string.error_field_required));
            focusView = txtEmail;
            cancel[0] = true;
        } else if (!isEmailValid(email)) {
            txtEmail.setError(getString(R.string.error_invalid_email));
            mFloatLabelEmail.setError(getString(R.string.error_invalid_email));
            focusView = txtEmail;
            cancel[0] = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(contrasenia)) {
            txtContrasena.setError(getString(R.string.error_invalid_password));
            mFloatLabelContrasena.setError(getString(R.string.error_field_required));
            focusView = txtContrasena;
            cancel[0] = true;
        }else if (!isPasswordValid(contrasenia)) {
            txtContrasena.setError(getString(R.string.error_invalid_password));
            mFloatLabelContrasena.setError("La contraseña debe contener al menos 6 caracteres");
            focusView = txtContrasena;
            cancel[0] = true;
        }


        // Fecha Nacimiento
        if (TextUtils.isEmpty(FechaNac)) {
            txtFechaNacimiento.setError(getString(R.string.error_field_required));
            mFloatLabelFechaNacimiento.setError(getString(R.string.error_field_required));
            focusView = txtFechaNacimiento;
            cancel[0] = true;
        }
        // Sexo
        if (codSexo==null ) {
            mFloatLabelSexo.setError(getString(R.string.error_field_required));
            focusView = spSexo;
            cancel[0] = true;
        }
        if (spSexo.getSelectedItem().toString().equals("Sexo") ) {
            focusView = spSexo;
            showLoginError("Por favor, completá todos los campos");
            cancel[0] = true;
        }

        if (cancel[0]) {
            focusView.requestFocus();
        } else {
            final String[] error = {""};
            UsuarioRegisterBody usuarioARegistrar = new UsuarioRegisterBody(nombre,apellido,email,telefono,contrasenia,FechaNac,usuario_tipo_idusuario_tipo,sexo_idsexo,usuario_estado_idusuario_estado);
            Log.d("register",new Gson().toJson(usuarioARegistrar));
            Call<ApiResponse> registerCall = mVinosYBodegasApi.registroNormal(usuarioARegistrar);
            registerCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    // Ocultar progreso
                    // Procesar errores
                    if (!response.isSuccessful()) {
                        if (response.errorBody()
                                .contentType()
                                .subtype()
                                .equals("application/json")) {
                            //Log.d("register", "se recibio respuesta json (con error): " + response.errorBody().toString());

                        } else {
                            Gson gson = new Gson();
                            ApiResponse mensaje = null;

                            try {
                                mensaje = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(mensaje.getEstado().equals("3")) {
                                error[0] = "Su email ya se encuentra registrado";
                                yaExiste[0] =true;
                            }
                            Log.d("register", "Mensaje: " + mensaje.getMensaje() + " Estado>"+mensaje.getEstado());
                        }

                        showLoginError(error[0]);
                        return;
                    }
                    //Log.d("logindb", "RAW: " + response.raw().toString());
                    showLoginError(response.body().getMensaje());

                    // Guardar usuario en preferencias
                    //SessionPrefs.get(RegistrarmeActivity.this).saveUsuario(response.body());

                    //Intentamos loguearnos (solo si no existe el usuario)
                    if(!yaExiste[0]){
                        attemptLogin();
                    }else{
                        showLoginError(error[0]);
                    }


                    // Ir a la pantalla principal
                    //showLoginScreen();
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    showLoginError(t.getMessage());
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            if (!mEmail.equals("")) {
                return 2;
            }

            if (!mPassword.equals("")) {
                return 3;
            }

            return 1;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            mAuthTask = null;

            switch (success) {
                case 1:
                    showPrincipalScreen();
                    break;
                case 2:
                case 3:
                    showLoginError("Número de identificación o contraseña inválidos");
                    break;
                case 4:
                    showLoginError(getString(R.string.error_server));
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void showLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void showPrincipalScreen() {
        startActivity(new Intent(this, SeleccionarDireccion.class));
        finish();
    }

    private void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    //////////////////   FACEBOOK    ///////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void registerFacebookLogin(final String idfacebook, final String e_mail, String nombre, String apellido, String foto, String ciudad,String FechaNac, String genero) {
        Call<Usuario> registerFbCall = mVinosYBodegasApi.registroFb(new FbRegisterBody(idfacebook, e_mail, nombre, apellido, foto,ciudad,FechaNac, "1", genero, "1"));
        registerFbCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                // Ocultar progreso
                String error="";
                // Procesar errores
                if (!response.isSuccessful()) {

                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("application/json")) {
                        //Log.d("logindb", "se recibio respuesta json (con error): " + response.errorBody().toString());
                    } else {
                        //POR ARQUITECTURA DEL SERVER NO PUEDE HABER EMAIL REPETIDO, ENTONCES INTENTAMOS LOGUEAR SI YA ESTA REGISTRADO
                        //Log.d("logindb", "hubo un error: " + response.message() + " Al parecer, su email ya esta registrado");
                        FacebookLogin(e_mail, idfacebook);
                    }
                    return;
                }
                Log.d("logindb", "RAW: " + response.raw().toString());
                Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                // Guardar usuario en preferencias
                //SessionPrefs.get(RegistrarmeActivity.this).saveUsuario(response.body());

                // Ir a la pantalla principal
                FacebookLogin(e_mail,idfacebook);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                showLoginError(t.getMessage());
            }
        });
    }

    private void FacebookLogin(String e_mail, String idfacebook) {
        Call<Usuario> registerFbCall = mVinosYBodegasApi.loginFb(new FbLoginBody(e_mail, idfacebook));
        registerFbCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                // Ocultar progreso
                // Procesar errores
                if (!response.isSuccessful()) {
                    String error;
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("application/json")) {
                        //Log.d("logindb", "se recibio respuesta json (con error): " + response.errorBody().toString());
                    } else {
                        Log.d("logindb", "hubo un error: " + response.message());
                    }
                    return;
                }
                Log.d("logindb", "RAW: " + response.raw().toString());
                Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                // Guardar usuario en preferencias
                SessionPrefs.get(RegistrarmeActivity.this).saveUsuario(response.body());

                // Guardar el regId para enviar notificaciones en la BD
                displayFirebaseRegId();

                // Ir a la pantalla principal
                mostrarPantallaPerfilUsuario();

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                showLoginError(t.getMessage());
            }
        });
    }

    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String e_mail = txtEmail.getText().toString();
        String contrasena = txtContrasena.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            focusView.requestFocus();
        } else {
            //showProgress(true);
            Call<Usuario> loginCall = mVinosYBodegasApi.login(new LoginBody(e_mail, contrasena));
            loginCall.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    // Ocultar progreso
                    //showProgress(false);
                    // Procesar errores
                    if (!response.isSuccessful()) {
                        String error;
                        if (response.errorBody()
                                .contentType()
                                .subtype()
                                .equals("application/json")) {
                            //Log.d("logindb", "se recibio respuesta json (con error): " + response.errorBody().toString());
                        } else {
                            error = response.message();
                            //Log.d("logindb", "hubo un error: " + response.message());
                        }

                        //showLoginError(error);
                        return;
                    }
                    Log.d("logindb", "RAW: " + response.raw().toString());
                    Log.d("logindb", "Nombre: " + response.body().getNombre());
                    Log.d("logindb", "Apellido: " + response.body().getApellido());
                    Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                    // Guardar usuario en preferencias
                    SessionPrefs.get(RegistrarmeActivity.this).saveUsuario(response.body());

                    // Guardar el regId para enviar notificaciones en la BD
                    displayFirebaseRegId();

                    // Ir a la pantalla principal
                    showPrincipalScreen();
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    //showProgress(false);
                    showLoginError(t.getMessage());
                }
            });
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void updateLabel() {
        //Log.d("logindb","UPDATE LABEL");
        String myFormat = "yyyy-MM-dd";
        java.text.SimpleDateFormat sdf = null;

            sdf = new java.text.SimpleDateFormat(myFormat, Locale.US);

            //Log.d("logindb","Fecha sin formato: "+myCalendar.getTime().toString());
            assert sdf != null;
            //Log.d("logindb","Fecha con formato: "+ sdf.format(myCalendar.getTime()));
            txtFechaNacimiento.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {

        //Log.d("regId", "Registrar el regID");
        //// PARTE DE MENSAJERIA VIA FCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notification

                    //displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
        //Log.d("firebaseMensaje","Me registre al topic GLOBAL");

        //String idusuario = SessionPrefs.get(this).getPrefUsuarioCiudad();
        //String regId = SessionPrefs.get(LoginActivity.this).getPrefUsuarioRegId();
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, 0);
        String idusuario = pref1.getString(SessionPrefs.PREF_IDUSUARIO,null);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        //Log.d("regId", "Firebase reg id: " + regId);
        //Log.d("regId", "Firebase idusuario: " + idusuario);

        Call<ApiResponse> call = mVinosYBodegasApi.registrarRegId(new regIdBody(regId,idusuario));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Ocultar progreso
                // Procesar errores
                if (!response.isSuccessful()) {
                    String error;
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("application/json")) {
                        //Log.d("regId", "se recibio respuesta json (con error): " + response.errorBody().toString());
                    } else {
                        //POR ARQUITECTURA DEL SERVER NO PUEDE HABER EMAIL REPETIDO, ENTONCES INTENTAMOS LOGUEAR SI YA ESTA REGISTRADO
                        //Log.d("regId", "hubo un error, no pude registrar el RegId en la BD");
                        //FacebookLogin(e_mail, idfacebook);
                    }
                    return;
                }
                Log.d("regId", "Registre correctamente el RegId en la BD");
                //Log.d("regId", "RAW: " + response.raw().toString());
                //Log.d("regId", "Parseada: " + response.body().getMensaje());
                //Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                // Guardar usuario en preferencias
                //SessionPrefs.get(LoginActivity.this).saveUsuario(response.body());

                // Ir a la pantalla principal
                //FacebookLogin(e_mail,idfacebook);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showLoginError(t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void mostrarPantallaPerfilUsuario(){
        startActivity(new Intent(this, MiPerfilActivity.class));
        finish();
    }

}
