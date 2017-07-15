package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.FlightRouteInfo;

/**
 * 
 * @author jhollan4
 *
 */

public class FRMetaInfo extends CustomComponent {

	private static final long serialVersionUID = -2718986455485823804L;
	String routeName;
	String routeId; 
	int numWaypoints;
	CheckBox tableView;
	Button editButton;
	
	public FRMetaInfo(String name, int numCoords){
		//used if route is selected
		HorizontalLayout content = new HorizontalLayout();
		HorizontalLayout buttons = new HorizontalLayout();
		VerticalLayout controls = new VerticalLayout();
		Label nameLabel;
		
		routeName = name;
		numWaypoints = numCoords;
		
		if(numWaypoints == 1){
			nameLabel = new Label("<b>" + routeName + "</b>" + " (" +  numWaypoints +  " waypoint)", ContentMode.HTML);
		}
		else{
			nameLabel = new Label("<b>" + routeName + "</b>" + " (" +  numWaypoints +  " waypoints)", ContentMode.HTML);
		}
		
		editButton = new Button("Edit");
		Button deleteButton = new Button("Delete");
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/editButtonFull.PNG"));
		FileResource deleteIcon = new FileResource(new File(basepath+"/VAADIN/img/deleteButtonFull.PNG"));
		
		editButton.setIcon(editIcon);
		deleteButton.setIcon(deleteIcon);
		
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			
		tableView = new CheckBox("Table View");
		content.setStyleName("fr_route_meta_info");
		content.addStyleName("has_route");
		
		tableView.setValue(true);
		
		buttons.addComponents(editButton, deleteButton);
		controls.addComponents(buttons, tableView);
		content.addComponents(nameLabel, controls);

		controls.addStyleName("route_meta_controls");
		nameLabel.addStyleName("route_meta_name");
		
		setCompositionRoot(content);
		
	}
	public FRMetaInfo(FlightRouteInfo info){		
		this(info.getName(), info.getWaypoints().size());	
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
		numWaypoints = num;
	}
	public CheckBox getCheckBox(){
		return tableView;
	}
	public Button getEditButton(){
		return editButton;
	}
	
}
