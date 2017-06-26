package edu.nd.dronology.ui.vaadin.flightroutes;

import java.awt.Panel;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class FReditBar extends CustomComponent{

	HorizontalLayout totalLayout = new HorizontalLayout();
	Button cancelButton = new Button("Cancel");
	Button saveButton = new Button("Save");
	
	Label textLabel = new Label("Editing Route");
	Label smallText = new Label("Left click to add a new waypoint. Right click to finish drawing and add a finish point. Drag waypoints to move.");
	
	public FReditBar() {
		
		textLabel.setStyleName("large_text");
		smallText.setStyleName("small_text");
		//cancelButton.setStyleName("cancel_button");
		
		cancelButton.setHeight("25px");
		saveButton.setHeight("25px");
		totalLayout.addComponents(textLabel, smallText, cancelButton, saveButton);
		//totalLayout.setWidth("500px");
		setCompositionRoot(totalLayout);
	}
	public Button getCancelButton(){
		return cancelButton;
	}
	public Button getSaveButton(){
		return saveButton;
	}
	
}
