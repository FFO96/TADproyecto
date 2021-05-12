/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import java.util.ArrayList;
import com.mycompany.proyecto.models.Apartamento;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import javax.servlet.annotation.WebServlet;
import com.mycompany.proyecto.models.ElementoSel;
import com.mycompany.proyecto.models.Propietario;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import java.net.UnknownHostException;
import org.bson.types.ObjectId;
import com.mycompany.proyecto.models.ElementoSel;
import com.mycompany.proyecto.models.Review;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mycompany.proyecto.models.Apartamento;
import com.mycompany.proyecto.models.Cliente;
import com.mycompany.proyecto.models.Limpiador;
import com.mycompany.proyecto.models.Oferta;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.bson.types.ObjectId;

/**
 *
 * @author Gonzalo
 */
@Theme("mytheme")
public class ListadoOfertas extends UI {

    ArrayList<Oferta> ofertas = new ArrayList();
    ArrayList<Apartamento> apartamentos = new ArrayList();

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        final VerticalLayout layout = new VerticalLayout();
        /* Tabla de ofertas */
        Table table = new Table("Lista de ofertas");
        table.addContainerProperty("Precio", String.class, null);
        table.addContainerProperty("Fecha", String.class, null);
        table.addContainerProperty("Apartamento", String.class, null);
        table.addContainerProperty("Descuento", String.class, null);
        
        table.setSelectable(true);
        table.setPageLength(table.size());

        /* ComboBox de apartamentos */
        apartamentos = listarApartamentos();
        final BeanItemContainer<ElementoSel> containerApartamentos = new BeanItemContainer<ElementoSel>(ElementoSel.class);
        for (int i = 0; i < apartamentos.size(); i++) {
            containerApartamentos.addItem(new ElementoSel(apartamentos.get(i).getObject_id(), apartamentos.get(i).getAlias()));
        }
        ComboBox comboboxApartamentos = new ComboBox("Seleciona un apartamento:", containerApartamentos);
        comboboxApartamentos.setContainerDataSource(containerApartamentos);
        comboboxApartamentos.setNullSelectionAllowed(true);
        comboboxApartamentos.setItemCaptionPropertyId("nombre");
        comboboxApartamentos.setNewItemsAllowed(true);
        comboboxApartamentos.setImmediate(true);

        /* Botones y campos */
        final TextField precioEdit = new TextField("Precio:");
        final TextField fechaEdit = new TextField("Fecha:");
        final TextField descuentoEdit = new TextField("Descuento:");
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar oferta");
        Button btnCrear = new Button("Crear oferta");
        Button btnGuardar = new Button("Guardar oferta");

        ofertas = listarOfertas();
        for (int i = 0; i < ofertas.size(); i++) {
            table.addItem(new Object[]{
                ofertas.get(i).getPrecio(),
                ofertas.get(i).getFecha(),
                ofertas.get(i).getApartamento().getNombre(),
                ofertas.get(i).getDescuento()
            }, i);
        }
        
        // Listener campos edit
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                precioEdit.setValue((String) event.getItem().getItemProperty("Precio").getValue());
                fechaEdit.setValue((String) event.getItem().getItemProperty("Fecha").getValue());
                descuentoEdit.setValue((String) event.getItem().getItemProperty("Descuento").getValue());
            }
        });
        
        // Listener boton edit
        btnEdit.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs y que se ha seleccionado un elemento de la tabla para poder guardar los cambios
            if (ofertas.isEmpty() || precioEdit.getValue() == "" || fechaEdit.getValue() == "" || descuentoEdit.getValue() == "" || table.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay ofertas \n o el campo de contenido se encuentra vacio.");
            } else {
                ofertas.get((int) table.getValue()).setPrecio(precioEdit.getValue());
                ofertas.get((int) table.getValue()).setFecha(fechaEdit.getValue());
                ofertas.get((int) table.getValue()).setDescuento(descuentoEdit.getValue());
                
                editOferta(ofertas.get((int) table.getValue()));
                
                precioEdit.setValue("");
                fechaEdit.setValue("");
                descuentoEdit.setValue("");
                
                table.removeAllItems();
                for (int i = 0; i < ofertas.size(); i++) {
                    table.addItem(new Object[]{
                        ofertas.get(i).getPrecio(),
                        ofertas.get(i).getFecha(),
                        ofertas.get(i).getApartamento().getNombre(),
                        ofertas.get(i).getDescuento()
                    }, i);
                }
            }
        });
        
        btnBorrar.addClickListener(e -> {

            if (table.getValue() == null) {
                Notification.show("No es posible eliminar un limpiador si no ha seleccionado en la tabla");
            } else {
                borrarOferta(ofertas.get((int) table.getValue()));
                precioEdit.setValue("");
                fechaEdit.setValue("");
                descuentoEdit.setValue("");
                
                ofertas = listarOfertas();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < ofertas.size(); i++) {
                    table.addItem(new Object[]{
                        ofertas.get(i).getPrecio(),
                        ofertas.get(i).getFecha(),
                        ofertas.get(i).getApartamento().getNombre(),
                        ofertas.get(i).getDescuento(),
                    }, i);
                }
            }
        });
        
        // Listener sobre el boton de crear un nuevo limpiador 
        btnCrear.addClickListener(event -> {
            precioEdit.setValue("");
            fechaEdit.setValue("");
            descuentoEdit.setValue("");
            layout.removeAllComponents();
            layout.addComponents(precioEdit, fechaEdit, descuentoEdit, comboboxApartamentos, btnGuardar);
        });
        
        btnGuardar.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs
            if (precioEdit.getValue() == "" || fechaEdit.getValue() == "" ||  descuentoEdit.getValue() == "" || comboboxApartamentos.getValue() == null) {
                Notification.show("No es posible crear un limpiador si alguno de los campos se encuentra vacío.");
            } else {
                crearOferta(new Oferta(
                        precioEdit.getValue(), 
                        fechaEdit.getValue(), 
                        (ElementoSel) comboboxApartamentos.getValue(),
                        descuentoEdit.getValue()
                ));
                
                precioEdit.clear();
                fechaEdit.clear();
                descuentoEdit.clear();
                
                layout.removeAllComponents();
                layout.addComponents(btnCrear, table, precioEdit, fechaEdit, descuentoEdit, btnEdit, btnBorrar);
                // actualizamos la lista de limpiadores
                ofertas = listarOfertas();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < ofertas.size(); i++) {
                    table.addItem(new Object[]{
                        ofertas.get(i).getPrecio(),
                        ofertas.get(i).getFecha(),
                        ofertas.get(i).getApartamento().getNombre(),
                        ofertas.get(i).getDescuento()
                    }, i);
                }
            }
        });

        layout.addComponents(btnCrear, table, precioEdit, fechaEdit, descuentoEdit,btnEdit, btnBorrar);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

    }

    // LEER
    public ArrayList<Apartamento> listarApartamentos() {
        DBCursor cursor = null;
        ArrayList<Apartamento> listaApartamentos = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionA = db.getCollection("apartamentos");
            cursor = collectionA.find();

            DBObject elemento;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                listaApartamentos.add(new Apartamento((ObjectId) elemento.get("_id"), (String) elemento.get("alias"), (String) elemento.get("direccion"), (String) elemento.get("metros2"), (String) elemento.get("precio"), new ElementoSel((ObjectId) elemento.get("propietario.id"), (String) elemento.get("nombre"))));
            }

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaApartamentos;
    }

    public ArrayList<Oferta> listarOfertas() {
        DBCursor cursor = null;
        ArrayList<Oferta> listaOfertas = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("ofertas");
            cursor = collectionL.find();

            DBObject elemento;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                BasicDBObject apartamento = (BasicDBObject) elemento.get("apartamento");
                listaOfertas.add(new Oferta(
                        (ObjectId) elemento.get("_id"),
                        (String) elemento.get("precio"),
                        (String) elemento.get("fecha"),
                        new ElementoSel(
                                (ObjectId) apartamento.get("id"),
                                apartamento.getString("nombre")
                        ),
                        (String) elemento.get("descuento")
                )
                );
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaOfertas;
    }

    // EDITAR
    public void editOferta(Oferta oferta) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("ofertas");
            BasicDBObject set = new BasicDBObject();
            set = new BasicDBObject();
            
            set.append("$set", new BasicDBObject()
                    .append("precio", oferta.getPrecio())
                    .append("fecha", oferta.getFecha()));
           
            BasicDBObject searchQuery = new BasicDBObject().append("_id", oferta.getObject_id());
            
            collectionL.update(searchQuery, set);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    // BORRAR
    public void borrarOferta(Oferta oferta) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("ofertas");

            collectionL.remove(new BasicDBObject("_id", oferta.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    // CREAR
    public void crearOferta(Oferta oferta) {
        DBCursor cursor = null;
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionA = db.getCollection("ofertas");
            
            
            BasicDBObject apartamento = new BasicDBObject();
            apartamento.append("id", oferta.getApartamento().getId())
                        .append("nombre", oferta.getApartamento().getNombre());
            
            System.out.println(oferta.getApartamento().getNombre());
            
            BasicDBObject document = new BasicDBObject();
            document.append("precio", oferta.getPrecio())
                    .append("fecha", oferta.getFecha())
                    .append("apartamento", apartamento)
                    .append("descuento", oferta.getDescuento()
                    );

            collectionA.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    
    @WebServlet(urlPatterns = "/ofertas/*", name = "ListadoOfertasServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoOfertas.class, productionMode = false)
    public static class ListadoOfertasServlet extends VaadinServlet {
    }

}
