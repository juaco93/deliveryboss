package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 13/03/2018.
 */

public class ApiResponseUsuario {

    String imagen;
    String nombre;
    String apellido;
    String telefono;
    String e_mail;
    String idgenero;
    String fecha_nacimiento;

    public ApiResponseUsuario(String imagen, String nombre, String apellido, String telefono, String e_mail, String idgenero, String fecha_nacimiento) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.e_mail = e_mail;
        this.idgenero = idgenero;
        this.fecha_nacimiento = fecha_nacimiento;
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

    public String getIdgenero() {
        return idgenero;
    }

    public void setIdgenero(String idgenero) {
        this.idgenero = idgenero;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }
}
