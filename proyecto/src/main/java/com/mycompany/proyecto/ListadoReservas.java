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
    ArrayList<Reserva> ReservasList = new ArrayList();
    ArrayList<Cliente> ClientesList = new ArrayList();
    

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        Table table = new Table("Lista de clientes");
        table.addContainerProperty("DNI", String.class, null);
        table.addContainerProperty("Nombre y apellidos", String.class, null);
        table.addContainerProperty("Telefono", String.class, null);
        //Hacemos que sea seleccionable
        table.setSelectable(true);
        table.setPageLength(table.size());

        Table tableReservas = new Table("Reservas del cliente");
        tableReservas.addContainerProperty("Reserva", String.class, null);
        //Hacemos que sea seleccionable
        tableReservas.setSelectable(true);
        tableReservas.setPageLength(table.size());

        final TextField nameEdit = new TextField("Nombre del cliente:");
        final TextField dniEdit = new TextField("DNI del cliente:");
        final TextField telefonoEdit = new TextField("Telefono del cliente:");
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar cliente");
        Button btnCrear = new Button("Crear cliente");
        Button btnGuardar = new Button("Guardar cliente");

        clientsList = listarClientes();
        // Se completa la tabla si hay clientes en la lista
        for (int i = 0; i < clientsList.size(); i++) {
            table.addItem(new Object[]{clientsList.get(i).getDni(), clientsList.get(i).getNombre(), clientsList.get(i).getTelefono()}, i);
        }

        // Listener sobre la tabla para una vez que se pulse sobre una fila de la tabla, los datos del cliente aparezcan en los inputs para poder editarlos
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                tableReservas.removeAllItems();
                nameEdit.setValue((String) event.getItem().getItemProperty("Nombre y apellidos").getValue());
                telefonoEdit.setValue((String) event.getItem().getItemProperty("Telefono").getValue());
                dniEdit.setValue((String) event.getItem().getItemProperty("DNI").getValue());

                Cliente cliente = clientsList.get((int) event.getItemId());
                for (int i = 0; i < cliente.getReservas().size(); i++) {
                    tableReservas.addItem(new Object[]{cliente.getReservas().get(i).getNombre()}, i);
                }
                layout.addComponent(tableReservas);
            }
        });

        // Listener sobre el boton de edición del cliente seleccionado en la tabla
        btnEdit.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs y que se ha seleccionado un elemento de la tabla para poder guardar los cambios
            if (clientsList.isEmpty() || nameEdit.getValue() == "" || telefonoEdit.getValue() == "" || dniEdit.getValue() == "" || table.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay clientes \n o alguno de los campos se encuentra vacío.");
            } else {
                // Se cambian los datos introducidos en el propietario correspondiente en la lista
                clientsList.get((int) table.getValue()).setDni(dniEdit.getValue());
                clientsList.get((int) table.getValue()).setNombre(nameEdit.getValue());
                clientsList.get((int) table.getValue()).setTelefono(telefonoEdit.getValue());

                editCliente(clientsList.get((int) table.getValue()));
                // Se limpian los inputs del formulario de edición
                nameEdit.setValue("");
                telefonoEdit.setValue("");
                dniEdit.setValue("");
                tableReservas.removeAllItems();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < clientsList.size(); i++) {
                    table.addItem(new Object[]{clientsList.get(i).getDni(), clientsList.get(i).getNombre(), clientsList.get(i).getTelefono()}, i);
                }
                layout.removeComponent(tableReservas);
            }

        });

        // Listener sobre el boton de borrar el cliente seleccionado
        btnBorrar.addClickListener(e -> {

            if (table.getValue() == null) {
                Notification.show("No es posible eliminar un cliente si no ha seleccionado en la tabla");
            } else {
                borrarCliente(clientsList.get((int) table.getValue()));
                nameEdit.setValue("");
                telefonoEdit.setValue("");
                dniEdit.setValue("");
                layout.removeComponent(tableReservas);
                // actualizamos la lista de clientes
                clientsList = listarClientes();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < clientsList.size(); i++) {
                    table.addItem(new Object[]{clientsList.get(i).getDni(), clientsList.get(i).getNombre(), clientsList.get(i).getTelefono()}, i);
                }
                
            }

        });

        // Listener sobre el boton de crear un nuevo cliente 
        btnCrear.addClickListener(e -> {
            nameEdit.setValue("");
            telefonoEdit.setValue("");
            dniEdit.setValue("");
            layout.removeAllComponents();
            layout.addComponents(nameEdit, dniEdit, telefonoEdit, btnGuardar);
        });
        btnGuardar.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs
            if (nameEdit.getValue() == "" || telefonoEdit.getValue() == "" || dniEdit.getValue() == "") {
                Notification.show("No es posible crear un cliente si alguno de los campos se encuentra vacío.");
            } else {
                crearCliente(new Cliente(dniEdit.getValue(), nameEdit.getValue(), telefonoEdit.getValue()));
                nameEdit.setValue("");
                telefonoEdit.setValue("");
                dniEdit.setValue("");
                layout.removeAllComponents();
                layout.addComponents(btnCrear, table, nameEdit, dniEdit, telefonoEdit, btnEdit, btnBorrar);
                // actualizamos la lista de clientes
                clientsList = listarClientes();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < clientsList.size(); i++) {
                    table.addItem(new Object[]{clientsList.get(i).getDni(), clientsList.get(i).getNombre(), clientsList.get(i).getTelefono()}, i);
                }
            }
        });

        layout.addComponents(btnCrear, table, nameEdit, dniEdit, telefonoEdit, btnEdit, btnBorrar);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    public void crearCliente(Cliente cliente) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionC = db.getCollection("clientes");

            BasicDBObject document;
            document = new BasicDBObject();
            document.append("dni", cliente.getDni());
            document.append("nombre", cliente.getNombre());
            document.append("telefono", cliente.getTelefono());
            collectionC.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void borrarCliente(Cliente cliente) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionC = db.getCollection("clientes");

            // Se elimina el cliente cusando el _id para indentificarlo en la coleccion
            collectionC.remove(new BasicDBObject("_id", cliente.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void editCliente(Cliente cliente) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "clientes"
            DBCollection collectionC = db.getCollection("clientes");

            BasicDBObject set = new BasicDBObject(); // Objeto con el que vamosa hacer set del atributo a cambiar
            set = new BasicDBObject();
            // Indica el atributo y su valor a establecer ($set)
            set.append("$set", new BasicDBObject().append("nombre", cliente.getNombre()).append("telefono", cliente.getTelefono()).append("dni", cliente.getDni()));
            // Indica el filtro a usar para aplicar la modificacion
            BasicDBObject searchQuery = new BasicDBObject().append("_id", cliente.getObject_id());
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
            //Obtencion coleccion "clientes"
            DBCollection collectionC = db.getCollection("clientes");
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

    @WebServlet(urlPatterns = "/reservas/*", name = "ListadoReservasServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoReservas.class, productionMode = false)
    public static class ListadoReservasServlet extends VaadinServlet {
    }
}
