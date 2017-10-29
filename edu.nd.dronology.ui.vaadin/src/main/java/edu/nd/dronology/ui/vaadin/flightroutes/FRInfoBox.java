package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRUnsavedChangesConfirmation.ChangeType;

/**
 * 
 * This layout gives information about each of the flight routes along with options to edit or delete.
 * 
 * @author James Holland
 *
 */

@SuppressWarnings("serial")
public class FRInfoBox extends CustomComponent {
	private FlightRouteInfo flightRouteInfo;
	
	private HorizontalLayout titleBar = new HorizontalLayout();
		
	//Creqte FRInfoBox in Flight Route view -- with edit and delete buttons
	public FRInfoBox(FRInfoPanel infoPanel, FlightRouteInfo flightRouteInfo){
		this(flightRouteInfo);
		
		// Imports images for buttons.
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/edit.png"));
		FileResource trashIcon = new FileResource(new File(basepath+"/VAADIN/img/trashcan.png"));
		
		Button editButton = new Button();
		Button trashButton = new Button();
		
		editButton.setIcon(editIcon);
		trashButton.setIcon(trashIcon);	
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		trashButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			
		// Adds listener to the delete button on the route box /
		trashButton.addListener(e->{
			if (infoPanel.getControls().getMainLayout().getMapComponent().getMapUtilities().isEditable()) {
				// Checks if the route is in edit mode.
				infoPanel.getControls().getMainLayout().getUnsavedChangesConfirmation().showWindow(
						infoPanel.getHighlightedFRInfoBox().getFlightRouteInfo().getName(), ChangeType.DELETE_ROUTE, e);
			} else {	
				infoPanel.getControls().getMainLayout().getDeleteRouteConfirmation().showWindow(getFlightRouteInfo(), e);
			}
		});
		// A click on the edit button enables editing, unless edit mode is already enabled, in which case the user is prompted about losing changes.
		editButton.addClickListener(e -> {
			if (!infoPanel.getControls().getMainLayout().getMapComponent().getMapUtilities().isEditable()) {
				infoPanel.getControls().getMainLayout().switchRoute(this);
				infoPanel.getControls().getMainLayout().getMapComponent().getEditModeController().enterEditMode();
			} else {
				if (infoPanel.getHighlightedFRInfoBox() != null &&
						flightRouteInfo.getId().equals(infoPanel.getHighlightedFRInfoBox().getId()))
					return;
				infoPanel.getControls().getMainLayout().getUnsavedChangesConfirmation().showWindow(
						infoPanel.getHighlightedFRInfoBox().getFlightRouteInfo().getName(), ChangeType.EDIT_ANOTHER, e);
			}
			infoPanel.getControls().getMainLayout().getMapComponent().getEditModeController().enterEditMode();
		});
		
		titleBar.addComponents(trashButton, editButton);
	}
	
	// This infobox constructor is called from activeflights.
	public FRInfoBox (FlightRouteInfo flightRouteInfo) {
		this.flightRouteInfo = flightRouteInfo;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm aaa");
		long creationTime = flightRouteInfo.getDateCreated();
		String creationFormatted = sdf.format(new Date(creationTime));
		long modifiedTime = flightRouteInfo.getDateModified();
		String modifiedFormatted = sdf.format(new Date(modifiedTime));
		
		this.addStyleName("info_box");
		this.addStyleName("fr_info_box");

		VerticalLayout routeDescription = new VerticalLayout();
		routeDescription.addStyleName("detailed_info_well");
		
		VerticalLayout allContent = new VerticalLayout();
		
		// Create name id label.
		Label nameIdLabel = new Label(flightRouteInfo.getName());
		nameIdLabel.addStyleName("info_box_name");
			
		// Creates 3 different labels and adds styles to format them appropriately.
		Label createdLabel = new Label("Created:  " + creationFormatted);
		Label modifiedLabel = new Label("Last Modified:  " + modifiedFormatted);
		Label lengthLabel = new Label("Total Length: " + String.valueOf(flightRouteInfo.getLenght()));
		
		routeDescription.addComponents(createdLabel, modifiedLabel, lengthLabel);
		
		titleBar.addComponents(nameIdLabel);
		
		// Adds all content together and aligns the buttons on the right.
		allContent.addComponents(titleBar, routeDescription);
		
		setCompositionRoot(allContent);
	}
	
	public FlightRouteInfo getFlightRouteInfo() {
		return flightRouteInfo;
	}
	// Gets the name of the route.
	public String getName() {
		return flightRouteInfo.getName();
	}
	// Gets the route id.
	@Override
	public String getId() {
		return flightRouteInfo.getId();
	}
}

