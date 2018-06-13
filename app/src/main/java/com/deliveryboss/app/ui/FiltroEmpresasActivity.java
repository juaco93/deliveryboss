package com.deliveryboss.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.deliveryboss.app.R;

import org.w3c.dom.Text;


public class FiltroEmpresasActivity extends AppCompatActivity {

    public static final int RESULT_CODE_OK = 1;
    public static final int RESULT_CODE_BACK = 2;

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    public static final String EXTRA_FILTRO_CERCANIA = "EXTRA_FILTRO_CERCANIA";
    public static final String EXTRA_FILTRO_CALIFICACION = "EXTRA_FILTRO_CALIFICACION";
    public static final String EXTRA_FILTRO_AZ = "EXTRA_FILTRO_AZ";
    public static final String EXTRA_FILTRO_ZA = "EXTRA_FILTRO_ZA";
    public static final String EXTRA_FILTRO_ABIERTO_HOY = "EXTRA_FILTRO_ABIERTO_HOY";
    public static final String EXTRA_FILTRO_DELIVERY_EN_MI_ZONA = "EXTRA_FILTRO_DELIVERY_EN_MI_ZONA";

    TextView filtroCercania;
    TextView filtroCalificacion;
    TextView filtroAZ;
    TextView filtroZA;
    TextView filtroAbiertoHoy;
    TextView filtroDeliveryEnMiZona;
    TextView btnFiltroAceptar;

    String[] filtros;
    Intent intentDeRegreso;

    View rootLayout;

    private int revealX;
    private int revealY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_empresas);

        intentDeRegreso = new Intent(this,PrincipalActivity.class);

        filtroCercania = findViewById(R.id.txtFiltroCercania);
        filtroCalificacion = findViewById(R.id.txtFiltroCalificacion);
        filtroAZ = findViewById(R.id.txtFiltroNombreAZ);
        filtroZA = findViewById(R.id.txtFiltroNombreZA);
        filtroAbiertoHoy = findViewById(R.id.txtFiltroAbiertoHoy);
        filtroDeliveryEnMiZona = findViewById(R.id.txtFiltroDeliveryEnMiZona);
        btnFiltroAceptar = findViewById(R.id.btnFiltroAceptar);
        btnFiltroAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAPantallaPrincipal();
            }
        });

        inicializarOrdenamiento(filtroCercania);
        inicializarOrdenamiento(filtroCalificacion);
        inicializarOrdenamiento(filtroAZ);
        inicializarOrdenamiento(filtroZA);
        inicializarFiltro(filtroAbiertoHoy);
        inicializarFiltro(filtroDeliveryEnMiZona);



        final Intent intent = getIntent();

        // Inicializacion segun datos de previo filtrado //
        if(intent.getBooleanExtra("Abierto hoy",false)) {
            marcarBotonPresionado(filtroAbiertoHoy);
        }else{
            blanquearBoton(filtroAbiertoHoy);
        }
        if(intent.getBooleanExtra("Delivery en mi zona",false)){
            marcarBotonPresionado(filtroDeliveryEnMiZona);
        }else{
            blanquearBoton(filtroDeliveryEnMiZona);
        }
        if(intent.getBooleanExtra("Nombre A-Z",false)){
            marcarBotonPresionado(filtroAZ);
        }else{
            blanquearBoton(filtroAZ);
        }
        if(intent.getBooleanExtra("Nombre Z-A",false)){
            marcarBotonPresionado(filtroZA);
        }else{
            blanquearBoton(filtroZA);
        }
        if(intent.getBooleanExtra("Calificación",false)){
            marcarBotonPresionado(filtroCalificacion);
        }else{
            blanquearBoton(filtroCalificacion);
        }
        if(intent.getBooleanExtra("Cercanía",false)){
            marcarBotonPresionado(filtroCercania);
        }else{
            blanquearBoton(filtroCercania);
        }


        rootLayout = findViewById(R.id.root_layout);

        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);


            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);

            //Log.d("juaco1993","Reveal X: "+revealX);




            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }

        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(400);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(400);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    setResult(RESULT_CODE_OK,intentDeRegreso);
                    finish();
                    //startActivity(intentDeRegreso);
                }
            });
            circularReveal.start();
        }
    }

    private void inicializarFiltro(final TextView texto){
        texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("juaco1993","Click en filtro>"+texto.getText().toString());
                if(texto.getBackground().getConstantState()==getResources().getDrawable(R.drawable.rounded_corner).getConstantState()){
                    marcarBotonPresionado(texto);
                }else{
                    blanquearBoton(texto);
                }
            }
        });
    }

    private void inicializarOrdenamiento(final TextView texto){
        texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("juaco1993","Click en ordenamiento>"+texto.getText().toString());
                if(texto.getBackground().getConstantState()==getResources().getDrawable(R.drawable.rounded_corner).getConstantState()){
                    blanquearSeleccionesRadio();

                    marcarBotonPresionado(texto);
                }
            }
        });
    }

    private void blanquearSeleccionesRadio(){
        blanquearBoton(filtroCercania);
        blanquearBoton(filtroCalificacion);
        blanquearBoton(filtroAZ);
        blanquearBoton(filtroZA);
    }

    private void marcarBotonPresionado(TextView texto){
        texto.setTextColor(getResources().getColor(R.color.colorComida));
        texto.setBackground(getResources().getDrawable(R.drawable.rounded_corner_white));
        intentDeRegreso.putExtra(texto.getText().toString(),true);
    }

    private void blanquearBoton(TextView texto){
        texto.setTextColor(getResources().getColor(R.color.cardview_light_background));
        texto.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
        intentDeRegreso.putExtra(texto.getText().toString(),false);
    }

    private void irAPantallaPrincipal(){
        unRevealActivity();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CODE_BACK,intentDeRegreso);
        super.onBackPressed();
    }
}
