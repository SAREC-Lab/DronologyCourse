package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.CustomComponent;

/**
 * This is the control panel framework for the Flight Routes UI
 * 
 * @author Jinghui Cheng
 */
public class FRControlsComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;
	FRInfoPanel information = new FRInfoPanel();
	
	public FRControlsComponent() {
		this.setWidth("100%");
		addStyleName("controls_component");
		setCompositionRoot(information);
	}
	public FRInfoPanel getInfoPanel(){
		return information;
	}
}
