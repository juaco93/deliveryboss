package com.deliveryboss.app.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.deliveryboss.app.data.api.model.Orden_detalle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.deliveryboss.app.R;
import com.deliveryboss.app.data.api.DeliverybossApi;
import com.deliveryboss.app.data.api.model.ApiResponse;
import com.deliveryboss.app.data.api.model.Calificacion;
import com.deliveryboss.app.data.api.model.MessageEvent;
import com.deliveryboss.app.data.api.model.Orden;
import com.deliveryboss.app.data.prefs.SessionPrefs;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalificacionDialogFragment extends DialogFragment {

    Spinner spRecibisteTuOrden;
    SimpleRatingBar calificacion;
    SimpleRatingBar calificacion2;
    SimpleRatingBar calificacion3;
    EditText cuerpoCalificacion;
    TextView lbCalificacionNombreEmpresa;
    TextView lbCalificacionGeneral;
    Button btnEnviarCalificacion;
    Orden orden;
    Float notaGeneral = 0.0f;
    private boolean ordenRecibida;
    private boolean noSeleccionado;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private Context mContext;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_calificacion_dialog, null);

        mContext = getActivity();

        // Get your views by using view.findViewById() here and do your listeners.
        spRecibisteTuOrden = (Spinner) view.findViewById(R.id.tgCalificacionOrdenRecibida);
        calificacion = (SimpleRatingBar) view.findViewById(R.id.RatingCalificacion);
        calificacion2 = (SimpleRatingBar) view.findViewById(R.id.RatingCalificacion2);
        calificacion3 = (SimpleRatingBar) view.findViewById(R.id.RatingCalificacion3);
        lbCalificacionNombreEmpresa = (TextView) view.findViewById(R.id.lbCalificacionNombreEmpresa);
        lbCalificacionGeneral = (TextView) view.findViewById(R.id.lbCalificacionGeneral);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.si_no, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.dropdown_item_layoutpropio);
        spRecibisteTuOrden.setAdapter(adapter);
        spRecibisteTuOrden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("logindb","posicion ordenRecibida: " + String.valueOf(position));
                if(position==1){
                    noSeleccionado=false;
                    ordenRecibida=true;
                }else{
                    if(position==2) {
                        noSeleccionado=false;
                        ordenRecibida = false;
                    }
                    if(position==0){
                        noSeleccionado=true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ordenRecibida = false;
            }
        });

                calificacion.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                        calcularNotaGeneral();
                    }
                });
        calificacion2.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                calcularNotaGeneral();
            }
        });
        calificacion3.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                calcularNotaGeneral();
            }
        });

        cuerpoCalificacion = (EditText) view.findViewById(R.id.txtCuerpoCalificacion);
        btnEnviarCalificacion = (Button) view.findViewById(R.id.btnEnviarCalificacion);
        btnEnviarCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chequearCalificacion()) {
                    if(chequearRecibido()) {
                        modificarEstadoOrden();
                        enviarCalificacion();
                        //dismiss();
                        // Modificado a dismiss() a dissmissalowingstateloss() porque al venir de una notificacion tiraba excepcion luego de calificar
                        CalificacionDialogFragment.this.dismissAllowingStateLoss();
                    }else{
                        showErrorMessage("¡Indicá si es que recibiste tu orden!");
                    }
                }else{
                    showErrorMessage("¡No podés calificar en 0!");
                }
            }
        });

        if(getArguments().getString("orden")!=null){
            orden = (new Gson()).fromJson((getArguments().getString("orden")),Orden.class);
            lbCalificacionNombreEmpresa.setText(orden.getNombre_empresa());
        }

        // Set the dialog layout
        builder.setView(view);

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

        return builder.create();
    }



    private void enviarCalificacion(){
        Log.d("calificacion","Enviando Calificacion al server");

        String authorization = SessionPrefs.get(getActivity()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getActivity()).getPrefUsuarioIdUsuario();
        String idempresa = orden.getEmpresa_idempresa();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHora = sdf.format(new Date());
        String cal = String.valueOf(calificacion.getRating());
        String cal2 = String.valueOf(calificacion2.getRating());
        String cal3 = String.valueOf(calificacion3.getRating());
        String calGeneral = lbCalificacionGeneral.getText().toString();
        String cuerpoCal = cuerpoCalificacion.getText().toString();
        String calificacion_empresa_idcalificacion_empresa = "";
        String comentario_empresa = "";
        String orden_idorden = orden.getIdorden();

        // Realizar petición HTTP
        Call<ApiResponse> call = mDeliverybossApi.insertarCalificacion(authorization,idempresa,idusuario ,new Calificacion("",fechaHora,cal,cal2,cal3,calGeneral,cuerpoCal,calificacion_empresa_idcalificacion_empresa,comentario_empresa,orden_idorden,idusuario,idempresa,"",""));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call,
                                   Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    //String error = "Ha ocurrido un error. Contacte al administrador";
                    String error = "Ocurrió un error. Contactanos a info@deliveryboss.com.ar";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        try {
                            // Reportar causas de error no relacionado con la API
                            Log.d("calificacion", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //showLoadingIndicator(false);
                    //showErrorMessage(error);
                    return;
                }
                Log.d("calificacion", "Respuesta del SV:" + response.body().getMensaje());
                showErrorMessage(response.body().getMensaje());
                EventBus.getDefault().post(new MessageEvent("1","Dialogo calificar cerrado"));
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("logindb", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Comprueba tu conexión a Internet");
            }
        });
    }

    private void modificarEstadoOrden(){
        Log.d("calificacion","Modificando el estado de la Orden");

        String authorization = SessionPrefs.get(getActivity()).getPrefUsuarioToken();
        String idorden = orden.getIdorden();

        List<Orden_detalle> vacia = null;
        //vacia.add(new Orden_detalle("","","","","","","",""));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHora = sdf.format(new Date());
        String recibida="";
        if(ordenRecibida){
            recibida="1";
        }else{
            recibida="0";
        }

        Orden ordenMod = new Orden(idorden,"","","","","","","","","","","","","1","","","",vacia,recibida,fechaHora);

        Log.d("calificacion",(new Gson()).toJson(ordenMod));

        // Realizar petición HTTP
        Call<ApiResponse> call2 = mDeliverybossApi.modificarOrden(authorization,idorden,ordenMod);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call,
                                   Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    //String error = "Ha ocurrido un error. Contacte al administrador";
                    String error = "Ocurrió un error. Contactanos a info@deliveryboss.com.ar";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                        try {
                            Log.d("calificacion", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            // Reportar causas de error no relacionado con la API
                            Log.d("calificacion", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
                Log.d("calificacion", "Respuesta del SV:" + response.body().getMensaje());
              //  showErrorMessage(response.body().getMensaje());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("calificacion", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Comprueba tu conexión a Internet");
            }
        });
    }

    private boolean chequearCalificacion() {
        Boolean correcto = false;
        if (calificacion.getRating() >= 1 && calificacion2.getRating() >= 1 && calificacion3.getRating() >= 1) {
            correcto = true;
        }else correcto=false;
        return correcto;
    }

    private boolean chequearRecibido(){
        Boolean correcto = false;
        if (noSeleccionado){
            correcto = false;
        }else correcto=true;
        return correcto;
    }





    private void calcularNotaGeneral(){
        notaGeneral = (calificacion.getRating() + calificacion2.getRating() + calificacion3.getRating())/3;
        lbCalificacionGeneral.setText(String.valueOf(notaGeneral).substring(0,3));
    }


    private void showErrorMessage(String error) {
        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
    }


}
