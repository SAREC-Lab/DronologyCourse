package edu.nd.dronology.ui.vaadin.activeflights;

import java.io.File;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class AFInfoBox extends CustomComponent{
	private static final long serialVersionUID = -8541147696474050819L;
	private boolean visible = false;
  /**
   * default constructor
   */
	public AFInfoBox(){
		VerticalLayout mainContent = new VerticalLayout();
		HorizontalLayout topContent = new HorizontalLayout();
		VerticalLayout statusContent = new VerticalLayout();
		GridLayout bottomContent = new GridLayout(2, 1);
		VerticalLayout bottomButtons = new VerticalLayout();
		VerticalLayout bottomSwitch = new VerticalLayout();
		
		/**
		 * top layer components
		 */
		CheckBox check = new CheckBox();
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    FileResource resource = new FileResource(new File(basepath+"/VAADIN/img/drone_icon.png"));
    Image droneImage = new Image();
    droneImage.setSource(resource);
    droneImage.setWidth("30px");
    droneImage.setHeight("30px");
    String name = "NAME/ID of UAV";
    String status = "Status";
    int batteryLife = 0;
    Label statusInfo1 = new Label("<b>" + name + "</b>", ContentMode.HTML);
		Label statusInfo2 = new Label("Status: " + status, ContentMode.HTML);
		Label statusInfo3 = new Label("Battery Life: " + Integer.toString(batteryLife) + " min", ContentMode.HTML);
		statusContent.addComponents(statusInfo1, statusInfo2, statusInfo3);
		statusContent.setSpacing(false);
		Label health = new Label();
		String healthColor = "green";
		health.setCaptionAsHtml(true);
		health.setCaption( "<span style=\'color: " + healthColor + " !important;\'> " + VaadinIcons.CIRCLE.getHtml()  + "</span>");
		topContent.addComponents(check, droneImage, statusContent, health);
		topContent.setSpacing(false);
		topContent.setComponentAlignment(check, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(droneImage, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(statusContent, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(health, Alignment.MIDDLE_LEFT);
		
		/**
		 * default middle layer components
		 */
		double lat = 0;
		double lon = 0;
		double alt = 0;
		double speed = 0;
		Label locationInfo1 = new Label("Latitude:\t" + Double.toString(lat), ContentMode.HTML);
		Label locationInfo2 = new Label("Longitude:\t" + Double.toString(lon), ContentMode.HTML);
		Label locationInfo3 = new Label("Altitude:\t" + Double.toString(alt) + "feet", ContentMode.HTML);
		Label locationInfo4 = new Label("Ground Speed:\t" + Double.toString(speed) + "mph", ContentMode.HTML);
		
		/**
		 * bottom layer components
		 */
		Switch hoverSwitch = new Switch();
		Label caption = new Label("Hover in Place");
		bottomSwitch.addComponents(caption, hoverSwitch);
		
		Button returnToHome = new Button("Return to Home");
		Button assignNewRoute = new Button("Assign New Route");
		returnToHome.setHeight("30px");
		assignNewRoute.setHeight("30px");
		
		bottomButtons.addComponents(returnToHome, assignNewRoute);
		bottomContent.addComponent(bottomSwitch, 0, 0);		
		bottomContent.setComponentAlignment(bottomSwitch, Alignment.MIDDLE_LEFT);
		bottomContent.addComponent(bottomButtons, 1, 0);
		bottomContent.setComponentAlignment(bottomButtons, Alignment.TOP_LEFT);
		
		mainContent.addComponents(topContent, locationInfo1, locationInfo2, locationInfo3, locationInfo4, bottomContent);
		mainContent.setComponentAlignment(bottomContent, Alignment.TOP_LEFT);
		mainContent.setComponentAlignment(locationInfo1, Alignment.MIDDLE_CENTER);
		mainContent.setComponentAlignment(locationInfo2, Alignment.MIDDLE_CENTER);
		mainContent.setComponentAlignment(locationInfo3, Alignment.MIDDLE_CENTER);
		mainContent.setComponentAlignment(locationInfo4, Alignment.MIDDLE_CENTER);
		mainContent.setSizeUndefined();
		mainContent.setSpacing(false);
		
		locationInfo1.setVisible(visible);
		locationInfo2.setVisible(visible);
		locationInfo3.setVisible(visible);
		locationInfo4.setVisible(visible);
		bottomContent.setVisible(visible);
		topContent.addLayoutClickListener(e->{
				Component child = e.getChildComponent();
				if(child == null || !child.getClass().getCanonicalName().equals("com.vaadin.ui.CheckBox")){
					if (visible){
						visible = false;
						locationInfo1.setVisible(visible);
						locationInfo2.setVisible(visible);
						locationInfo3.setVisible(visible);
						locationInfo4.setVisible(visible);
						bottomContent.setVisible(visible);
					}
					else {
						visible = true;
						locationInfo1.setVisible(visible);
						locationInfo2.setVisible(visible);
						locationInfo3.setVisible(visible);
						locationInfo4.setVisible(visible);
						bottomContent.setVisible(visible);
					}
			}		
		});
		mainContent.addStyleName("af_info_panel");
		setCompositionRoot(mainContent);
	}
	
	/**
	 * non default constructor
	 * @param name
	 * @param status
	 * @param batteryLife
	 * @param lat
	 * @param lon
	 * @param alt
	 * @param speed
	 */
	public AFInfoBox(String name, String status, int batteryLife, double lat, double lon, double alt, double speed){
		VerticalLayout mainContent = new VerticalLayout();
		HorizontalLayout topContent = new HorizontalLayout();
		VerticalLayout statusContent = new VerticalLayout();
		GridLayout bottomContent = new GridLayout(2, 1);
		VerticalLayout bottomButtons = new VerticalLayout();
		VerticalLayout bottomSwitch = new VerticalLayout();
		
		/**
		 * top layer components
		 */
		CheckBox check = new CheckBox();
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    FileResource resource = new FileResource(new File(basepath+"/VAADIN/img/drone_icon.png"));
    Image droneImage = new Image();
    droneImage.setSource(resource);
    droneImage.setWidth("30px");
    droneImage.setHeight("30px");
    Label statusInfo1 = new Label("<b>" + name + "</b>", ContentMode.HTML);
		Label statusInfo2 = new Label("Status: " + status, ContentMode.HTML);
		Label statusInfo3 = new Label("Battery Life: " + Integer.toString(batteryLife) + " min", ContentMode.HTML);
		statusContent.addComponents(statusInfo1, statusInfo2, statusInfo3);
		statusContent.setSpacing(false);
		Label health = new Label();
		String healthColor = "green";
		health.setCaptionAsHtml(true);
		health.setCaption( "<span style=\'color: " + healthColor + " !important;\'> " + VaadinIcons.CIRCLE.getHtml()  + "</span>");
		topContent.addComponents(check, droneImage, statusContent, health);
		topContent.setSpacing(false);
		topContent.setComponentAlignment(check, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(droneImage, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(statusContent, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(health, Alignment.MIDDLE_LEFT);
		
		/**
		 * middle layer components
		 */
		Label locationInfo1 = new Label("Latitude:\t" + Double.toString(lat), ContentMode.HTML);
		Label locationInfo2 = new Label("Longitude:\t" + Double.toString(lon), ContentMode.HTML);
		Label locationInfo3 = new Label("Altitude:\t" + Double.toString(alt) + "feet", ContentMode.HTML);
		Label locationInfo4 = new Label("Ground Speed:\t" + Double.toString(speed) + "mph", ContentMode.HTML);
		
		/**
		 * bottom layer components
		 */
		Switch hoverSwitch = new Switch();
		Label caption = new Label("Hover in Place");
		bottomSwitch.addComponents(caption, hoverSwitch);
		
		Button returnToHome = new Button("Return to Home");
		Button assignNewRoute = new Button("Assign New Route");
		returnToHome.setHeight("30px");
		assignNewRoute.setHeight("30px");
		
		bottomButtons.addComponents(returnToHome, assignNewRoute);
		bottomContent.addComponent(bottomSwitch, 0, 0);		
		bottomContent.setComponentAlignment(bottomSwitch, Alignment.MIDDLE_LEFT);
		bottomContent.addComponent(bottomButtons, 1, 0);
		bottomContent.setComponentAlignment(bottomButtons, Alignment.TOP_LEFT);
		
		mainContent.addComponents(topContent, locationInfo1, locationInfo2, locationInfo3, locationInfo4, bottomContent);
		mainContent.setComponentAlignment(bottomContent, Alignment.TOP_LEFT);
		mainContent.setComponentAlignment(locationInfo1, Alignment.MIDDLE_CENTER);
		mainContent.setComponentAlignment(locationInfo2, Alignment.MIDDLE_CENTER);
		mainContent.setComponentAlignment(locationInfo3, Alignment.MIDDLE_CENTER);
		mainContent.setComponentAlignment(locationInfo4, Alignment.MIDDLE_CENTER);
		mainContent.setSizeUndefined();
		mainContent.setSpacing(false);
		
		locationInfo1.setVisible(visible);
		locationInfo2.setVisible(visible);
		locationInfo3.setVisible(visible);
		locationInfo4.setVisible(visible);
		bottomContent.setVisible(visible);
		topContent.addLayoutClickListener(e->{
				Component child = e.getChildComponent();
				if(child == null || !child.getClass().getCanonicalName().equals("com.vaadin.ui.CheckBox")){
					if (visible){
						visible = false;
						locationInfo1.setVisible(visible);
						locationInfo2.setVisible(visible);
						locationInfo3.setVisible(visible);
						locationInfo4.setVisible(visible);
						bottomContent.setVisible(visible);
					}
					else {
						visible = true;
						locationInfo1.setVisible(visible);
						locationInfo2.setVisible(visible);
						locationInfo3.setVisible(visible);
						locationInfo4.setVisible(visible);
						bottomContent.setVisible(visible);
					}		
			}	
		});
		mainContent.addStyleName("af_info_panel");
		setCompositionRoot(mainContent);
	}
}
