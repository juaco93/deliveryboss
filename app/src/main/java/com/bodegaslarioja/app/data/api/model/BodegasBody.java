package com.bodegaslarioja.app.data.api.model;

/**
 * Created by Joaquin on 24/6/2017.
 */

public class BodegasBody {
    String idbodega;
    String nombre;
    String token;
    String fecha_hora_alta;
    String imagen;
    String imagen1;
    String imagen2;
    String imagen3;
    String cuit;
    String dni;
    String direccion;
    String ciudad;
    String compra_minima;
    String telefono1;
    String telefono2;
    String idempresa_rubro;
    String rubro1;
    String rubro2;
    String rubro3;
    String idtipo_entrega;
    String tipo_entrega;
    String historia;
    String e_commerce_activo;
    String activo;
    String inactivo_fecha_hora;

    public BodegasBody(String idbodega, String nombre, String token, String fecha_hora_alta, String imagen, String imagen1, String imagen2, String imagen3, String cuit, String dni, String direccion, String ciudad, String compra_minima, String telefono1, String telefono2, String idempresa_rubro, String rubro1, String rubro2, String rubro3, String idtipo_entrega, String tipo_entrega, String historia, String e_commerce_activo, String activo, String inactivo_fecha_hora) {
        this.idbodega = idbodega;
        this.nombre = nombre;
        this.token = token;
        this.fecha_hora_alta = fecha_hora_alta;
        this.imagen = imagen;
        this.imagen1 = imagen1;
        this.imagen2 = imagen2;
        this.imagen3 = imagen3;
        this.cuit = cuit;
        this.dni = dni;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.compra_minima = compra_minima;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.idempresa_rubro = idempresa_rubro;
        this.rubro1 = rubro1;
        this.rubro2 = rubro2;
        this.rubro3 = rubro3;
        this.idtipo_entrega = idtipo_entrega;
        this.tipo_entrega = tipo_entrega;
        this.historia = historia;
        this.e_commerce_activo = e_commerce_activo;
        this.activo = activo;
        this.inactivo_fecha_hora = inactivo_fecha_hora;
    }

    public String getIdbodega() {
        return idbodega;
    }

    public void setIdbodega(String idbodega) {
        this.idbodega = idbodega;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFecha_hora_alta() {
        return fecha_hora_alta;
    }

    public void setFecha_hora_alta(String fecha_hora_alta) {
        this.fecha_hora_alta = fecha_hora_alta;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getImagen1() {
        return imagen1;
    }

    public void setImagen1(String imagen1) {
        this.imagen1 = imagen1;
    }

    public String getImagen2() {
        return imagen2;
    }

    public void setImagen2(String imagen2) {
        this.imagen2 = imagen2;
    }

    public String getImagen3() {
        return imagen3;
    }

    public void setImagen3(String imagen3) {
        this.imagen3 = imagen3;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCompra_minima() {
        return compra_minima;
    }

    public void setCompra_minima(String compra_minima) {
        this.compra_minima = compra_minima;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getIdempresa_rubro() {
        return idempresa_rubro;
    }

    public void setIdempresa_rubro(String idempresa_rubro) {
        this.idempresa_rubro = idempresa_rubro;
    }

    public String getRubro1() {
        return rubro1;
    }

    public void setRubro1(String rubro1) {
        this.rubro1 = rubro1;
    }

    public String getRubro2() {
        return rubro2;
    }

    public void setRubro2(String rubro2) {
        this.rubro2 = rubro2;
    }

    public String getRubro3() {
        return rubro3;
    }

    public void setRubro3(String rubro3) {
        this.rubro3 = rubro3;
    }

    public String getIdtipo_entrega() {
        return idtipo_entrega;
    }

    public void setIdtipo_entrega(String idtipo_entrega) {
        this.idtipo_entrega = idtipo_entrega;
    }

    public String getTipo_entrega() {
        return tipo_entrega;
    }

    public void setTipo_entrega(String tipo_entrega) {
        this.tipo_entrega = tipo_entrega;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public String getE_commerce_activo() {
        return e_commerce_activo;
    }

    public void setE_commerce_activo(String e_commerce_activo) {
        this.e_commerce_activo = e_commerce_activo;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getInactivo_fecha_hora() {
        return inactivo_fecha_hora;
    }

    public void setInactivo_fecha_hora(String inactivo_fecha_hora) {
        this.inactivo_fecha_hora = inactivo_fecha_hora;
    }
}
