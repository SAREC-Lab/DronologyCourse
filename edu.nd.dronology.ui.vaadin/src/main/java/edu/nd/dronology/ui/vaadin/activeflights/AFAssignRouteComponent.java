package edu.nd.dronology.ui.vaadin.activeflights;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.ui.vaadin.flightroutes.FRInfoBox;
import edu.nd.dronology.ui.vaadin.flightroutes.FRInfoPanel;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMainLayout;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class AFAssignRouteComponent extends CustomComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3476532205257979147L;
	
	private VerticalLayout content = new VerticalLayout();
	private HorizontalLayout topContent = new HorizontalLayout();
	private HorizontalLayout sideContent = new HorizontalLayout();
	private HorizontalLayout bottomButtons = new HorizontalLayout();
	private VerticalLayout sideButtons = new VerticalLayout();
	private VerticalLayout panelContent = new VerticalLayout();
	private FRMainLayout frLayout = new FRMainLayout();
	private Panel sidePanel = new Panel();
	private Button cancel = new Button("Cancel");
	private Button apply = new Button("Apply");
	private Button left = new Button("<");
	private Button right = new Button(">");
	private int numRoutes = 0;
	private Switch hoverSwitch = new Switch();
	private Button returnToHome = new Button("Return to Home");
	int index = -1;
	
	public AFAssignRouteComponent(String name, String status, double batteryLife, String healthColor, double lat,
			double lon, double alt, double speed){
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource resource = new FileResource(new File(basepath + "/VAADIN/img/drone_icon.png"));
		Image droneImage = new Image();
		droneImage.setSource(resource);
		
		VerticalLayout statusContent = new VerticalLayout();
		
		Label statusInfo1 = new Label();
		Label statusInfo2 = new Label();
		Label statusInfo3 = new Label();
		
		statusInfo1.setValue("Assigning Routes for " + name);
		statusInfo1.addStyleName("info_box_name");
		statusInfo1.addStyleName(ValoTheme.LABEL_BOLD);
		statusInfo2.setValue("Status: " + status);
		batteryLife = Math.round(batteryLife * 100);
		batteryLife = batteryLife / 100;
		statusInfo3.setValue("Battery Life: " + Double.toString(batteryLife) + " %");
		statusContent.addComponents(statusInfo1, statusInfo2, statusInfo3);
		statusContent.setSpacing(false);
		Label health = new Label();
		health.setCaptionAsHtml(true);
		health.setCaption(
				"<span style=\'color: " + healthColor + " !important;\'> " + VaadinIcons.CIRCLE.getHtml() + "</span>");
		if (healthColor.equals("green"))
			health.setDescription("Normally Functionable");
		else if (healthColor.equals("yellow"))
			health.setDescription("Needs Attention");
		else if (healthColor.equals("red"))
			health.setDescription("Needs Immediate Attention");
		
		topContent.addComponents(droneImage, statusContent, health);
		topContent.setSpacing(false);
		
		VerticalLayout coordinates = new VerticalLayout();
		VerticalLayout altAndSpeed = new VerticalLayout();
		HorizontalLayout positionInfo = new HorizontalLayout();
		
		Label locationInfo1 = new Label();
		Label locationInfo2 = new Label();
		Label locationInfo3 = new Label();
		Label locationInfo4 = new Label();
		
		locationInfo1.setValue("Latitude:\t" + Double.toString(Math.round((lat) * 1000000.0) / 1000000.0));
		locationInfo2.setValue("Longitude:\t" + Double.toString(Math.round((lon) * 1000000.0) / 1000000.0));
		locationInfo3.setValue("Altitude:\t" + Double.toString(alt) + " meters");
		speed = Math.round(speed * 100);
		speed = speed / 100;
		locationInfo4.setValue("Ground Speed:\t" + Double.toString(speed) + " mph");
		
		coordinates.addComponents(locationInfo1, locationInfo2);
		altAndSpeed.addComponents(locationInfo3, locationInfo4);
		positionInfo.addComponents(coordinates, altAndSpeed);
		
		topContent.addComponent(positionInfo);
		
		VerticalLayout buttons = new VerticalLayout();
		
		HorizontalLayout bottomSwitch = new HorizontalLayout();
		Label caption = new Label("Hover in Place");
		bottomSwitch.addComponents(caption, hoverSwitch);
		
		buttons.addComponents(bottomSwitch, returnToHome);
		
		topContent.addComponent(buttons);
		
		sidePanel.addStyleName("fr_info_panel");
		sidePanel.addStyleName("control_panel");
		sidePanel.setContent(panelContent);
		sidePanel.setCaption(numRoutes + " Routes Assigned");
		apply.setEnabled(true);
		
		sideButtons.addComponents(left, right);
		sideButtons.setComponentAlignment(left, Alignment.MIDDLE_CENTER);
		sideButtons.setComponentAlignment(right, Alignment.MIDDLE_CENTER);
		
		left.addClickListener( e-> {
			if (frLayout.getIndex() != -1){
				FlightRouteInfo selectedFlight = frLayout.getControls().getInfoPanel().getFlight(frLayout.getIndex());
				long creationTime = selectedFlight.getDateCreated();
				SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm aaa");
				String creationFormatted = sdf.format(new Date(creationTime));
				
				long modifiedTime = selectedFlight.getDateModified();
				String modifiedFormatted = sdf.format(new Date(modifiedTime));
				
				String length = String.valueOf(selectedFlight.getLenght());
				addRoute(selectedFlight.getName(), selectedFlight.getId(), creationFormatted, modifiedFormatted, length);
			}
			else
				Notification.show("Please select route to assign.");
		});
		
		right.addClickListener( e -> {
			if(index != -1){
				removeRoute(this.index);
				this.index = -1;
			}
			else
				Notification.show("Please select assigned route to remove.");
		});
		
		panelContent.addLayoutClickListener( e -> {
			Component child = e.getChildComponent();
			if(panelContent.getComponentIndex(child) != -1){
				((AFDragAndDropBox) child).getRoute().addStyleName("info_box_focus");
				frLayout.switchWindows(null, frLayout.getMap(), ((AFDragAndDropBox) child).getRoute());
			}
			index = panelContent.getComponentIndex(child);
			//will use when connecting to Dronology
			FlightRouteInfo flightInfo = frLayout.getControls().getInfoPanel().getFlight(index);
			
			int numComponents = panelContent.getComponentCount();
			
			// when one route is clicked, the others go back to default background color
			for (int i = 0; i < numComponents; i++) {
				if (i != index) {
					((AFDragAndDropBox) panelContent.getComponent(i)).getRoute().removeStyleName("info_box_focus");
				}
			}	
		});
		
		
		sideContent.addComponents(sidePanel, sideButtons, frLayout);
		bottomButtons.addComponents(cancel, apply);
		bottomButtons.setComponentAlignment(cancel, Alignment.MIDDLE_RIGHT);
		bottomButtons.setComponentAlignment(apply, Alignment.MIDDLE_RIGHT);
		content.addComponents(topContent, sideContent, bottomButtons);
		
		setCompositionRoot(content);
		
	}
	
	public void addRoute(String name, String ID, String created, String modified, String length) {
		AFDragAndDropBox wrapper = new AFDragAndDropBox(name, ID, created, modified, length);
		panelContent.addComponent(wrapper);
		numRoutes += 1;
		sidePanel.setCaption(numRoutes + "Routes Assigned");
	}
	
	public void removeRoute(int index){
		panelContent.removeComponent(panelContent.getComponent(index));
		numRoutes -= 1;
		sidePanel.setCaption(numRoutes + "Routes Assigned");
	}
	
	public Button getCancel(){
		return cancel;
	}
	
	public Button getApply(){
		return apply;
	}
	
}
