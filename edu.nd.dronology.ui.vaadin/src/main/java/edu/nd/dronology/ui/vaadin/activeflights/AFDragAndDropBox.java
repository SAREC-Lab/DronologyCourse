package edu.nd.dronology.ui.vaadin.activeflights;

import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;

import edu.nd.dronology.ui.vaadin.flightroutes.FRInfoBox;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class AFDragAndDropBox extends DragAndDropWrapper{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4022488329187877737L;
	private DragAndDropWrapper wrapper;
	private FRInfoBox route;
	
	@SuppressWarnings("deprecation")
	public AFDragAndDropBox(String name, String ID, String created, String modified, String length){
			
		  route = new FRInfoBox(name, ID, created, modified, length);
			wrapper = new DragAndDropWrapper(route);
			wrapper.setDragStartMode(DragStartMode.COMPONENT);
			wrapper.setSizeUndefined();
			setCompositionRoot(wrapper);
	}
	
	public FRInfoBox getRoute(){
		return route;
	}
}
