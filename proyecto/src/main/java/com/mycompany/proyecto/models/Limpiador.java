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
 * @author Fernando
 */
public class Limpiador {
    private ObjectId object_id;
    private String dni;
    private String nombre;
    private String telefono;
    private ElementoSel apartamento;

    public Limpiador(ObjectId object_id, String dni, String nombre, String telefono, ElementoSel apartamento) {
        this.object_id = object_id;
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.apartamento = apartamento;
    }
    
    public Limpiador(String dni, String nombre, String telefono, ElementoSel apartamento) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.apartamento = apartamento;
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

    public ElementoSel getApartamento() {
        return apartamento;
    }

    public void setApartamento(ElementoSel apartamento) {
        this.apartamento = apartamento;
    }
    
    
    
}
