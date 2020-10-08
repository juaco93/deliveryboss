package com.deliveryboss.app.data.api.model;

public class Venta_detalle {
    String idventa_detalle;
    String idventa;
    String idbodega;
    String cantidad;
    String idproducto;
    String producto;
    String precio;
    String importe_subtotal;
    String activo;
    Boolean isSelected;

    public Venta_detalle(String idventa_detalle, String idventa, String idbodega, String cantidad, String idproducto, String producto, String precio, String importe_subtotal, String activo) {
        this.idventa_detalle = idventa_detalle;
        this.idventa = idventa;
        this.idbodega = idbodega;
        this.cantidad = cantidad;
        this.idproducto = idproducto;
        this.producto = producto;
        this.precio = precio;
        this.importe_subtotal = importe_subtotal;
        this.activo = activo;
        this.setSelected(false);
    }

    public String getIdventa_detalle() {
        return idventa_detalle;
    }

    public void setIdventa_detalle(String idventa_detalle) {
        this.idventa_detalle = idventa_detalle;
    }

    public String getIdventa() {
        return idventa;
    }

    public void setIdventa(String idventa) {
        this.idventa = idventa;
    }

    public String getIdbodega() {
        return idbodega;
    }

    public void setIdbodega(String idbodega) {
        this.idbodega = idbodega;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
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

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getImporte_subtotal() {
        return importe_subtotal;
    }

    public void setImporte_subtotal(String importe_subtotal) {
        this.importe_subtotal = importe_subtotal;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
