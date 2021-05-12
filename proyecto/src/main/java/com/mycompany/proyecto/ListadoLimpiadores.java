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
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.MenuBar;
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
public class ListadoLimpiadores extends UI {

    //Lista de las reviews
    ArrayList<Limpiador> limpiadoresList = new ArrayList();
    //Lista de los clientes
    ArrayList<Apartamento> apartamentosList = new ArrayList();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //sesion
        VaadinSession vSession = VaadinSession.getCurrent();
        WrappedSession httpSession = vSession.getSession();
        if(httpSession.getAttribute("nombre")==null){
            getUI().getPage().setLocation("/");
        }
        final VerticalLayout layout = new VerticalLayout();
        Table table = new Table("Lista de limpiadores");
        table.addContainerProperty("DNI", String.class, null);
        table.addContainerProperty("Nombre", String.class, null);
        table.addContainerProperty("Telefono", String.class, null);
        table.addContainerProperty("Apartamento", String.class, null);
        //Hacemos que sea seleccionable
        table.setSelectable(true);
        table.setPageLength(table.size());

        apartamentosList = listarApartamentos();
        final BeanItemContainer<ElementoSel> containerApartamentos = new BeanItemContainer<ElementoSel>(ElementoSel.class);

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
        for (int i = 0; i < apartamentosList.size(); i++) {
            containerApartamentos.addItem(new ElementoSel(apartamentosList.get(i).getObject_id(), apartamentosList.get(i).getAlias()));
        }
        // Creamos un combobox de los apartamentos
        ComboBox comboboxApartamentos = new ComboBox("Seleciona un apartamento:", containerApartamentos);

        comboboxApartamentos.setContainerDataSource(containerApartamentos);
        comboboxApartamentos.setNullSelectionAllowed(true);
        // El atributo que se mostrará en el combobox será el alias del apartamento
        comboboxApartamentos.setItemCaptionPropertyId("nombre");
        comboboxApartamentos.setNewItemsAllowed(true);
        comboboxApartamentos.setImmediate(true);

        final TextField dniEdit = new TextField("DNI:");
        final TextField nombreEdit = new TextField("Nombre:");
        final TextField telefonoEdit = new TextField("Telefono:");
        Button btnEdit = new Button("Guardar Cambios");
        Button btnBorrar = new Button("Borrar limpiador");
        Button btnCrear = new Button("Crear limpiador");
        Button btnGuardar = new Button("Guardar limpiador");

        limpiadoresList = listarLimpiadores();
        // Se completa la tabla si hay limpiadores en la lista
        for (int i = 0; i < limpiadoresList.size(); i++) {
            table.addItem(new Object[]{limpiadoresList.get(i).getDni(), limpiadoresList.get(i).getNombre(), limpiadoresList.get(i).getTelefono(), limpiadoresList.get(i).getApartamento().getNombre()}, i);
        }

        // Listener sobre la tabla para una vez que se pulse sobre una fila de la tabla, el contenido del limpiador aparezcan en los inputs para poder editarlo
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                dniEdit.setValue((String) event.getItem().getItemProperty("DNI").getValue());
                nombreEdit.setValue((String) event.getItem().getItemProperty("Nombre").getValue());
                telefonoEdit.setValue((String) event.getItem().getItemProperty("Telefono").getValue());
            }
        });

        // Listener sobre el boton de edición del limpiador seleccionado en la tabla
        btnEdit.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs y que se ha seleccionado un elemento de la tabla para poder guardar los cambios
            if (limpiadoresList.isEmpty() || dniEdit.getValue() == "" || nombreEdit.getValue() == "" || telefonoEdit.getValue() == "" || table.getValue() == null) {
                Notification.show("No es posible guardar cambios si no hay limpiadores \n o el campo de contenido se encuentra vacio.");
            } else {
                // Se cambian los datos introducidos en el limpiador correspondiente en la lista
                limpiadoresList.get((int) table.getValue()).setDni(dniEdit.getValue());
                limpiadoresList.get((int) table.getValue()).setNombre(nombreEdit.getValue());
                limpiadoresList.get((int) table.getValue()).setTelefono(telefonoEdit.getValue());

                editLimpiador(limpiadoresList.get((int) table.getValue()));
                // Se limpian los inputs del formulario de edición
                dniEdit.setValue("");
                nombreEdit.setValue("");
                telefonoEdit.setValue("");
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < limpiadoresList.size(); i++) {
                    table.addItem(new Object[]{limpiadoresList.get(i).getDni(), limpiadoresList.get(i).getNombre(), limpiadoresList.get(i).getTelefono(), limpiadoresList.get(i).getApartamento().getNombre()}, i);
                }
            }

        });

        // Listener sobre el boton de borrar el limpiador seleccionado
        btnBorrar.addClickListener(e -> {

            if (table.getValue() == null) {
                Notification.show("No es posible eliminar un limpiador si no ha seleccionado en la tabla");
            } else {
                borrarLimpiador(limpiadoresList.get((int) table.getValue()));
                dniEdit.setValue("");
                nombreEdit.setValue("");
                telefonoEdit.setValue("");
                // actualizamos la lista de reviews
                limpiadoresList = listarLimpiadores();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < limpiadoresList.size(); i++) {
                    table.addItem(new Object[]{limpiadoresList.get(i).getDni(), limpiadoresList.get(i).getNombre(), limpiadoresList.get(i).getTelefono(), limpiadoresList.get(i).getApartamento().getNombre()}, i);
                }
            }
        });

        // Listener sobre el boton de crear un nuevo limpiador 
        btnCrear.addClickListener(event -> {
            dniEdit.setValue("");
            nombreEdit.setValue("");
            telefonoEdit.setValue("");
            layout.removeAllComponents();
            layout.addComponents(dniEdit, nombreEdit, telefonoEdit, comboboxApartamentos, btnGuardar);
        });

        btnGuardar.addClickListener(e -> {
            // Se comprueba que hay elementos en todos los inputs
            if (dniEdit.getValue() == "" || nombreEdit.getValue() == "" || telefonoEdit.getValue() == "" || comboboxApartamentos.getValue() == null) {
                Notification.show("No es posible crear un limpiador si alguno de los campos se encuentra vacío.");
            } else {

                crearLimpiador(new Limpiador(dniEdit.getValue(), nombreEdit.getValue(), telefonoEdit.getValue(), (ElementoSel) comboboxApartamentos.getValue()));
                dniEdit.clear();
                nombreEdit.clear();
                telefonoEdit.clear();
                layout.removeAllComponents();
                layout.addComponents(btnCrear, table, dniEdit, nombreEdit, telefonoEdit, btnEdit, btnBorrar);
                // actualizamos la lista de limpiadores
                limpiadoresList = listarLimpiadores();
                //Se actualiza la tabla para que muestre la lista actualizada. Esto se hace borrando el contenido de la tabla y añadiendole la lista de nuevo
                table.removeAllItems();
                for (int i = 0; i < limpiadoresList.size(); i++) {
                    table.addItem(new Object[]{limpiadoresList.get(i).getDni(), limpiadoresList.get(i).getNombre(), limpiadoresList.get(i).getTelefono(), limpiadoresList.get(i).getApartamento().getNombre()}, i);
                }
                //getUI().getPage().setLocation("/ListadoReviews/");

            }
        });

        layout.addComponents(btnCrear, table, dniEdit, nombreEdit, telefonoEdit, btnEdit, btnBorrar);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    public void crearLimpiador(Limpiador limpiador) {
        DBCursor cursor = null;
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("limpiadores");
            DBCollection collectionA = db.getCollection("apartamentos");

            // Creamos la query para buscar el apartamento segun su _id
            BasicDBObject queryA = new BasicDBObject();
            queryA.put("_id", limpiador.getApartamento().getId());
            // Busqueda del apartamento para poder introducir su alias y direccion en el limpiador
            DBObject apartamento = collectionA.findOne(queryA);

            BasicDBObject document = new BasicDBObject();
            // Creamos los objetos DB para apartamento que vamos a añadir al limpiador
            BasicDBObject apartamentoAux = new BasicDBObject();
            apartamentoAux.append("id", apartamento.get("_id")).append("alias", apartamento.get("alias")).append("direccion", apartamento.get("direccion"));
            document.append("dni", limpiador.getDni()).append("nombre", limpiador.getNombre()).append("telefono", limpiador.getTelefono()).append("apartamento", apartamentoAux);
            collectionL.insert(document);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void borrarLimpiador(Limpiador limpiador) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            DBCollection collectionL = db.getCollection("limpiadores");

            // Se elimina el limpiador usando el _id para indentificarlo en la coleccion
            collectionL.remove(new BasicDBObject("_id", limpiador.getObject_id()));
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void editLimpiador(Limpiador limpiador) {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "limpiadores"
            DBCollection collectionL = db.getCollection("limpiadores");

            BasicDBObject set = new BasicDBObject(); // Objeto con el que vamosa hacer set del atributo a cambiar
            set = new BasicDBObject();
            // Indica el atributo y su valor a establecer ($set)
            set.append("$set", new BasicDBObject().append("dni", limpiador.getDni()).append("nombre", limpiador.getNombre()).append("telefono", limpiador.getTelefono()));
            // Indica el filtro a usar para aplicar la modificacion
            BasicDBObject searchQuery = new BasicDBObject().append("_id", limpiador.getObject_id());
            //Se hace el update sobre la coleccion
            collectionL.update(searchQuery, set);

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public ArrayList<Limpiador> listarLimpiadores() {
        DBCursor cursor = null;
        ArrayList<Limpiador> listaLimpiadores = new ArrayList();
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "limpiadores"
            DBCollection collectionL = db.getCollection("limpiadores");
            cursor = collectionL.find();

            DBObject elemento;
            ArrayList<ElementoSel> reservas = null;
            while (cursor.hasNext()) {
                elemento = cursor.next();
                BasicDBObject apartamento = (BasicDBObject) elemento.get("apartamento");
                String apartamentoString = apartamento.getString("alias") + " en " + apartamento.getString("direccion");
                listaLimpiadores.add(new Limpiador((ObjectId) elemento.get("_id"), (String) elemento.get("dni"), (String) elemento.get("nombre"), (String) elemento.get("telefono"), new ElementoSel((ObjectId) apartamento.get("id"), apartamentoString)));
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return listaLimpiadores;
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

    @WebServlet(urlPatterns = "/limpiadores/*", name = "ListadoLimpiadoresServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ListadoLimpiadores.class, productionMode = false)
    public static class ListadoLimpiadoresServlet extends VaadinServlet {
    }
}
