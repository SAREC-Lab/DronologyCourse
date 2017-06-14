package edu.nd.dronology.ui.vaadin.activeflights;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class AFInfoPanel extends CustomComponent{
	private static final long serialVersionUID = -3663049148276256302L;
	private Panel panel = new Panel();
	private VerticalLayout content = new VerticalLayout();
	private int numUAVs = content.getComponentCount();
	private boolean selectAll = true;
	private boolean visible = false;
	public AFInfoPanel(){
		
		panel.setCaption(Integer.toString(numUAVs) + " Active UAVs");
		panel.setContent(content);
		panel.setHeight("500px");
		setCompositionRoot(panel);
		
		HorizontalLayout buttons = new HorizontalLayout();
		
		Button selectButton = new Button("Select all");
	  selectButton.addStyleName(ValoTheme.BUTTON_LINK);
	  Button visibleButton = new Button("Expand all");
	  visibleButton.addStyleName(ValoTheme.BUTTON_LINK);
	  
	  buttons.addComponents(selectButton, visibleButton);
	  
	  selectButton.addClickListener( e -> {
	  	if (selectAll){
	  		selectAll(true);
	  		selectButton.setCaption("Deselect all");
	  		selectAll = false;
	  	}
	  	else {
	  		selectAll(false);
	  		selectButton.setCaption("Select all");
	  		selectAll = true;
	  	}
	  });
	  
	  visibleButton.addClickListener( e -> {
	  	if (visible){
	  		visible = false;
	  		setVisibility(true);
	  		visibleButton.setCaption("Expand all");
	  	}
	  	else {
	  		visible = true;
	  		setVisibility(false);
	  		visibleButton.setCaption("Collapse all");
	  	}
	  });
		
	  content.addComponent(buttons);
	  
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
		numUAVs = content.getComponentCount();
		panel.setCaption(Integer.toString(numUAVs) + " Active UAVs");
	}
	
	public void addBox(){
		AFInfoBox box = new AFInfoBox();
		content.addComponent(box);
		numUAVs = content.getComponentCount();
		panel.setCaption(Integer.toString(numUAVs) + " Active UAVs");
	}
	
	public boolean removeBox(String name){
		for(int i = 1; i < numUAVs; i++){
			AFInfoBox box = (AFInfoBox) content.getComponent(i);
			if (box.getName().equals(name)){
				content.removeComponent(box);
				numUAVs = content.getComponentCount();
				panel.setCaption(Integer.toString(numUAVs) + " Active UAVs");
				return true;
			}
		}
		return false;
	}
	
	public void selectAll(boolean select){
		for(int i = 1; i < numUAVs; i++){
			AFInfoBox box = (AFInfoBox) content.getComponent(i);
			box.setIsChecked(select);
		}
	}
	
	public void setVisibility(boolean visible){
		for(int i = 1; i < numUAVs; i++){
			AFInfoBox box = (AFInfoBox) content.getComponent(i);
			box.setBoxVisible(visible);
		}
	}
	
}

