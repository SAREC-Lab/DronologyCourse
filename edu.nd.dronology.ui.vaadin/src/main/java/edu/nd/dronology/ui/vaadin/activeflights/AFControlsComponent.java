package edu.nd.dronology.ui.vaadin.activeflights;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class AFControlsComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;
	
	public AFControlsComponent() {
		this.setWidth("100%");
		addStyleName("af_controls_component");
		
		VerticalLayout content = new VerticalLayout();
		
		Button btn = new Button("Test");
		content.addComponent(btn);
		
        setCompositionRoot(content);
	}
}
