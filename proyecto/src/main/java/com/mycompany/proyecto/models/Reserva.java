/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto.models;

import java.util.ArrayList;
import org.bson.types.ObjectId;

/**
 *
 * @author pguerra
 */
public class Reserva {
    private ObjectId object_id;
    private String fecha;
    private String precio;
    private ElementoSel cliente;
    private ElementoSel apartamento;

    public Reserva(ObjectId object_id, String fecha, String precio, String nombre, ElementoSel cliente, ElementoSel apartamento) {
        this.object_id = object_id;
        this.fecha = fecha;
        this.precio = precio;
        this.cliente = cliente;
        this.apartamento = apartamento;
    }

    public ObjectId getObject_id() {
        return object_id;
    }

    public void setObject_id(ObjectId object_id) {
        this.object_id = object_id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public ElementoSel getCliente() {
        return cliente;
    }

    public void setCliente(ElementoSel cliente) {
        this.cliente = cliente;
    }

    public ElementoSel getApartamento() {
        return apartamento;
    }

    public void setApartamento(ElementoSel apartamento) {
        this.apartamento = apartamento;
    }

}
