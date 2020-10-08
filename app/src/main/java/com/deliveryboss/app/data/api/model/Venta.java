package com.deliveryboss.app.data.api.model;

import java.util.List;

public class Venta {
    String idventa;
    String fecha_hora;
    String idbodega;
    String idcliente;
    String importe_total;
    String nota;
    String idventa_tipo;
    String idventa_estado;
    String nombre_bodega;
    String telefono1_bodega;
    String telefono2_bodega;
    String venta_estado;
    List<Venta_detalle> venta_detalle;

    public Venta(String idventa, String fecha_hora, String idbodega, String idcliente, String importe_total, String nota, String idventa_tipo, String idventa_estado, List<Venta_detalle> venta_detalle) {
        this.idventa = idventa;
        this.fecha_hora = fecha_hora;
        this.idbodega = idbodega;
        this.idcliente = idcliente;
        this.importe_total = importe_total;
        this.nota = nota;
        this.idventa_tipo = idventa_tipo;
        this.idventa_estado = idventa_estado;
        this.venta_detalle = venta_detalle;
    }

    public String getIdventa() {
        return idventa;
    }

    public void setIdventa(String idventa) {
        this.idventa = idventa;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getIdbodega() {
        return idbodega;
    }

    public void setIdbodega(String idbodega) {
        this.idbodega = idbodega;
    }

    public String getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(String idcliente) {
        this.idcliente = idcliente;
    }

    public String getImporte_total() {
        return importe_total;
    }

    public void setImporte_total(String importe_total) {
        this.importe_total = importe_total;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getIdventa_tipo() {
        return idventa_tipo;
    }

    public void setIdventa_tipo(String idventa_tipo) {
        this.idventa_tipo = idventa_tipo;
    }

    public List<Venta_detalle> getVenta_detalle() {
        return venta_detalle;
    }

    public void setVenta_detalle(List<Venta_detalle> venta_detalle) {
        this.venta_detalle = venta_detalle;
    }

    public String getIdventa_estado() {
        return idventa_estado;
    }

    public void setIdventa_estado(String idventa_estado) {
        this.idventa_estado = idventa_estado;
    }

    public String getNombre_bodega() {
        return nombre_bodega;
    }

    public void setNombre_bodega(String nombre_bodega) {
        this.nombre_bodega = nombre_bodega;
    }

    public String getTelefono1_bodega() {
        return telefono1_bodega;
    }

    public void setTelefono1_bodega(String telefono1_bodega) {
        this.telefono1_bodega = telefono1_bodega;
    }

    public String getTelefono2_bodega() {
        return telefono2_bodega;
    }

    public void setTelefono2_bodega(String telefono2_bodega) {
        this.telefono2_bodega = telefono2_bodega;
    }

    public String getVenta_estado() {
        return venta_estado;
    }

    public void setVenta_estado(String venta_estado) {
        this.venta_estado = venta_estado;
    }
}
