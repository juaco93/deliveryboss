package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 23/6/2017.
 */

public class LoginBody {
    private String e_mail;
    private String contrasena;
    //private String idfacebook;

    public LoginBody(String e_mail, String contrasena) {
        this.e_mail = e_mail;
        this.contrasena = contrasena;
        //this.idfacebook = idfacebook;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /*public String getIdfacebook() {
        return idfacebook;
    }

    public void setIdfacebook(String idfacebook) {
        this.idfacebook = idfacebook;
    }*/
}
