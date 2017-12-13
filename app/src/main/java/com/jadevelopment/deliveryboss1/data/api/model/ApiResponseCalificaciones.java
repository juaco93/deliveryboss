package com.jadevelopment.deliveryboss1.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 13/7/2017.
 */

public class ApiResponseCalificaciones {
    private List<Calificacion> datos;

    public ApiResponseCalificaciones(List<Calificacion> datos) {
        this.datos = datos;
    }

    public List<Calificacion> getDatos() {
        return datos;
    }
}
