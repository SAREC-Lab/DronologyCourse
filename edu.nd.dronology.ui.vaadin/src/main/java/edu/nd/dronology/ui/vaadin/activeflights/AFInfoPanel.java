package edu.nd.dronology.ui.vaadin.activeflights;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class AFInfoPanel extends CustomComponent{
	private static final long serialVersionUID = -3663049148276256302L;
	private Panel panel = new Panel();
	private VerticalLayout content = new VerticalLayout();
	public AFInfoPanel(){
		panel.setCaption("Active UAVs");
		panel.setContent(content);
		panel.setHeight("500px");
		setCompositionRoot(panel);
		/**
		 * dummy/example boxes
		 */
		addBox("Patrick", "Enroute", 124, 41.3145, -86.25324, 150, 30);
		addBox();
		addBox("Falvey", "Hovering", 88, 41.3234, -86.353, 200, 0);
	}
	
	public void addBox(String name, String status, int batteryLife, double lat, double lon, double alt, double speed){
		AFInfoBox box = new AFInfoBox(name, status, batteryLife, lat, lon, alt, speed);
		content.addComponent(box);
	}
	
	public void addBox(){
		AFInfoBox box = new AFInfoBox();
		content.addComponent(box);
	}
}

