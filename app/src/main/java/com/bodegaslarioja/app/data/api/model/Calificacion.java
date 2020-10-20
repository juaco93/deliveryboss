package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 12/7/2017.
 */

public class Calificacion {
    String idcalificacion;
    String fecha_hora;
    String calificacion1;
    String calificacion2;
    String calificacion3;
    String calificacion_general;
    String comentario;
    String calificacion_empresa_idcalificacion_empresa;
    String comentario_empresa;
    String orden_idorden;
    String usuario_idusuario;
    String empresa_idempresa;

    // Campos para mostrar en pestana calificaciones de cada empresa
    String usuario;
    String imagen;

    public Calificacion(String idcalificacion, String fecha_hora, String calificacion1, String calificacion2, String calificacion3, String calificacion_general, String comentario, String calificacion_empresa_idcalificacion_empresa, String comentario_empresa, String orden_idorden, String usuario_idusuario, String empresa_idempresa, String usuario, String imagen) {
        this.idcalificacion = idcalificacion;
        this.fecha_hora = fecha_hora;
        this.calificacion1 = calificacion1;
        this.calificacion2 = calificacion2;
        this.calificacion3 = calificacion3;
        this.calificacion_general = calificacion_general;
        this.comentario = comentario;
        this.calificacion_empresa_idcalificacion_empresa = calificacion_empresa_idcalificacion_empresa;
        this.comentario_empresa = comentario_empresa;
        this.orden_idorden = orden_idorden;
        this.usuario_idusuario = usuario_idusuario;
        this.empresa_idempresa = empresa_idempresa;
        this.usuario = usuario;
        this.imagen = imagen;
    }

    public String getIdcalificacion() {
        return idcalificacion;
    }

    public void setIdcalificacion(String idcalificacion) {
        this.idcalificacion = idcalificacion;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getCalificacion1() {
        return calificacion1;
    }

    public void setCalificacion1(String calificacion1) {
        this.calificacion1 = calificacion1;
    }

    public String getCalificacion2() {
        return calificacion2;
    }

    public void setCalificacion2(String calificacion2) {
        this.calificacion2 = calificacion2;
    }

    public String getCalificacion3() {
        return calificacion3;
    }

    public void setCalificacion3(String calificacion3) {
        this.calificacion3 = calificacion3;
    }

    public String getCalificacion_general() {
        return calificacion_general;
    }

    public void setCalificacion_general(String calificacion_general) {
        this.calificacion_general = calificacion_general;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getCalificacion_empresa_idcalificacion_empresa() {
        return calificacion_empresa_idcalificacion_empresa;
    }

    public void setCalificacion_empresa_idcalificacion_empresa(String calificacion_empresa_idcalificacion_empresa) {
        this.calificacion_empresa_idcalificacion_empresa = calificacion_empresa_idcalificacion_empresa;
    }

    public String getComentario_empresa() {
        return comentario_empresa;
    }

    public void setComentario_empresa(String comentario_empresa) {
        this.comentario_empresa = comentario_empresa;
    }

    public String getOrden_idorden() {
        return orden_idorden;
    }

    public void setOrden_idorden(String orden_idorden) {
        this.orden_idorden = orden_idorden;
    }

    public String getUsuario_idusuario() {
        return usuario_idusuario;
    }

    public void setUsuario_idusuario(String usuario_idusuario) {
        this.usuario_idusuario = usuario_idusuario;
    }

    public String getEmpresa_idempresa() {
        return empresa_idempresa;
    }

    public void setEmpresa_idempresa(String empresa_idempresa) {
        this.empresa_idempresa = empresa_idempresa;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
