/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto.models;

import org.bson.types.ObjectId;

/**
 *
 * @author Fernando
 */
public class Apartamento {
    private ObjectId object_id;
    private String alias;
    private String direccion;
    private String metros2;
    private String precio;
    private ElementoSel propietario;

    public Apartamento(ObjectId object_id, String alias, String direccion, String metros2, String precio, ElementoSel propietario) {
        this.object_id = object_id;
        this.alias = alias;
        this.direccion = direccion;
        this.metros2 = metros2;
        this.precio = precio;
        this.propietario = propietario;
    }
    
    public Apartamento( String alias, String direccion, String metros2, String precio, ElementoSel propietario) {
        this.alias = alias;
        this.direccion = direccion;
        this.metros2 = metros2;
        this.precio = precio;
        this.propietario = propietario;
    }

    public ObjectId getObject_id() {
        return object_id;
    }

    public void setObject_id(ObjectId object_id) {
        this.object_id = object_id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMetros2() {
        return metros2;
    }

    public void setMetros2(String metros2) {
        this.metros2 = metros2;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public ElementoSel getPropietario() {
        return propietario;
    }

    public void setPropietario(ElementoSel propietario) {
        this.propietario = propietario;
    }
    
    
}
