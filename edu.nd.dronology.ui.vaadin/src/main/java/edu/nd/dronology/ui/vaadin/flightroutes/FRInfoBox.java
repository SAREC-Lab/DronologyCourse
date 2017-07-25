package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;
import java.util.ArrayList;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.FlightRouteInfo;

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
	private FRDeleteRoute delete = new FRDeleteRoute();
	private Button trashButton;
	private FlightRouteInfo finfo;
	private int index = 0;
	private Button editButton;
	int counter;
	String whichBox;
	
	String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	
	public FRInfoBox(String name, String id, String created, String modified, String length, FRInfoPanel panel){
	
		this.name = name;
		this.id = id;
		this.created = created;
		this.modified = modified;
		this.length = length;
		
		this.addStyleName("info_box");
		this.addStyleName("fr_info_box");

		VerticalLayout routeDescription = new VerticalLayout();
		routeDescription.addStyleName("detailed_info_well");
		
		HorizontalLayout titleBar = new HorizontalLayout();
		VerticalLayout allContent = new VerticalLayout();
		
		//create name id label
		Label nameidLabel = new Label(name);
		nameidLabel.addStyleName("info_box_name");
			
		//this next section creates 3 different labels and adds styles to format them appropriately
		Label createdLabel = new Label("Created:  " + created);
		Label modifiedLabel = new Label("Last Modified:  " + modified);
		Label lengthLabel = new Label("Total Length: " + length);
		
		routeDescription.addComponents(createdLabel, modifiedLabel, lengthLabel);
		
		//imports images for buttons
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/edit.png"));
		FileResource trashIcon = new FileResource(new File(basepath+"/VAADIN/img/trashcan.png"));
		
		editButton = new Button();
		trashButton = new Button();
		
		editButton.setIcon(editIcon);
		trashButton.setIcon(trashIcon);
		
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		trashButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		titleBar.addComponents(nameidLabel, trashButton, editButton);
		
		//adds all content together and aligns the buttons on the right
		allContent.addComponents(titleBar, routeDescription);
		
		setCompositionRoot(allContent);

		trashButton.addListener(e->{
			UI.getCurrent().addWindow(delete.getWindow());
			whichBox = this.getid();
			for(int i = 0; i < panel.getRoutes().getComponentCount(); i++){
				FRInfoBox local = (FRInfoBox) panel.getRoutes().getComponent(i);
				if(local.getid().equals(whichBox)){
					index = counter;
				}else{
					counter++;
				}
			}
			finfo = panel.getFlight(index);
			delete.setRouteInfoTobeDeleted(finfo);
			panel.refreshRoutes();	
		});
		
		delete.getYesButton().addClickListener(e->{
			panel.refreshRoutes();
		});
		
		editButton.addClickListener(e->{
			panel.getControls().getLayout().enableMapEdit();
			panel.getControls().getLayout().editClick(this);
		});
	}
		
	
	public FRInfoBox(String name, String id, String created, String modified, String length){
		
		this.name = name;
		this.id = id;
		this.created = created;
		this.modified = modified;
		this.length = length;
		
		this.addStyleName("info_box");
		this.addStyleName("fr_info_box");

		VerticalLayout routeDescription = new VerticalLayout();
		routeDescription.addStyleName("detailed_info_well");
		
		HorizontalLayout titleBar = new HorizontalLayout();
		VerticalLayout allContent = new VerticalLayout();
		
		//create name id label
		Label nameidLabel = new Label(name);
		nameidLabel.addStyleName("info_box_name");
			
		//this next section creates 3 different labels and adds styles to format them appropriately
		Label createdLabel = new Label("Created:  " + created);
		Label modifiedLabel = new Label("Last Modified:  " + modified);
		Label lengthLabel = new Label("Total Length: " + length);
		
		routeDescription.addComponents(createdLabel, modifiedLabel, lengthLabel);
		
		//imports images for buttons
		FileResource editIcon = new FileResource(new File(basepath+"/VAADIN/img/edit.png"));
		FileResource trashIcon = new FileResource(new File(basepath+"/VAADIN/img/trashcan.png"));
		
		editButton = new Button();
		trashButton = new Button();
		
		editButton.setIcon(editIcon);
		trashButton.setIcon(trashIcon);
		
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		trashButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		titleBar.addComponents(nameidLabel, trashButton, editButton);
		
		//adds all content together and aligns the buttons on the right
		allContent.addComponents(titleBar, routeDescription);
		
		setCompositionRoot(allContent);
	}
	
	
	//default if no parameters are passed
	public FRInfoBox(FRInfoPanel panel){	
		this("NAME", "id", "Jun 3, 2017, 9:24 AM", "Jun 8, 2017, 11:04 AM", "2.1 miles", panel);
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
	public FRDeleteRoute getDeleteBar(){
		return delete;
	}
	public Button getTrashButton(){
		return trashButton;
	}
	public Button getEditButton(){
		return editButton;
	}
	public int getIndex(FRInfoPanel panel){
		String whichBox = this.getId();
		ArrayList<FRInfoBox> listBoxes = panel.getBoxList();
		int counter = 0;
		index = 0; 
		for(FRInfoBox box: listBoxes){
			if(box.getId() != null && whichBox.equals(box.getId())){
				index = counter;
			}
			counter++;
		}
		return index;
	}
}

