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
import com.mycompany.proyecto.models.Reserva;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.bson.types.ObjectId;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class ListadoReservas extends UI {

    //Lista de los reservas
    ArrayList<Cliente> ReservasList = new ArrayList();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        Table table = new Table("Lista de reservas");
        table.addContainerProperty("DNI", String.class, null);
        table.addContainerProperty("Nombre y apellidos", String.class, null);
        table.addContainerProperty("Telefono", String.class, null);
        //Hacemos que sea seleccionable
        table.setSelectable(true);
        table.setPageLength(table.size());

        Table tableReservas = new Table("Clientes de la reserva");
        tableReservas.addContainerProperty("Reserva", String.class, null);
        //Hacemos que sea seleccionable
        tableReservas.setSelectable(true);
        tableReservas.setPageLength(table.size());

        final TextField fechaEdit = new TextField("Fecha:");
        final TextField precioEdit = new TextField("Precio:");
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar reserva");
        Button btnCrear = new Button("Crear reserva");
        Button btnGuardar = new Button("Guardar reserva");

        ReservasList = listarClientes();
        // Se completa la tabla si hay reservas en la lista
        for (int i = 0; i < ReservasList.size(); i++) {
            table.addItem(new Object[]{ReservasList.get(i).getDni(), ReservasList.get(i).getNombre(), ReservasList.get(i).getTelefono()}, i);
        }

        // Listener sobre la tabla para una vez que se pulse sobre una fila de la tabla, los datos del reserva aparezcan en los inputs para poder editarlos
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                tableReservas.removeAllItems();
                fechaEdit.setValue((String) event.getItem().getItemProperty("fecha").getValue());
                precioEdit.setValue((String) event.getItem().getItemProperty("precio").getValue());

                Cliente reserva = ReservasList.get((int) event.getItemId());
                for (int i = 0; i < reserva.getReservas().size(); i++) {
                    tableReservas.addItem(new Object[]{reserva.getReservas().get(i).getNombre()}, i);
                }
                layout.addComponent(tableReservas);
            }
        });

        // Listener sobre el boton de edición del reserva seleccionado en la tabla
        btnEdit.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs y que se ha seleccionado un elemento de la tabla para poder guardar los cambios
            if (ReservasList.isEmpty() || fechaEdit.getValue() == "" || precioEdit.getValue() == "" || table.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay reservas \n o alguno de los campos se encuentra vacío.");
            } else {
                // Se cambian los datos introducidos en el propietario correspondiente en la lista

                ReservasList.get((int) table.getValue()).setNombre(fechaEdit.getValue());
                ReservasList.get((int) table.getValue()).setTelefono(precioEdit.getValue());

                editCliente(ReservasList.get((int) table.getValue()));
                // Se limpian los inputs del formulario de edición
                fechaEdit.setValue("");
                precioEdit.setValue("");
                tableReservas.removeAllItems();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < ReservasList.size(); i++) {
                    table.addItem(new Object[]{ReservasList.get(i).getDni(), ReservasList.get(i).getNombre(), ReservasList.get(i).getTelefono()}, i);
                }
                layout.removeComponent(tableReservas);
            }

        });

        // Listener sobre el boton de borrar el reserva seleccionado
        btnBorrar.addClickListener(e -> {

            if (table.getValue() == null) {
                Notification.show("No es posible eliminar un reserva si no ha seleccionado en la tabla");
            } else {
                borrarCliente(ReservasList.get((int) table.getValue()));
                fechaEdit.setValue("");
                precioEdit.setValue("");
                layout.removeComponent(tableReservas);
                // actualizamos la lista de reservas
                ReservasList = listarClientes();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < ReservasList.size(); i++) {
                    table.addItem(new Object[]{ReservasList.get(i).getDni(), ReservasList.get(i).getNombre(), ReservasList.get(i).getTelefono()}, i);
                }
                
            }

        });

        // Listener sobre el boton de crear un nuevo reserva 
        btnCrear.addClickListener(e -> {
            fechaEdit.setValue("");
            precioEdit.setValue("");
            layout.removeAllComponents();
            layout.addComponents(fechaEdit, precioEdit, btnGuardar);
        });
        btnGuardar.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs
            if (fechaEdit.getValue() == "" || precioEdit.getValue() == "") {
                Notification.show("No es posible crear un reserva si alguno de los campos se encuentra vacío.");
            } else {
                //crearReserva(new Reserva( fechaEdit.getValue(), precioEdit.getValue()));
                fechaEdit.setValue("");
                precioEdit.setValue("");
                layout.removeAllComponents();
                layout.addComponents(btnCrear, table, fechaEdit, precioEdit, btnEdit, btnBorrar);
                // actualizamos la lista de reservas
                ReservasList = listarClientes();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < ReservasList.size(); i++) {
                    table.addItem(new Object[]{ReservasList.get(i).getDni(), ReservasList.get(i).getNombre(), ReservasList.get(i).getTelefono()}, i);
                }
            }
        });

        layout.addComponents(btnCrear, table, fechaEdit, precioEdit, btnEdit, btnBorrar);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    public void crearCliente(Cliente reserva) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionC = db.getCollection("reservas");

            BasicDBObject document;
            document = new BasicDBObject();
            document.append("dni", reserva.getDni());
            document.append("nombre", reserva.getNombre());
            document.append("telefono", reserva.getTelefono());
            collectionC.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void borrarCliente(Cliente reserva) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionC = db.getCollection("reservas");

            // Se elimina el reserva cusando el _id para indentificarlo en la coleccion
            collectionC.remove(new BasicDBObject("_id", reserva.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void editCliente(Cliente reserva) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "reservas"
            DBCollection collectionC = db.getCollection("reservas");

            BasicDBObject set = new BasicDBObject(); // Objeto con el que vamosa hacer set del atributo a cambiar
            set = new BasicDBObject();
            // Indica el atributo y su valor a establecer ($set)
            set.append("$set", new BasicDBObject().append("nombre", reserva.getNombre()).append("telefono", reserva.getTelefono()).append("dni", reserva.getDni()));
            // Indica el filtro a usar para aplicar la modificacion
            BasicDBObject searchQuery = new BasicDBObject().append("_id", reserva.getObject_id());
            //Se hace el update sobre la coleccion
            collectionC.update(searchQuery, set);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public ArrayList<Cliente> listarClientes() {
        DBCursor cursor = null;
        ArrayList<Cliente> listaClientes = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "reservas"
            DBCollection collectionC = db.getCollection("reservas");
            cursor = collectionC.find();

            DBObject elemento;
            ArrayList<ElementoSel> reservas = null;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                BasicDBList reservasList = (BasicDBList) elemento.get("reservas");
                reservas = new ArrayList();
                if (reservasList != null) {
                    for (int i = 0; i < reservasList.size(); i++) {

                        BasicDBObject reserva = (BasicDBObject) reservasList.get(i);
                        BasicDBObject apartamento = (BasicDBObject) reserva.get("apartamento");
                        String reservaString = "Fecha: " + reserva.getString("fecha") + ", Apartamento: " + apartamento.getString("alias");
                        reservas.add(new ElementoSel((ObjectId) reserva.get("id"), reservaString));

                    }
                }
                listaClientes.add(new Cliente((ObjectId) elemento.get("_id"), (String) elemento.get("dni"), (String) elemento.get("nombre"), (String) elemento.get("telefono"), reservas));
            }

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return listaClientes;
    }

    @WebServlet(urlPatterns = "/reservas/*", name = "ListadoClientesServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoReservas.class, productionMode = false)
    public static class ListadoClientesServlet extends VaadinServlet {
    }
}
