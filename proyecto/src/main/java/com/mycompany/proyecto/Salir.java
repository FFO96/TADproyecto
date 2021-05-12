/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.proyecto;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author Patricio
 */
public class Salir extends UI {

    @Override
    protected void init(VaadinRequest request) {
        getUI().getPage().setLocation("/");
    }
    @WebServlet(urlPatterns = "/salir/*", name = "SalirServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = Salir.class, productionMode = false)
    public static class SalirServlet extends VaadinServlet {
    }
}
