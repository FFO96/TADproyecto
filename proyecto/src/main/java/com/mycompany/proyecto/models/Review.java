/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto.models;

import org.bson.types.ObjectId;
import java.util.ArrayList;

/**
 *
 * @author Fernando
 */
public class Review {
    private ObjectId object_id;
    private String contenido;
    private ElementoSel cliente;
    private ElementoSel apartamento;

    public Review(ObjectId object_id, String contenido, ElementoSel cliente, ElementoSel apartamento) {
        this.object_id = object_id;
        this.contenido = contenido;
        this.cliente = cliente;
        this.apartamento = apartamento;
    }
    
    public Review(String contenido, ElementoSel cliente, ElementoSel apartamento) {
        this.contenido = contenido;
        this.cliente = cliente;
        this.apartamento = apartamento;
    }

    public ObjectId getObject_id() {
        return object_id;
    }

    public void setObject_id(ObjectId object_id) {
        this.object_id = object_id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
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
