package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CssLayout;

/**
 * This is the main layout for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRMainLayout extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	public FRMainLayout() {
		addStyleName("af_main_layout");
		
		CssLayout content = new CssLayout();
		content.setSizeFull();
		
		FRControlsComponent controls = new FRControlsComponent();
        
		FRMapComponent map = new FRMapComponent(
  		"VAADIN/sbtiles/{z}/{x}/{y}.png",
  		"South Bend");
    map.setCenter(41.68, -86.25);
    map.setZoomLevel(13);
    
    content.addComponents(controls, map);
    setCompositionRoot(content);
	}
}
