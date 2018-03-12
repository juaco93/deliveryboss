package com.jadevelopment.deliveryboss1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.CircleTransform;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;
import com.squareup.picasso.Picasso;

public class MiPerfilActivity extends AppCompatActivity {
    ImageView perfilFoto;
    EditText perfilNombre;
    EditText perfilApellido;
    EditText perfilEmail;
    Button cvCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Remover título de la action bar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        perfilFoto = (ImageView) findViewById(R.id.imgPerfilFoto);
        perfilNombre = (EditText) findViewById(R.id.txtPerfilNombre);
        perfilApellido = (EditText) findViewById(R.id.txtPerfilApellido);
        perfilEmail = (EditText) findViewById(R.id.txtPerfilEmail);
        cvCerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);

        String nombre = SessionPrefs.get(this).getPrefUsuarioNombre();
        String apellido = SessionPrefs.get(this).getPrefUsuarioApellido();
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
                        .into(perfilFoto);
            }}
        perfilNombre.setText(nombre);
        perfilApellido.setText(apellido);
        perfilEmail.setText(email);
        cvCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionPrefs.get(getApplicationContext()).logOut();
                checkUserSession();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(this,PrincipalActivity.class);
                startActivity(intent);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }


}
