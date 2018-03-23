package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 22/9/2017.
 */

public class CiudadSugerida {
    String ciudad;
    String comentario;
    String e_mail;

    public CiudadSugerida(String ciudad, String comentario, String e_mail) {
        this.ciudad = ciudad;
        this.comentario = comentario;
        this.e_mail = e_mail;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }
}
