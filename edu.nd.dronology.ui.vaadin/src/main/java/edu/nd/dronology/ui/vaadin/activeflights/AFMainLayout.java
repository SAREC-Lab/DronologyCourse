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
	
	public AFMainLayout() {
		addStyleName("af_main_layout");
		
		CssLayout content = new CssLayout();
		content.setSizeFull();
		
		AFControlsComponent controls = new AFControlsComponent();
        
			AFMapComponent map = new AFMapComponent(
				"VAADIN/sbtiles/{z}/{x}/{y}.png",
				"South Bend");
			map.setCenter(41.68, -86.25);
			map.setZoomLevel(13);
			
			content.addComponents(controls, map);
			setCompositionRoot(content);
	}
}
