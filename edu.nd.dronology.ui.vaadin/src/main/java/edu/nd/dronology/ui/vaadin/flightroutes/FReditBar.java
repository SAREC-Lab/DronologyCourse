package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * This class defines the bar overlaid on the map that signals when a route is in edit mode
 * 
 * @author James Holland
 */

public class FReditBar extends CustomComponent{

	private static final long serialVersionUID = 2389713576038720628L;
	
	private HorizontalLayout totalLayout = new HorizontalLayout();
	private Button cancelButton = new Button("Cancel");
	private Button saveButton = new Button("Save");	
	private Label textLabel = new Label("Editing Route");
	private Label smallText = new Label("Left click to add a new waypoint. Drag waypoints to move.");
	
	public FReditBar(FRMapComponent map) {
		setStyleName("fr_edit_bar");
		textLabel.setStyleName("large_text");
		smallText.setStyleName("small_text");
		
		cancelButton.setHeight("25px");
		saveButton.setHeight("25px");
		totalLayout.addComponents(textLabel, smallText, cancelButton, saveButton);
		setCompositionRoot(totalLayout);
		
		//click listeners for the cancel and saves buttons on edit bar - function are defined in FRMapComponent
		cancelButton.addClickListener(e->{
			map.cancelClick();
		});
		saveButton.addClickListener(e->{
			map.saveClick();
		});
	}
	public Button getCancelButton(){
		return cancelButton;
	}
	public Button getSaveButton(){
		return saveButton;
	}
	
}
