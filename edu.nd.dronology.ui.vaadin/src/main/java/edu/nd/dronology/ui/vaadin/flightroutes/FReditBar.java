package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class FReditBar extends CustomComponent{

	private static final long serialVersionUID = 2389713576038720628L;
	HorizontalLayout totalLayout = new HorizontalLayout();
	Button cancelButton = new Button("Cancel");
	Button saveButton = new Button("Save");
	
	Label textLabel = new Label("Editing Route");
	Label smallText = new Label("Left click to add a new waypoint. Right click to finish drawing and add a finish point. Drag waypoints to move.");
	
	public FReditBar() {
		setStyleName("fr_top_bar");
		textLabel.setStyleName("large_text");
		smallText.setStyleName("small_text");
		
		cancelButton.setHeight("25px");
		saveButton.setHeight("25px");
		totalLayout.addComponents(textLabel, smallText, cancelButton, saveButton);
		setCompositionRoot(totalLayout);
	}
	public Button getCancelButton(){
		return cancelButton;
	}
	public Button getSaveButton(){
		return saveButton;
	}
	
}
