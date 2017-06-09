package edu.nd.dronology.ui.vaadin.start;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import edu.nd.dronology.ui.vaadin.activeflights.AFMainLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 * 
 * @author Jinghui Cheng
 */
@Theme("mytheme")
public class MyUI extends UI {
	private static final long serialVersionUID = 8561111247076018949L;
	
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		AFMainLayout activeflights = new AFMainLayout();
        setContent(activeflights);
        activeflights.setSizeFull();
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
		private static final long serialVersionUID = -5422579448768796912L;
    }
}
