package edu.nd.dronology.ui.vaadin.activeflights;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.ui.vaadin.flightroutes.FRInfoBox;
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
	
	public AFAssignRouteComponent(){
		
		sidePanel.addStyleName("fr_info_panel");
		sidePanel.addStyleName("control_panel");
		sidePanel.setContent(panelContent);
		addRoute("Testing", "1234", "Mar 19, 2015, 4:32PM", "Jul 12, 2016, 7:32AM", "5.1mi");
		apply.setEnabled(false);
		
		sideButtons.addComponents(left, right);
		
		sideContent.addComponents(sidePanel, sideButtons, frLayout);
		bottomButtons.addComponents(cancel, apply);
		content.addComponents(sideContent, bottomButtons);
		
		setCompositionRoot(content);
		
	}
	
	public void addRoute(String name, String ID, String created, String modified, String length) {
		FRInfoBox route = new FRInfoBox(name, ID, created, modified, length);
		panelContent.addComponent(route);
		//numberRoutes += 1;
	}
	
}
