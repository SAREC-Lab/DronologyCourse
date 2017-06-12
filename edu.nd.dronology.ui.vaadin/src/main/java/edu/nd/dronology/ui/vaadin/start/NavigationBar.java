package edu.nd.dronology.ui.vaadin.start;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.ui.vaadin.activeflights.AFMainLayout;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMainLayout;

/**
 * This navigation bar switches between the active flights
 * layout and the flight routes layout.
 * 
 * @author Patrick Falvey
 *
 */

public class NavigationBar extends CustomComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -511507929126974047L;
  private VerticalLayout content = new VerticalLayout();
  private AFMainLayout activeflights = new AFMainLayout();
  private FRMainLayout flightroutes = new FRMainLayout();
	public NavigationBar(){
		
		MenuBar.Command changeToActive = new MenuBar.Command(){
			private static final long serialVersionUID = -8302013415286995087L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				content.removeComponent(flightroutes);
				content.addComponent(activeflights);
			}
		};
		
		MenuBar.Command changeToRoutes = new MenuBar.Command(){
			private static final long serialVersionUID = -4634294050634183985L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				content.removeComponent(activeflights);
				content.addComponent(flightroutes);
			}
		};
		
		activeflights.setSizeFull();
		flightroutes.setSizeFull();
		
		MenuBar menuBar = new MenuBar();
		menuBar.setWidth("100%");
		
		menuBar.addItem("Active Flights", changeToActive);
		menuBar.addItem("Flight Routes", changeToRoutes);

    content.addComponents(menuBar, activeflights);
    
    setCompositionRoot(content);
	}
}
