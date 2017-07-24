package edu.nd.dronology.ui.vaadin.start;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
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

	private static final long serialVersionUID = -511507929126974047L;
	private VerticalLayout content = new VerticalLayout();
	private AFMainLayout activeFlights = new AFMainLayout();
	private FRMainLayout flightRoutes = new FRMainLayout();
	public NavigationBar(){
		
		class NavigationBarCommand implements MenuBar.Command {
			private static final long serialVersionUID = 1L;

			private MenuItem previous = null;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				if (selectedItem.getText().equals("Active Flights")) {
					content.removeComponent(flightRoutes);
					content.addComponent(activeFlights);
				} else if (selectedItem.getText().equals("Flight Routes")) {
					content.removeComponent(activeFlights);
					content.addComponent(flightRoutes);
				} else {
					return;
				}
				
				if (previous != null)
          previous.setStyleName(null);
				selectedItem.setStyleName("active");
				previous = selectedItem;
			}
			
			public void setDefaultItem (MenuItem defaultItem) {
				previous = defaultItem;
				menuSelected(defaultItem);
			}
		}
		
		NavigationBarCommand menubarCommand = new NavigationBarCommand();
		
		activeFlights.setSizeFull();
		flightRoutes.setSizeFull();
		
		MenuBar menuBar = new MenuBar();
		menuBar.setWidth("100%");

		content.addComponents(menuBar);

		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource afIcon = new FileResource(new File(basepath + "/VAADIN/img/radar_icon.png"));
		FileResource frIcon = new FileResource(new File(basepath + "/VAADIN/img/route_icon.png"));
		
		MenuItem activeFlightsItem = menuBar.addItem("Active Flights", afIcon, menubarCommand);
		menubarCommand.setDefaultItem(activeFlightsItem);
		menuBar.addItem("Flight Routes", frIcon, menubarCommand);
    
		setCompositionRoot(content);
	}
	
	public AFMainLayout getAFLayout(){
		return activeFlights;
	}
	
	public FRMainLayout getFRLayout(){
		return flightRoutes;
	}
}
