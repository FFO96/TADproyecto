/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto;
import com.mycompany.proyecto.models.ElementoSel;
import com.mycompany.proyecto.models.Cliente;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mycompany.proyecto.models.Propietario;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
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
public class ListadoPropietarios extends UI{

    ArrayList<Propietario> propietariosList = new ArrayList();
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //sesion
        VaadinSession vSession = VaadinSession.getCurrent();
        WrappedSession httpSession = vSession.getSession();
        if(httpSession.getAttribute("nombre")==null){
            getUI().getPage().setLocation("/");
        }
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
        
        /* Tabla de apartamentos */
        Table tableApartamentos = new Table("Apartamentos del propietario");
        tableApartamentos.addContainerProperty("Apartamentos", String.class, null);
        tableApartamentos.setSelectable(true);
        tableApartamentos.setPageLength(tableApartamentos.size());
        
        /* Campos de texto y botones */
        final TextField dniEdit = new TextField("Dni del propietario:");
        final TextField nombreEdit = new TextField("Nombre del propietario:");
        final TextField telefonoEdit = new TextField("Telefono del propietario:");
        final TextField direccionEdit = new TextField("Direccion del propietario:");
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar propietario");
        Button btnCrear = new Button("Crear propietario");
        Button btnGuardar = new Button("Guardar propietario");
        
        propietariosList = listarPropietarios();
        for (int i = 0; i < propietariosList.size(); i++) {
            tablePropietarios.addItem(new Object[]{
                propietariosList.get(i).getDni(),
                propietariosList.get(i).getNombre(),
                propietariosList.get(i).getTelefono(),
                propietariosList.get(i).getDireccion_facturacion()
            }, i);
        }
        
        /* Listener tabla de propietarios y mostrar apartamentos */
        tablePropietarios.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                tableApartamentos.removeAllItems();
                
                dniEdit.setValue((String) event.getItem().getItemProperty("DNI").getValue());
                nombreEdit.setValue((String) event.getItem().getItemProperty("Nombre").getValue());
                telefonoEdit.setValue((String) event.getItem().getItemProperty("Telefono").getValue());
                direccionEdit.setValue((String) event.getItem().getItemProperty("Direccion facturacion").getValue());

                Propietario propietario = propietariosList.get((int) event.getItemId());
                for (int i = 0; i < propietario.getApartamentos().size(); i++) {
                    tableApartamentos.addItem(new Object[]{
                        propietario.getApartamentos().get(i).getNombre()
                    }, i);
                }
                layout.addComponent(tableApartamentos);
            }
        });
        
        // Listener editar
        btnEdit.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs y que se ha seleccionado un elemento de la tabla para poder guardar los cambios
            if (propietariosList.isEmpty() || dniEdit.getValue() == "" || nombreEdit.getValue() == "" || telefonoEdit.getValue() == "" || direccionEdit.getValue() == "" || tablePropietarios.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay clientes \n o alguno de los campos se encuentra vacío.");
            } else {
                // Se cambian los datos introducidos en el propietario correspondiente en la lista
                propietariosList.get((int) tablePropietarios.getValue()).setDni(dniEdit.getValue());
                propietariosList.get((int) tablePropietarios.getValue()).setNombre(nombreEdit.getValue());
                propietariosList.get((int) tablePropietarios.getValue()).setTelefono(telefonoEdit.getValue());
                propietariosList.get((int) tablePropietarios.getValue()).setDireccion_facturacion(direccionEdit.getValue());

                editPropietario(propietariosList.get((int) tablePropietarios.getValue()));
                
                dniEdit.setValue("");
                nombreEdit.setValue("");
                telefonoEdit.setValue("");
                direccionEdit.setValue("");
                //tableReservas.removeAllItems();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                tablePropietarios.removeAllItems();
                for (int i = 0; i < propietariosList.size(); i++) {
                    tablePropietarios.addItem(new Object[]{
                        propietariosList.get(i).getDni(),
                        propietariosList.get(i).getNombre(),
                        propietariosList.get(i).getTelefono(),
                        propietariosList.get(i).getDireccion_facturacion()
                    }, i);
                }
                //layout.removeComponent(tableReservas);
            }
        });
        
        // Listener borrar
        btnBorrar.addClickListener(e -> {
            if (tablePropietarios.getValue() == null) {
                Notification.show("No es posible eliminar un propietario si no ha seleccionado en la tabla");
            } else {
                borrarPropietario(propietariosList.get((int) tablePropietarios.getValue()));
                
                dniEdit.setValue("");
                nombreEdit.setValue("");
                telefonoEdit.setValue("");
                direccionEdit.setValue("");
                
                //layout.removeComponent(tableReservas);
                
                propietariosList = listarPropietarios();
                tablePropietarios.removeAllItems();
                for (int i = 0; i < propietariosList.size(); i++) {
                    tablePropietarios.addItem(new Object[]{
                        propietariosList.get(i).getDni(),
                        propietariosList.get(i).getNombre(),
                        propietariosList.get(i).getTelefono(),
                        propietariosList.get(i).getDireccion_facturacion()
                    }, i);
                }
            }
        });
        
        // Listener sobre el boton de crear un nuevo cliente 
        btnCrear.addClickListener(e -> {
            dniEdit.setValue("");
            nombreEdit.setValue("");
            telefonoEdit.setValue("");
            direccionEdit.setValue("");
            
            layout.removeAllComponents();
            layout.addComponents(dniEdit, nombreEdit, telefonoEdit, direccionEdit, btnGuardar);
        });
        btnGuardar.addClickListener(e -> {
            if (dniEdit.getValue() == "" || nombreEdit.getValue() == "" || telefonoEdit.getValue() == "" || direccionEdit.getValue() == "" ) {
                Notification.show("No es posible crear un propietario si alguno de los campos se encuentra vacío.");
            } else {
                crearPropietario(new Propietario(dniEdit.getValue(), nombreEdit.getValue(), telefonoEdit.getValue(), direccionEdit.getValue()));
                
                dniEdit.setValue("");
                nombreEdit.setValue("");
                telefonoEdit.setValue("");
                direccionEdit.setValue("");
                
                layout.removeAllComponents();
                layout.addComponents(btnCrear, tablePropietarios, nombreEdit, dniEdit, telefonoEdit, direccionEdit, btnEdit, btnBorrar);
                
                propietariosList = listarPropietarios();
                
                tablePropietarios.removeAllItems();
                for (int i = 0; i < propietariosList.size(); i++) {
                    tablePropietarios.addItem(new Object[]{
                        propietariosList.get(i).getDni(),
                        propietariosList.get(i).getNombre(),
                        propietariosList.get(i).getTelefono(),
                        propietariosList.get(i).getDireccion_facturacion()
                    }, i);
                }
            }
        });
        
        layout.addComponents(btnCrear, tablePropietarios, nombreEdit, dniEdit, telefonoEdit, direccionEdit, btnEdit, btnBorrar);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }
    
    // LEER
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
                        apartamentos.add(new ElementoSel((ObjectId) apartamento.get("id"),apartamento.getString("alias")));
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
        
    // EDITAR
    public void editPropietario(Propietario propietario) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("propietarios");
            
            BasicDBObject set = new BasicDBObject();
            set = new BasicDBObject();
            
            set.append("$set", new BasicDBObject()
                    .append("dni", propietario.getDni())
                    .append("nombre", propietario.getNombre())
                    .append("telefono", propietario.getTelefono())
                    .append("direccion_facturacion", propietario.getDireccion_facturacion())
            );
            
            BasicDBObject searchQuery = new BasicDBObject()
                    .append("_id", propietario.getObject_id());
            
            collectionL.update(searchQuery, set);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    // BORRAR
    public void borrarPropietario(Propietario propietario) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionC = db.getCollection("propietarios");

            collectionC.remove(new BasicDBObject("_id", propietario.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    // Crear
    public void crearPropietario(Propietario propietario) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionC = db.getCollection("propietarios");

            BasicDBObject document;
            document = new BasicDBObject();
            document.append("dni", propietario.getDni());
            document.append("nombre", propietario.getNombre());
            document.append("telefono", propietario.getTelefono());
            document.append("direccion_facturacion", propietario.getDireccion_facturacion());
            
            collectionC.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    @WebServlet(urlPatterns = "/propietarios/*", name = "ListadoPropietariosServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoPropietarios.class, productionMode = false)
    public static class ListadoPropietariosServlet extends VaadinServlet {
    }
    
}
