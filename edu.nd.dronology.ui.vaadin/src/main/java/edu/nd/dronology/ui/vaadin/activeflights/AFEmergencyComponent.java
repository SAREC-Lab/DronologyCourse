package edu.nd.dronology.ui.vaadin.activeflights;

import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

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
		caption.addStyleName(ValoTheme.LABEL_BOLD);
    NativeButton hover = new NativeButton("All UAVs Hover in Place");
    NativeButton home = new NativeButton("All UAVs Return to Home");
    home.setCaptionAsHtml(true);
    layout.addStyleName(ValoTheme.LAYOUT_CARD);
    hover.setWidth("125px");
    home.setWidth("135px");
    
    buttons.addComponents(hover, home);
    layout.addComponents(caption, buttons);
    
    setCompositionRoot(layout);
	}
	
	public void addOnClickListener(LayoutClickListener listener){
    buttons.addLayoutClickListener(listener);
	}
	
}
