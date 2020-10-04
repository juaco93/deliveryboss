package com.deliveryboss.app.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deliveryboss.app.data.api.model.BodegasBody;
import com.deliveryboss.app.data.api.model.Delivery;
import com.deliveryboss.app.data.api.model.DeliveryRequest;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.data.util.Utilidades;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.VinosYBodegasApi;
import com.deliveryboss.app.data.api.model.ApiResponseCalificaciones;
import com.deliveryboss.app.data.api.model.Calificacion;
import com.deliveryboss.app.data.prefs.SessionPrefs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class InfoEmpresaFragment extends Fragment{
    private Retrofit mRestAdapter;
    private VinosYBodegasApi mVinosYBodegasApi;
    List<Calificacion> serverCalificaciones;
    Usuario_direccion direccionUsuario;
    DeliveryRequest calcularPrecioDelivery;
    private FloatingActionButton mSharedFab;

    TextView lunes;
    TextView martes;
    TextView miercoles;
    TextView jueves;
    TextView viernes;
    TextView sabado;
    TextView domingo;
    TextView feriados;
    TextView precioDelivery;
    TextView tiempoDelivery;
    TextView lbCalificacion;
    TextView lbCalificacionFloat;
    TextView lbRubroElegido;
    TextView lbCalificacionUsuario;
    TextView lbCantidadCalificacion;
    TextView compraMinima;
    TextView lbCalificacion1;
    TextView lbCalificacion2;
    TextView lbCalificacion3;


    SimpleRatingBar detalleCalificacion;
    SimpleRatingBar calificacionUsuario;
    String cuerpoAnteriorCalificacion;
    String anteriorCalificacion;

    MapView mMapView;
    private GoogleMap googleMap;


    TextView lblunes;
    TextView lbmartes;
    TextView lbmiercoles;
    TextView lbjueves;
    TextView lbviernes;
    TextView lbsabado;
    TextView lbdomingo;
    BodegasBody empresa;


            public InfoEmpresaFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_info_empresa, container, false);

            // Inicializar GSON
            Gson gson =
                    new GsonBuilder()
                            .create();

            // Crear conexión al servicio REST
            mRestAdapter = new Retrofit.Builder()
                    .baseUrl(VinosYBodegasApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            // Crear conexión a la API de Deliveryboss
            mVinosYBodegasApi = mRestAdapter.create(VinosYBodegasApi.class);

            lunes = (TextView) v.findViewById(R.id.txtLunes);
            martes = (TextView) v.findViewById(R.id.txtMartes);
            miercoles = (TextView) v.findViewById(R.id.txtMiercoles);
            jueves = (TextView) v.findViewById(R.id.txtJueves);
            viernes = (TextView) v.findViewById(R.id.txtViernes);
            sabado = (TextView) v.findViewById(R.id.txtSabado);
            domingo = (TextView) v.findViewById(R.id.txtDomingo);
            feriados = v.findViewById(R.id.txtFeriados);

            lblunes = (TextView) v.findViewById(R.id.lbLunes);
            lbmartes = (TextView) v.findViewById(R.id.lbMartes);
            lbmiercoles = (TextView) v.findViewById(R.id.lbMiercoles);
            lbjueves = (TextView) v.findViewById(R.id.lbJueves);
            lbviernes = (TextView) v.findViewById(R.id.lbViernes);
            lbsabado = (TextView) v.findViewById(R.id.lbSabado);
            lbdomingo = (TextView) v.findViewById(R.id.lbDomingo);

            precioDelivery = (TextView) v.findViewById(R.id.txtDeliveryORetiro);
            tiempoDelivery = (TextView) v.findViewById(R.id.txtDemora);
            compraMinima = (TextView) v.findViewById(R.id.txtInfoCompraMinima);

            lbCalificacion = (TextView) v.findViewById(R.id.txtNombreEmpresa);
            lbCalificacionFloat = (TextView) v.findViewById(R.id.lbCalificacionFloat);
            lbCalificacion1 = (TextView) v.findViewById(R.id.lbCalificacion1);
            lbCalificacion2 = (TextView) v.findViewById(R.id.lbCalificacion2);
            lbCalificacion3 = (TextView) v.findViewById(R.id.lbCalificacion3);

            lbRubroElegido = (TextView) v.findViewById(R.id.lbRubroElegido);
            //lbCalificacionUsuario = (TextView) v.findViewById(R.id.lbCalificacionUsuario);
            //detalleCalificacion = (SimpleRatingBar) v.findViewById(R.id.DetalleCalificacion);
            //calificacionUsuario = (SimpleRatingBar) v.findViewById(R.id.rbCalificacionUsuario);
            lbCantidadCalificacion =(TextView) v.findViewById(R.id.lbCantidadCalificacion);


            ///////////// FUNCIONES ////////////////

            ////// CARD HORARIOS //////////
            limpiarHorarios();
            Intent intentRecibido = getActivity().getIntent();
            empresa = (new Gson()).fromJson((intentRecibido.getStringExtra("empresaJson")), BodegasBody.class);
            String idUsuario = SessionPrefs.get(getContext()).getPrefUsuarioIdUsuario();
            String usuarioIdciudad = SessionPrefs.get(getContext()).getPrefUsuarioDireccionIdCiudad();
            String usuarioIddireccion = SessionPrefs.get(getContext()).getPrefUsuarioIdDireccion();
            String usuarioCiudad = SessionPrefs.get(getContext()).getPrefUsuarioCiudad();
            String usuarioCalle = SessionPrefs.get(getContext()).getPrefUsuarioDireccionCalle();
            String usuarioNumero = SessionPrefs.get(getContext()).getPrefUsuarioDireccionNumero();
            String usuarioLatitud = SessionPrefs.get(getContext()).getPrefUsuarioDireccionLatitud();
            String usuarioLongitud = SessionPrefs.get(getContext()).getPrefUsuarioDireccionLongitud();
            direccionUsuario = new Usuario_direccion(usuarioIddireccion,idUsuario,usuarioIdciudad,usuarioCiudad,usuarioCalle,usuarioNumero,"","","",usuarioLatitud,usuarioLongitud);

            calcularPrecioDelivery = Utilidades.calcularPrecioDelivery(direccionUsuario,empresa);
            mostrarHorarios();



            //////// CARD UBICACION ///////////
            mMapView = (MapView) v.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume(); // needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }


            //obtenerCalificacionUsuario();

            return v;
        }

        private void mostrarHorarios(){
            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            String diaActual = sdf.format(new Date());

            //Log.d("dia", diaActual);

            Boolean abierto= false;
            String turno1desde = "";
            String turno2desde = "";
            String turno1hasta = "";
            String turno2hasta = "";




            switch (diaActual){
                case "lun.":
                    lunes.setTypeface(null, Typeface.BOLD);
                    lblunes.setTypeface(null, Typeface.BOLD);
                    break;
                case "mar.":
                    martes.setTypeface(null, Typeface.BOLD);
                    lbmartes.setTypeface(null, Typeface.BOLD);
                    break;
                case "mié.":
                    miercoles.setTypeface(null, Typeface.BOLD);
                    lbmiercoles.setTypeface(null, Typeface.BOLD);
                    break;
                case "jue.":
                    jueves.setTypeface(null, Typeface.BOLD);
                    lbjueves.setTypeface(null, Typeface.BOLD);
                    break;
                case "vie.":
                    viernes.setTypeface(null, Typeface.BOLD);
                    lbviernes.setTypeface(null, Typeface.BOLD);
                    break;
                case "sáb.":
                    sabado.setTypeface(null, Typeface.BOLD);
                    lbsabado.setTypeface(null, Typeface.BOLD);
                    break;
                case "dom.":
                    domingo.setTypeface(null, Typeface.BOLD);
                    lbdomingo.setTypeface(null, Typeface.BOLD);
                    break;
            }
        }


/*
        private void mostrarHorarios(){
            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            String diaActual = sdf.format(new Date());
            String turno1 = "";
            String turno2 = "";
            if(empresa.getHorario()!=null) {
                String[] dias = empresa.getHorario().split(",");
                for (String dia : dias) {
                    if (dia != null) {
                        String[] secciones = dia.split("-");

                        String iddia = secciones[0];
                        if(secciones[1]!=null) turno1 = secciones[1];
                        if(secciones[2]!=null) turno2 = secciones[2];

                        switch (iddia){
                            case "lun.":
                                lunes.setText(turno1 + " y "+ turno2);
                                if(turno1.equals("0:00 a 0:00")) lunes.setText("ABIERTO LAS 24HS");
                                break;
                            case "mar.":
                                martes.setText(turno1 + " y "+ turno2);
                                if(turno1.equals("0:00 a 0:00")) martes.setText("ABIERTO LAS 24HS");
                                break;
                            case "mié.":
                                miercoles.setText(turno1 + " y "+ turno2);
                                if(turno1.equals("0:00 a 0:00")) miercoles.setText("ABIERTO LAS 24HS");
                                break;
                            case "jue.":
                                jueves.setText(turno1 + " y "+ turno2);
                                if(turno1.equals("0:00 a 0:00")) jueves.setText("ABIERTO LAS 24HS");
                                break;
                            case "vie.":
                                viernes.setText(turno1 + " y "+ turno2);
                                if(turno1.equals("0:00 a 0:00")) viernes.setText("ABIERTO LAS 24HS");
                                break;
                            case "sáb.":
                                sabado.setText(turno1 + " y "+ turno2);
                                if(turno1.equals("0:00 a 0:00")) sabado.setText("ABIERTO LAS 24HS");
                                break;
                            case "dom.":
                                domingo.setText(turno1 + " y "+ turno2);
                                if(turno1.equals("0:00 a 0:00")) domingo.setText("ABIERTO LAS 24HS");
                                break;
                        }
                        switch (diaActual){
                            case "lun.":
                                lunes.setTypeface(null, Typeface.BOLD);
                                lblunes.setTypeface(null, Typeface.BOLD);
                                break;
                            case "mar.":
                                martes.setTypeface(null, Typeface.BOLD);
                                lbmartes.setTypeface(null, Typeface.BOLD);
                                break;
                            case "mié.":
                                miercoles.setTypeface(null, Typeface.BOLD);
                                lbmiercoles.setTypeface(null, Typeface.BOLD);
                                break;
                            case "jue.":
                                jueves.setTypeface(null, Typeface.BOLD);
                                lbjueves.setTypeface(null, Typeface.BOLD);
                                break;
                            case "vie.":
                                viernes.setTypeface(null, Typeface.BOLD);
                                lbviernes.setTypeface(null, Typeface.BOLD);
                                break;
                            case "sáb.":
                                sabado.setTypeface(null, Typeface.BOLD);
                                lbsabado.setTypeface(null, Typeface.BOLD);
                                break;
                            case "dom.":
                                domingo.setTypeface(null, Typeface.BOLD);
                                lbdomingo.setTypeface(null, Typeface.BOLD);
                                break;
                        }

                        Log.d("dia1", "dia: "+ iddia +"turno1: " + turno1 + "turno2" + turno2);

                        /*if(diaActual.equals(iddia)){
                            if(!turno1.equals("") && !turno2.equals("")) holder.horarios.setText("HOY "+turno1 +" Y "+turno2);
                            if(turno2.equals("0:01 a 0:01")) holder.horarios.setText("HOY "+turno1);
                            if(turno1.equals("0:00 a 0:00")) holder.horarios.setText("ABIERTO LAS 24HS");
                            //abierto=true;
                        }
                    }
                }
            }
        }*/

    private void limpiarHorarios(){
        lunes.setTypeface(null, Typeface.NORMAL);
        lblunes.setTypeface(null, Typeface.NORMAL);
        martes.setTypeface(null, Typeface.NORMAL);
        lbmartes.setTypeface(null, Typeface.NORMAL);
        miercoles.setTypeface(null, Typeface.NORMAL);
        lbmiercoles.setTypeface(null, Typeface.NORMAL);
        jueves.setTypeface(null, Typeface.NORMAL);
        lbjueves.setTypeface(null, Typeface.NORMAL);
        viernes.setTypeface(null, Typeface.NORMAL);
        lbviernes.setTypeface(null, Typeface.NORMAL);
        sabado.setTypeface(null, Typeface.NORMAL);
        lbsabado.setTypeface(null, Typeface.NORMAL);
        domingo.setTypeface(null, Typeface.NORMAL);
        lbdomingo.setTypeface(null, Typeface.NORMAL);
    }

    public void obtenerCalificacionUsuario(){
    }

    public void onResume(){
        super.onResume();
        //obtenerCalificacionUsuario();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mSharedFab = null; // To avoid keeping/leaking the reference of the FAB
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    public void shareFab(FloatingActionButton fab) {
        if (fab == null) { // When the FAB is shared to another Fragment
            if (mSharedFab != null) {
                mSharedFab.setOnClickListener(null);
            }
            mSharedFab = null;
        }
        else {
            mSharedFab = fab;
            mSharedFab.setImageResource(R.drawable.cajita_sola);
        }
    }

}
