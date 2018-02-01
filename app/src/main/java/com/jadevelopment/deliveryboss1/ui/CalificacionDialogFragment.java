package com.jadevelopment.deliveryboss1.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.jadevelopment.deliveryboss1.R;
import com.jadevelopment.deliveryboss1.data.api.DeliverybossApi;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponse;
import com.jadevelopment.deliveryboss1.data.api.model.ApiResponseEmpresas;
import com.jadevelopment.deliveryboss1.data.api.model.Calificacion;
import com.jadevelopment.deliveryboss1.data.api.model.MessageEvent;
import com.jadevelopment.deliveryboss1.data.api.model.Orden;
import com.jadevelopment.deliveryboss1.data.api.model.Usuario_direccion;
import com.jadevelopment.deliveryboss1.data.prefs.SessionPrefs;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalificacionDialogFragment extends DialogFragment {

    SimpleRatingBar calificacion;
    SimpleRatingBar calificacion2;
    SimpleRatingBar calificacion3;
    EditText cuerpoCalificacion;
    TextView lbCalificacionNombreEmpresa;
    TextView lbCalificacionGeneral;
    Button btnEnviarCalificacion;
    Orden orden;
    Float notaGeneral = 0.0f;
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
        calificacion = (SimpleRatingBar) view.findViewById(R.id.RatingCalificacion);
        calificacion2 = (SimpleRatingBar) view.findViewById(R.id.RatingCalificacion2);
        calificacion3 = (SimpleRatingBar) view.findViewById(R.id.RatingCalificacion3);
        lbCalificacionNombreEmpresa = (TextView) view.findViewById(R.id.lbCalificacionNombreEmpresa);
        lbCalificacionGeneral = (TextView) view.findViewById(R.id.lbCalificacionGeneral);

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
                if(calificacion.getRating()>=1 && calificacion2.getRating()>=1 && calificacion3.getRating()>=1) {
                    enviarCalificacion();
                    //dismiss();
                    // Modificado a dismiss() a dissmissalowingstateloss() porque al venir de una notificacion tiraba excepcion luego de calificar
                    CalificacionDialogFragment.this.dismissAllowingStateLoss();
                }else{
                    showErrorMessage("¡No podés calificar en 0!");
                }
            }
        });

        if(getArguments().getString("orden")!=null){
            orden = (new Gson()).fromJson((getArguments().getString("orden")),Orden.class);
            lbCalificacionNombreEmpresa.setText(orden.getNombre_fantasia());
        }

        // Set the dialog layout
        builder.setView(view);

        return builder.create();
    }



    private void enviarCalificacion(){
        Log.d("calificacion","Enviando Calificacion al server");


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


        String authorization = SessionPrefs.get(getActivity()).getPrefUsuarioToken();
        String idusuario = SessionPrefs.get(getActivity()).getPrefUsuarioIdUsuario();
        String idempresa = orden.getEmpresa_idempresa();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-Mdd HH:mm:ss");
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
                    String error = "Ha ocurrido un error. Contacte al administrador";
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

    private void calcularNotaGeneral(){
        notaGeneral = (calificacion.getRating() + calificacion2.getRating() + calificacion3.getRating())/3;
        lbCalificacionGeneral.setText(String.valueOf(notaGeneral).substring(0,3));
    }


    private void showErrorMessage(String error) {
        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
    }


}
