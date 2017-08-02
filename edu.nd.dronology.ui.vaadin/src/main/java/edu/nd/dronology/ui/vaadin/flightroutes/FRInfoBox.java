package edu.nd.dronology.ui.vaadin.flightroutes;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.FlightRouteInfo;

/**
 * 
 * This layout gives information about each of the flight routes along with options to edit or delete
 * 
 * @author jhollan4
 *
 */

public class FRInfoBox extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout allContent;
	private VerticalLayout routeDescription;
	private HorizontalLayout titleBar;
	private String name;
	private String id;
	private String modified;
	private String whichBox;
	private Button editButton;
	private Button trashButton;
	private FRDeleteRoute deleteRoute = new FRDeleteRoute();
	private FlightRouteInfo finfo;
	private Label nameIdLabel;
	private int counter;
	private int index = 0;
	
	String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	
	public FRInfoBox(String name, String id, String created, String modified, String length, FRInfoPanel panel){
	
		this.name = name;
		this.id = id;
		this.modified = modified;
		
		this.addStyleName("info_box");
		this.addStyleName("fr_info_box");

		routeDescription = new VerticalLayout();
		titleBar = new HorizontalLayout();
		allContent = new VerticalLayout();
		routeDescription.addStyleName("detailed_info_well");
	
		//create name id label
		nameIdLabel = new Label(name);
		nameIdLabel.addStyleName("info_box_name");
			
		//this section creates 3 different labels and adds styles to format them appropriately
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
		
		titleBar.addComponents(nameIdLabel, trashButton, editButton);
		
		//adds all content together and aligns the buttons on the right
		allContent.addComponents(titleBar, routeDescription);
		
		setCompositionRoot(allContent);

		//adds listener to the delete button on the route box 
		trashButton.addListener(e->{
			if(panel.getControls().getLayout().getMap().getUtilities().isEditable()){  //checks if the route is in edit mode
				panel.getControls().getLayout().deleteInEdit();
			}else{
				UI.getCurrent().addWindow(deleteRoute.getWindow());
				whichBox = this.getid();
				//uses the id of the specific infobox and the route list from the infopanel to find the index of the route that should be deleted 
				for(int i = 0; i < panel.getRoutes().getComponentCount(); i++){
					FRInfoBox local = (FRInfoBox) panel.getRoutes().getComponent(i);
					if(local.getid().equals(whichBox)){
						index = counter;
					}else{
						counter++;
					}
				}
				//gets the FlightRouteInfo at that index and sets it to be deleted
				finfo = panel.getFlight(index);
				deleteRoute.setRouteInfoTobeDeleted(finfo);
			}
		});
		//refreshes routes immediately after the "yes" on window is clicked 
		deleteRoute.getYesButton().addClickListener(e -> {
			panel.refreshRoutes();
		});
		//a click on the edit button enables editing, unless edit mode is already enabled, in which case the user is prompted about losing changes
		editButton.addClickListener(e -> {
			if (!panel.getControls().getLayout().getMap().getUtilities().isEditable()) {
				panel.getControls().getLayout().enableMapEdit();
				panel.getControls().getLayout().editClick(this);
			} else {
				HorizontalLayout buttons = new HorizontalLayout();
				Button yes = new Button("Yes");
				Button no = new Button("No");
				buttons.addComponents(yes, no);
				
				VerticalLayout windowContent = new VerticalLayout();
				Label statement = new Label("You have unsaved changes on " + name + ".");
				Label question = new Label ("Are you sure you want to discard all unsaved changes?");
				
				windowContent.addComponents(statement, question, buttons);
				
				Window warning;
				warning = new Window(null, windowContent);
				warning.setModal(true);
				warning.setClosable(false);
				warning.setResizable(false);
				
				warning.addStyleName("confirm_window");
				buttons.addStyleName("confirm_button_area");
				yes.addStyleName("btn-danger");
				
				UI.getCurrent().addWindow(warning);
				
				yes.addClickListener(event -> {
					UI.getCurrent().removeWindow(warning);
					panel.getControls().getLayout().getMap().displayNoRoute();
					panel.getControls().getLayout().getMap().exitEditMode();
				});
				no.addClickListener(event -> {
					UI.getCurrent().removeWindow(warning);
				});
			}
			panel.getControls().getLayout().getMap().editButton();
		});
	}
	//this infobox constructor is called from activeflights
	public FRInfoBox(String name, String id, String created, String modified, String length){
		
		this.name = name;
		this.id = id;
		this.modified = modified;
		
		this.addStyleName("info_box");
		this.addStyleName("fr_info_box");

		VerticalLayout routeDescription = new VerticalLayout();
		routeDescription.addStyleName("detailed_info_well");
		
		HorizontalLayout titleBar = new HorizontalLayout();
		VerticalLayout allContent = new VerticalLayout();
		
		//create name id label
		Label nameIdLabel = new Label(name);
		nameIdLabel.addStyleName("info_box_name");
			
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
		
		titleBar.addComponents(nameIdLabel, trashButton, editButton);
		
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
	public FRDeleteRoute getDeleteBar(){
		return deleteRoute;
	}
	public Button getTrashButton(){
		return trashButton;
	}
	public Button getEditButton(){
		return editButton;
	}
}

