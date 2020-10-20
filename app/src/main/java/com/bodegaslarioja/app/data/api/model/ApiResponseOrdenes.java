package com.bodegaslarioja.app.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 5/8/2017.
 */

public class ApiResponseOrdenes {
    private List<Orden> datos;

    public ApiResponseOrdenes(List<Orden> datos) {
        this.datos = datos;
    }

    public List<Orden> getDatos() {
        return datos;
    }
}
