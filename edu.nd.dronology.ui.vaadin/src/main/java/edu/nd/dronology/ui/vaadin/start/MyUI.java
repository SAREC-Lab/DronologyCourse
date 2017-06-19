package edu.nd.dronology.ui.vaadin.start;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;

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
@Push
public class MyUI extends UI {
	private static final long serialVersionUID = 8561111247076018949L;
	
	@Override
    protected void init(VaadinRequest vaadinRequest) {
			provider.init("localhost", 9898);
			NavigationBar navigationBar = new NavigationBar();
			setContent(navigationBar);
			navigationBar.setSizeFull();
			/**
			 * update drone information every second
			 */
			Timer t = new Timer( );
			t.scheduleAtFixedRate(new TimerTask() {
			    @Override
			    public void run() {
			      access(() -> navigationBar.getAFLayout().getControls().getPanel().refreshDrones());
			    }
			}, 1000,1000);
    }
	public static BaseServiceProvider getProvider(){
		return provider;
	}
	
	private static BaseServiceProvider provider = new BaseServiceProvider();
	
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
		private static final long serialVersionUID = -5422579448768796912L;
		
		
    }
}
