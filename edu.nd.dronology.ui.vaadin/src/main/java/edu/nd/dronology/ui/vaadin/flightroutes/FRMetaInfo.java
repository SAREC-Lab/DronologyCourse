package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.flightroutes.windows.FRUnsavedChangesConfirmation.ChangeType;

/**
 * 
 * This is the info bar above the map that gives route information and contains the edit and delete buttons.
 * 
 * @author jhollan4
 *
 */

@SuppressWarnings("serial")
public class FRMetaInfo extends CustomComponent {
	private FRMapComponent map;
	private Label nameLabel;
	private Label waypointNumLabel;
	private Label descriptionLabel;
	
	private HorizontalLayout allContent;
	private VerticalLayout leftSide;
	private VerticalLayout rightSide;
	private CheckBox autoZoomingCheckBox;
	private CheckBox tableViewCheckBox;

	public FRMetaInfo(FRMapComponent map) {
		this.map = map;
		
		waypointNumLabel = new Label ("");
		nameLabel = new Label("No Route Selected");
		
		// The two labels are initialized separately so that they can be changed independently later.
		HorizontalLayout labels = new HorizontalLayout();
		labels.addComponents(nameLabel, waypointNumLabel);

		autoZoomingCheckBox = new CheckBox("Zoom to Route");
		autoZoomingCheckBox.setValue(true);
		tableViewCheckBox = new CheckBox("Table View");
		tableViewCheckBox.setValue(true);
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/editButtonFull.png"));	
		Button editButton = new Button("Edit");
		editButton.setIcon(editIcon);
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		FileResource deleteIcon = new FileResource(new File(basepath+"/VAADIN/img/deleteButtonFull.png"));
		Button deleteButton = new Button("Delete");
		deleteButton.setIcon(deleteIcon);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		// A layout is used to hold the description label so that a LayoutClickListener can be added later.
		HorizontalLayout descriptionHolder = new HorizontalLayout();
		descriptionLabel = new Label("");
		descriptionHolder.addComponent(descriptionLabel);	
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(editButton, deleteButton);
		
		HorizontalLayout checkboxes = new HorizontalLayout();
		checkboxes.addComponents(autoZoomingCheckBox, tableViewCheckBox);
		
		leftSide = new VerticalLayout();
		leftSide.addComponents(labels, descriptionHolder);

		rightSide = new VerticalLayout();
		rightSide.addComponents(buttons, checkboxes);
		
		// "leftSide" includes the labels and description, while "rightSide" includes the buttons and checkboxes.
		allContent = new HorizontalLayout();
		allContent.setStyleName("fr_route_meta_info");
		allContent.addStyleName("has_route");
		// only add left side when initialized, right side is added when a route is selected
		allContent.addComponent(leftSide);

		rightSide.addStyleName("route_meta_controls");
		leftSide.addStyleName("route_meta_label_description");

		TextField nameEditField = new TextField();
		TextField descriptionEditField = new TextField();
		
		nameEditField.setStyleName("name_edit_field");
		descriptionEditField.setStyleName("description_edit_field");
		nameLabel.setStyleName("name_lable");
		waypointNumLabel.setStyleName("waypoint_num_lable");
		descriptionLabel.setStyleName("description_lable");
		
		// Click listeners for the edit and delete buttons.
		editButton.addClickListener(e -> {
			map.getEditModeController().enterEditMode();
		});
		deleteButton.addClickListener(e -> {
			if (map.getMapUtilities().isEditable()) {
				map.getMainLayout().getUnsavedChangesConfirmation().showWindow(
						map.getMainLayout().getControls().getInfoPanel().getHighlightedFRInfoBox().getFlightRouteInfo().getName(), ChangeType.DELETE_ROUTE, e);
			} else {
				map.getMainLayout().getDeleteRouteConfirmation().showWindow(
						map.getMainLayout().getControls().getInfoPanel().getHighlightedFRInfoBox().getFlightRouteInfo(), e);
			}
		});
		
		// Double click allows user to edit label by turning it into a textbox.
		labels.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if (event.getClickedComponent() == nameLabel) {
					if (event.isDoubleClick()) {
						// Change layout to accommodate for the textfield.
						allContent.removeAllComponents();
						
						HorizontalLayout nameArea = new HorizontalLayout();
						nameArea.addComponents(nameEditField, waypointNumLabel);
						
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
				if (event.getClickedComponent() == descriptionLabel) {
					if (event.isDoubleClick()) {	
						// Change layout to accommodate for the textfield.
						allContent.removeAllComponents();
						labels.removeAllComponents();
						
						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addStyleName("route_meta_label_description");
						labels.addComponents(nameLabel, waypointNumLabel);
						textLayout.addComponents(labels, descriptionEditField);		
						allContent.addComponents(textLayout, rightSide);
					}
				}
			}
		});
		// Textfield turns back into the correct label once the user clicks away.
		nameEditField.addBlurListener(e -> {			
			// Removes and re-adds components so that they are in correct layout.
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
			allContent.removeAllComponents();
			labels.removeAllComponents();
			nameLabel = new Label("<b>" + nameEditField.getValue() + "</b>", ContentMode.HTML);
			labels.addComponents(nameLabel, waypointNumLabel);
			
			leftSide.removeAllComponents();
			leftSide.addComponents(labels, descriptionHolder);
			
			allContent.addComponents(leftSide, rightSide);
			
			// Gets the value from the textbox and saves it to Dronology.
			rightSide.addStyleName("route_meta_controls");
			String routeName = nameEditField.getValue();
			map.setRouteNameDescription(routeName, null);
			nameEditField.setValue(routeName);
			
			// Waits to refresh routes so dronology can save.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
		});
		// Once the user clicks away from the description field, the correct label is shown.
		descriptionEditField.addBlurListener(e -> {
			// Removes and re-adds components so that they are in correct layout.
			String routeDescription = descriptionEditField.getValue();
			descriptionLabel.setValue(routeDescription);
			
			allContent.removeAllComponents();
			labels.removeAllComponents();
			labels.addComponents(nameLabel, waypointNumLabel);
			leftSide.addComponents(labels, descriptionHolder);
			allContent.addComponents(leftSide, rightSide);
			
			// Gets the value from the textbox and saves it to Dronology.
			rightSide.addStyleName("route_meta_controls");
			map.setRouteNameDescription(null, routeDescription);			
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();	
		});
		// Hides the table.
		tableViewCheckBox.addValueChangeListener(event -> {
			if (tableViewCheckBox.getValue()) {
				map.displayTable();
			} else {
				map.displayNoTable();
			}
		});
		
		setCompositionRoot(allContent);
	}

	public void showInfoForSelectedRoute (FlightRouteInfo info) {
		nameLabel.setValue(info.getName());
		descriptionLabel.setValue(map.getRouteDescription());
		setNumWaypoints(info.getWaypoints().size());
		
		allContent.addComponent(rightSide);
	}
	public void showInfoWhenNoRouteIsSelected () {
		nameLabel.setValue("No Route Selected");
		descriptionLabel.setValue("");
		waypointNumLabel.setValue("");
		allContent.removeComponent(rightSide);
	}
	
	// Ensures that the correct description of waypoints is shown.
	public void setNumWaypoints(int num) {
		if(num == 1) {
			waypointNumLabel.setValue("(" + num + " waypoint)");
		} else {
			waypointNumLabel.setValue("(" + num + " waypoints)");
		}
	}
	
	public boolean isAutoZoomingChecked() {
		return autoZoomingCheckBox.getValue() == true;
	}
	public boolean isTableViewChecked() {
		return tableViewCheckBox.getValue() == true;
	}
}
