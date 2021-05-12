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
import com.mycompany.proyecto.models.Apartamento;
import com.mycompany.proyecto.models.Limpiador;
import com.mycompany.proyecto.models.Reserva;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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

    //Lista de las reservas
    ArrayList<Reserva> reservasList = new ArrayList();
    //Lista de los clientes y apartamentos
    ArrayList<Apartamento> apartamentosList = new ArrayList();
    ArrayList<Cliente> clientesList = new ArrayList();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //sesion
        VaadinSession vSession = VaadinSession.getCurrent();
        WrappedSession httpSession = vSession.getSession();
        if(httpSession.getAttribute("nombre")==null){
            getUI().getPage().setLocation("/");
        }
        final VerticalLayout layout = new VerticalLayout();
        Table table = new Table("Lista de reservas");
        table.addContainerProperty("Fecha", String.class, null);
        table.addContainerProperty("Precio", String.class, null);
        table.addContainerProperty("Apartamento", String.class, null);
        table.addContainerProperty("Cliente", String.class, null);

        //Hacemos que sea seleccionable
        table.setSelectable(true);
        table.setPageLength(table.size());
        
        apartamentosList = listarApartamentos();
        final BeanItemContainer<ElementoSel> containerApartamentos = new BeanItemContainer<ElementoSel>(ElementoSel.class);
        clientesList = listarClientes();
        final BeanItemContainer<ElementoSel> containerClientes = new BeanItemContainer<ElementoSel>(ElementoSel.class);
        
        //Menu
        MenuBar barmenu = new MenuBar();
        layout.addComponent(barmenu);
        //Evento para el menu
        MenuBar.Command mycommand = new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation("/"+selectedItem.getText().toLowerCase()+"/");
            }  
        };
        //Lista del menu
        MenuItem apartamentos = barmenu.addItem("Apartamentos", null, mycommand);
        MenuItem clientes = barmenu.addItem("Clientes", null, mycommand);
        MenuItem propietarios = barmenu.addItem("Propietarios", null, mycommand);
        MenuItem facturas = barmenu.addItem("Facturas", null, mycommand);
        MenuItem limpiadores = barmenu.addItem("Limpiadores", null, mycommand);
        MenuItem reviews = barmenu.addItem("Reviews", null, mycommand);
        MenuItem reservas = barmenu.addItem("Reservas", null, mycommand);
        MenuItem cerrarSesion = barmenu.addItem("Salir", null, mycommand);



        // Añadimos los limpiadores al container
        for (int i = 0; i < apartamentosList.size(); i++) {
            containerApartamentos.addItem(new ElementoSel(apartamentosList.get(i).getObject_id(), apartamentosList.get(i).getAlias()));
        }
        for (int i = 0; i < clientesList.size(); i++) {
            containerClientes.addItem(new ElementoSel(clientesList.get(i).getObject_id(), clientesList.get(i).getNombre()));
        }
        // Creamos un combobox de los apartamentos
        ComboBox comboboxApartamentos = new ComboBox("Seleciona un apartamento:", containerApartamentos);
        ComboBox comboboxClientes = new ComboBox("Seleciona un cliente:", containerClientes);


        comboboxApartamentos.setContainerDataSource(containerApartamentos);
        comboboxApartamentos.setNullSelectionAllowed(true);
        // El atributo que se mostrará en el combobox será el alias del apartamento
        comboboxApartamentos.setItemCaptionPropertyId("nombre");
        comboboxApartamentos.setNewItemsAllowed(true);
        comboboxApartamentos.setImmediate(true);
        
        comboboxClientes.setContainerDataSource(containerClientes);
        comboboxClientes.setNullSelectionAllowed(true);
        // El atributo que se mostrará en el combobox será el nombre del cliente
        comboboxClientes.setItemCaptionPropertyId("nombre");
        comboboxClientes.setNewItemsAllowed(true);
        comboboxClientes.setImmediate(true);

        //a partir de aqui, reserva
        final TextField precioEdit = new TextField("Precio:");
        final TextField fechaEdit = new TextField("Fecha:");
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar reserva");
        Button btnCrear = new Button("Crear reserva");
        Button btnGuardar = new Button("Guardar reserva");

        reservasList = listarReservas();
        // Se completa la tabla si hay limpiadores en la lista
        for (int i = 0; i < reservasList.size(); i++) {
            table.addItem(new Object[]{ reservasList.get(i).getFecha(), reservasList.get(i).getPrecio(), reservasList.get(i).getApartamento().getNombre(), reservasList.get(i).getCliente().getNombre()}, i);
        }

        // Listener sobre la tabla para una vez que se pulse sobre una fila de la tabla, el contenido del limpiador aparezcan en los inputs para poder editarlo
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                precioEdit.setValue((String) event.getItem().getItemProperty("Precio").getValue());
                fechaEdit.setValue((String) event.getItem().getItemProperty("Fecha").getValue());
                
            }
        });

        // Listener sobre el boton de edición del limpiador seleccionado en la tabla
        btnEdit.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs y que se ha seleccionado un elemento de la tabla para poder guardar los cambios
            if (reservasList.isEmpty() || precioEdit.getValue() == "" || fechaEdit.getValue() == "" || table.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay reservas \n o el campo de contenido se encuentra vacio.");
            } else {
                // Se cambian los datos introducidos en el limpiador correspondiente en la lista
                reservasList.get((int) table.getValue()).setFecha(fechaEdit.getValue());
                reservasList.get((int) table.getValue()).setPrecio(precioEdit.getValue());

                editReserva(reservasList.get((int) table.getValue()));
                // Se limpian los inputs del formulario de edición
                precioEdit.setValue("");
                fechaEdit.setValue("");
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < reservasList.size(); i++) {
                    table.addItem(new Object[]{reservasList.get(i).getPrecio(), reservasList.get(i).getFecha(), reservasList.get(i).getApartamento().getNombre(), reservasList.get(i).getCliente().getNombre()}, i);
                }
            }

        });

        // Listener sobre el boton de borrar el limpiador seleccionado
        btnBorrar.addClickListener(e -> {

            if (table.getValue() == null) {
                Notification.show("No es posible eliminar una reserva si no ha seleccionado en la tabla");
            } else {
                borrarReserva(reservasList.get((int) table.getValue()));
                precioEdit.setValue("");
                fechaEdit.setValue("");
                // actualizamos la lista de reviews
                reservasList = listarReservas();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < reservasList.size(); i++) {
                    table.addItem(new Object[]{reservasList.get(i).getPrecio(), reservasList.get(i).getFecha(), reservasList.get(i).getApartamento().getNombre(), reservasList.get(i).getCliente().getNombre()}, i);
                }
            }
        });

        // Listener sobre el boton de crear un nuevo limpiador 
        btnCrear.addClickListener(event -> {
            precioEdit.setValue("");
            fechaEdit.setValue("");
            layout.removeAllComponents();
            layout.addComponents(fechaEdit, precioEdit, comboboxApartamentos, comboboxClientes, btnGuardar);
        });

        btnGuardar.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs
            if ( fechaEdit.getValue() == "" || precioEdit.getValue() == "" || comboboxApartamentos.getValue() == null || comboboxClientes.getValue() == null) {
                Notification.show("No es posible crear una reserva si alguno de los campos se encuentra vacío.");
            } else {
                System.out.println(comboboxApartamentos.getValue().toString());
                crearReserva(new Reserva(fechaEdit.getValue(), precioEdit.getValue(), (ElementoSel) comboboxApartamentos.getValue(), (ElementoSel) comboboxClientes.getValue()));
                fechaEdit.clear();
                precioEdit.clear();
                layout.removeAllComponents();
                layout.addComponents(btnCrear, table, fechaEdit, precioEdit, btnEdit, btnBorrar);
                // actualizamos la lista de limpiadores
                reservasList = listarReservas();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < reservasList.size(); i++) {
                    table.addItem(new Object[]{reservasList.get(i).getPrecio(), reservasList.get(i).getFecha(), reservasList.get(i).getApartamento().getNombre(), reservasList.get(i).getCliente().getNombre()}, i);
                }
                //getUI().getPage().setLocation("/ListadoReviews/");

            }
        });

        layout.addComponents(btnCrear, table, fechaEdit, precioEdit, btnEdit, btnBorrar);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    public void crearReserva(Reserva reserva) {
        DBCursor cursor = null;
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionR = db.getCollection("reservas");
            DBCollection collectionA = db.getCollection("apartamentos");
            DBCollection collectionC = db.getCollection("clientes");


            // Creamos la query para buscar el apartamento segun su _id
            BasicDBObject queryA = new BasicDBObject();
            System.out.println(reserva.getApartamento());
            queryA.put("_id", reserva.getApartamento().getId());
            // Busqueda del apartamento para poder introducir su alias y direccion en el limpiador
            DBObject apartamento = collectionA.findOne(queryA);
            
            // Creamos la query para buscar el cliente segun su _id
            BasicDBObject queryC = new BasicDBObject();
            queryC.put("_id", reserva.getCliente().getId());
            // Busqueda del cliente para poder introducir su alias y direccion en el limpiador
            DBObject cliente = collectionC.findOne(queryC);

            BasicDBObject document = new BasicDBObject();
            // Creamos los objetos DB para apartamento que vamos a añadir al limpiador
            BasicDBObject apartamentoAux = new BasicDBObject();
            BasicDBObject clienteAux = new BasicDBObject();
            apartamentoAux.append("id", apartamento.get("_id")).append("alias", apartamento.get("alias")).append("direccion", apartamento.get("direccion"));
            clienteAux.append("id", cliente.get("_id")).append("nombre", cliente.get("nombre")).append("dni", cliente.get("dni"));
            document.append("fecha", reserva.getFecha()).append("precio", reserva.getPrecio()).append("apartamento", apartamentoAux).append("cliente", clienteAux);
            collectionR.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void borrarReserva(Reserva reserva) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionR = db.getCollection("reservas");

            // Se elimina el limpiador usando el _id para indentificarlo en la coleccion
            collectionR.remove(new BasicDBObject("_id", reserva.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void editReserva(Reserva reserva) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "limpiadores"
            DBCollection collectionR = db.getCollection("reservas");

            BasicDBObject set = new BasicDBObject(); // Objeto con el que vamosa hacer set del atributo a cambiar
            set = new BasicDBObject();
            // Indica el atributo y su valor a establecer ($set)
            set.append("$set", new BasicDBObject().append("fecha", reserva.getFecha()).append("precio", reserva.getPrecio()));
            // Indica el filtro a usar para aplicar la modificacion
            BasicDBObject searchQuery = new BasicDBObject().append("_id", reserva.getObject_id());
            //Se hace el update sobre la coleccion
            collectionR.update(searchQuery, set);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public ArrayList<Reserva> listarReservas() {
        DBCursor cursor = null;
        ArrayList<Reserva> listaReservas = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "limpiadores"
            DBCollection collectionR = db.getCollection("reservas");
            cursor = collectionR.find();

            DBObject elemento;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                BasicDBObject apartamento = (BasicDBObject) elemento.get("apartamento");
                BasicDBObject cliente = (BasicDBObject) elemento.get("cliente");
                String apartamentoString = apartamento.getString("alias") + " en " + apartamento.getString("direccion");
                String clienteString = cliente.getString("nombre") + " dni: " + cliente.getString("dni");

                listaReservas.add(new Reserva((ObjectId) elemento.get("_id"), (String) elemento.get("fecha"), (String) elemento.get("precio"), new ElementoSel((ObjectId) apartamento.get("id"), apartamentoString), new ElementoSel((ObjectId) cliente.get("id"), clienteString)));
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaReservas;
    }

    public ArrayList<Apartamento> listarApartamentos() {
        DBCursor cursor = null;
        ArrayList<Apartamento> listaApartamentos = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "apartamentos"
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
    public ArrayList<Cliente> listarClientes() {
        DBCursor cursor = null;
        ArrayList<Cliente> listaCliente = new ArrayList();
        ArrayList<ElementoSel> listaResClien = new ArrayList();
        
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "apartamentos"
            DBCollection collectionA = db.getCollection("clientes");
            cursor = collectionA.find();

            DBObject elemento;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                listaResClien.add(new ElementoSel((ObjectId) elemento.get("reserva")));
                listaCliente.add(new Cliente((ObjectId) elemento.get("_id"), (String) elemento.get("dni"), (String) elemento.get("nombre"), (String) elemento.get("telefono"), listaResClien));

            }

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaCliente;
    }
    
    
    
    
    @WebServlet(urlPatterns = "/reservas/*", name = "ListadoReservasServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoReservas.class, productionMode = false)
    public static class ListadoReservasServlet extends VaadinServlet {
    }
}
