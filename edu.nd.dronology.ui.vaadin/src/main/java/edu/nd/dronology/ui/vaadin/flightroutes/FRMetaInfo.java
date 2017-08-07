package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.flightroutes.confirmation.FRUnsavedChangesConfirmation.ChangeType;

/**
 * 
 * This is the info bar above the map that gives route information and contains the edit and delete buttons.
 * 
 * @author jhollan4
 *
 */

public class FRMetaInfo extends CustomComponent {
	private static final long serialVersionUID = -2718986455485823804L;

	private String routeName;
	private int numWaypoints;
	private Label nameOnly;
	private Label waypointLabel;
	private Button editButton;
	private Button deleteButton;
	private CheckBox autoZooming;
	private CheckBox tableView;
	private String routeDescription;
	private TextField textField = new TextField();
	private TextField descriptionField = new TextField();

	public FRMetaInfo(String name, int numCoords, FRMapComponent map, boolean zoomRoute) {
		routeName = name;
		numWaypoints = numCoords;
		nameOnly = new Label("<b>" + routeName + "</b>", ContentMode.HTML);
		
		if(numWaypoints == 1) {
			waypointLabel = new Label(" (" +  numWaypoints +  " waypoint)", ContentMode.HTML);
		} else {
			waypointLabel = new Label(" (" +  numWaypoints +  " waypoints)", ContentMode.HTML);
		}
		// The two labels are initialized separately so that they can be changed independently later.
		HorizontalLayout labels = new HorizontalLayout();
		labels.addComponents(nameOnly, waypointLabel);

		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/editButtonFull.png"));	
		editButton = new Button("Edit");
		editButton.setIcon(editIcon);
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		FileResource deleteIcon = new FileResource(new File(basepath+"/VAADIN/img/deleteButtonFull.png"));
		deleteButton = new Button("Delete");
		deleteButton.setIcon(deleteIcon);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

		autoZooming = new CheckBox("Zoom to Route");
		autoZooming.setValue(zoomRoute);
		
		HorizontalLayout zoomContent = new HorizontalLayout();
		zoomContent.setStyleName("fr_route_meta_info");
		zoomContent.addStyleName("has_route");
		
		tableView = new CheckBox("Table View");
		
		HorizontalLayout allContent = new HorizontalLayout();
		allContent.setStyleName("fr_route_meta_info");
		allContent.addStyleName("has_route");
		tableView.setValue(true);
		
		// A layout is used to hold the description label so that a LayoutClickListener can be added later.
		HorizontalLayout descriptionHolder = new HorizontalLayout();
		routeDescription = map.getRouteDescription();
		Label description = new Label(routeDescription);
		descriptionHolder.addComponent(description);	
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(editButton, deleteButton);
		
		HorizontalLayout checkboxes = new HorizontalLayout();
		checkboxes.addComponents(autoZooming, tableView);
		
		VerticalLayout leftSide = new VerticalLayout();
		leftSide.addComponents(labels, descriptionHolder);

		VerticalLayout rightSide = new VerticalLayout();
		rightSide.addComponents(buttons, checkboxes);
		
		// "leftSide" includes the labels and description, while "rightSide" includes the buttons and checkboxes.
		allContent.addComponents(leftSide, rightSide);
		allContent.setComponentAlignment(leftSide, Alignment.TOP_LEFT);
		allContent.setComponentAlignment(rightSide, Alignment.MIDDLE_RIGHT);

		rightSide.addStyleName("route_meta_controls");
		leftSide.addStyleName("route_meta_label_description");
		
		textField.setValue(name);
		descriptionField.setValue(routeDescription);
		
		textField.setStyleName("name_edit_field");
		descriptionField.setStyleName("description_edit_field");
		nameOnly.setStyleName("name_lable");
		waypointLabel.setStyleName("waypoint_num_lable");
		description.setStyleName("description_lable");
		
		// Click listeners for the edit and delete buttons.
		editButton.addClickListener(e -> {
			map.editButton();
		});
		deleteButton.addClickListener(e -> {
			if (map.getUtilities().isEditable()) {
				map.getMainLayout().getUnsavedChangesConfirmation().showWindow(
						map.getMainLayout().getCurrentRouteName(), ChangeType.DELETE_ROUTE, e);
			} else {
				map.getMainLayout().getDeleteRouteConfirmation().showWindow(
						map.getSelectedRoute());
			}
		});
		
		// Double click allows user to edit label by turning it into a textbox.
		labels.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if (event.getClickedComponent() == nameOnly) {
					if (event.isDoubleClick()) {
						// Change layout to accommodate for the textfield.
						allContent.removeAllComponents();
						
						HorizontalLayout nameArea = new HorizontalLayout();
						nameArea.addComponents(textField, waypointLabel);
						
						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addComponents(nameArea, descriptionHolder);
						textLayout.addStyleName("route_meta_label_description");

						allContent.addComponents(textLayout, rightSide);
					}
				}
			}
		});
		// Double click allows user to edit description by turning it into a textbox.
		descriptionHolder.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if (event.getClickedComponent() == description) {
					if (event.isDoubleClick()) {	
						// Change layout to accommodate for the textfield.
						allContent.removeAllComponents();
						labels.removeAllComponents();
						
						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addStyleName("route_meta_label_description");
						labels.addComponents(nameOnly, waypointLabel);
						textLayout.addComponents(labels, descriptionField);		
						allContent.addComponents(textLayout, rightSide);
					}
				}
			}
		});
		// Textfield turns back into the correct label once the user clicks away.
		textField.addBlurListener(e -> {			
			// Removes and re-adds components so that they are in correct layout.
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
			allContent.removeAllComponents();
			labels.removeAllComponents();
			nameOnly = new Label("<b>" + textField.getValue() + "</b>", ContentMode.HTML);
			labels.addComponents(nameOnly, waypointLabel);
			
			leftSide.removeAllComponents();
			leftSide.addComponents(labels, descriptionHolder);
			
			allContent.addComponents(leftSide, rightSide);
			
			// Gets the value from the textbox and saves it to Dronology.
			rightSide.addStyleName("route_meta_controls");
			String routeName = textField.getValue();
			map.setRouteNameDescription(routeName, true);
			textField.setValue(routeName);
			
			// Waits to refresh routes so dronology can save.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
		});
		// Once the user clicks away from the description field, the correct label is shown.
		descriptionField.addBlurListener(e -> {
			// Removes and re-adds components so that they are in correct layout.
			routeDescription = descriptionField.getValue();
			description.setValue(routeDescription);
			
			allContent.removeAllComponents();
			labels.removeAllComponents();
			labels.addComponents(nameOnly, waypointLabel);
			leftSide.addComponents(labels, descriptionHolder);
			allContent.addComponents(leftSide, rightSide);
			
			// Gets the value from the textbox and saves it to Dronology.
			rightSide.addStyleName("route_meta_controls");
			descriptionField.setValue(routeDescription);
			map.setRouteNameDescription(routeDescription, false);
			routeDescription = map.getRouteDescription();	
			
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();	
		});
		
		setCompositionRoot(allContent);
	}	
	// Constructor when no route is selected.
	public FRMetaInfo() {
		routeName = "No Route Selected";
		Label nameLabel = new Label(routeName);
		
		HorizontalLayout information = new HorizontalLayout();
		information.addComponent(nameLabel);
		information.setComponentAlignment(nameLabel, Alignment.MIDDLE_LEFT);
		information.setStyleName("fr_route_meta_info");
		information.addStyleName("no_route");
		
		setCompositionRoot(information);
	}
	// Constructor is equivalent to the previous one except that it takes a FlightRouteInfo object as a parameter.
	public FRMetaInfo(FlightRouteInfo info, FRMapComponent map, boolean zoomRoute) {
		this(info.getName(), info.getWaypoints().size(), map, zoomRoute);	
	}
	// Ensures that the correct description of waypoints is shown.
	public void setNumWaypoints(int num) {
		if (waypointLabel == null) {
			waypointLabel = new Label();
		}
		numWaypoints = num;
		if(numWaypoints == 1) {
			waypointLabel.setValue("<b>" + routeName + "</b>" + " (" + numWaypoints + " waypoint)");
			waypointLabel.setContentMode(ContentMode.HTML);
		} else {
			waypointLabel.setValue("<b>" + routeName + "</b>" + " (" + numWaypoints + " waypoints)");
			waypointLabel.setContentMode(ContentMode.HTML);
		}
	}
	public void setName(String routeName) {
		this.routeName = routeName; 
	}
	public CheckBox getAutoZooming() {
		return autoZooming;
	}
	public CheckBox getCheckBox() {
		return tableView;
	}
	public Button getEditButton() {
		return editButton;
	}
	public Button getDeleteButton() {
		return deleteButton;
	}
}
