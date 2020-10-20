package com.bodegaslarioja.app.data.api.model;

import java.util.List;

public class ApiResponseVentas {
    private List<Venta> datos;

    public ApiResponseVentas(List<Venta> datos) {
        this.datos = datos;
    }

    public List<Venta> getDatos() {
        return datos;
    }
}
