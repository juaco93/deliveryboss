package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 06/04/2018.
 */

public class empresa_horario {
    String idempresa_horario;
    String dia;
    String turno1_desde;
    String turno1_hasta;
    String turno2_desde;
    String turno2_hasta;

    public empresa_horario(String idempresa_horario, String dia, String turno1_desde, String turno1_hasta, String turno2_desde, String turno2_hasta) {
        this.idempresa_horario = idempresa_horario;
        this.dia = dia;
        this.turno1_desde = turno1_desde;
        this.turno1_hasta = turno1_hasta;
        this.turno2_desde = turno2_desde;
        this.turno2_hasta = turno2_hasta;
    }

    public String getIdempresa_horario() {
        return idempresa_horario;
    }

    public void setIdempresa_horario(String idempresa_horario) {
        this.idempresa_horario = idempresa_horario;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getTurno1_desde() {
        return turno1_desde;
    }

    public void setTurno1_desde(String turno1_desde) {
        this.turno1_desde = turno1_desde;
    }

    public String getTurno1_hasta() {
        return turno1_hasta;
    }

    public void setTurno1_hasta(String turno1_hasta) {
        this.turno1_hasta = turno1_hasta;
    }

    public String getTurno2_desde() {
        return turno2_desde;
    }

    public void setTurno2_desde(String turno2_desde) {
        this.turno2_desde = turno2_desde;
    }

    public String getTurno2_hasta() {
        return turno2_hasta;
    }

    public void setTurno2_hasta(String turno2_hasta) {
        this.turno2_hasta = turno2_hasta;
    }
}
