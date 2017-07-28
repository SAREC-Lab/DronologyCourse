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
	private String routeName;
	private String routeId; 
	private int numWaypoints;
	private CheckBox autoZooming;
	private CheckBox tableView;
	private Button editButton;
	private Button deleteButton;
	private Label nameLabel;
	private Label nameOnly;
	private boolean toDo;
	private TextField textField = new TextField();
	private TextField descriptionField = new TextField();
	private String routeDescription;
	boolean descriptionSet = false;
	
	public FRMetaInfo(String name, int numCoords, FRMapComponent map, boolean toDo){
		//used if route is selected
		HorizontalLayout content = new HorizontalLayout();
		HorizontalLayout zoomContent = new HorizontalLayout();
		HorizontalLayout buttons = new HorizontalLayout();
		HorizontalLayout checkboxes = new HorizontalLayout();
		VerticalLayout controls = new VerticalLayout();
		
		
		routeName = name;
		numWaypoints = numCoords;
		
		nameOnly = new Label("<b>" + routeName + "</b>", ContentMode.HTML);
		
		if(numWaypoints == 1){
			nameLabel = new Label(" (" +  numWaypoints +  " waypoint)", ContentMode.HTML);
		}
		else{
			nameLabel = new Label(" (" +  numWaypoints +  " waypoints)", ContentMode.HTML);
		}
		
		HorizontalLayout labels = new HorizontalLayout();
		labels.addComponents(nameOnly, nameLabel);
		
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
		
		this.toDo = toDo;
		autoZooming.setValue(toDo);
		
		tableView = new CheckBox("Table View");
		content.setStyleName("fr_route_meta_info");
		content.addStyleName("has_route");
		
		tableView.setValue(true);
		
		VerticalLayout labelDescription = new VerticalLayout();
		routeDescription = map.getRouteDescription();
	
		Label description = new Label(routeDescription);
		
		HorizontalLayout descriptionLayout = new HorizontalLayout();
		descriptionLayout.addComponent(description);
		
		labelDescription.addComponents(labels, descriptionLayout);
		
		buttons.addComponents(editButton, deleteButton);
		checkboxes.addComponents(autoZooming, tableView);
		controls.addComponents(buttons, checkboxes);
		content.addComponents(labelDescription, controls);
		content.setComponentAlignment(labelDescription, Alignment.TOP_LEFT);
		content.setComponentAlignment(controls, Alignment.MIDDLE_RIGHT);

		controls.addStyleName("route_meta_controls");
		labelDescription.addStyleName("route_meta_label_description");
		
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
		
		textField.setValue(name);
		descriptionField.setValue(routeDescription);
		
		textField.setStyleName("name_edit_field");
		descriptionField.setStyleName("description_edit_field");
		nameOnly.setStyleName("name_lable");
		nameLabel.setStyleName("waypoint_num_lable");
		description.setStyleName("description_lable");

		//double click
		labels.addLayoutClickListener(new LayoutClickListener(){
	
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event){
				if(event.getClickedComponent() == nameOnly){
					if(event.isDoubleClick()){
						content.removeAllComponents();
						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addStyleName("route_meta_label_description");
						HorizontalLayout nameArea = new HorizontalLayout();
						nameArea.addComponents(textField, nameLabel);
						
						textLayout.addComponents(nameArea, descriptionLayout);
						content.addComponents(textLayout, controls);
					}
				}
			}
		});
		
		descriptionLayout.addLayoutClickListener(new LayoutClickListener(){
			
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event){
				if(event.getClickedComponent() == description){
					if(event.isDoubleClick()){
						
						content.removeAllComponents();
						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addStyleName("route_meta_label_description");
						labels.removeAllComponents();
						labels.addComponents(nameOnly, nameLabel);
						textLayout.addComponents(labels, descriptionField);
						
						content.addComponents(textLayout, controls);
					}
				}
			}
		});
		
		textField.addBlurListener(e->{
			//occurs when you click away			
			
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
		
			content.removeAllComponents();
			labels.removeAllComponents();
			//nameOnly.setValue(textField.getValue());
			nameOnly = new Label("<b>" + textField.getValue() + "</b>", ContentMode.HTML);
			labels.addComponents(nameOnly, nameLabel);
			
			labelDescription.removeAllComponents();
			labelDescription.addComponents(labels, descriptionLayout);
			
			content.addComponents(labelDescription, controls);
			controls.addStyleName("route_meta_controls");
			
			String routeName = textField.getValue();
			
			controls.addStyleName("route_meta_controls");
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
		
		descriptionField.addBlurListener(e->{
			routeDescription = descriptionField.getValue();
			
			description.setValue(routeDescription);
			content.removeAllComponents();
			labels.removeAllComponents();
			labels.addComponents(nameOnly, nameLabel);
			labelDescription.addComponents(labels, descriptionLayout);
			content.addComponents(labelDescription, controls);
			controls.addStyleName("route_meta_controls");
			descriptionField.setValue(routeDescription);
		
			map.setRouteNameDescription(routeDescription, false);
			routeDescription = map.getRouteDescription();	
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
			
		});
		
		setCompositionRoot(content);
				
		
	}
	public FRMetaInfo(FlightRouteInfo info, FRMapComponent map, boolean toDo){
		this(info.getName(), info.getWaypoints().size(), map, toDo);	
	}	
	
	public FRMetaInfo(){
		//used if no route selected
		HorizontalLayout information = new HorizontalLayout();
		routeName = "No Route Selected";
		Label nameLabel = new Label(routeName);
		
		information.setStyleName("fr_route_meta_info");
		information.addStyleName("no_route");
		
		information.addComponent(nameLabel);
		information.setComponentAlignment(nameLabel, Alignment.MIDDLE_LEFT);
		setCompositionRoot(information);
	}
	
	public void setName(String name){
		routeName = name; 
	}
	public void setRouteId(String id){
		routeId = id;
	}
	public void setNumWaypoints(int num){
		if (nameLabel == null) {
			nameLabel = new Label();
		}
		numWaypoints = num;
		if(numWaypoints == 1){
			nameLabel.setValue("<b>" + routeName + "</b>" + " (" +  numWaypoints +  " waypoint)");
			nameLabel.setContentMode(ContentMode.HTML);
		}
		else{
			nameLabel.setValue("<b>" + routeName + "</b>" + " (" +  numWaypoints +  " waypoints)");
			nameLabel.setContentMode(ContentMode.HTML);
		}
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
