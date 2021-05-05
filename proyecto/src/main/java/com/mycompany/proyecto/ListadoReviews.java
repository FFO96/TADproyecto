package com.mycompany.proyecto;

import com.mycompany.proyecto.models.ElementoSel;
import com.mycompany.proyecto.models.Review;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mycompany.proyecto.models.Cliente;
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
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class ListadoReviews extends UI {

    //Lista de las reviews
    ArrayList<Review> reviewsList = new ArrayList();
    //Lista de los clientes
    ArrayList<Cliente> clientsList = new ArrayList();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        Table table = new Table("Lista de reviews");
        table.addContainerProperty("Contenido", String.class, null);
        table.addContainerProperty("Cliente", String.class, null);
        table.addContainerProperty("Apartamento", String.class, null);
        //Hacemos que sea seleccionable
        table.setSelectable(true);
        table.setPageLength(table.size());

        clientsList = listarClientes();
        final BeanItemContainer<ElementoSel> containerClientes = new BeanItemContainer<ElementoSel>(ElementoSel.class);

        
        // Creamos un combobox de los clientes
        ComboBox comboboxClientes = new ComboBox("Seleciona un cliente:", containerClientes);

        // Instanciamos el contrenedor y combobox de reservas para usarlo en el evento tras elegir un cliente en el desplegable de creacion de review
        BeanItemContainer<ElementoSel> containerReservas = new BeanItemContainer<ElementoSel>(ElementoSel.class);
        ComboBox comboboxReservas = new ComboBox("Seleciona una reserva:", containerReservas);

        final TextArea contenidoEdit = new TextArea("Contenido de la review:");
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar cliente");
        Button btnCrear = new Button("Crear review");
        Button btnGuardar = new Button("Guardar review");

        reviewsList = listarReviews();
        // Se completa la tabla si hay reviews en la lista
        for (int i = 0; i < reviewsList.size(); i++) {
            table.addItem(new Object[]{reviewsList.get(i).getContenido(), reviewsList.get(i).getCliente().getNombre(), reviewsList.get(i).getApartamento().getNombre()}, i);
        }

        // Listener sobre la tabla para una vez que se pulse sobre una fila de la tabla, el contenido de la review aparezcan en el input para poder editarlo
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                contenidoEdit.setValue((String) event.getItem().getItemProperty("Contenido").getValue());
            }
        });

        // Listener sobre el boton de edición de la review seleccionada en la tabla
        btnEdit.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs y que se ha seleccionado un elemento de la tabla para poder guardar los cambios
            if (reviewsList.isEmpty() || contenidoEdit.getValue() == "" || table.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay reviews \n o el campo de contenido se encuentra vacio.");
            } else {
                // Se cambian los datos introducidos en el propietario correspondiente en la lista
                reviewsList.get((int) table.getValue()).setContenido(contenidoEdit.getValue());

                editReview(reviewsList.get((int) table.getValue()));
                // Se limpian los inputs del formulario de edición
                contenidoEdit.setValue("");
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < reviewsList.size(); i++) {
                    table.addItem(new Object[]{reviewsList.get(i).getContenido(), reviewsList.get(i).getCliente().getNombre(), reviewsList.get(i).getApartamento().getNombre()}, i);
                }
            }

        });

        // Listener sobre el boton de borrar el cliente seleccionado
        btnBorrar.addClickListener(e -> {

            if (table.getValue() == null) {
                Notification.show("No es posible eliminar una review si no ha seleccionado en la tabla");
            } else {
                borrarReview(reviewsList.get((int) table.getValue()));
                contenidoEdit.setValue("");
                // actualizamos la lista de reviews
                reviewsList = listarReviews();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < reviewsList.size(); i++) {
                    table.addItem(new Object[]{reviewsList.get(i).getContenido(), reviewsList.get(i).getCliente().getNombre(), reviewsList.get(i).getApartamento().getNombre()}, i);
                }
            }
        });

        // Listener sobre el boton de crear un nuevo cliente 
        btnCrear.addClickListener(event -> {
            comboboxClientes.removeAllItems();
            // Añadimos los clientes al container
        for (int i = 0; i < clientsList.size(); i++) {
            containerClientes.addItem(new ElementoSel(clientsList.get(i).getObject_id(), clientsList.get(i).getNombre()));
        }
            comboboxClientes.setContainerDataSource(containerClientes);
            comboboxClientes.setNullSelectionAllowed(true);
            // El atributo que se mostrará en el combobox será el nombre
            comboboxClientes.setItemCaptionPropertyId("nombre");
            comboboxClientes.setNewItemsAllowed(true);
            comboboxClientes.setImmediate(true);
            contenidoEdit.setValue("");
            layout.removeAllComponents();
            layout.addComponents(contenidoEdit, comboboxClientes);
        });

        comboboxClientes.addValueChangeListener(event -> {
            layout.removeAllComponents();
            layout.addComponents(contenidoEdit, comboboxClientes);

            ElementoSel cliente = (ElementoSel) comboboxClientes.getValue();

            ArrayList<ElementoSel> reservasList = buscarReservasCliente(cliente);

            if (reservasList.isEmpty()) {
                Notification.show("El cliente no tiene hecha ninguna reserva.\n Necesita hacerse referencia a una reserva para añadir una review.");
            } else {
                
                //Borramos el combobox de reservas por si hubiera opciones previas
                comboboxReservas.removeAllItems();
                // Añadimos las reservas al container
                for (int i = 0; i < reservasList.size(); i++) {
                    containerReservas.addItem(new ElementoSel(reservasList.get(i).getId(), reservasList.get(i).getNombre()));
                }
                comboboxReservas.setContainerDataSource(containerReservas);
                comboboxReservas.setNullSelectionAllowed(true);
                // El atributo que se mostrará en el combobox será el nombre
                comboboxReservas.setItemCaptionPropertyId("nombre");
                comboboxReservas.setNewItemsAllowed(true);
                comboboxReservas.setImmediate(true);
                layout.addComponents(comboboxReservas, btnGuardar);
            }
        });

        btnGuardar.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs
            if (contenidoEdit.getValue() == "" || comboboxClientes.getValue() == null || comboboxReservas.getValue() == null) {
                Notification.show("No es posible crear una review si alguno de los campos se encuentra vacío.");
            } else {

                crearReview(new Review(contenidoEdit.getValue(), (ElementoSel) comboboxClientes.getValue(), (ElementoSel) comboboxReservas.getValue()));
                contenidoEdit.clear();
                //comboboxClientes.setNullSelectionItemId(null);
                //comboboxReservas.setNullSelectionItemId(null);
                layout.removeAllComponents();
                layout.addComponents(btnCrear, table, contenidoEdit, btnEdit, btnBorrar);
                // actualizamos la lista de reviews
                reviewsList = listarReviews();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < reviewsList.size(); i++) {
                    table.addItem(new Object[]{reviewsList.get(i).getContenido(), reviewsList.get(i).getCliente().getNombre(), reviewsList.get(i).getApartamento().getNombre()}, i);
                }
getUI().getPage().setLocation("/ListadoReviews/");

            }
        });

        layout.addComponents(btnCrear, table, contenidoEdit, btnEdit, btnBorrar);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    public void crearReview(Review review) {
        DBCursor cursor = null;
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionR = db.getCollection("reviews");
            DBCollection collectionA = db.getCollection("apartamentos");

            // Creamos la query para buscar el apartamento segun su _id
            BasicDBObject queryA = new BasicDBObject();
            queryA.put("_id", review.getApartamento().getId());
            // Busqueda del apartamento para poder introducir su alisa y direccion en la review
            DBObject apartamento = collectionA.findOne(queryA);

            BasicDBObject document = new BasicDBObject();
            // Creamos los objetos DB para cliente y apartamento que vamos a añadir a la review
            BasicDBObject clienteAux = new BasicDBObject();
            BasicDBObject apartamentoAux = new BasicDBObject();
            clienteAux.append("id", review.getCliente().getId()).append("nombre", review.getCliente().getNombre());
            apartamentoAux.append("id", apartamento.get("_id")).append("alias", apartamento.get("alias")).append("direccion", apartamento.get("direccion"));
            document.append("contenido", review.getContenido()).append("cliente", clienteAux).append("apartamento", apartamentoAux);
            collectionR.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void borrarReview(Review review) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionR = db.getCollection("reviews");

            // Se elimina el cliente cusando el _id para indentificarlo en la coleccion
            collectionR.remove(new BasicDBObject("_id", review.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void editReview(Review review) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "reviews"
            DBCollection collectionR = db.getCollection("reviews");

            BasicDBObject set = new BasicDBObject(); // Objeto con el que vamosa hacer set del atributo a cambiar
            set = new BasicDBObject();
            // Indica el atributo y su valor a establecer ($set)
            set.append("$set", new BasicDBObject().append("contenido", review.getContenido()));
            // Indica el filtro a usar para aplicar la modificacion
            BasicDBObject searchQuery = new BasicDBObject().append("_id", review.getObject_id());
            //Se hace el update sobre la coleccion
            collectionR.update(searchQuery, set);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public ArrayList<Review> listarReviews() {
        DBCursor cursor = null;
        ArrayList<Review> listaReviews = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "reviews"
            DBCollection collectionR = db.getCollection("reviews");
            cursor = collectionR.find();

            DBObject elemento;
            ArrayList<ElementoSel> reservas = null;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                BasicDBObject cliente = (BasicDBObject) elemento.get("cliente");
                BasicDBObject apartamento = (BasicDBObject) elemento.get("apartamento");
                String apartamentoString = apartamento.getString("alias") + " en " + apartamento.getString("direccion");
                listaReviews.add(new Review((ObjectId) elemento.get("_id"), (String) elemento.get("contenido"), new ElementoSel((ObjectId) cliente.get("id"), (String) cliente.get("nombre")), new ElementoSel((ObjectId) cliente.get("id"), apartamentoString)));
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaReviews;
    }

    public ArrayList<ElementoSel> buscarReservasCliente(ElementoSel cliente) {
        DBCursor cursor = null;
        ArrayList<ElementoSel> reservas = new ArrayList();

        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "reservas"
            DBCollection collectionR = db.getCollection("reservas");

            // Creamos la query para buscar el apartamento segun su _id
            BasicDBObject queryA = new BasicDBObject();
            queryA.put("cliente.id", cliente.getId());
            cursor = collectionR.find(queryA);

            DBObject elemento;
            DBObject apartamentoAux;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                apartamentoAux = (DBObject) elemento.get("apartamento");
                reservas.add(new ElementoSel((ObjectId) apartamentoAux.get("id"), (String) apartamentoAux.get("alias")));
                //Cabe mencionar que aunque esta lista representa las reservas, el objectId que se guarda es el del apartamento de dicha reserva
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reservas;
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

    @WebServlet(urlPatterns = "/ListadoReviews/*", name = "ListadoReviewsServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoReviews.class, productionMode = false)
    public static class ListadoReviewsServlet extends VaadinServlet {
    }
}
