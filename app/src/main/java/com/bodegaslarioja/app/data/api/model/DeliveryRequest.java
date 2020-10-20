package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 11/04/2018.
 */

public class DeliveryRequest {
    Integer estado;
    String mensaje;
    Delivery datos;

    public DeliveryRequest(Integer estado, String mensaje, Delivery datos) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Delivery getDatos() {
        return datos;
    }

    public void setDatos(Delivery datos) {
        this.datos = datos;
    }
}
