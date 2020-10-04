package com.deliveryboss.app.data.util;

import android.content.Context;
import android.util.Log;

import com.deliveryboss.app.data.api.model.BodegasBody;
import com.deliveryboss.app.data.api.model.Delivery;
import com.deliveryboss.app.data.api.model.DeliveryRequest;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.data.prefs.SessionPrefs;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Joaquin on 09/04/2018.
 */

public class Utilidades {

    public static boolean ChequearLocalAbiertoHoy(BodegasBody empresa){

        //if(!abierto)holder.horarios.setText("HOY CERRADO");
        return false;
    }


    public static DeliveryRequest calcularPrecioDelivery(Usuario_direccion destino, BodegasBody origen){
        LatLng local;
        LatLng usuario;
        Double distancia=0.0;
        float precio = 0.0f;
        boolean enZonaDeCobertura = false;

        return null;
    }

    public static void setearDireccionPorDefecto(Context context, String direccion){
        if(direccion!=null){
            Usuario_direccion direccionUsuario = (new Gson()).fromJson(direccion,Usuario_direccion.class);
            SessionPrefs.get(context).saveDireccion(direccionUsuario.getIdusuario_direccion(), direccionUsuario.getCiudad_idciudad(), direccionUsuario.getCiudad(), direccionUsuario.getCalle(), direccionUsuario.getNumero(), direccionUsuario.getLatitud(), direccionUsuario.getLongitud());
        }
    }

    public static String formatearDistancia(Double distancia){
        DecimalFormat df = new DecimalFormat("#.#");
        Double distanciaEnKm = distancia/1000;
        String stDistancia = df.format(distanciaEnKm) + " km";
        return  stDistancia;
    }
}
