package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the menu that allows the user to add a new route with a specified  name and description.
 * 
 * @author James Holland
 */

public class FRNewRoute extends CustomComponent{
	
	private static final long serialVersionUID = 6262120678931967634L;
	
	private VerticalLayout totalLayout = new VerticalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private Label directions = new Label("Please enter a route name");
	private Label description = new Label("Route Description");
	private TextArea descriptionField = new TextArea();
	private TextField inputField = new TextField();
	private Button cancelButton = new Button("Cancel");
	private Button drawButton = new Button("Draw Route");
	
	public FRNewRoute() {
		// Arranges layout of new route window.
		buttonLayout.addComponents(cancelButton, drawButton);
		totalLayout.addComponents(directions, inputField, description, descriptionField, buttonLayout);
		
		this.addStyleName("fr_add_route_layout");
		buttonLayout.addStyleName("confirm_button_area");
		drawButton.addStyleName("btn-okay");
		
		setCompositionRoot(totalLayout);
	}
	public Button getDrawButton() {
		return drawButton;
	}
	public TextField getInputField() {
		return inputField;
	}
	public Button getCancelButton() {
		return cancelButton;
	}
	public TextArea getDescriptionField() {
		return descriptionField;
	}
}
