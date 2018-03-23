package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 26/6/2017.
 */

public class Producto {
    String idproducto;
    String producto;
    String producto_detalle;
    String precio;
    String descuento;
    String empresa_idempresa;
    String producto_rubro_idproducto_rubro;

    public Producto(String idproducto, String producto, String producto_detalle, String precio, String descuento, String empresa_idempresa, String producto_rubro_idproducto_rubro) {
        this.idproducto = idproducto;
        this.producto = producto;
        this.producto_detalle = producto_detalle;
        this.precio = precio;
        this.descuento = descuento;
        this.empresa_idempresa = empresa_idempresa;
        this.producto_rubro_idproducto_rubro = producto_rubro_idproducto_rubro;
    }

    public String getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(String idproducto) {
        this.idproducto = idproducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getProducto_detalle() {
        return producto_detalle;
    }

    public void setProducto_detalle(String producto_detalle) {
        this.producto_detalle = producto_detalle;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public String getEmpresa_idempresa() {
        return empresa_idempresa;
    }

    public void setEmpresa_idempresa(String empresa_idempresa) {
        this.empresa_idempresa = empresa_idempresa;
    }

    public String getProducto_rubro_idproducto_rubro() {
        return producto_rubro_idproducto_rubro;
    }

    public void setProducto_rubro_idproducto_rubro(String producto_rubro_idproducto_rubro) {
        this.producto_rubro_idproducto_rubro = producto_rubro_idproducto_rubro;
    }
}
