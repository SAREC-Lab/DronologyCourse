package edu.nd.dronology.ui.vaadin.flightroutes.windows;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.ui.vaadin.flightroutes.FRMapComponent;
import edu.nd.dronology.ui.vaadin.utils.UIWayPoint;

// Window that allows the user to enter altitude and transit speed information.
@SuppressWarnings("serial")
public class FRNewWayPointWindow extends Window {
	public FRNewWayPointWindow(FRMapComponent mapComponent) {
		HorizontalLayout buttons = new HorizontalLayout();
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		buttons.addComponents(saveButton, cancelButton);

		// Creates a vertical layout, which is then used to instantiate a window.
		VerticalLayout windowContent = new VerticalLayout();
		TextField altitudeField = new TextField("Altitude: ");
		TextField transitSpeedField = new TextField("Transit Speed: ");

		windowContent.addComponents(altitudeField, transitSpeedField, buttons);
		
		saveButton.addClickListener(event -> {
			boolean canSave = true;
			boolean transitSpeedInvalid = false;
			String caption = "";
			
			String altitude = altitudeField.getValue();
			try {
				Float.valueOf(altitude);
			} catch (NumberFormatException ex) {
				caption = "Altitude must be a number.";
				canSave = false;
			}
			
			if (altitude.isEmpty()) {
				caption = "Altitude must be a number.";
				canSave = false;
			}
			
			String transitSpeed = transitSpeedField.getValue();
			try {
				Float.valueOf(transitSpeed);
			} catch (NumberFormatException ex) {
				if (caption.isEmpty()) {
					caption = "Transit speed must be a number.";
				} else {
					caption = caption + "\n" + "Transit speed must be a number.";
				}
				canSave = false;
				transitSpeedInvalid = true;
			}
			
			if (transitSpeed.isEmpty() && !transitSpeedInvalid) {
				if (caption.isEmpty()) {
					caption = "Transit speed must be a number.";
				} else {
					caption = caption + "\n" + "Transit speed must be a number.";
				}
				canSave = false;
			}
			
		    	if (canSave) {
		    		UI.getCurrent().removeWindow(this);
		    		
		    		UIWayPoint w = (UIWayPoint) mapComponent.getMapUtilities().getMapAddMarkerListener().getNewMarker().getData();
		    		w.setAltitude(altitude);
		    		w.setTransitSpeed(transitSpeed);
		    		
		    		mapComponent.updateLinesAndGrid();
		    		mapComponent.updateWayPointCount(mapComponent.getMapUtilities());
		    	}
		    	else {
		    		Notification.show(caption);
		    	}
		    	// Checks to make sure the input altitude and transit speed are valid floats. If they are not, an error is output in the form of a Notification.
		});
		
		cancelButton.addClickListener(event -> {
			mapComponent.getMapUtilities().removePinById(
					mapComponent.getMapUtilities().getMapAddMarkerListener().getNewMarker().getId());
			UI.getCurrent().removeWindow(this);
		});
		
		this.setContent(windowContent);
		this.addStyleName("confirm_window");
		buttons.addStyleName("confirm_button_area");
		saveButton.addStyleName("btn-okay");
		
		this.setModal(true);
		this.setClosable(false);
		this.setResizable(false);
	}
}
