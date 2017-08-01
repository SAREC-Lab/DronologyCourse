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

/**
 * 
 * This is the info bar above the map that gives route information and contains the edit and delete buttons
 * 
 * @author jhollan4
 *
 */

public class FRMetaInfo extends CustomComponent {

	private static final long serialVersionUID = -2718986455485823804L;
	private CheckBox autoZooming;
	private CheckBox tableView;
	private Button editButton;
	private Button deleteButton;
	private Label waypointLabel;
	private Label nameOnly;
	private TextField textField = new TextField();
	private TextField descriptionField = new TextField();
	private String routeDescription;
	private String routeName;
	boolean descriptionSet = false;
	private int numWaypoints;
	
	public FRMetaInfo(String name, int numCoords, FRMapComponent map, boolean toDo){
		//used if route is selected
		HorizontalLayout allContent = new HorizontalLayout();
		HorizontalLayout zoomContent = new HorizontalLayout();
		HorizontalLayout buttons = new HorizontalLayout();
		HorizontalLayout checkboxes = new HorizontalLayout();
		HorizontalLayout descriptionHolder = new HorizontalLayout();
		HorizontalLayout labels = new HorizontalLayout();
		VerticalLayout rightSide = new VerticalLayout();
		VerticalLayout leftSide = new VerticalLayout();
		
		routeName = name;
		numWaypoints = numCoords;
		
		nameOnly = new Label("<b>" + routeName + "</b>", ContentMode.HTML);
		
		if(numWaypoints == 1){
			waypointLabel = new Label(" (" +  numWaypoints +  " waypoint)", ContentMode.HTML);
		}
		else{
			waypointLabel = new Label(" (" +  numWaypoints +  " waypoints)", ContentMode.HTML);
		}
		
		labels.addComponents(nameOnly, waypointLabel);
		
		editButton = new Button("Edit");
		deleteButton = new Button("Delete");
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/editButtonFull.png"));
		FileResource deleteIcon = new FileResource(new File(basepath+"/VAADIN/img/deleteButtonFull.png"));
		
		editButton.setIcon(editIcon);
		deleteButton.setIcon(deleteIcon);
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

		autoZooming = new CheckBox("Zoom to Route");
		zoomContent.setStyleName("fr_route_meta_info");
		zoomContent.addStyleName("has_route");
		autoZooming.setValue(toDo);
		
		tableView = new CheckBox("Table View");
		allContent.setStyleName("fr_route_meta_info");
		allContent.addStyleName("has_route");
		tableView.setValue(true);
		
		routeDescription = map.getRouteDescription();
		Label description = new Label(routeDescription);
		descriptionHolder.addComponent(description);	
		
		leftSide.addComponents(labels, descriptionHolder);
		buttons.addComponents(editButton, deleteButton);
		checkboxes.addComponents(autoZooming, tableView);
		rightSide.addComponents(buttons, checkboxes);
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
		
		//click listeners for the edit and delete buttons
		editButton.addClickListener(e->{
			map.editButton();
		});
		deleteButton.addClickListener(e->{
			if (map.getUtils().isEditable()) {
				map.getMainLayout().deleteInEdit();
			}else{
				map.deleteClick();
			}
		});
		
		//double click allows user to edit label by turning it into a textbox
		labels.addLayoutClickListener(new LayoutClickListener(){
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event){
				if(event.getClickedComponent() == nameOnly){
					if(event.isDoubleClick()){
						//change layout to accomodate textfield
						allContent.removeAllComponents();
						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addStyleName("route_meta_label_description");
						HorizontalLayout nameArea = new HorizontalLayout();
						
						nameArea.addComponents(textField, waypointLabel);
						textLayout.addComponents(nameArea, descriptionHolder);
						allContent.addComponents(textLayout, rightSide);
					}
				}
			}
		});
		//double click allows user to edit description by turning it into a textbox
		descriptionHolder.addLayoutClickListener(new LayoutClickListener(){
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event){
				if(event.getClickedComponent() == description){
					if(event.isDoubleClick()){	
						//change layout to accomodate textfield
						allContent.removeAllComponents();
						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addStyleName("route_meta_label_description");
						labels.removeAllComponents();
						labels.addComponents(nameOnly, waypointLabel);
						textLayout.addComponents(labels, descriptionField);		
						allContent.addComponents(textLayout, rightSide);
					}
				}
			}
		});
		//textfield turns back into correct label once the user clicks away
		textField.addBlurListener(e->{			
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
			allContent.removeAllComponents();
			labels.removeAllComponents();
	
			nameOnly = new Label("<b>" + textField.getValue() + "</b>", ContentMode.HTML);
			labels.addComponents(nameOnly, waypointLabel);
			leftSide.removeAllComponents();
			leftSide.addComponents(labels, descriptionHolder);
			
			allContent.addComponents(leftSide, rightSide);
			
			rightSide.addStyleName("route_meta_controls");
			String routeName = textField.getValue();
			map.setRouteNameDescription(routeName, true);
			textField.setValue(routeName);
			
			//waits to refresh routes so dronology can save
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
		});
		//once the user clicks away from the description field, correct label is shown
		descriptionField.addBlurListener(e->{
			routeDescription = descriptionField.getValue();
			description.setValue(routeDescription);
			
			allContent.removeAllComponents();
			labels.removeAllComponents();
			labels.addComponents(nameOnly, waypointLabel);
			leftSide.addComponents(labels, descriptionHolder);
			allContent.addComponents(leftSide, rightSide);
			
			rightSide.addStyleName("route_meta_controls");
			descriptionField.setValue(routeDescription);
			map.setRouteNameDescription(routeDescription, false);
			routeDescription = map.getRouteDescription();	
			
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();	
		});
		
		setCompositionRoot(allContent);
	}	
	//constructor when no route is selected
	public FRMetaInfo(){
		HorizontalLayout information = new HorizontalLayout();
		routeName = "No Route Selected";
		Label nameLabel = new Label(routeName);
		
		information.setStyleName("fr_route_meta_info");
		information.addStyleName("no_route");
		
		information.addComponent(nameLabel);
		information.setComponentAlignment(nameLabel, Alignment.MIDDLE_LEFT);
		setCompositionRoot(information);
	}
	//constructor is equivalent to the previous one except that it takes a FlightRouteInfo object as a parameter
	public FRMetaInfo(FlightRouteInfo info, FRMapComponent map, boolean toDo){
		this(info.getName(), info.getWaypoints().size(), map, toDo);	
	}
	//ensures that the correct description of waypoints is shown
	public void setNumWaypoints(int num){
		if (waypointLabel == null) {
			waypointLabel = new Label();
		}
		numWaypoints = num;
		if(numWaypoints == 1){
			waypointLabel.setValue("<b>" + routeName + "</b>" + " (" +  numWaypoints +  " waypoint)");
			waypointLabel.setContentMode(ContentMode.HTML);
		}
		else{
			waypointLabel.setValue("<b>" + routeName + "</b>" + " (" +  numWaypoints +  " waypoints)");
			waypointLabel.setContentMode(ContentMode.HTML);
		}
	}
	public void setName(String name){
		routeName = name; 
	}
	public CheckBox getAutoZooming(){
		return autoZooming;
	}
	public CheckBox getCheckBox(){
		return tableView;
	}
	public Button getEditButton(){
		return editButton;
	}
	public Button getDeleteButton(){
		return deleteButton;
	}
}
