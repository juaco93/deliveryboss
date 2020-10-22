package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 09/04/2018.
 */

public class empresa_delivery {

    String idempresa_delivery;
    String radio1;
    String precio1;
    String radio2;
    String precio2;
    String radio3;
    String precio3;
    String radio4;
    String precio4;
    String empresa_idempresa;

    public empresa_delivery(String idempresa_delivery, String radio1, String precio1, String radio2, String precio2, String radio3, String precio3, String radio4, String precio4, String empresa_idempresa) {
        this.idempresa_delivery = idempresa_delivery;
        this.radio1 = radio1;
        this.precio1 = precio1;
        this.radio2 = radio2;
        this.precio2 = precio2;
        this.radio3 = radio3;
        this.precio3 = precio3;
        this.radio4 = radio4;
        this.precio4 = precio4;
        this.empresa_idempresa = empresa_idempresa;
    }

    public String getIdempresa_delivery() {
        return idempresa_delivery;
    }

    public void setIdempresa_delivery(String idempresa_delivery) {
        this.idempresa_delivery = idempresa_delivery;
    }

    public String getRadio1() {
        return radio1;
    }

    public void setRadio1(String radio1) {
        this.radio1 = radio1;
    }

    public String getPrecio1() {
        return precio1;
    }

    public void setPrecio1(String precio1) {
        this.precio1 = precio1;
    }

    public String getRadio2() {
        return radio2;
    }

    public void setRadio2(String radio2) {
        this.radio2 = radio2;
    }

    public String getPrecio2() {
        return precio2;
    }

    public void setPrecio2(String precio2) {
        this.precio2 = precio2;
    }

    public String getRadio3() {
        return radio3;
    }

    public void setRadio3(String radio3) {
        this.radio3 = radio3;
    }

    public String getPrecio3() {
        return precio3;
    }

    public void setPrecio3(String precio3) {
        this.precio3 = precio3;
    }

    public String getRadio4() {
        return radio4;
    }

    public void setRadio4(String radio4) {
        this.radio4 = radio4;
    }

    public String getPrecio4() {
        return precio4;
    }

    public void setPrecio4(String precio4) {
        this.precio4 = precio4;
    }

    public String getEmpresa_idempresa() {
        return empresa_idempresa;
    }

    public void setEmpresa_idempresa(String empresa_idempresa) {
        this.empresa_idempresa = empresa_idempresa;
    }
}
