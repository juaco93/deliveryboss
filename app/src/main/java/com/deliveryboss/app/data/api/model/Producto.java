package com.deliveryboss.app.data.api.model;

/**
 * Created by Joaquin on 26/6/2017.
 */

public class Producto extends ListItem{

    String idproducto;
    String fecha_hora_alta;
    String producto;
    String descripcion;
    String imagen;
    String precio1;
    String precio2;
    String stock;
    String descuento;
    String activo;
    String idbodega;
    String producto_rubro;

    public Producto(String idproducto, String fecha_hora_alta, String producto, String descripcion, String imagen, String precio1, String precio2, String stock, String descuento, String activo, String idbodega, String producto_rubro) {
        this.idproducto = idproducto;
        this.fecha_hora_alta = fecha_hora_alta;
        this.producto = producto;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.precio1 = precio1;
        this.precio2 = precio2;
        this.stock = stock;
        this.descuento = descuento;
        this.activo = activo;
        this.idbodega = idbodega;
        this.producto_rubro = producto_rubro;
    }

    public String getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(String idproducto) {
        this.idproducto = idproducto;
    }

    public String getFecha_hora_alta() {
        return fecha_hora_alta;
    }

    public void setFecha_hora_alta(String fecha_hora_alta) {
        this.fecha_hora_alta = fecha_hora_alta;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getPrecio1() {
        return precio1;
    }

    public void setPrecio1(String precio1) {
        this.precio1 = precio1;
    }

    public String getPrecio2() {
        return precio2;
    }

    public void setPrecio2(String precio2) {
        this.precio2 = precio2;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getIdbodega() {
        return idbodega;
    }

    public void setIdbodega(String idbodega) {
        this.idbodega = idbodega;
    }

    public String getProducto_rubro() {
        return producto_rubro;
    }

    public void setProducto_rubro(String producto_rubro) {
        this.producto_rubro = producto_rubro;
    }

    public int getType() {
        return TYPE_ITEM;
    }
}
