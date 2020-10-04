package com.deliveryboss.app.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 25/6/2017.
 */

public class ApiResponseBodegas {
    private List<BodegasBody> datos;

    public ApiResponseBodegas(List<BodegasBody> datos) {
        this.datos = datos;
    }

    public List<BodegasBody> getDatos() {
        return datos;
    }
}
