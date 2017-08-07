package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.CustomComponent;

/**
 * This is the control panel framework for the Flight Routes UI. It has an info panel, which displays the list of flight routes, and a main layout, which
 * displays the map and grid.
 * 
 * @author Jinghui Cheng
 */
public class FRControlsComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;
	FRInfoPanel information = new FRInfoPanel(this);
	FRMainLayout mainLayout;
	
	public FRControlsComponent(FRMainLayout layout) {
		this.setWidth("100%");
		addStyleName("controls_component");
		setCompositionRoot(information);
		mainLayout = layout;
	}
	public FRInfoPanel getInfoPanel() {
		return information;
		// Returns the list of flight routes, which appears on the left hand side of the display.
	}
	public FRMainLayout getMainLayout() {
		return mainLayout;
		// Returns the table and grid combination, which appears on the right hand side of the display.
	}
}