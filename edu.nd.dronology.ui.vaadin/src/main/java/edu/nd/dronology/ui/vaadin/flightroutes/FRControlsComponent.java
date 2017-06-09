package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the control panel framework for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRControlsComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	public FRControlsComponent() {
		this.setWidth("100%");
		addStyleName("af_controls_component");
		
		VerticalLayout content = new VerticalLayout();
		
		Button btn = new Button("FR Page");
		content.addComponent(btn);
    setCompositionRoot(content);
	}
}
