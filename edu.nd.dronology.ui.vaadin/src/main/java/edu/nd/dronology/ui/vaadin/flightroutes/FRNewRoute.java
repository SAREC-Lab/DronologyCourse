package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class FRNewRoute extends CustomComponent{
	
	Label directions = new Label("Please enter a route name");
	TextField inputField = new TextField();
	Button cancelButton = new Button("Cancel");
	Button drawButton = new Button("Draw Route");
	
	HorizontalLayout buttonLayout = new HorizontalLayout();
	VerticalLayout totalLayout = new VerticalLayout();
	
	public FRNewRoute(){
		buttonLayout.addComponents(cancelButton, drawButton);
		totalLayout.addComponents(directions, inputField, buttonLayout);
		
		setCompositionRoot(totalLayout);
	}
	
	public Button getDrawButton(){
		return drawButton;
	}
	public TextField getInputField(){
		return inputField;
	}
}
