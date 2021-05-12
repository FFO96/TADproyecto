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
 * @author Gonzalo
 */
public class Propietario {
   
    private ObjectId object_id;
    private String dni;
    private String nombre;
    private String telefono;
    private String direccion_facturacion;
    private ArrayList<ElementoSel> apartamentos;

    public Propietario(ObjectId object_id, String dni, String nombre, String telefono, String direccion_facturacion, ArrayList<ElementoSel> apartamentos) {
        this.object_id = object_id;
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion_facturacion = direccion_facturacion;
        this.apartamentos = apartamentos;
    }

    public Propietario(String dni, String nombre, String telefono, String direccion_facturacion) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion_facturacion = direccion_facturacion;
    }

    public ObjectId getObject_id() {
        return object_id;
    }

    public void setObjectId(ObjectId object_id) {
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

    public ArrayList<ElementoSel> getApartamentos() {
        return this.apartamentos;
    }

    public void setApartamentos(ArrayList<ElementoSel> apartamentos) {
        this.apartamentos = apartamentos;
    }
}
