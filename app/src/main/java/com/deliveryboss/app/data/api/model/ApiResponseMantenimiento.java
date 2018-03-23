package com.deliveryboss.app.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 2/2/2018.
 */

public class ApiResponseMantenimiento {
    private List<Mantenimiento> datos;

    public ApiResponseMantenimiento(List<Mantenimiento> datos) {
        this.datos = datos;
    }

    public List<Mantenimiento> getDatos() {
        return datos;
    }
}
