package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * This class defines the bar overlayed on the map that signals when a route is in edit mode
 * 
 * @author James Holland
 */

public class FReditBar extends CustomComponent{

	private static final long serialVersionUID = 2389713576038720628L;
	HorizontalLayout totalLayout = new HorizontalLayout();
	Button cancelButton = new Button("Cancel");
	Button saveButton = new Button("Save");
	
	Label textLabel = new Label("Editing Route");
	Label smallText = new Label("Left click to add a new waypoint. Drag waypoints to move.");
	
	public FReditBar(FRMapComponent map) {
		setStyleName("fr_edit_bar");
		textLabel.setStyleName("large_text");
		smallText.setStyleName("small_text");
		
		cancelButton.setHeight("25px");
		saveButton.setHeight("25px");
		totalLayout.addComponents(textLabel, smallText, cancelButton, saveButton);
		setCompositionRoot(totalLayout);
		
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
