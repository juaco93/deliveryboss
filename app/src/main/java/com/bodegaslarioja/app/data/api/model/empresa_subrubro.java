package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 19/04/2018.
 */

public class empresa_subrubro {
    String empresa_idempresa;
    String idempresa_subrubro;
    String idsubrubro1;
    String subrubro1;
    String idsubrubro2;
    String subrubro2;
    String idsubrubro3;
    String subrubro3;

    public empresa_subrubro(String empresa_idempresa, String idempresa_subrubro, String idsubrubro1, String subrubro1, String idsubrubro2, String subrubro2, String idsubrubro3, String subrubro3) {
        this.empresa_idempresa = empresa_idempresa;
        this.idempresa_subrubro = idempresa_subrubro;
        this.idsubrubro1 = idsubrubro1;
        this.subrubro1 = subrubro1;
        this.idsubrubro2 = idsubrubro2;
        this.subrubro2 = subrubro2;
        this.idsubrubro3 = idsubrubro3;
        this.subrubro3 = subrubro3;
    }

    public String getEmpresa_idempresa() {
        return empresa_idempresa;
    }

    public void setEmpresa_idempresa(String empresa_idempresa) {
        this.empresa_idempresa = empresa_idempresa;
    }

    public String getIdempresa_subrubro() {
        return idempresa_subrubro;
    }

    public void setIdempresa_subrubro(String idempresa_subrubro) {
        this.idempresa_subrubro = idempresa_subrubro;
    }

    public String getIdsubrubro1() {
        return idsubrubro1;
    }

    public void setIdsubrubro1(String idsubrubro1) {
        this.idsubrubro1 = idsubrubro1;
    }

    public String getSubrubro1() {
        return subrubro1;
    }

    public void setSubrubro1(String subrubro1) {
        this.subrubro1 = subrubro1;
    }

    public String getIdsubrubro2() {
        return idsubrubro2;
    }

    public void setIdsubrubro2(String idsubrubro2) {
        this.idsubrubro2 = idsubrubro2;
    }

    public String getSubrubro2() {
        return subrubro2;
    }

    public void setSubrubro2(String subrubro2) {
        this.subrubro2 = subrubro2;
    }

    public String getIdsubrubro3() {
        return idsubrubro3;
    }

    public void setIdsubrubro3(String idsubrubro3) {
        this.idsubrubro3 = idsubrubro3;
    }

    public String getSubrubro3() {
        return subrubro3;
    }

    public void setSubrubro3(String subrubro3) {
        this.subrubro3 = subrubro3;
    }
}
