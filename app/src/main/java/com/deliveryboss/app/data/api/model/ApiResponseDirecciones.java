package com.deliveryboss.app.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 15/8/2017.
 */

public class ApiResponseDirecciones {
    private List<Usuario_direccion> datos;

    public ApiResponseDirecciones(List<Usuario_direccion> datos) {
        this.datos = datos;
    }

    public List<Usuario_direccion> getDatos() {
        return datos;
    }
}
