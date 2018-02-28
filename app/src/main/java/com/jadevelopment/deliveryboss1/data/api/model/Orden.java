package com.jadevelopment.deliveryboss1.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 4/8/2017.
 */

public class Orden {
    String idorden;
    String fecha_hora;
    String fecha_hora_estado;
    String info_estado;
    String estado;
    String usuario_idusuario;
    String empresa_idempresa;
    String usuario_direccion_idusuario_direccion;
    String importe_total;
    String paga_con;
    String nota;
    String tipo_entrega_idtipo_entrega;
    String orden_estado_idorden_estado;
    String nombre_empresa;
    String telefono;
    String calificado;
    List<Orden_detalle> orden_detalle;

    public Orden(String fecha_hora, String fecha_hora_estado, String info_estado, String estado, String usuario_idusuario, String empresa_idempresa, String usuario_direccion_idusuario_direccion, String importe_total, String paga_con, String nota, String tipo_entrega_idtipo_entrega, String orden_estado_idorden_estado, String nombre_empresa, String telefono, String calificado, List<Orden_detalle> orden_detalle) {
        this.fecha_hora = fecha_hora;
        this.fecha_hora_estado = fecha_hora_estado;
        this.info_estado = info_estado;
        this.estado = estado;
        this.usuario_idusuario = usuario_idusuario;
        this.empresa_idempresa = empresa_idempresa;
        this.usuario_direccion_idusuario_direccion = usuario_direccion_idusuario_direccion;
        this.importe_total = importe_total;
        this.paga_con = paga_con;
        this.nota = nota;
        this.tipo_entrega_idtipo_entrega = tipo_entrega_idtipo_entrega;
        this.orden_estado_idorden_estado = orden_estado_idorden_estado;
        this.nombre_empresa = nombre_empresa;
        this.telefono = telefono;
        this.calificado = calificado;
        this.orden_detalle = orden_detalle;
    }

    public String getIdorden() {
        return idorden;
    }

    public void setIdorden(String idorden) {
        this.idorden = idorden;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getFecha_hora_estado() {
        return fecha_hora_estado;
    }

    public void setFecha_hora_estado(String fecha_hora_estado) {
        this.fecha_hora_estado = fecha_hora_estado;
    }

    public String getInfo_estado() {
        return info_estado;
    }

    public void setInfo_estado(String info_estado) {
        this.info_estado = info_estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public String getUsuario_direccion_idusuario_direccion() {
        return usuario_direccion_idusuario_direccion;
    }

    public void setUsuario_direccion_idusuario_direccion(String usuario_direccion_idusuario_direccion) {
        this.usuario_direccion_idusuario_direccion = usuario_direccion_idusuario_direccion;
    }

    public String getImporte_total() {
        return importe_total;
    }

    public void setImporte_total(String importe_total) {
        this.importe_total = importe_total;
    }

    public String getPaga_con() {
        return paga_con;
    }

    public void setPaga_con(String paga_con) {
        this.paga_con = paga_con;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getTipo_entrega_idtipo_entrega() {
        return tipo_entrega_idtipo_entrega;
    }

    public void setTipo_entrega_idtipo_entrega(String tipo_entrega_idtipo_entrega) {
        this.tipo_entrega_idtipo_entrega = tipo_entrega_idtipo_entrega;
    }

    public String getOrden_estado_idorden_estado() {
        return orden_estado_idorden_estado;
    }

    public void setOrden_estado_idorden_estado(String orden_estado_idorden_estado) {
        this.orden_estado_idorden_estado = orden_estado_idorden_estado;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCalificado() {
        return calificado;
    }

    public void setCalificado(String calificado) {
        this.calificado = calificado;
    }

    public List<Orden_detalle> getOrden_detalle() {
        return orden_detalle;
    }

    public void setOrden_detalle(List<Orden_detalle> orden_detalle) {
        this.orden_detalle = orden_detalle;
    }

}
