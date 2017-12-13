package com.jadevelopment.deliveryboss1.data.api.model;

/**
 * Created by Joaquin on 15/8/2017.
 */

public class Usuario_direccion {
    String idusuario_direccion;
    String usuario_idusuario;
    String ciudad_idciudad;
    String ciudad;
    String calle;
    String numero;
    String habitacion;
    String barrio;
    String telefono;
    String indicaciones;

    public Usuario_direccion(String idusuario_direccion, String usuario_idusuario, String ciudad_idciudad, String ciudad, String calle, String numero, String habitacion, String barrio, String telefono, String indicaciones) {
        this.idusuario_direccion = idusuario_direccion;
        this.usuario_idusuario = usuario_idusuario;
        this.ciudad_idciudad = ciudad_idciudad;
        this.ciudad = ciudad;
        this.calle = calle;
        this.numero = numero;
        this.habitacion = habitacion;
        this.barrio = barrio;
        this.telefono = telefono;
        this.indicaciones = indicaciones;
    }

    public String getIdusuario_direccion() {
        return idusuario_direccion;
    }

    public void setIdusuario_direccion(String idusuario_direccion) {
        this.idusuario_direccion = idusuario_direccion;
    }

    public String getUsuario_idusuario() {
        return usuario_idusuario;
    }

    public void setUsuario_idusuario(String usuario_idusuario) {
        this.usuario_idusuario = usuario_idusuario;
    }

    public String getCiudad_idciudad() {
        return ciudad_idciudad;
    }

    public void setCiudad_idciudad(String ciudad_idciudad) {
        this.ciudad_idciudad = ciudad_idciudad;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(String habitacion) {
        this.habitacion = habitacion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
}
