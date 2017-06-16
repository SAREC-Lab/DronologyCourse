package edu.nd.dronology.ui.vaadin.flightroutes;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * This is the list of selectable flight routes 
 * 
 * @author jhollan4
 *
 */

public class FRInfoPanel extends CustomComponent {

	private static final long serialVersionUID = -2505608328159312876L;
	
	private int numberRoutes;
	private Panel panel = new Panel();
	
	//total layout is made up of the title/buttons on top and the routes on the bottom
	private VerticalLayout totalLayout = new VerticalLayout();
	private VerticalLayout routes = new VerticalLayout();
	private HorizontalLayout buttons = new HorizontalLayout();
	private String routeInputName;
	
	public FRInfoPanel(){
		
		addRoute("Fast ", "46033", "Jun 5, 2017, 2:04AM", "Jun 7, 2017, 3:09AM", "10mi");
		addRoute("Slow ", "48293", "Aug 10, 2016, 5:04PM", "Aug 23, 2017, 5:12PM", "3.1mi");
		addRoute("Direct ", "48213", "Jan 5, 2017, 8:00AM", "Feb 12, 2017, 3:56PM", "1.2mi");
		
		panel.setCaption(numberRoutes + " Routes in database");
		panel.setContent(totalLayout);
		panel.addStyleName("fr_info_panel");
		panel.addStyleName("control_panel");
		
		Button newRoute = new Button("+");
		newRoute.setWidth("42px");
		
		VerticalLayout popupContent = new VerticalLayout();
		
		TextField inputField = new TextField();
		inputField.addValueChangeListener(e -> {
			routeInputName = (String) inputField.getValue();
			Notification.show(routeInputName);
			addRoute(routeInputName, "41342", "Mar 19, 2015, 4:32PM", "Jul 12, 2016, 7:32AM", "5.1mi");
		});
		
		popupContent.addComponent(inputField);
		
		PopupView popup = new PopupView(null, popupContent);
		buttons.addComponent(popup); //?
		
		newRoute.addClickListener(e->{	
			popup.setPopupVisible(true);
			
		});
		
		buttons.addComponents(newRoute, popup); 
		
		Button filter = new Button("Filter");
		filter.setWidth("68px");
		
		buttons.addComponents(filter);
		
		totalLayout.addComponents(buttons, routes);
		
		setCompositionRoot(panel);
		
	}
	
	public void addRoute(){
		FRInfoBox route = new FRInfoBox();
		routes.addComponent(route);
		numberRoutes += 1;
	}
	
	public void addRoute(String name, String ID, String created, String modified, String length){
		FRInfoBox route = new FRInfoBox(name, ID, created, modified, length);
		routes.addComponent(route);
		numberRoutes += 1;
	}
	
	public boolean removeBox(String name){
		for(int i = 0; i < numberRoutes; i++){
			FRInfoBox route = (FRInfoBox) routes.getComponent(i);
			if(route.getName().equals(name)){
				routes.removeComponent(route);
				return true;
			}
		}
		return false;
	}

}
