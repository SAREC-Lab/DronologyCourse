package edu.nd.dronology.ui.vaadin.start;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class SplashScreen extends CustomComponent{
	private static final long serialVersionUID = 8713736216069854680L;
	private Panel screen = new Panel("Welcome!");
	private ProgressBar bar = new ProgressBar(0.0f);
	
	public SplashScreen(){
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    FileResource resource = new FileResource(new File(basepath+"/VAADIN/img/drone_icon.png"));
    
    Image droneImage = new Image("Welcome to Dronology", resource);
    
    VerticalLayout content = new VerticalLayout();
    
    bar.setCaption("Loading...");
    content.addComponents(bar, droneImage);
    
    screen.setContent(content);
    screen.setSizeFull();
    setCompositionRoot(screen);
	}
	
	/**
	 * This is a dummy progress bar used for this version.
	 */
	public void load(){
		float current = bar.getValue();
    while (current <1.0f){
    	bar.setValue(current + 0.10f);
      current = bar.getValue();
    }
	}

}
