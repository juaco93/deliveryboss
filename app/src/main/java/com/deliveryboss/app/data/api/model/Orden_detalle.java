package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 4/8/2017.
 */

public class Orden_detalle {
    String idorden_detalle;
    String orden_idorden;
    String cantidad;
    String producto_idproducto;
    String producto_nombre;
    String producto_precio;
    String producto_rubro;
    String orden_detalle_subtotal;
    Boolean isSelected;

    public Orden_detalle(String idorden_detalle, String orden_idorden, String cantidad, String producto_idproducto, String producto_nombre, String producto_precio, String producto_rubro, String orden_detalle_subtotal) {
        this.idorden_detalle = idorden_detalle;
        this.orden_idorden = orden_idorden;
        this.cantidad = cantidad;
        this.producto_idproducto = producto_idproducto;
        this.producto_nombre = producto_nombre;
        this.producto_precio = producto_precio;
        this.producto_rubro = producto_rubro;
        this.orden_detalle_subtotal = orden_detalle_subtotal;
        this.setSelected(false);
    }

    public String getIdorden_detalle() {
        return idorden_detalle;
    }

    public void setIdorden_detalle(String idorden_detalle) {
        this.idorden_detalle = idorden_detalle;
    }

    public String getOrden_idorden() {
        return orden_idorden;
    }

    public void setOrden_idorden(String orden_idorden) {
        this.orden_idorden = orden_idorden;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getProducto_idproducto() {
        return producto_idproducto;
    }

    public void setProducto_idproducto(String producto_idproducto) {
        this.producto_idproducto = producto_idproducto;
    }

    public String getProducto_nombre() {
        return producto_nombre;
    }

    public void setProducto_nombre(String producto_nombre) {
        this.producto_nombre = producto_nombre;
    }

    public String getProducto_precio() {
        return producto_precio;
    }

    public void setProducto_precio(String producto_precio) {
        this.producto_precio = producto_precio;
    }

    public String getProducto_rubro() {
        return producto_rubro;
    }

    public void setProducto_rubro(String producto_rubro) {
        this.producto_rubro = producto_rubro;
    }

    public String getOrden_detalle_subtotal() {
        return orden_detalle_subtotal;
    }

    public void setOrden_detalle_subtotal(String orden_detalle_subtotal) {
        this.orden_detalle_subtotal = orden_detalle_subtotal;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
