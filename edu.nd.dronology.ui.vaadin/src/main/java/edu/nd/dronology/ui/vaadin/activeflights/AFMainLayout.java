package edu.nd.dronology.ui.vaadin.activeflights;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CssLayout;

/**
 * This is the main layout for the Active Flights UI
 * 
 * @author Jinghui Cheng
 */
public class AFMainLayout extends CustomComponent {
	private static final long serialVersionUID = 1L;
	private AFControlsComponent controls = new AFControlsComponent();
	private AFMapComponent map = new AFMapComponent(
			"VAADIN/sbtiles/{z}/{x}/{y}.png",
			"South Bend");
	public AFMainLayout() {
		addStyleName("main_layout");
		
		CssLayout content = new CssLayout();
		content.setSizeFull();	
			
			content.addComponents(controls, map);
			setCompositionRoot(content);
	}
	
	public AFControlsComponent getControls(){
		return controls;
	}
	
	public AFMapComponent getAFMap(){
		return map;
	}
}
