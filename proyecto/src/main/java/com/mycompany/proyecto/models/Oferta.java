/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto.models;

import org.bson.types.ObjectId;

/**
 *
 * @author Gonzalo
 */
public class Oferta {
    
    private ObjectId object_id;
    private String precio;
    private String fecha;
    private ElementoSel apartamento;
    private String descuento;

    public Oferta(ObjectId object_id, String precio, String fecha, ElementoSel apartamento, String descuento) {
        this.object_id = object_id;
        this.precio = precio;
        this.fecha = fecha;
        this.apartamento = apartamento;
        this.descuento = descuento;
    }
    
     public Oferta(String precio, String fecha, ElementoSel apartamento, String descuento) {
        this.precio = precio;
        this.fecha = fecha;
        this.apartamento = apartamento;
        this.descuento = descuento;
    }

    public ObjectId getObject_id() {
        return object_id;
    }

    public void setObject_id(ObjectId object_id) {
        this.object_id = object_id;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public ElementoSel getApartamento() {
        return this.apartamento;
    }

    public void setApartamento(ElementoSel apartamento) {
        this.apartamento = apartamento;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }
    
    
}
