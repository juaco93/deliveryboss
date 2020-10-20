package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 22/8/2017.
 */

public class Ciudad {
    String idciudad;
    String ciudad;


    public Ciudad(String idciudad, String ciudad) {
        this.idciudad = idciudad;
        this.ciudad = ciudad;
    }

    public String getIdciudad() {
        return idciudad;
    }

    public void setIdciudad(String idciudad) {
        this.idciudad = idciudad;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

}
