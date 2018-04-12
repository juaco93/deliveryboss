package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 10/04/2018.
 */

public class Delivery {
    Double distancia;
    Float precio;

    public Delivery(Double distancia, Float precio) {
        this.distancia = distancia;
        this.precio = precio;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }
}
