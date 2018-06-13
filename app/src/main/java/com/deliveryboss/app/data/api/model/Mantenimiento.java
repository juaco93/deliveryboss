package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 2/2/2018.
 */

public class Mantenimiento {
    String idmodo_mantenimiento;
    String estado;         // 0=inactivo     1=activo
    String titulo;
    String mensaje;

    public Mantenimiento(String idmodo_mantenimiento, String estado, String titulo, String mensaje) {
        this.idmodo_mantenimiento = idmodo_mantenimiento;
        this.estado = estado;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }

    public String getIdmodo_mantenimiento() {
        return idmodo_mantenimiento;
    }

    public void setIdmodo_mantenimiento(String idmodo_mantenimiento) {
        this.idmodo_mantenimiento = idmodo_mantenimiento;
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
