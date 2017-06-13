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
		addBox(false, "Patrick", "Enroute", 124, "red", 41.3145, -86.25324, 150, 30, false);
		addBox();
		addBox(true, "Falvey", "Hovering", 88, "yellow", 41.3234, -86.353, 200, 0, true);
	}
	
	public void addBox(boolean isChecked, String name, String status, int batteryLife, String healthColor, double lat, double lon, double alt, double speed, boolean hoverInPlace){
		AFInfoBox box = new AFInfoBox(isChecked, name, status, batteryLife, healthColor, lat, lon, alt, speed, hoverInPlace);
		content.addComponent(box);
	}
	
	public void addBox(){
		AFInfoBox box = new AFInfoBox();
		content.addComponent(box);
	}
	public boolean removeBox(String name){
		int numBoxes = content.getComponentCount();
		for(int i = 0; i < numBoxes; i++){
			AFInfoBox box = (AFInfoBox) content.getComponent(i);
			if (box.getName().equals(name)){
				content.removeComponent(box);
				return true;
			}
		}
		return false;
	}
}

