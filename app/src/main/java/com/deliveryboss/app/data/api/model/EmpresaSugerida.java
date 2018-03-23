package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 21/9/2017.
 */

public class EmpresaSugerida {
    String tipo_sugerencia;
    String nombre;
    String apellido;
    String telefono;
    String e_mail;
    String nombre_empresa;
    String direccion_empresa;
    String ciudad_empresa;

    public EmpresaSugerida(String tipo_sugerencia, String nombre, String apellido, String telefono, String e_mail, String nombre_empresa, String direccion_empresa, String ciudad_empresa) {
        this.tipo_sugerencia = tipo_sugerencia;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.e_mail = e_mail;
        this.nombre_empresa = nombre_empresa;
        this.direccion_empresa = direccion_empresa;
        this.ciudad_empresa = ciudad_empresa;
    }

    public String getTipo_sugerencia() {
        return tipo_sugerencia;
    }

    public void setTipo_sugerencia(String tipo_sugerencia) {
        this.tipo_sugerencia = tipo_sugerencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getDireccion_empresa() {
        return direccion_empresa;
    }

    public void setDireccion_empresa(String direccion_empresa) {
        this.direccion_empresa = direccion_empresa;
    }

    public String getCiudad_empresa() {
        return ciudad_empresa;
    }

    public void setCiudad_empresa(String ciudad_empresa) {
        this.ciudad_empresa = ciudad_empresa;
    }
}
