package edu.nd.dronology.ui.vaadin.utils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class DronologyNotification extends Window{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4016984326213185603L;
	private Window notification = new Window("Notification");
	private VerticalLayout subContent = new VerticalLayout();
	private Label label = new Label("Notification Text");
	private Button close = new Button("Close");
	
	public DronologyNotification(){
		
		close.addClickListener( e-> {
			UI.getCurrent().removeWindow(notification);
		});
		
		subContent.addComponents(label, close);
		notification.setContent(subContent);
		notification.setModal(true);
		notification.center();
		UI.getCurrent().addWindow(notification);	
	}
	
	public void setNotificationText(String text){
		label.setValue(text);
	}
	
	public Button getClose(){
		return this.close;
	}
	
	public Window getNotificationWindow(){
		return notification;
	}
	
	public Label getNotificationLabel(){
		return label;
	}

	public VerticalLayout getWindowContent(){
		return subContent;
	}
}
