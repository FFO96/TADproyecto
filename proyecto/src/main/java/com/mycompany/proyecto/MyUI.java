package com.mycompany.proyecto;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mycompany.proyecto.models.Cliente;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.AlignmentInfo.Bits;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.net.UnknownHostException;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        final HorizontalLayout panelHorizontal = new HorizontalLayout();
        layout.setSizeFull();
        GridLayout grid = new GridLayout(3, 3);

        grid.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        grid.setHeight(100, Sizeable.UNITS_PERCENTAGE);

        Panel middlecenter = new Panel("WHO?RENTS acceso");
        FormLayout form = new FormLayout();
        TextField tf1 = new TextField("Nombre");
        tf1.setIcon(FontAwesome.USER);
        tf1.setRequired(true);
        tf1.addValidator(new NullValidator("Obligatorio", false));
        form.addComponent(tf1);

        TextField tf2 = new TextField("Contraseña");
        tf2.setIcon(FontAwesome.LOCK);
        tf2.setRequired(true);
        tf2.addValidator(new NullValidator("Obligatorio", false));
        form.addComponent(tf2);
        Button enviado = new Button("Enviar");
        form.addComponent(enviado);
        
        enviado.addClickListener(i -> {
            //redirigir a reservas si accedes
            if(consultarAcceso(tf1.getValue(), tf2.getValue())){
                getUI().getPage().setLocation("/reservas/"); //redirigimos al listado
            }
            else{
                Notification.show("Contraseña incorrecta", "",Notification.Type.WARNING_MESSAGE);
            }
         });

        panelHorizontal.addComponents(form);

        middlecenter.setContent(panelHorizontal);
        
        grid.addComponent(middlecenter, 1, 1);
        grid.setComponentAlignment(middlecenter,
                  new Alignment(Bits.ALIGNMENT_VERTICAL_CENTER |
                                Bits.ALIGNMENT_HORIZONTAL_CENTER));
        grid.setSpacing(true);


        /*Button middleright = new Button("Middle Right");
        grid.addComponent(middleright, 2, 1);
        grid.setComponentAlignment(middleright,
                  new Alignment(Bits.ALIGNMENT_VERTICAL_CENTER |
                                Bits.ALIGNMENT_RIGHT));*/
        
        layout.addComponent(grid);
        setContent(layout);
    }
    
    public boolean consultarAcceso(String user, String pwd) {
        boolean flag = false;
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            // Conectar a la base de datos
            DB db = mongoClient.getDB("alquileres");
            //Obtencion coleccion "admin"
            DBCollection collectionC = db.getCollection("admins");

            BasicDBObject set = new BasicDBObject(); // Objeto con el que vamosa hacer set del atributo a cambiar
            set = new BasicDBObject();
            // Indica el filtro a usar para aplicar la modificacion
            BasicDBObject searchQuery = new BasicDBObject().append("nombre", user).append("password", pwd);
            //Se hace el update sobre la coleccion
            DBCursor cursor = collectionC.find(searchQuery);

            if(cursor.hasNext()){
                flag = true;
            }
         } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return flag;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
