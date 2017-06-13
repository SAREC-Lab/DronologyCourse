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
	private boolean isChecked;
	private String name;
	private String status;
	private int batteryLife;
	private String healthColor;
	private double lat;
	private double lon;
	private double alt;
	private double speed;
	private boolean hoverInPlace;
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
		isChecked = false;
		check.setValue(isChecked);
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    FileResource resource = new FileResource(new File(basepath+"/VAADIN/img/drone_icon.png"));
    Image droneImage = new Image();
    droneImage.setSource(resource);
    droneImage.setWidth("50px");
    droneImage.setHeight("50px");
    name = "NAME/ID of UAV";
    status = "Status";
    batteryLife = 0;
    Label statusInfo1 = new Label("<b>" + name + "</b>", ContentMode.HTML);
		Label statusInfo2 = new Label("Status: " + status, ContentMode.HTML);
		Label statusInfo3 = new Label("Battery Life: " + Integer.toString(batteryLife) + " min", ContentMode.HTML);
		statusContent.addComponents(statusInfo1, statusInfo2, statusInfo3);
		statusContent.setSpacing(false);
		Label health = new Label();
		healthColor = "green";
		health.setCaptionAsHtml(true);
		health.setCaption( "<span style=\'color: " + healthColor + " !important;\'> " + VaadinIcons.CIRCLE.getHtml()  + "</span>");
		if (this.healthColor.equals("green"))
			health.setDescription("Normally Functionable");
		else if (this.healthColor.equals("yellow"))
			health.setDescription("Needs Attention");
		else if (this.healthColor.equals("red"))
			health.setDescription("Needs Immediate Attention");
		topContent.addComponents(check, droneImage, statusContent, health);
		topContent.setSpacing(false);
		topContent.setComponentAlignment(check, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(droneImage, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(statusContent, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(health, Alignment.MIDDLE_RIGHT);
		
		/**
		 * default middle layer components
		 */
		lat = 0;
		alt = 0;
		speed = 0;
		lon = 0;
		Label locationInfo1 = new Label("Latitude:\t" + Double.toString(lat), ContentMode.HTML);
		Label locationInfo2 = new Label("Longitude:\t" + Double.toString(lon), ContentMode.HTML);
		Label locationInfo3 = new Label("Altitude:\t" + Double.toString(alt) + "feet", ContentMode.HTML);
		Label locationInfo4 = new Label("Ground Speed:\t" + Double.toString(speed) + "mph", ContentMode.HTML);
		
		/**
		 * bottom layer components
		 */
		Switch hoverSwitch = new Switch();
		Label caption = new Label("Hover in Place");
		hoverInPlace = false;
		hoverSwitch.setValue(hoverInPlace);
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
		setCompositionRoot(mainContent);
	}
	
	/**
	 * non default constructor
	 * @param isChecked 
	 * @param name
	 * @param status
	 * @param batteryLife
	 * @param healthColor 
	 * @param lat
	 * @param lon
	 * @param alt
	 * @param speed
	 * @param hoverInPlace 
	 */
	public AFInfoBox(boolean isChecked, String name, String status, int batteryLife, String healthColor, double lat, double lon, double alt, double speed, boolean hoverInPlace){
		this.isChecked = isChecked;
		this.name = name;
		this.status = status;
		this.batteryLife = batteryLife;
		this.healthColor = healthColor;
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
		this.speed = speed;
		this.hoverInPlace = hoverInPlace;
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
		check.setValue(this.isChecked);
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    FileResource resource = new FileResource(new File(basepath+"/VAADIN/img/drone_icon.png"));
    Image droneImage = new Image();
    droneImage.setSource(resource);
    droneImage.setWidth("50px");
    droneImage.setHeight("50px");
    Label statusInfo1 = new Label("<b>" + this.name + "</b>", ContentMode.HTML);
		Label statusInfo2 = new Label("Status: " + this.status, ContentMode.HTML);
		Label statusInfo3 = new Label("Battery Life: " + Integer.toString(this.batteryLife) + " min", ContentMode.HTML);
		statusContent.addComponents(statusInfo1, statusInfo2, statusInfo3);
		statusContent.setSpacing(false);
		Label health = new Label();
		health.setCaptionAsHtml(true);
		health.setCaption( "<span style=\'color: " + this.healthColor + " !important;\'> " + VaadinIcons.CIRCLE.getHtml()  + "</span>");
		if (this.healthColor.equals("green"))
			health.setDescription("Normally Functionable");
		else if (this.healthColor.equals("yellow"))
			health.setDescription("Needs Attention");
		else if (this.healthColor.equals("red"))
			health.setDescription("Needs Immediate Attention");
		topContent.addComponents(check, droneImage, statusContent, health);
		topContent.setSpacing(false);
		topContent.setComponentAlignment(check, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(droneImage, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(statusContent, Alignment.MIDDLE_LEFT);
		topContent.setComponentAlignment(health, Alignment.MIDDLE_RIGHT);
		
		/**
		 * middle layer components
		 */
		Label locationInfo1 = new Label("Latitude:\t" + Double.toString(this.lat), ContentMode.HTML);
		Label locationInfo2 = new Label("Longitude:\t" + Double.toString(this.lon), ContentMode.HTML);
		Label locationInfo3 = new Label("Altitude:\t" + Double.toString(this.alt) + "feet", ContentMode.HTML);
		Label locationInfo4 = new Label("Ground Speed:\t" + Double.toString(this.speed) + "mph", ContentMode.HTML);
		
		/**
		 * bottom layer components
		 */
		Switch hoverSwitch = new Switch();
		hoverSwitch.setValue(this.hoverInPlace);
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
		setCompositionRoot(mainContent);
	}
	
	public void setIsChecked(boolean isChecked){
		this.isChecked = isChecked;
	}
	
	public boolean getIsChecked(){
		return this.isChecked;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public void setBatteryLife(int batteryLife){
		this.batteryLife = batteryLife;
	}
	
	public int getBatteryLife(){
		return this.batteryLife;
	}
	
	public void setHealthColor(String healthColor){
		this.healthColor = healthColor;
	}
	
	public String getHealthColor(){
		return this.healthColor;
	}
	
	public void setLat(double lat){
		this.lat = lat;
	}
	
	public double getLat(){
		return this.lat;
	}
	
	public void setLon(double lon){
		this.lon = lon;
	}
	
	public double getLon(){
		return this.lon;
	}
	
	public void setAlt(double alt){
		this.alt = alt;
	}
	
	public double getAlt(){
		return this.alt;
	}
	
	public void setSpeed(double speed){
		this.speed = speed;
	}
	
	public double getSpeed(){
		return this.speed;
	}
	
	public void setHoverInPlace(boolean hoverInPlace){
		this.hoverInPlace = hoverInPlace;
	}
	
	public boolean getHoverInPlace(){
		return this.hoverInPlace;
	}
}
