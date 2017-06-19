package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * This gives information about each of the flight routes and buttons for
 * editing or deleting a route
 * 
 * @author jhollan4
 *
 */

public class FRInfoBox extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String id;
	private String created;
	private String modified;
	private String length;
	
	String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	
	public FRInfoBox(String name, String id, String created, String modified, String length){
			
		this.name = name;
		this.id = id;
		this.created = created;
		this.modified = modified;
		this.length = length;
		
		VerticalLayout routeDescription = new VerticalLayout();  
		VerticalLayout buttons = new VerticalLayout();  //
		HorizontalLayout allContent = new HorizontalLayout();
		
		//this next section creates 4 different labels and adds styles to format them appropriately
		Label nameidLabel = new Label(name + " ID: " + id);
		nameidLabel.addStyleName("list_style_name");
		Label createdLabel = new Label("Created:  " + created);
		createdLabel.addStyleName("fr_route_created");
		Label modifiedLabel = new Label("Last Modified:  " + modified);
		modifiedLabel.addStyleName("fr_route_modified");
		Label lengthLabel = new Label("Total Length: " + length);
		lengthLabel.addStyleName("fr_route_length");
		
		routeDescription.addComponents(nameidLabel, createdLabel, modifiedLabel, lengthLabel);
		routeDescription.setSpacing(false);
		
		//imports images for buttons
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/edit.png"));
		FileResource trashIcon = new FileResource(new File(basepath+"/VAADIN/img/trashcan.png"));
		
		Button editButton = new Button();
		Button trashButton = new Button();
		
		editButton.setHeight("25px");
		editButton.setWidth("25px");
		
		trashButton.setHeight("25px");
		trashButton.setWidth("25px");
		
		editButton.setIcon(editIcon);
		trashButton.setIcon(trashIcon);
		
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		trashButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		buttons.addComponents(editButton, trashButton);
		
		//adds all content together and aligns the buttons on the right
		allContent.addComponents(routeDescription, buttons);
		allContent.setComponentAlignment(routeDescription, Alignment.MIDDLE_LEFT);
		allContent.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
		
		setCompositionRoot(allContent);
		
	}
	
	//default if no parameters are passesd
	public FRInfoBox(){	
		this("NAME", "id", "Jun 3, 2017, 9:24 AM", "Jun 8, 2017, 11:04 AM", "2.1 miles");
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getid(){
		return id;
	}
	
	public void setid(String id){
		this.id = id;
	}
	
	public String getModified(){
		return modified;
	}
	
	public void setModified(String modified){
		this.modified = modified;
	}
	
	public String getLength(String length){
		return length;
	}
	
	public void setLength(String length){
		this.length = length;
	}
}

