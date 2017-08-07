package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.flightroutes.confirmation.FRUnsavedChangesConfirmation.ChangeType;

/**
 * 
 * This layout gives information about each of the flight routes along with options to edit or delete.
 * 
 * @author James Holland
 *
 */

@SuppressWarnings("serial")
public class FRInfoBox extends CustomComponent {
	private FRInfoPanel infoPanel;
	
	private VerticalLayout allContent;
	private VerticalLayout routeDescription;
	private HorizontalLayout titleBar;
	private String name;
	private String id;
	private String modified;
	private Button editButton;
	private Button trashButton;
	private Label nameIdLabel;
	
	String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	
	public FRInfoBox(String name, String id, String created, String modified, String length, FRInfoPanel infoPanel){
		this.infoPanel = infoPanel;
		this.name = name;
		this.id = id;
		this.modified = modified;
		
		this.addStyleName("info_box");
		this.addStyleName("fr_info_box");

		routeDescription = new VerticalLayout();
		titleBar = new HorizontalLayout();
		allContent = new VerticalLayout();
		routeDescription.addStyleName("detailed_info_well");
	
		// Create name id label.
		nameIdLabel = new Label(name);
		nameIdLabel.addStyleName("info_box_name");
			
		// Creates 3 different labels and adds styles to format them appropriately.
		Label createdLabel = new Label("Created:  " + created);
		Label modifiedLabel = new Label("Last Modified:  " + modified);
		Label lengthLabel = new Label("Total Length: " + length);
		
		routeDescription.addComponents(createdLabel, modifiedLabel, lengthLabel);
		
		// Imports images for buttons.
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/edit.png"));
		FileResource trashIcon = new FileResource(new File(basepath+"/VAADIN/img/trashcan.png"));
		
		editButton = new Button();
		trashButton = new Button();
		
		editButton.setIcon(editIcon);
		trashButton.setIcon(trashIcon);	
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		trashButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		titleBar.addComponents(nameIdLabel, trashButton, editButton);
		
		// Adds all content together and aligns the buttons on the right.
		allContent.addComponents(titleBar, routeDescription);
		
		setCompositionRoot(allContent);
		
		// Adds listener to the delete button on the route box /
		trashButton.addListener(e->{
			if (infoPanel.getControls().getMainLayout().getMap().getUtilities().isEditable()) {
				// Checks if the route is in edit mode.
				infoPanel.getControls().getMainLayout().getUnsavedChangesConfirmation().showWindow(
						infoPanel.getControls().getMainLayout().getCurrentRouteName(), ChangeType.DELETE_ROUTE, e);
			} else {	
				infoPanel.getControls().getMainLayout().getDeleteRouteConfirmation().showWindow(getFlightRouteInfo());
			}
		});
		// A click on the edit button enables editing, unless edit mode is already enabled, in which case the user is prompted about losing changes.
		editButton.addClickListener(e -> {
			if (infoPanel.getControls().getMainLayout().getMap().getSelectedRoute() != null &&
					this.id.equals(infoPanel.getControls().getMainLayout().getMap().getSelectedRoute().getId()))
				return;
			
			if (!infoPanel.getControls().getMainLayout().getMap().getUtilities().isEditable()) {
				infoPanel.getControls().getMainLayout().enableMapEdit();
				infoPanel.getControls().getMainLayout().editClick(this);
			} else {
				infoPanel.getControls().getMainLayout().getUnsavedChangesConfirmation().showWindow(
						infoPanel.getControls().getMainLayout().getCurrentRouteName(), ChangeType.EDIT_ANOTHER, e);
			}
			infoPanel.getControls().getMainLayout().getMap().editButton();
		});
	}
	// This infobox constructor is called from activeflights.
	public FRInfoBox(String name, String id, String created, String modified, String length) {
		this.name = name;
		this.id = id;
		this.modified = modified;
		
		this.addStyleName("info_box");
		this.addStyleName("fr_info_box");

		VerticalLayout routeDescription = new VerticalLayout();
		routeDescription.addStyleName("detailed_info_well");
		
		HorizontalLayout titleBar = new HorizontalLayout();
		VerticalLayout allContent = new VerticalLayout();
		
		// Create name id label.
		Label nameIdLabel = new Label(name);
		nameIdLabel.addStyleName("info_box_name");
			
		// Creates 3 different labels and adds styles to format them appropriately.
		Label createdLabel = new Label("Created:  " + created);
		Label modifiedLabel = new Label("Last Modified:  " + modified);
		Label lengthLabel = new Label("Total Length: " + length);
		
		routeDescription.addComponents(createdLabel, modifiedLabel, lengthLabel);
		
		// Imports images for buttons.
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/edit.png"));
		FileResource trashIcon = new FileResource(new File(basepath+"/VAADIN/img/trashcan.png"));
		
		editButton = new Button();
		trashButton = new Button();
		
		editButton.setIcon(editIcon);
		trashButton.setIcon(trashIcon);
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		trashButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		titleBar.addComponents(nameIdLabel, trashButton, editButton);
		
		// Adds all content together and aligns the buttons on the right.
		allContent.addComponents(titleBar, routeDescription);
		
		setCompositionRoot(allContent);
	}
	// Default if no parameters are passed.
	public FRInfoBox(FRInfoPanel panel) {	
		this("NAME", "id", "Jun 3, 2017, 9:24 AM", "Jun 8, 2017, 11:04 AM", "2.1 miles", panel);
	}
	
	public FlightRouteInfo getFlightRouteInfo() {
		String id = this.getId();
		// Uses the id of the specific infobox and the route list from the infopanel to find the index of the route that should be deleted.
		for (int i = 0; i < infoPanel.getRoutes().getComponentCount(); i++) {
			FRInfoBox local = (FRInfoBox) infoPanel.getRoutes().getComponent(i);
			if (local.getId().equals(id)) {
				return infoPanel.getFlightRouteInfo(i);
			}
		}
		return null;
	}
	// Gets the name of the route.
	public String getName() {
		return name;
	}
	// Sets the name of the route.
	public void setName(String name) {
		this.name = name;
	}
	// Gets the route id.
	@Override
	public String getId() {
		return id;
	}
	// Sets the route id.
	public void setid(String id) {
		this.id = id;
	}
	// Gets the modified time and date.
	public String getModified() {
		return modified;
	}
	// Sets the modified time and date.
	public void setModified(String modified) {
		this.modified = modified;
	}
	// Gets the delete button on the edit box.
	public Button getTrashButton() {
		return trashButton;
	}
	// Gets the edit button on the info box.
	public Button getEditButton() {
		return editButton;
	}
}

