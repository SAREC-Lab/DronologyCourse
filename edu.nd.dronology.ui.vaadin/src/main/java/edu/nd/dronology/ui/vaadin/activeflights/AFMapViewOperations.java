package edu.nd.dronology.ui.vaadin.activeflights;

import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class AFMapViewOperations extends CustomComponent{

	private static final long serialVersionUID = -2249802562670339842L;
	private HorizontalLayout buttons = new HorizontalLayout();
	
	private NativeButton follow = new NativeButton("Follow Selected<br>UAVs on Map");
  private NativeButton viewAll = new NativeButton("View All UAVs<br>on Map");
  
	public AFMapViewOperations(){
		VerticalLayout layout = new VerticalLayout();
		layout.addStyleName("af_operations_group");
    
		Label caption = new Label("Map View Operations");
    follow.setCaptionAsHtml(true);
    viewAll.setCaptionAsHtml(true);
    
    buttons.addComponents(follow, viewAll);
    layout.addComponents(caption, buttons);
    
    setCompositionRoot(layout);
	}
	
	public NativeButton getFollowButton(){
		return follow;
	}
	
	public NativeButton getViewButton(){
		return viewAll;
	}
	
	public void addOnClickListener(LayoutClickListener listener){
    buttons.addLayoutClickListener(listener);
	}
}
