package edu.nd.dronology.ui.vaadin.activeflights;

import java.io.File;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
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
import com.vaadin.ui.themes.ValoTheme;

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
	
	private CheckBox check = new CheckBox();
	private Label statusInfo1 = new Label();
	private Label statusInfo2 = new Label();
	private Label statusInfo3 = new Label();
	private Label health = new Label();
	private Label locationInfo1 = new Label();
	private Label locationInfo2 = new Label();
	private Label locationInfo3 = new Label();
	private Label locationInfo4 = new Label();
	private Switch hoverSwitch = new Switch();

	private VerticalLayout mainContent = new VerticalLayout();
	private HorizontalLayout topContent = new HorizontalLayout();
	private VerticalLayout middleContent = new VerticalLayout();
	private GridLayout bottomContent = new GridLayout(2, 1);
	
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
		
		this.addStyleName("af_info_box");
		
		VerticalLayout statusContent = new VerticalLayout();
		VerticalLayout bottomButtons = new VerticalLayout();
		VerticalLayout bottomSwitch = new VerticalLayout();

		topContent.addStyleName("af_info_top_content");
		middleContent.addStyleName("af_info_middle_content");
		bottomContent.addStyleName("af_info_bottom_content");
		/**
		 * top layer components
		 */
		
		check.setValue(this.isChecked);
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    FileResource resource = new FileResource(new File(basepath+"/VAADIN/img/drone_icon.png"));
    Image droneImage = new Image();
    droneImage.setSource(resource);
 
    statusInfo1.setValue(name);
    statusInfo1.addStyleName(ValoTheme.LABEL_BOLD);
		statusInfo2.setValue("Status: " + status);
		statusInfo3.setValue("Battery Life: " + Integer.toString(batteryLife) + " min");
		statusContent.addComponents(statusInfo1, statusInfo2, statusInfo3);
		statusContent.setSpacing(false);
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
		topContent.setComponentAlignment(check, Alignment.TOP_LEFT);
		topContent.setComponentAlignment(droneImage, Alignment.TOP_LEFT);
		topContent.setComponentAlignment(statusContent, Alignment.TOP_LEFT);
		topContent.setComponentAlignment(health, Alignment.TOP_RIGHT);
		
		/**
		 * middle layer components
		 */

		locationInfo1.setValue("Latitude:\t" + Double.toString(this.lat));
		locationInfo2.setValue("Longitude:\t" + Double.toString(this.lon));
		locationInfo3.setValue("Altitude:\t" + Double.toString(this.alt) + "feet");
		locationInfo4.setValue("Ground Speed:\t" + Double.toString(this.speed) + "mph");
		middleContent.addComponents(locationInfo1, locationInfo2, locationInfo3, locationInfo4);
		
		/**
		 * bottom layer components
		 */
		hoverSwitch.setValue(this.hoverInPlace);
		hoverSwitch.addValueChangeListener( e -> {
			this.setHoverInPlace(this.hoverSwitch.getValue());
		});
		Label caption = new Label("Hover in Place");
		bottomSwitch.addComponents(caption, hoverSwitch);
		
		Button returnToHome = new Button("Return to Home");
		Button assignNewRoute = new Button("Assign New Route");
		returnToHome.setHeight("30px");
		assignNewRoute.setHeight("30px");
		
		bottomButtons.addComponents(returnToHome, assignNewRoute);
		bottomContent.addComponent(bottomSwitch, 0, 0);		
		bottomContent.addComponent(bottomButtons, 1, 0);
		
		mainContent.addComponents(topContent, middleContent, bottomContent);
		mainContent.setSizeUndefined();
		mainContent.setSpacing(false);
		
		middleContent.setVisible(visible);
		bottomContent.setVisible(visible);
		topContent.addLayoutClickListener(e->{
				Component child = e.getChildComponent();
				if(child == null || !child.getClass().getCanonicalName().equals("com.vaadin.ui.CheckBox")){
					setBoxVisible(visible);
			}	
		});
		setCompositionRoot(mainContent);
	}
	
  /**
   * default constructor
   */
	public AFInfoBox(){
		this(false, "NAME/ID of UAV", "Status", 0, "green", 0, 0, 0, 0, false);
	}
	
	public void setIsChecked(boolean isChecked){
		this.isChecked = isChecked;
		check.setValue(this.isChecked);
	}
	
	public boolean getIsChecked(){
		return this.isChecked;
	}
	
	public void setName(String name){
		this.name = name;
		statusInfo1.setValue(name);
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setStatus(String status){
		this.status = status;
		statusInfo2.setValue("Status: " + status);
		if (this.status.equals("Hovering"))
				this.hoverSwitch.setValue(true);
		else
			this.hoverSwitch.setValue(false);
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public void setBatteryLife(int batteryLife){
		this.batteryLife = batteryLife;
		statusInfo3.setValue("Battery Life: " + Integer.toString(batteryLife) + " min");
	}
	
	public int getBatteryLife(){
		return this.batteryLife;
	}
	
	public void setHealthColor(String healthColor){
		this.healthColor = healthColor;
		health.setCaption( "<span style=\'color: " + healthColor + " !important;\'> " + VaadinIcons.CIRCLE.getHtml()  + "</span>");
		if (this.healthColor.equals("green"))
			health.setDescription("Normally Functionable");
		else if (this.healthColor.equals("yellow"))
			health.setDescription("Needs Attention");
		else if (this.healthColor.equals("red"))
			health.setDescription("Needs Immediate Attention");
	}
	
	public String getHealthColor(){
		return this.healthColor;
	}
	
	public void setLat(double lat){
		this.lat = lat;
		locationInfo1.setValue("Latitude:\t" + Double.toString(this.lat));
	}
	
	public double getLat(){
		return this.lat;
	}
	
	public void setLon(double lon){
		this.lon = lon;
		locationInfo2.setValue("Longitude:\t" + Double.toString(this.lon));
	}
	
	public double getLon(){
		return this.lon;
	}
	
	public void setAlt(double alt){
		this.alt = alt;
		locationInfo3.setValue("Altitude:\t" + Double.toString(this.alt) + "feet");
	}
	
	public double getAlt(){
		return this.alt;
	}
	
	public void setSpeed(double speed){
		this.speed = speed;
		locationInfo4.setValue("Ground Speed:\t" + Double.toString(this.speed) + "mph");
	}
	
	public double getSpeed(){
		return this.speed;
	}
	
	public void setHoverInPlace(boolean hoverInPlace){
		this.hoverInPlace = hoverInPlace;
		hoverSwitch.setValue(this.hoverInPlace);
		if (this.hoverInPlace){
			this.status = "Hovering";
			statusInfo2.setValue("Status: " + status);
		} else {
			this.status = "Enroute";
			statusInfo2.setValue("Status: " + status);
		}
	}
	
	public boolean getHoverInPlace(){
		return this.hoverInPlace;
	}
	
	public void setBoxVisible(boolean visible){
		if (visible){
			this.visible = false;
			middleContent.setVisible(this.visible);
			bottomContent.setVisible(this.visible);
		}
		else {
			this.visible = true;
			middleContent.setVisible(this.visible);
			bottomContent.setVisible(this.visible);
		}	
	}
	
	public boolean getBoxVisible(){
		return this.visible;
	}
	
}
