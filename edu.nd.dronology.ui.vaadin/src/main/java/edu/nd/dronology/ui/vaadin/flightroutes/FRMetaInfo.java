package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;

import com.vaadin.event.MouseEvents;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
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
	private Panel labelHolder = new Panel();
	
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
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/editButtonFull.PNG"));
		FileResource deleteIcon = new FileResource(new File(basepath+"/VAADIN/img/deleteButtonFull.PNG"));
		
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
		
		buttons.addComponents(editButton, deleteButton);
		checkboxes.addComponents(autoZooming, tableView);
		controls.addComponents(buttons, checkboxes);
		content.addComponents(labels, controls);
		content.setComponentAlignment(labels, Alignment.MIDDLE_LEFT);
		content.setComponentAlignment(controls, Alignment.MIDDLE_RIGHT);

		controls.addStyleName("route_meta_controls");
		nameLabel.addStyleName("route_meta_name");
		nameOnly.addStyleName("route_meta_name");
		content.setWidth("1550px");
		
		editButton.addClickListener(e->{
			map.editButton();
		});
		deleteButton.addClickListener(e->{
			map.deleteClick();
		});
		
		//textField.setPropertyDataSource(nameLabel);
		textField.setValue(name);
		/*
		labels.addLayoutClickListener(e->{
			content.removeAllComponents();
			HorizontalLayout textLayout = new HorizontalLayout();
			textLayout.addComponents(textField, nameLabel);
			content.addComponents(textLayout, controls);
			map.getMainLayout().getControls().getInfoPanel().refreshRoutes();
		});
		*/
		//double click
		labels.addLayoutClickListener(new LayoutClickListener(){
	
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event){
				if(event.getClickedComponent() == nameOnly){
					if(event.isDoubleClick()){
						content.removeAllComponents();
						HorizontalLayout textLayout = new HorizontalLayout();
						textLayout.addComponents(textField, nameLabel);
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
			labels.addComponents(nameOnly, nameLabel);
			
			content.addComponents(labels, controls);
			controls.addStyleName("route_meta_controls");
			nameLabel.addStyleName("route_meta_name");
			nameOnly.addStyleName("route_meta_name");
			
			String routeName = textField.getValue();
			
			controls.addStyleName("route_meta_controls");
			map.setRouteName(routeName);
			textField.setValue(routeName);
			nameOnly.setValue(routeName);
			
			//waits to refresh routes so dronology can save
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
