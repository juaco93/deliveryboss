package com.deliveryboss.app.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 09/04/2018.
 */

public class ApiResponseEmpresa_delivery {
    private List<empresa_delivery> datos;

    public ApiResponseEmpresa_delivery(List<empresa_delivery> datos) {
        this.datos = datos;
    }

    public List<empresa_delivery> getDatos() {
        return datos;
    }
}
