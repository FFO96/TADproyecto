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
public class Cliente {
    private ObjectId object_id;
    private String dni;
    private String nombre;
    private String telefono;
    private ArrayList<ElementoSel> reservas;

    public Cliente(ObjectId object_id, String dni, String nombre, String telefono, ArrayList<ElementoSel> reservas) {
        this.object_id = object_id;
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.reservas = reservas;
    }
    
    public Cliente(String dni, String nombre, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
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

    public ArrayList<ElementoSel> getReservas() {
        return reservas;
    }

    public void setReservas(ArrayList<ElementoSel> reservas) {
        this.reservas = reservas;
    }
    
    
}
