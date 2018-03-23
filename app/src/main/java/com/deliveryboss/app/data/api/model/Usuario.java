package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 23/6/2017.
 */

public class Usuario {
    String idusuario;
    String token;
    String imagen;
    String nombre;
    String apellido;
    String telefono;
    String e_mail;
    String sexo_idsexo;
    String fecha_nacimiento;

    public Usuario(String idusuario, String token, String imagen, String nombre, String apellido, String telefono, String e_mail, String sexo_idsexo, String fecha_nacimiento) {
        this.idusuario = idusuario;
        this.token = token;
        this.imagen = imagen;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.e_mail = e_mail;
        this.sexo_idsexo = sexo_idsexo;
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
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

    public String getSexo_idsexo() {
        return sexo_idsexo;
    }

    public void setSexo_idsexo(String sexo_idsexo) {
        this.sexo_idsexo = sexo_idsexo;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }
}
