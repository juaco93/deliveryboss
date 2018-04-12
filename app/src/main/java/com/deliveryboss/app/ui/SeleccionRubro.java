package com.deliveryboss.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.CircleTransform;
import com.deliveryboss.app.data.app.Config;
import com.deliveryboss.app.data.prefs.SessionPrefs;
import com.deliveryboss.app.data.util.NotificationUtils;
import com.squareup.picasso.Picasso;

public class SeleccionRubro extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private String drawerTitle;
    ImageView fotoPerfil;
    TextView userName;
    TextView userEmail;
    View rootView;

    CardView cvComida;
    CardView cvMercado;
    String idCiudad;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = getLayoutInflater().inflate(R.layout.activity_seleccion_rubro,
                null);
        setContentView(rootView);

        checkUserSession();
        setToolbar(); // Setear Toolbar como action bar

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        fotoPerfil = (ImageView) headerView.findViewById(R.id.circle_image);
        userName = (TextView) headerView.findViewById(R.id.userName);
        userEmail = (TextView) headerView.findViewById(R.id.userEmail);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
            // Añadir carácteristicas

            String nombre = SessionPrefs.get(this).getPrefUsuarioNombre();
            String email = SessionPrefs.get(this).getPrefUsuarioEmail();
            String imagen = SessionPrefs.get(this).getPrefUsuarioImagen();

            if(imagen!=null){
            if(!imagen.isEmpty()) {
                Picasso.with(this).setLoggingEnabled(true);
                Picasso
                        .with(this)
                        .load(imagen)
                        .fit()
                        .transform(new CircleTransform())
                        .into(fotoPerfil);
            }}
            userName.setText(nombre);
            userEmail.setText(email);

        }

        drawerTitle = getResources().getString(R.string.home_item);
        if (savedInstanceState == null) {
            // Seleccionar item
        }

        cvComida = (CardView) findViewById(R.id.cvComida);
        cvComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionRubro.this, PrincipalActivity.class);
                intent.putExtra("ciudad",idCiudad);
                intent.putExtra("rubro","1");
                startActivity(intent);
                finish();
            }
        });
        cvMercado = (CardView) findViewById(R.id.cvMercado);
        cvMercado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionRubro.this, PrincipalActivity.class);
                intent.putExtra("ciudad",idCiudad);
                intent.putExtra("rubro","3");
                startActivity(intent);
                finish();
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeleccionRubro.this, MiPerfilActivity.class));
            }
        });
        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeleccionRubro.this, MiPerfilActivity.class));
            }
        });


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
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                }
            }
        };
        //displayFirebaseRegId();

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRubro);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);

                        //Hacer lo que tenga que hacer
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_ordenes:
                                // launch new intent instead of loading fragment
                                startActivity(new Intent(SeleccionRubro.this, MisOrdenesActivity.class));
                                break;
                            case R.id.nav_direcciones:
                                startActivity(new Intent(SeleccionRubro.this, MisDireccionesActivity.class));
                                break;
                            case R.id.nav_cambiar_direccion:
                                startActivity(new Intent(SeleccionRubro.this, SeleccionarDireccion.class));
                                break;
                            case R.id.nav_sugerirempresa:
                                startActivity(new Intent(SeleccionRubro.this, SugerirEmpresa.class));
                                break;
                            case R.id.nav_compartir:
                                compartirApp();
                                break;
                            /*case R.id.nav_log_out:
                                SessionPrefs.get(getApplicationContext()).logOut();
                                checkUserSession();
                                break;*/

                        }
                        // Cerrar drawer
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    protected void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(rootView);
        rootView = null;
        System.gc();
    }

    public void compartirApp(){
        Intent sendText = new Intent();
        sendText.setAction(Intent.ACTION_SEND);
        sendText.putExtra(Intent.EXTRA_TEXT, "Hola descarga deliveryboss, es muy recomendable --> https://play.google.com/store/apps/details?id=com.deliveryboss.app");
        sendText.setType("text/plain");
        startActivity(sendText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
