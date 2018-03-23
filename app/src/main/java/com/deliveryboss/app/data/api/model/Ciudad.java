package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 22/8/2017.
 */

public class Ciudad {
    String idciudad;
    String ciudad;
    String codigo_postal;
    String provincia_idprovincia;


    public Ciudad(String idciudad, String ciudad, String codigo_postal, String provincia_idprovincia) {
        this.idciudad = idciudad;
        this.ciudad = ciudad;
        this.codigo_postal = codigo_postal;
        this.provincia_idprovincia = provincia_idprovincia;
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

    public String getCodigo_postal() {
        return codigo_postal;
    }

    public void setCodigo_postal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
    }

    public String getProvincia_idprovincia() {
        return provincia_idprovincia;
    }

    public void setProvincia_idprovincia(String provincia_idprovincia) {
        this.provincia_idprovincia = provincia_idprovincia;
    }
}
