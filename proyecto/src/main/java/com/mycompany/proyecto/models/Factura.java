/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto.models;

import org.bson.types.ObjectId;

/**
 *
 * @author Patricio
 */
public class Factura {
    private ObjectId object_id;
    private String motivo;
    private String importe;
    private ElementoSel propietario;

    public Factura(ObjectId object_id, String motivo, String importe, ElementoSel propietario) {
        this.object_id = object_id;
        this.motivo = motivo;
        this.importe = importe;
        this.propietario = propietario;
    }
    
    public Factura( String motivo, String importe, ElementoSel propietario) {
        this.motivo = motivo;
        this.importe = importe;
        this.propietario = propietario;
    }

    public ObjectId getObject_id() {
        return object_id;
    }

    public void setObject_id(ObjectId object_id) {
        this.object_id = object_id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public ElementoSel getPropietario() {
        return propietario;
    }

    public void setPropietario(ElementoSel propietario) {
        this.propietario = propietario;
    }
    
    
}
