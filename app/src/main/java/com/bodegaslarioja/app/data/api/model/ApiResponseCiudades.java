package com.bodegaslarioja.app.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 22/8/2017.
 */

public class ApiResponseCiudades {
    private List<Ciudad> datos;

    public ApiResponseCiudades(List<Ciudad> datos) {
        this.datos = datos;
    }

    public List<Ciudad> getDatos() {
        return datos;
    }
}