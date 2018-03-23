package com.deliveryboss.app.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 25/6/2017.
 */

public class ApiResponseEmpresas {
    private List<EmpresasBody> datos;

    public ApiResponseEmpresas(List<EmpresasBody> datos) {
        this.datos = datos;
    }

    public List<EmpresasBody> getDatos() {
        return datos;
    }
}
