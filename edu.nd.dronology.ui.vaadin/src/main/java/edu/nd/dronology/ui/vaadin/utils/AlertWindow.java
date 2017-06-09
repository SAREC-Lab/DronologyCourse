package edu.nd.dronology.ui.vaadin.utils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This is a simple Vaadin Alert Window
 * 
 * @author Jinghui Cheng
 */
public class AlertWindow extends Window {
	private static final long serialVersionUID = 2621348163200455408L;

	public AlertWindow(String message) {
		this.center();
		this.setWidth("300px");
		this.setClosable(false);
		this.setModal(true);
		this.setResizable(false);
		this.setDraggable(false);

    VerticalLayout layout = new VerticalLayout();
    Label text = new Label(message);
    Button okButton = new Button("OK", event->close());
    layout.addComponents(text, okButton);
    
    this.setContent(layout);
	}
}
