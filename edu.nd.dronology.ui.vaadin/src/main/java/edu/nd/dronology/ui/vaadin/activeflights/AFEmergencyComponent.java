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
public class AFEmergencyComponent extends CustomComponent{
	private static final long serialVersionUID = -650745296345774988L;
	private HorizontalLayout buttons = new HorizontalLayout();
	public AFEmergencyComponent(){
		VerticalLayout layout = new VerticalLayout();
		layout.addStyleName("af_emergency_operations");
    
		Label caption = new Label("Emergency Operations");
    NativeButton hover = new NativeButton("All UAVs<br>Hover in Place");
    NativeButton home = new NativeButton("All UAVs<br>Return to Home");
    hover.setCaptionAsHtml(true);
    hover.addStyleName("button_warning");
    home.setCaptionAsHtml(true);
    home.addStyleName("button_warning");
    
    buttons.addComponents(hover, home);
    layout.addComponents(caption, buttons);
    
    setCompositionRoot(layout);
	}
	
	public void addOnClickListener(LayoutClickListener listener){
    buttons.addLayoutClickListener(listener);
	}
	
}
