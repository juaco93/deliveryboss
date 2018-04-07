package com.deliveryboss.app.data.api.model;

import java.util.List;

/**
 * Created by Joaquin on 24/6/2017.
 */

public class EmpresasBody {
    String idempresa;
    String logo;
    String nombre_empresa;
    String direccion;
    String latitud;
    String longitud;
    String tiempo_minimo_entrega;
    String tiempo_maximo_entrega;
    String zona_delivery;
    String precio_delivery;
    String compra_minima;
    String telefono_empresa;
    String oculto;
    String cerrado;
    String empresa_rubro;
    String tipo_entrega_idtipo_entrega;
    String subrubro;
    List<empresa_horario> empresa_horario;
    String calificacion_general;
    String cantidad_calificacion;
    String calificacion1;
    String calificacion2;
    String calificacion3;
    String ciudad;


    public EmpresasBody(String idempresa, String logo, String nombre_empresa, String direccion, String latitud, String longitud, String tiempo_minimo_entrega, String tiempo_maximo_entrega, String zona_delivery, String precio_delivery, String compra_minima, String telefono_empresa, String oculto, String cerrado, String empresa_rubro, String tipo_entrega_idtipo_entrega, String subrubro, List<com.deliveryboss.app.data.api.model.empresa_horario> empresa_horario, String calificacion_general, String cantidad_calificacion, String calificacion1, String calificacion2, String calificacion3, String ciudad) {
        this.idempresa = idempresa;
        this.logo = logo;
        this.nombre_empresa = nombre_empresa;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tiempo_minimo_entrega = tiempo_minimo_entrega;
        this.tiempo_maximo_entrega = tiempo_maximo_entrega;
        this.zona_delivery = zona_delivery;
        this.precio_delivery = precio_delivery;
        this.compra_minima = compra_minima;
        this.telefono_empresa = telefono_empresa;
        this.oculto = oculto;
        this.cerrado = cerrado;
        this.empresa_rubro = empresa_rubro;
        this.tipo_entrega_idtipo_entrega = tipo_entrega_idtipo_entrega;
        this.subrubro = subrubro;
        this.empresa_horario = empresa_horario;
        this.calificacion_general = calificacion_general;
        this.cantidad_calificacion = cantidad_calificacion;
        this.calificacion1 = calificacion1;
        this.calificacion2 = calificacion2;
        this.calificacion3 = calificacion3;
        this.ciudad = ciudad;
    }

    public String getIdempresa() {
        return idempresa;
    }

    public void setIdempresa(String idempresa) {
        this.idempresa = idempresa;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTiempo_minimo_entrega() {
        return tiempo_minimo_entrega;
    }

    public void setTiempo_minimo_entrega(String tiempo_minimo_entrega) {
        this.tiempo_minimo_entrega = tiempo_minimo_entrega;
    }

    public String getTiempo_maximo_entrega() {
        return tiempo_maximo_entrega;
    }

    public void setTiempo_maximo_entrega(String tiempo_maximo_entrega) {
        this.tiempo_maximo_entrega = tiempo_maximo_entrega;
    }

    public String getZona_delivery() {
        return zona_delivery;
    }

    public void setZona_delivery(String zona_delivery) {
        this.zona_delivery = zona_delivery;
    }

    public String getPrecio_delivery() {
        return precio_delivery;
    }

    public void setPrecio_delivery(String precio_delivery) {
        this.precio_delivery = precio_delivery;
    }

    public String getCompra_minima() {
        return compra_minima;
    }

    public void setCompra_minima(String compra_minima) {
        this.compra_minima = compra_minima;
    }

    public String getTelefono_empresa() {
        return telefono_empresa;
    }

    public void setTelefono_empresa(String telefono_empresa) {
        this.telefono_empresa = telefono_empresa;
    }

    public String getOculto() {
        return oculto;
    }

    public void setOculto(String oculto) {
        this.oculto = oculto;
    }

    public String getCerrado() {
        return cerrado;
    }

    public void setCerrado(String cerrado) {
        this.cerrado = cerrado;
    }

    public String getEmpresa_rubro() {
        return empresa_rubro;
    }

    public void setEmpresa_rubro(String empresa_rubro) {
        this.empresa_rubro = empresa_rubro;
    }

    public String getTipo_entrega_idtipo_entrega() {
        return tipo_entrega_idtipo_entrega;
    }

    public void setTipo_entrega_idtipo_entrega(String tipo_entrega_idtipo_entrega) {
        this.tipo_entrega_idtipo_entrega = tipo_entrega_idtipo_entrega;
    }

    public String getSubrubro() {
        return subrubro;
    }

    public void setSubrubro(String subrubro) {
        this.subrubro = subrubro;
    }

    public List<com.deliveryboss.app.data.api.model.empresa_horario> getEmpresa_horario() {
        return empresa_horario;
    }

    public void setEmpresa_horario(List<com.deliveryboss.app.data.api.model.empresa_horario> empresa_horario) {
        this.empresa_horario = empresa_horario;
    }

    public String getCalificacion_general() {
        return calificacion_general;
    }

    public void setCalificacion_general(String calificacion_general) {
        this.calificacion_general = calificacion_general;
    }

    public String getCantidad_calificacion() {
        return cantidad_calificacion;
    }

    public void setCantidad_calificacion(String cantidad_calificacion) {
        this.cantidad_calificacion = cantidad_calificacion;
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
