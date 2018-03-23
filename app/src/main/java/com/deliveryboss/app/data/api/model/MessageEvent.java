package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 24/8/2017.
 */

public class MessageEvent {
    String idevento;
    String descripcion;

    public MessageEvent(String idevento, String descripcion) {
        this.idevento = idevento;
        this.descripcion = descripcion;
    }

    public String getIdevento() {
        return idevento;
    }

    public void setIdevento(String idevento) {
        this.idevento = idevento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
