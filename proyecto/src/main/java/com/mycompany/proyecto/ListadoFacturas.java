/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mycompany.proyecto.models.Apartamento;
import com.mycompany.proyecto.models.Cliente;
import com.mycompany.proyecto.models.ElementoSel;
import com.mycompany.proyecto.models.Factura;
import com.mycompany.proyecto.models.Limpiador;
import com.mycompany.proyecto.models.Propietario;
import com.mycompany.proyecto.models.Reserva;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import org.bson.types.ObjectId;

/**
 *
 * @author Patricio
 */
@Theme("mytheme")
public class ListadoFacturas extends UI{
    //Lista de las reviews
    ArrayList<Factura> facturasList = new ArrayList();
    //Lista de los clientes
    ArrayList<Propietario> propietariosList = new ArrayList();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        Table table = new Table("Lista de facturas");
        table.addContainerProperty("Motivo", String.class, null);
        table.addContainerProperty("Importe", String.class, null);
        table.addContainerProperty("Propietario", String.class, null);


        //Hacemos que sea seleccionable
        table.setSelectable(true);
        table.setPageLength(table.size());

        propietariosList = listarPropietarios();
        final BeanItemContainer<ElementoSel> containerPropietarios = new BeanItemContainer<ElementoSel>(ElementoSel.class);
        
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
        MenuBar.MenuItem cerrarSesion = barmenu.addItem("Salir", null, mycommand);

        // Añadimos los limpiadores al container
        for (int i = 0; i < propietariosList.size(); i++) {
            containerPropietarios.addItem(new ElementoSel(propietariosList.get(i).getObject_id(), propietariosList.get(i).getDni()));
        }
        // Creamos un combobox de los apartamentos
        ComboBox comboboxPropietarios = new ComboBox("Seleciona un Propietario:", containerPropietarios);

        comboboxPropietarios.setContainerDataSource(containerPropietarios);
        comboboxPropietarios.setNullSelectionAllowed(true);
        // El atributo que se mostrará en el combobox será el alias del apartamento
        comboboxPropietarios.setItemCaptionPropertyId("nombre");
        comboboxPropietarios.setNewItemsAllowed(true);
        comboboxPropietarios.setImmediate(true);

        final TextField motivoEdit = new TextField("Motivo:");
        final TextField importeEdit = new TextField("Importe:");
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar factura");
        Button btnCrear = new Button("Crear factura");
        Button btnGuardar = new Button("Guardar factura");

        facturasList = listarFacturas();
        // Se completa la tabla si hay limpiadores en la lista
        for (int i = 0; i < facturasList.size(); i++) {
            table.addItem(new Object[]{facturasList.get(i).getMotivo(), facturasList.get(i).getImporte(), facturasList.get(i).getPropietario().getNombre()}, i);
        }

        // Listener sobre la tabla para una vez que se pulse sobre una fila de la tabla, el contenido del limpiador aparezcan en los inputs para poder editarlo
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                motivoEdit.setValue((String) event.getItem().getItemProperty("Motivo").getValue());
                importeEdit.setValue((String) event.getItem().getItemProperty("Importe").getValue());
            }
        });

        // Listener sobre el boton de edición del limpiador seleccionado en la tabla
        btnEdit.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs y que se ha seleccionado un elemento de la tabla para poder guardar los cambios
            if (facturasList.isEmpty() || motivoEdit.getValue() == "" || importeEdit.getValue() == "" || table.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay limpiadores \n o el campo de contenido se encuentra vacio.");
            } else {
                // Se cambian los datos introducidos en el limpiador correspondiente en la lista
                facturasList.get((int) table.getValue()).setMotivo(motivoEdit.getValue());
                facturasList.get((int) table.getValue()).setImporte(importeEdit.getValue());

                editFactura(facturasList.get((int) table.getValue()));
                // Se limpian los inputs del formulario de edición
                motivoEdit.setValue("");
                importeEdit.setValue("");
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < facturasList.size(); i++) {
                    table.addItem(new Object[]{facturasList.get(i).getMotivo(), facturasList.get(i).getImporte(), facturasList.get(i).getPropietario().getNombre()}, i);
                }
            }

        });

        // Listener sobre el boton de borrar el limpiador seleccionado
        btnBorrar.addClickListener(e -> {

            if (table.getValue() == null) {
                Notification.show("No es posible eliminar una factura si no ha seleccionado en la tabla");
            } else {
                borrarFactura(facturasList.get((int) table.getValue()));
                motivoEdit.setValue("");
                importeEdit.setValue("");
                // actualizamos la lista de reviews
                facturasList = listarFacturas();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < facturasList.size(); i++) {
                    table.addItem(new Object[]{facturasList.get(i).getMotivo(), facturasList.get(i).getImporte(), facturasList.get(i).getPropietario().getNombre()}, i);
                }
            }
        });

        // Listener sobre el boton de crear un nuevo limpiador 
        btnCrear.addClickListener(event -> {
            motivoEdit.setValue("");
            importeEdit.setValue("");
            layout.removeAllComponents();
            layout.addComponents(motivoEdit, importeEdit, comboboxPropietarios, btnGuardar);
        });

        btnGuardar.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs
            if (motivoEdit.getValue() == "" || importeEdit.getValue() == "" || comboboxPropietarios.getValue() == null) {
                Notification.show("No es posible crear una factura si alguno de los campos se encuentra vacío.");
            } else {

                crearFactura(new Factura(motivoEdit.getValue(), importeEdit.getValue(), (ElementoSel) comboboxPropietarios.getValue()));
                motivoEdit.clear();
                importeEdit.clear();
                layout.removeAllComponents();
                layout.addComponents(btnCrear, table, motivoEdit, importeEdit, btnEdit, btnBorrar);
                // actualizamos la lista de limpiadores
                facturasList = listarFacturas();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < facturasList.size(); i++) {
                    table.addItem(new Object[]{facturasList.get(i).getMotivo(), facturasList.get(i).getImporte(), facturasList.get(i).getPropietario().getNombre()}, i);
                }
                //getUI().getPage().setLocation("/ListadoReviews/");

            }
        });

        layout.addComponents(btnCrear, table, motivoEdit, importeEdit, btnEdit, btnBorrar);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    public void crearFactura(Factura factura) {
        DBCursor cursor = null;
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionF = db.getCollection("facturas");
            DBCollection collectionP = db.getCollection("propietarios");

            // Creamos la query para buscar el apartamento segun su _id
            BasicDBObject queryA = new BasicDBObject();
            queryA.put("_id", factura.getPropietario().getId());
            // Busqueda del apartamento para poder introducir su alias y direccion en el limpiador
            DBObject propietario = collectionP.findOne(queryA);

            BasicDBObject document = new BasicDBObject();
            // Creamos los objetos DB para apartamento que vamos a añadir al limpiador
            BasicDBObject propietarioAux = new BasicDBObject();
            propietarioAux.append("id", propietario.get("_id")).append("dni", propietario.get("dni")).append("nombre", propietario.get("nombre"));
            document.append("motivo", factura.getMotivo()).append("importe", factura.getImporte()).append("propietario", propietarioAux);
            collectionF.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void borrarFactura(Factura factura) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionF = db.getCollection("facturas");

            // Se elimina el limpiador usando el _id para indentificarlo en la coleccion
            collectionF.remove(new BasicDBObject("_id", factura.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void editFactura(Factura factura) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "limpiadores"
            DBCollection collectionL = db.getCollection("facturas");

            BasicDBObject set = new BasicDBObject(); // Objeto con el que vamosa hacer set del atributo a cambiar
            set = new BasicDBObject();
            // Indica el atributo y su valor a establecer ($set)
            set.append("$set", new BasicDBObject().append("motivo", factura.getMotivo()).append("importe", factura.getImporte()));
            // Indica el filtro a usar para aplicar la modificacion
            BasicDBObject searchQuery = new BasicDBObject().append("_id", factura.getObject_id());
            //Se hace el update sobre la coleccion
            collectionL.update(searchQuery, set);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public ArrayList<Factura> listarFacturas() {
        DBCursor cursor = null;
        ArrayList<Factura> listaFacturas = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "limpiadores"
            DBCollection collectionF = db.getCollection("facturas");
            cursor = collectionF.find();

            DBObject elemento;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                BasicDBObject propietario = (BasicDBObject) elemento.get("propietario");
                String propietarioString = propietario.getString("nombre") + " con Dni " + propietario.getString("dni");
                listaFacturas.add(new Factura((ObjectId) elemento.get("_id"), (String) elemento.get("motivo"), (String) elemento.get("importe"), new ElementoSel((ObjectId) propietario.get("id"), propietarioString)));
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaFacturas;
    }

    public ArrayList<Propietario> listarPropietarios() {
        DBCursor cursor = null;
        ArrayList<Propietario> listaPropietarios = new ArrayList();
        ArrayList<ElementoSel> listaPropApart = new ArrayList();

        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "apartamentos"
            DBCollection collectionP = db.getCollection("propietarios");
            cursor = collectionP.find();

            DBObject elemento;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                listaPropApart.add(new ElementoSel((ObjectId) elemento.get("apartamento.id")));
                listaPropietarios.add(new Propietario((ObjectId) elemento.get("_id"), (String) elemento.get("dni"), (String) elemento.get("nombre"), (String) elemento.get("telefono"), (String) elemento.get("direccion_facturacion"),listaPropApart));
            }

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaPropietarios;
    }

    @WebServlet(urlPatterns = "/facturas/*", name = "ListadoFacturasServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoFacturas.class, productionMode = false)
    public static class ListadoFacturasServlet extends VaadinServlet {
    }
}
