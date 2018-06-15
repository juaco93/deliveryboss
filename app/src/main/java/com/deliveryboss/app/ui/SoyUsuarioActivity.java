package com.deliveryboss.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.DeliverybossApi;
import com.deliveryboss.app.data.api.model.ApiResponse;
import com.deliveryboss.app.data.api.model.FbLoginBody;
import com.deliveryboss.app.data.api.model.FbRegisterBody;
import com.deliveryboss.app.data.api.model.LoginBody;
import com.deliveryboss.app.data.api.model.MiDeserializador;
import com.deliveryboss.app.data.api.model.Usuario;
import com.deliveryboss.app.data.api.model.regIdBody;
import com.deliveryboss.app.data.app.Config;
import com.deliveryboss.app.data.prefs.SessionPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SoyUsuarioActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout mFloatLabelEmail;
    private TextInputLayout mFloatLabelPassword;
    LoginButton loginButton;
    CallbackManager callbackManager;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_soy_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSoyUsuario);
        setSupportActionBar(toolbar);
        // Remover título de la action bar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Iniciar sesión");

        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Usuario.class, new MiDeserializador<Usuario>())
                        .create();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(DeliverybossApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // Crear conexión a la API de Deliveryboss
        mDeliverybossApi = mRestAdapter.create(DeliverybossApi.class);

        mEmailView = (EditText) findViewById(R.id.txtEmail);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);
        mFloatLabelEmail = (TextInputLayout) findViewById(R.id.float_label_email);
        mFloatLabelPassword = (TextInputLayout) findViewById(R.id.float_label_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (!isOnline()) {
                        showLoginError(getString(R.string.error_network));
                        return false;
                    }
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()) {
                    showLoginError(getString(R.string.error_network));
                    return;
                }
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        ///////////////////   LOGIN FACEBOOK    ///////////////////////
        loginButton = (LoginButton) findViewById(R.id.connectWithFbButton);
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
                            //String genero = object.optString("gender");
                            /*String generoCod = "";
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


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFloatLabelEmail.setError(null);
        mFloatLabelPassword.setError(null);

        // Store values at the time of the login attempt.
        String e_mail = mEmailView.getText().toString();
        String contrasena = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(contrasena) && !isPasswordValid(contrasena)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mFloatLabelPassword.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(e_mail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mFloatLabelEmail.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(e_mail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            mFloatLabelEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //showProgress(true);
            Call<Usuario> loginCall = mDeliverybossApi.login(new LoginBody(e_mail, contrasena));
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

                        showLoginError("Usuario o contraseña incorrectos");
                        return;
                    }
                    //Log.d("logindb", "RAW: " + response.raw().toString());
                    //Log.d("logindb", "Nombre: " + response.body().getNombre());
                    //Log.d("logindb", "Apellido: " + response.body().getApellido());
                    //Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                    // Guardar usuario en preferencias
                    SessionPrefs.get(SoyUsuarioActivity.this).saveUsuario(response.body());

                    // Guardar el regId para enviar notificaciones en la BD
                    displayFirebaseRegId();

                    // Ir a la pantalla principal
                    //showPrincipalScreen();
                    mostrarPantallaPerfilUsuario();
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    //showProgress(false);
                    showLoginError(t.getMessage());
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
            //showProgress(false);

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
            //showProgress(false);
        }
    }

    private void showPrincipalScreen() {
        startActivity(new Intent(this, SeleccionarCiudad.class));
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
        Call<Usuario> registerFbCall = mDeliverybossApi.registroFb(new FbRegisterBody(idfacebook, e_mail, nombre, apellido, foto,ciudad,FechaNac, "1", genero, "1"));
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
                        //POR ARQUITECTURA DEL SERVER NO PUEDE HABER EMAIL REPETIDO, ENTONCES INTENTAMOS LOGUEAR SI YA ESTA REGISTRADO
                        //Log.d("logindb", "hubo un error: " + response.message() + " Al parecer, su email ya esta registrado");
                        FacebookLogin(e_mail, idfacebook);
                    }
                    return;
                }
                //Log.d("logindb", "RAW: " + response.raw().toString());
                //Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                // Guardar usuario en preferencias
                //SessionPrefs.get(SoyUsuarioActivity.this).saveUsuario(response.body());

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
        Call<Usuario> registerFbCall = mDeliverybossApi.loginFb(new FbLoginBody(e_mail, idfacebook));
        registerFbCall.enqueue(new Callback<Usuario>() {
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
                        //Log.d("logindb", "hubo un error: " + response.message());
                    }
                    return;
                }
                //Log.d("logindb", "RAW: " + response.raw().toString());
                //Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                // Guardar usuario en preferencias
                SessionPrefs.get(SoyUsuarioActivity.this).saveUsuario(response.body());

                // Guardar el regId para enviar notificaciones en la BD
                displayFirebaseRegId();

                // Ir a la pantalla principal
                //showPrincipalScreen();
                mostrarPantallaPerfilUsuario();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                //showProgress(false);
                showLoginError(t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
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
        //// PARTE DE MENSAJERIA VIA FCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    //displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

        //displayFirebaseRegId();

        //String idusuario = SessionPrefs.get(this).getPrefUsuarioCiudad();
        //String regId = SessionPrefs.get(LoginActivity.this).getPrefUsuarioRegId();
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, 0);
        String idusuario = pref1.getString(SessionPrefs.PREF_IDUSUARIO,null);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        //Log.d("regId", "Firebase reg id: " + regId);
        //Log.d("regId", "Firebase idusuario: " + idusuario);

        Call<ApiResponse> call = mDeliverybossApi.registrarRegId(new regIdBody(regId,idusuario));
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
                        Log.d("regId", "hubo un error, no pude registrar el RegId en la BD");
                        //FacebookLogin(e_mail, idfacebook);
                    }
                    return;
                }
                //Log.d("regId", "Registre correctamente el RegId en la BD");
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

    public void mostrarPantallaPerfilUsuario(){
        startActivity(new Intent(this, MiPerfilActivity.class));
        finish();
    }

}