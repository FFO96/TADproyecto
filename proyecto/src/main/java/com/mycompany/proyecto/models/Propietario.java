/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto.models;

import org.bson.types.ObjectId;

/**
 *
 * @author pguerra
 */
public class Propietario {
    private ObjectId object_id;
    private String dni;
    private String nombre;
    private String telefono;
    private String direccion_facturacion;
    private ElementoSel propietario;

    public Propietario(ObjectId object_id, String dni, String nombre, String telefono, String direccion_facturacion, ElementoSel propietario) {
        this.object_id = object_id;
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion_facturacion = direccion_facturacion;
        this.propietario = propietario;
    }

    public Propietario(String dni, String nombre, String telefono, String direccion_facturacion, ElementoSel propietario) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion_facturacion = direccion_facturacion;
        this.propietario = propietario;
    }

    public ObjectId getObject_id() {
        return object_id;
    }

    public void setObject_id(ObjectId object_id) {
        this.object_id = object_id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion_facturacion() {
        return direccion_facturacion;
    }

    public void setDireccion_facturacion(String direccion_facturacion) {
        this.direccion_facturacion = direccion_facturacion;
    }

    public ElementoSel getPropietario() {
        return propietario;
    }

    public void setPropietario(ElementoSel propietario) {
        this.propietario = propietario;
    }

    
}
