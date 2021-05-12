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
import com.vaadin.ui.MenuBar;

/**
 *
 * @author Gonzalo
 */
@Theme("mytheme")
public class ListadoApartamentos extends UI{

    ArrayList<Apartamento> apartamentosList = new ArrayList();
    ArrayList<Propietario> propietariosList = new ArrayList();
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        
        //Menu
        MenuBar barmenu = new MenuBar();
        layout.addComponent(barmenu);
        //Evento para el menu
        MenuBar.Command mycommand = new MenuBar.Command() {
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                getUI().getPage().setLocation("/"+selectedItem.getText().toLowerCase()+"/");
            }  
        };
        
        //Lista del menu
        MenuBar.MenuItem apartamentos = barmenu.addItem("Apartamentos", null, mycommand);
        MenuBar.MenuItem clientes = barmenu.addItem("Clientes", null, mycommand);
        MenuBar.MenuItem propietarios = barmenu.addItem("Propietarios", null, mycommand);
        MenuBar.MenuItem facturas = barmenu.addItem("Facturas", null, mycommand);
        MenuBar.MenuItem limpiadores = barmenu.addItem("Limpiadores", null, mycommand);
        MenuBar.MenuItem reviews = barmenu.addItem("Reviews", null, mycommand);
        MenuBar.MenuItem reservas = barmenu.addItem("Reservas", null, mycommand);
        MenuBar.MenuItem ofertas = barmenu.addItem("Ofertas", null, mycommand);
        MenuBar.MenuItem cerrarSesion = barmenu.addItem("Salir", null, mycommand);
        
        /* Tabla de propietarios*/
        Table tablePropietarios = new Table("Lista de propietarios");
        tablePropietarios.addContainerProperty("DNI", String.class, null);
        tablePropietarios.addContainerProperty("Nombre", String.class, null);
        tablePropietarios.addContainerProperty("Telefono", String.class, null);
        tablePropietarios.addContainerProperty("Direccion facturacion", String.class, null);
        tablePropietarios.setSelectable(true);
        tablePropietarios.setPageLength(tablePropietarios.size());


        // Valores iniciales de la tabla
        Table table = new Table("Lista de apartamentos");
        table.addContainerProperty("Alias", String.class, null);
        table.addContainerProperty("Direccion", String.class, null);
        table.addContainerProperty("Metros", String.class, null);
        table.addContainerProperty("Precio", String.class, null);
        table.addContainerProperty("Propietario", String.class, null);
        table.setSelectable(true);
        table.setPageLength(table.size());
        
        // Se obtienen los propietarios
        propietariosList = listarPropietarios();
        final BeanItemContainer<ElementoSel> containerPropietarios = new BeanItemContainer<ElementoSel>(ElementoSel.class);
        // Añadimos los propietarios al container
        for (int i = 0; i < propietariosList.size(); i++) {
            containerPropietarios.addItem(new ElementoSel(propietariosList.get(i).getObject_id(), propietariosList.get(i).getNombre()));
        }
        
        apartamentosList = listarApartamentos();
        
        // Combobox para seleccionar los propietarios
        ComboBox comboboxPropietarios = new ComboBox("Seleciona un propietario:", containerPropietarios);
        comboboxPropietarios.setContainerDataSource(containerPropietarios);
        comboboxPropietarios.setNullSelectionAllowed(true);
        comboboxPropietarios.setItemCaptionPropertyId("nombre");
        comboboxPropietarios.setNewItemsAllowed(true);
        comboboxPropietarios.setImmediate(true);
        
        // Campos para introducir valores del apartamento
        final TextField aliasEdit = new TextField("Alias del apartamento:");
        final TextField direccionEdit = new TextField("Direccion del apartamento:");
        final TextField metrosEdit = new TextField("Metros del apartamento:");
        final TextField precioEdit = new TextField("Precio del apartamento:");
        
        // Botones de edicion borrado creación y guardar
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar Apartamento");
        Button btnCrear = new Button("Crear Apartamento");
        Button btnGuardar = new Button("Guardar Apartamento");
        
        // Se introducen los apartamentos en la tabla
        for (int i = 0; i < apartamentosList.size(); i++) {
            table.addItem(new Object[]{
                apartamentosList.get(i).getAlias(),
                apartamentosList.get(i).getDireccion(),
                apartamentosList.get(i).getMetros2(),
                apartamentosList.get(i).getPrecio(),
                apartamentosList.get(i).getPropietario().getNombre()
            }, i);
        }
        
        // Si se hace click en la tabla, los campos de texto tomaran los valores del elemento seleccionado
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                aliasEdit.setValue((String) event.getItem().getItemProperty("Alias").getValue());
                direccionEdit.setValue((String) event.getItem().getItemProperty("Direccion").getValue());
                metrosEdit.setValue((String) event.getItem().getItemProperty("Metros").getValue());
                precioEdit.setValue((String) event.getItem().getItemProperty("Precio").getValue());
            }
        });
        
        // Si se pulsa el botón de editar y hay un apartamento seleccionado
        btnEdit.addClickListener(e -> {
            if (apartamentosList.isEmpty() || aliasEdit.getValue() == "" || direccionEdit.getValue() == "" || metrosEdit.getValue()== "" || precioEdit.getValue() == "" || table.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay limpiadores \n o el campo de contenido se encuentra vacio.");
            } else {
                // 
                apartamentosList.get((int) table.getValue()).setAlias(aliasEdit.getValue());
                apartamentosList.get((int) table.getValue()).setDireccion(direccionEdit.getValue());
                apartamentosList.get((int) table.getValue()).setMetros2(metrosEdit.getValue());
                apartamentosList.get((int) table.getValue()).setPrecio(precioEdit.getValue());
                
                editApartamento(apartamentosList.get((int) table.getValue()));
                
                aliasEdit.setValue("");
                direccionEdit.setValue("");
                metrosEdit.setValue("");
                precioEdit.setValue("");
                        
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < apartamentosList.size(); i++) {
                    table.addItem(
                            new Object[]{
                                apartamentosList.get(i).getAlias(),
                                apartamentosList.get(i).getDireccion(),
                                apartamentosList.get(i).getMetros2(),
                                apartamentosList.get(i).getPrecio(),
                                apartamentosList.get(i).getPropietario().getNombre()
                            }, i);
                }
            }
        });
        
        // Listener sobre el boton de borrar el limpiador seleccionado
        btnBorrar.addClickListener(e -> {
            if (table.getValue() == null) {
                Notification.show("No es posible eliminar un limpiador si no ha seleccionado en la tabla");
            } else {
                borrarApartamento(apartamentosList.get((int) table.getValue()));
                aliasEdit.setValue("");
                direccionEdit.setValue("");
                precioEdit.setValue("");
                apartamentosList = listarApartamentos();
                table.removeAllItems();
                for (int i = 0; i < apartamentosList.size(); i++) {
                    table.addItem(
                            new Object[]{
                                apartamentosList.get(i).getAlias(),
                                apartamentosList.get(i).getDireccion(),
                                apartamentosList.get(i).getMetros2(),
                                apartamentosList.get(i).getPrecio(),
                                apartamentosList.get(i).getPropietario().getNombre()
                            }, i);
                }
            }
        });
        
        // Listener sobre el boton de crear un nuevo apartamento 
        btnCrear.addClickListener(event -> {
            aliasEdit.setValue("");
            direccionEdit.setValue("");
            metrosEdit.setValue("");
            precioEdit.setValue("");
            
            layout.removeAllComponents();
            layout.addComponents(aliasEdit, direccionEdit, metrosEdit, precioEdit,comboboxPropietarios , btnGuardar);
        });
        
        // Crear
        btnGuardar.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs
            if (aliasEdit.getValue() == "" || direccionEdit.getValue() == "" || metrosEdit.getValue() == "" || precioEdit.getValue() == "" || comboboxPropietarios.getValue() == null) {
                Notification.show("No es posible crear un limpiador si alguno de los campos se encuentra vacío.");
            } else {
                crearApartamento(new Apartamento(aliasEdit.getValue(), direccionEdit.getValue(), metrosEdit.getValue(), precioEdit.getValue(),(ElementoSel) comboboxPropietarios.getValue()));
                aliasEdit.clear();
                direccionEdit.clear();
                metrosEdit.clear();
                precioEdit.clear();
                layout.removeAllComponents();
                layout.addComponents(btnCrear, table, aliasEdit, direccionEdit, metrosEdit, precioEdit, btnEdit, btnBorrar);
                apartamentosList = listarApartamentos();
                table.removeAllItems();
                for (int i = 0; i < apartamentosList.size(); i++) {
                    table.addItem(
                            new Object[]{
                                apartamentosList.get(i).getAlias(),
                                apartamentosList.get(i).getDireccion(),
                                apartamentosList.get(i).getMetros2(),
                                apartamentosList.get(i).getPrecio(),
                                apartamentosList.get(i).getPropietario().getNombre()
                            }, i);
                }
            }
        });
        
        layout.addComponents(
                table,
                aliasEdit,
                direccionEdit,
                metrosEdit,
                precioEdit,
                btnEdit,
                btnBorrar,
                btnCrear
        );
        
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }
    
    public ArrayList<Apartamento> listarApartamentos() {
        DBCursor cursor = null;
        ArrayList<Apartamento> listaApartamentos = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionC = db.getCollection("apartamentos");
            cursor = collectionC.find();

            DBObject elemento;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                BasicDBObject propietario = (BasicDBObject) elemento.get("propietario");
                String propietarioString = propietario.getString("nombre");
                listaApartamentos.add(
                        new Apartamento(
                                (ObjectId) elemento.get("_id"),
                                (String) elemento.get("alias"),
                                (String) elemento.get("direccion"),
                                (String) elemento.get("metros2"),
                                (String) elemento.get("precio"),
                                new ElementoSel(
                                    (ObjectId) propietario.get("id"),
                                    (String) propietario.get("nombre")
                                )
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

        return listaApartamentos;
    }
    
    public ArrayList<Propietario> listarPropietarios(){
        DBCursor cursor = null;
        ArrayList<Propietario> listaPropietarios = new ArrayList();
        try{
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("propietarios");
            cursor = collectionL.find();
            
            DBObject elemento;
            ArrayList<ElementoSel> apartamentos = null;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                BasicDBList apartamentosList = (BasicDBList) elemento.get("apartamentos");
                apartamentos = new ArrayList();
                if(apartamentosList != null){
                    for (int i = 0; i < apartamentosList.size(); i++) {
                        BasicDBObject apartamento = (BasicDBObject) apartamentosList.get(i);
                        apartamentos.add(new ElementoSel((ObjectId) apartamento.get("id"),apartamento.getString("nombre")));
                    }
                }
                listaPropietarios.add(
                        new Propietario(
                                (ObjectId) elemento.get("_id"),
                                (String) elemento.get("dni"),
                                (String) elemento.get("nombre"),
                                (String) elemento.get("telefono"),
                                (String) elemento.get("direccion_facturacion"),
                                apartamentos
                        )
                );
            }

        } catch(UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaPropietarios;
    }
    
    
    /* OPERACIONES CRUD */
    
    // CREAR
    public void crearApartamento(Apartamento apartamento) {
        DBCursor cursor = null;
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionA = db.getCollection("apartamentos");
         
            BasicDBObject propietario = new BasicDBObject();
            propietario.append("id", apartamento.getPropietario().getId())
                        .append("nombre", apartamento.getPropietario().getNombre());
            BasicDBObject document = new BasicDBObject();
            document.append("alias", apartamento.getAlias())
                    .append("direccion", apartamento.getDireccion())
                    .append("metros2", apartamento.getMetros2())
                    .append("precio", apartamento.getPrecio())
                    .append("propietario", propietario);
            collectionA.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    
    // EDITAR
    public void editApartamento(Apartamento apartamento) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("apartamentos");
            
            BasicDBObject set = new BasicDBObject();
            set = new BasicDBObject();
            
            set.append("$set", new BasicDBObject()
                    .append("alias", apartamento.getAlias())
                    .append("direccion", apartamento.getDireccion())
                    .append("metros2", apartamento.getMetros2())
                    .append("precio", apartamento.getPrecio())
            );
            
            BasicDBObject searchQuery = new BasicDBObject()
                    .append("_id", apartamento.getObject_id());
            
            collectionL.update(searchQuery, set);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    // BORRAR
    public void borrarApartamento(Apartamento apartamento) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("apartamentos");
            collectionL.remove(new BasicDBObject("_id", apartamento.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    @WebServlet(urlPatterns = "/apartamentos/*", name = "ListadoApartamentosServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoApartamentos.class, productionMode = false)
    public static class ListadoApartamentosServlet extends VaadinServlet {
    }
}
