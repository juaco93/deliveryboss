package com.jadevelopment.deliveryboss1.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 26/6/2017.
 */

public class ApiResponseProductos {
    private List<Producto> datos;

    public ApiResponseProductos(List<Producto> datos) {
        this.datos = datos;
    }

    public List<Producto> getDatos() {
        return datos;
    }
}
