package com.bodegaslarioja.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bodegaslarioja.app.data.api.model.BodegasBody;
import com.bodegaslarioja.app.data.api.model.DeliveryRequest;
import com.bodegaslarioja.app.data.api.model.Usuario_direccion;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bodegaslarioja.app.R;
import com.bodegaslarioja.app.data.api.VinosYBodegasApi;
import com.bodegaslarioja.app.data.api.model.Calificacion;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class InfoEmpresaFragment extends Fragment{
    private Retrofit mRestAdapter;
    private VinosYBodegasApi mVinosYBodegasApi;
    List<Calificacion> serverCalificaciones;
    Usuario_direccion direccionUsuario;
    DeliveryRequest calcularPrecioDelivery;
    private FloatingActionButton mSharedFab;
    Context context;

    TextView nombreBodega;
    TextView rubrosBodega;
    TextView historia;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    TextView ciudad;
    TextView direccion;
    TextView telefono1;
    TextView telefono2;



    MapView mMapView;
    private GoogleMap googleMap;

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

            context = getContext();

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


            ///////////// FUNCIONES ////////////////

            ////// CARD HISTORIA //////////
            Intent intentRecibido = getActivity().getIntent();
            empresa = (new Gson()).fromJson((intentRecibido.getStringExtra("empresaJson")), BodegasBody.class);
            nombreBodega = (TextView) v.findViewById(R.id.txtNombreEmpresa);
            rubrosBodega = (TextView) v.findViewById(R.id.lbRubroElegido);
            historia = (TextView) v.findViewById(R.id.lbHistoriaBodega);
            ciudad = (TextView) v.findViewById(R.id.txtCiudad);
            direccion = (TextView) v.findViewById(R.id.lbDireccion);
            telefono1 = (TextView) v.findViewById(R.id.lbTelefono1);
            telefono2 = (TextView) v.findViewById(R.id.lbTelefono2);

            nombreBodega.setText(empresa.getNombre());
            historia.setText(empresa.getHistoria());

            ciudad.setText(empresa.getCiudad());
            direccion.setText(empresa.getDireccion());

            ////// CARD IMAGENES ////////
            img1 = (ImageView) v.findViewById(R.id.imgBodega1);
            img2 = (ImageView) v.findViewById(R.id.imgBodega2);
            img3 = (ImageView) v.findViewById(R.id.imgBodega3);
            //Carga de los logos de las empresas con Picasso
            if(empresa.getImagen1()!=null && !empresa.getImagen1().isEmpty()){
                Picasso
                        .with(context)
                        .load(empresa.getImagen1())
                        .fit() // will explain later
                        .into(img1);
            }else{
                img1.setVisibility(View.GONE);
            }
            if(empresa.getImagen2()!=null && !empresa.getImagen2().isEmpty()){
                Picasso
                        .with(context)
                        .load(empresa.getImagen2())
                        .fit() // will explain later
                        .into(img2);
            }else{
                img2.setVisibility(View.GONE);
            }
            if(empresa.getImagen3()!=null && !empresa.getImagen3().isEmpty()){
                Picasso
                        .with(context)
                        .load(empresa.getImagen3())
                        .fit() // will explain later
                        .into(img3);
            }else{
                img3.setVisibility(View.GONE);
            }

            if(empresa.getTelefono1()!=null){
                telefono1.setText(empresa.getTelefono1());
                telefono1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                telefono1.setPaintFlags(telefono1.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                telefono1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        //Log.d("telefono",orden.getTelefono().toString());
                        callIntent.setData(Uri.parse("tel:"+empresa.getTelefono1()));
                        context.startActivity(callIntent);
                    }
                });
            }else{

            }

            if(empresa.getTelefono2()!=null){
                telefono2.setText(empresa.getTelefono2());
                telefono2.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                telefono2.setPaintFlags(telefono2.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                telefono2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        //Log.d("telefono",orden.getTelefono().toString());
                        callIntent.setData(Uri.parse("tel:"+empresa.getTelefono2()));
                        context.startActivity(callIntent);
                    }
                });
            }else{

            }


            String rubros = "";
            if(empresa.getRubro1()!=null){
                rubros = empresa.getRubro1();
            }
            if(empresa.getRubro2()!=null){
                rubros += ", " + empresa.getRubro2();
            }
            if(empresa.getRubro3()!=null){
                rubros += ", " + empresa.getRubro3();
            }
            rubrosBodega.setText(rubros);


            return v;
        }

    public void onResume(){
        super.onResume();
        //obtenerCalificacionUsuario();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedFab = null; // To avoid keeping/leaking the reference of the FAB
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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
