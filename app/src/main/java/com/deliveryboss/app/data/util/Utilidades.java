package com.deliveryboss.app.data.util;

import android.content.Context;
import android.util.Log;

import com.deliveryboss.app.data.api.model.Delivery;
import com.deliveryboss.app.data.api.model.DeliveryRequest;
import com.deliveryboss.app.data.api.model.EmpresasBody;
import com.deliveryboss.app.data.api.model.Usuario_direccion;
import com.deliveryboss.app.data.api.model.empresa_delivery;
import com.deliveryboss.app.data.prefs.SessionPrefs;
import com.deliveryboss.app.ui.MisDireccionesActivity;
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

    public static boolean ChequearLocalAbiertoHoy(EmpresasBody empresa){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String diaActual = sdf.format(new Date());

        Log.d("dia", diaActual);

        Boolean abierto= false;
        String turno1desde = "";
        String turno2desde = "";
        String turno1hasta = "";
        String turno2hasta = "";

        int cantidad=empresa.getEmpresa_horario().size();
        for(int i=0;i<cantidad;i++){
            if(diaActual.equals(empresa.getEmpresa_horario().get(i).getDia())){
                // OBTENEMOS LOS TURNOS DEL DIA
                turno1desde=empresa.getEmpresa_horario().get(i).getTurno1_desde();
                turno2desde=empresa.getEmpresa_horario().get(i).getTurno2_desde();
                turno1hasta=empresa.getEmpresa_horario().get(i).getTurno1_hasta();
                turno2hasta=empresa.getEmpresa_horario().get(i).getTurno2_hasta();

                // SI TIENE LOS DOS TURNOS
                if(turno1desde!=null && turno2desde!=null) {
                    //holder.horarios.setText("HOY de "+turno1desde+" a " +turno1hasta+" Y de "+turno2desde+" a "+turno2hasta);
                    return true;
                }


                // SI TIENE SOLO TURNO 1
                if(turno1desde!=null && turno2desde==null){
                    //holder.horarios.setText("HOY de "+turno1desde+" a " +turno1hasta);
                    return true;
                }

                // SI TIENE SOLO TURNO 2
                if(turno2desde!=null && turno1desde==null){
                    //holder.horarios.setText("HOY de "+turno2desde+" a " +turno2hasta);
                    return true;
                }
            }
        }
        //if(!abierto)holder.horarios.setText("HOY CERRADO");
        return false;
    }


    public static DeliveryRequest calcularPrecioDelivery(Usuario_direccion destino, EmpresasBody origen){
        LatLng local;
        LatLng usuario;
        Double distancia=0.0;
        float precio = 0.0f;
        boolean enZonaDeCobertura = false;

        // Comprobamos que el usuario y la empresa tengan ambos definidos su ubicacion
        if(destino.getLatitud()!=null && destino.getLongitud()!=null && origen.getLatitud()!=null && origen.getLongitud()!=null && !destino.getLatitud().equals("") && !destino.getLongitud().equals("") && !origen.getLatitud().equals("") && !origen.getLongitud().equals("")){
                usuario = new LatLng(Double.parseDouble(destino.getLatitud()), Double.parseDouble(destino.getLongitud()));
                local = new LatLng(Double.parseDouble(origen.getLatitud()), Double.parseDouble(origen.getLongitud()));

                distancia = SphericalUtil.computeDistanceBetween(usuario, local);
        }else{
            Delivery objeto = new Delivery(0.0,0.0f);
            DeliveryRequest respuesta = new DeliveryRequest(4,"El usuario o empresa no tienen ubicacion registrada",objeto);
            return respuesta;
        }

        // Chequeamos que el local tenga definidos los rangos de delivery
        if(origen.getEmpresa_delivery().size()>0){

            // SI EL USUARIO SE ENCUENTRA EN LA ZONA 4
            if(distancia<=Double.parseDouble(origen.getEmpresa_delivery().get(0).getRadio4())){
                precio = Float.parseFloat(origen.getEmpresa_delivery().get(0).getPrecio4());
                enZonaDeCobertura=true;
            }

            // SI EL USUARIO SE ENCUENTRA EN LA ZONA 3
            if(distancia<=Double.parseDouble(origen.getEmpresa_delivery().get(0).getRadio3())){
                precio = Float.parseFloat(origen.getEmpresa_delivery().get(0).getPrecio3());
                enZonaDeCobertura=true;
            }

            // SI EL USUARIO SE ENCUENTRA EN LA ZONA 2
            if(distancia<=Double.parseDouble(origen.getEmpresa_delivery().get(0).getRadio2())){
                precio = Float.parseFloat(origen.getEmpresa_delivery().get(0).getPrecio2());
                enZonaDeCobertura=true;
            }


            // SI EL USUARIO SE ENCUENTRA EN LA ZONA 1
            if(distancia<=Double.parseDouble(origen.getEmpresa_delivery().get(0).getRadio1())){
                precio = Float.parseFloat(origen.getEmpresa_delivery().get(0).getPrecio1());
                enZonaDeCobertura=true;
            }
        }

        Log.d("deliveryUser","Distancia desde el usuario hasta el local--->"+distancia.toString()+" Local:"+origen.getNombre_empresa());
        Log.d("deliveryUser","Precio a pagar--->"+precio);

        // Chequeo de zona de cobertura y respuesta
        if(enZonaDeCobertura){
            Delivery objeto = new Delivery(distancia,precio);
            DeliveryRequest respuesta = new DeliveryRequest(1,"Ok",objeto);
            return respuesta;
        }else{
            Delivery objeto = new Delivery(distancia,0.0f);
            DeliveryRequest respuesta = new DeliveryRequest(2,"El usuario esta fuera de la zona de cobertura del local",objeto);
            return respuesta;
        }
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
        String stDistancia = df.format(distanciaEnKm) + "km";
        return  stDistancia;
    }
}
