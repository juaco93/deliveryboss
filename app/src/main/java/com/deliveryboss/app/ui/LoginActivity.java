package com.deliveryboss.app.ui;

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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.deliveryboss.app.data.api.model.MessageEvent;
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
import com.deliveryboss.app.data.api.model.MiDeserializador;
import com.deliveryboss.app.data.api.model.Usuario;
import com.deliveryboss.app.data.api.model.regIdBody;
import com.deliveryboss.app.data.app.Config;
import com.deliveryboss.app.data.prefs.SessionPrefs;
import java.text.SimpleDateFormat;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

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
    String regId;

    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("logindb","Entre a la pantalla de login");

        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

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

        ///////////////////   LOGIN FACEBOOK    ///////////////////////
        loginButton = (LoginButton)findViewById(R.id.connectWithFbButton1);
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



    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
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
        FbRegisterBody cuerpoRegistro = new FbRegisterBody(idfacebook, e_mail, nombre, apellido, foto,ciudad,FechaNac, "1", genero, "1");
        Log.d("logindb",(new Gson()).toJson(cuerpoRegistro));
        Call<Usuario> registerFbCall = mDeliverybossApi.registroFb(cuerpoRegistro);
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
                        Log.d("logindb", "se recibio respuesta json (con error): " + response.errorBody().toString());
                    } else {
                        //POR ARQUITECTURA DEL SERVER NO PUEDE HABER EMAIL REPETIDO, ENTONCES INTENTAMOS LOGUEAR SI YA ESTA REGISTRADO
                        //Log.d("logindb", "hubo un error: " + response.message() + " Al parecer, su email ya esta registrado");
                        FacebookLogin(e_mail, idfacebook);
                    }
                    return;
                }
                Log.d("logindb", "RAW: " + response.raw().toString());


                // Guardar usuario en preferencias
                SessionPrefs.get(LoginActivity.this).saveUsuario(response.body());

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
        Log.d("logindb", "Logueando por facebook: " + e_mail);
        Call<Usuario> registerFbCall = mDeliverybossApi.loginFb(new FbLoginBody(e_mail, idfacebook));
        registerFbCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                // Procesar errores
                if (!response.isSuccessful()) {
                    String error;
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("application/json")) {
                        Log.d("logindb", "se recibio respuesta json (con error): " + response.errorBody().toString());
                    } else {
                        Log.d("logindb", "hubo un error: " + response.message());
                    }
                    return;
                }
                Log.d("logindb", "RAW: " + response.raw().toString());
                Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                // Guardar usuario en preferencias
                SessionPrefs.get(LoginActivity.this).saveUsuario(response.body());

                // Guardar RegId para notificaciones en la BD
                displayFirebaseRegId();

                // Ir a la pantalla principal
                //showPrincipalScreen();
                mostrarPantallaPerfilUsuario();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                showLoginError(t.getMessage());
            }
        });
    }

    public void mostrarPantallaSoyUsuario(View v){
        startActivity(new Intent(this, SoyUsuarioActivity.class));
        finish();
    }

    public void mostrarPantallRegistrame(View v){
        startActivity(new Intent(this, RegistrarmeActivity.class));
        finish();
    }

    public void mostrarPantallaPerfilUsuario(){
        startActivity(new Intent(this, MiPerfilActivity.class));
        finish();
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

