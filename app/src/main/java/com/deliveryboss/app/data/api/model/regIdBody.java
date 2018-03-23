package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 14/10/2017.
 */

public class regIdBody {
    String regId;
    String idusuario;

    public regIdBody(String regId, String idusuario) {
        this.regId = regId;
        this.idusuario = idusuario;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }
}
