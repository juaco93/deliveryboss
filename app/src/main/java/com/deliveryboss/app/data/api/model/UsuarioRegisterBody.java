package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 17/7/2017.
 */

public class UsuarioRegisterBody {
    String nombre;
    String apellido;
    String e_mail;
    String telefono;
    String contrasena;
    String fecha_nacimiento;
    String usuario_tipo_idusuario_tipo;
    String sexo_idsexo;
    String usuario_estado_idusuario_estado;

    public UsuarioRegisterBody(String nombre, String apellido, String e_mail, String telefono, String contrasena, String fecha_nacimiento, String usuario_tipo_idusuario_tipo, String sexo_idsexo, String usuario_estado_idusuario_estado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.e_mail = e_mail;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.fecha_nacimiento = fecha_nacimiento;
        this.usuario_tipo_idusuario_tipo = usuario_tipo_idusuario_tipo;
        this.sexo_idsexo = sexo_idsexo;
        this.usuario_estado_idusuario_estado = usuario_estado_idusuario_estado;
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

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getUsuario_tipo_idusuario_tipo() {
        return usuario_tipo_idusuario_tipo;
    }

    public void setUsuario_tipo_idusuario_tipo(String usuario_tipo_idusuario_tipo) {
        this.usuario_tipo_idusuario_tipo = usuario_tipo_idusuario_tipo;
    }

    public String getSexo_idsexo() {
        return sexo_idsexo;
    }

    public void setSexo_idsexo(String sexo_idsexo) {
        this.sexo_idsexo = sexo_idsexo;
    }

    public String getUsuario_estado_idusuario_estado() {
        return usuario_estado_idusuario_estado;
    }

    public void setUsuario_estado_idusuario_estado(String usuario_estado_idusuario_estado) {
        this.usuario_estado_idusuario_estado = usuario_estado_idusuario_estado;
    }
}
