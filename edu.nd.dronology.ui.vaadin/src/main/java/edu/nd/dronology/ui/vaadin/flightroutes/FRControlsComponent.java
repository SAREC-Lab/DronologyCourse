package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;

/**
 * This is the control panel framework for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRControlsComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	public FRControlsComponent() {
		this.setWidth("100%");
		addStyleName("controls_component");
		
		HorizontalLayout content = new HorizontalLayout();
		FRInfoPanel information = new FRInfoPanel();
		
		content.addComponent(information);
		
		setCompositionRoot(content);
	}
}
