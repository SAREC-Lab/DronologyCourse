package edu.nd.dronology.ui.vaadin.start;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.utils.Configuration;
import edu.nd.dronology.ui.vaadin.utils.YesNoWindow;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 * 
 * @author Jinghui Cheng
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@Push
public class MyUI extends UI {
	private static BaseServiceProvider provider = new BaseServiceProvider();
	public static BaseServiceProvider getProvider(){
		return provider;
	}

	private static YesNoWindow yesNoWindow = new YesNoWindow();	
	public static YesNoWindow getYesNoWindow() {
		return yesNoWindow;
	}

	@Override
    protected void init(VaadinRequest vaadinRequest) {
		provider.init("localhost", 9898);
		NavigationBar navigationBar = new NavigationBar();
		setContent(navigationBar);
		
		navigationBar.setSizeFull();
		/**
		 * update drone information every second
		 */
		Configuration configuration = Configuration.getInstance();
		Timer t = new Timer( );
		t.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
			    	VaadinSession session = getSession();
			    	if (session != null){
			    		access(() -> {
			    			navigationBar.getAFLayout().getControls().getPanel().refreshDrones();
			    			String focused = navigationBar.getAFLayout().getControls().getPanel().getFocusedName();
			    			List<String> checkedNames = navigationBar.getAFLayout().getControls().getPanel().getChecked();
			    			navigationBar.getAFLayout().getAFMap().updateDroneMarkers(focused, checkedNames);
			    			navigationBar.getAFLayout().getAFMap().updateActiveFlightRoutes(focused, checkedNames);
			    			navigationBar.getAFLayout().continueFollowing();
			    		});
			    	}
		    }
		}, configuration.getRefreshRate(), configuration.getRefreshRate());
    }
	
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
		private static final long serialVersionUID = -5422579448768796912L;
		
    }
}
