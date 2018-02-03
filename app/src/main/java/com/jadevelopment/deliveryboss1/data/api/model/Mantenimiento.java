package com.jadevelopment.deliveryboss1.data.api.model;

/**
 * Created by Joaquin on 2/2/2018.
 */

public class Mantenimiento {
    String idmantenimiento;
    String estado;         // 0=inactivo     1=activo
    String titulo;
    String mensaje;

    public Mantenimiento(String idmantenimiento, String estado, String titulo, String mensaje) {
        this.idmantenimiento = idmantenimiento;
        this.estado = estado;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }

    public String getIdmantenimiento() {
        return idmantenimiento;
    }

    public void setIdmantenimiento(String idmantenimiento) {
        this.idmantenimiento = idmantenimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
