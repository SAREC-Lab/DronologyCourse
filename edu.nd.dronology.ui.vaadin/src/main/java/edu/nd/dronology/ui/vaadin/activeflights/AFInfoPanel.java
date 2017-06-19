package edu.nd.dronology.ui.vaadin.activeflights;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * 
 * @author Patrick Falvey
 *
 */

public class AFInfoPanel extends CustomComponent{
	private static final long serialVersionUID = -3663049148276256302L;
	private Panel panel = new Panel();
	private VerticalLayout content = new VerticalLayout();
	private int numUAVs = 0;
	private boolean selectAll = true;
	private boolean visible = false;
	private Map<String, DroneStatus> drones;
	private IDroneSetupRemoteService service;
  private BaseServiceProvider provider = MyUI.getProvider();
//private static final ILogger LOGGER = LoggerProvider.getLogger(AFInfoPanel.class);

	public AFInfoPanel(){	
		
		panel.setCaption(Integer.toString(numUAVs) + " Active UAVs");
		panel.setContent(content);
		panel.addStyleName("af_info_panel");
		panel.addStyleName("control_panel");
		
		HorizontalLayout buttons = new HorizontalLayout();
		VerticalLayout sideBar = new VerticalLayout();
		
		AFEmergencyComponent emergency = new AFEmergencyComponent();
		
		emergency.addOnClickListener( e -> {
			Component child = e.getChildComponent();
			if (child.getCaption().equals("All UAVs Hover in Place")){
				this.setAllToHover();
			}
		});

		sideBar.addComponents(panel, emergency);
		setCompositionRoot(sideBar);
		
		Button selectButton = new Button("Select all");
	  selectButton.addStyleName(ValoTheme.BUTTON_LINK);
	  selectButton.addStyleName("small_button_link");
	  Button visibleButton = new Button("Expand all");
	  visibleButton.addStyleName(ValoTheme.BUTTON_LINK);
	  visibleButton.addStyleName("small_button_link");
	  
	  buttons.addComponents(selectButton, visibleButton);
	  buttons.addStyleName("af_uav_list_controls");
	  
	  selectButton.addClickListener( e -> {
	  	if (selectAll){
	  		selectAll(true);
	  		selectButton.setCaption("Deselect all");
	  		selectAll = false;
	  	}
	  	else {
	  		selectAll(false);
	  		selectButton.setCaption("Select all");
	  		selectAll = true;
	  	}
	  });
	  
	  visibleButton.addClickListener( e -> {
	  	if (visible){
	  		visible = false;
	  		setVisibility(true);
	  		visibleButton.setCaption("Expand all");
	  	}
	  	else {
	  		visible = true;
	  		setVisibility(false);
	  		visibleButton.setCaption("Collapse all");
	  	}
	  });
		
	  content.addComponent(buttons);
	  numUAVs = content.getComponentCount() - 1;
	  
	  try {
			service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			drones = service.getDrones();
			for (Entry<String, DroneStatus> e:drones.entrySet()){
				addBox(false, e.getValue().getID(), e.getValue().getStatus(), e.getValue().getBatteryLevel(), "green", e.getValue().getLatitude(), e.getValue().getLongitude(), e.getValue().getAltitude(), e.getValue().getVelocity(), false);
			}
		} catch (DronologyServiceException | RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}
	
	public void addBox(boolean isChecked, String name, String status, double batteryLife, String healthColor, long lat, long lon, int alt, double speed, boolean hoverInPlace){
		AFInfoBox box = new AFInfoBox(isChecked, name, status, batteryLife, healthColor, lat, lon, alt, speed, hoverInPlace);
		content.addComponent(box);
		numUAVs = content.getComponentCount() - 1;
		panel.setCaption(Integer.toString(numUAVs) + " Active UAVs");
	}
	
	public void addBox(){
		AFInfoBox box = new AFInfoBox();
		content.addComponent(box);
		numUAVs = content.getComponentCount() - 1;
		panel.setCaption(Integer.toString(numUAVs) + " Active UAVs");
	}
	
	/**
	 * 
	 * @param name
	 * 				the name/ID of the drone
	 * @return
	 * 				returns true if successful. returns false if failed
	 */
	public boolean removeBox(String name){
		for(int i = 1; i < numUAVs + 1; i++){
			AFInfoBox box = (AFInfoBox) content.getComponent(i);
			if (box.getName().equals(name)){
				content.removeComponent(box);
				numUAVs = content.getComponentCount() - 1;
				panel.setCaption(Integer.toString(numUAVs) + " Active UAVs");
				return true;
			}
		}
		return false;
	}
	
	public void selectAll(boolean select){
		for(int i = 1; i < numUAVs + 1; i++){
			AFInfoBox box = (AFInfoBox) content.getComponent(i);
			box.setIsChecked(select);
		}
	}
	
	public void setVisibility(boolean visible){
		for(int i = 1; i < numUAVs + 1; i++){
			AFInfoBox box = (AFInfoBox) content.getComponent(i);
			box.setBoxVisible(visible);
		}
	}
	
	public void setAllToHover(){
		for(int i = 1; i < numUAVs + 1; i++){
			AFInfoBox box = (AFInfoBox) content.getComponent(i);
			box.setHoverInPlace(true);
		}
	}
	
	public void refreshDrones(){
		try {
			Map<String, DroneStatus> newDrones;
			newDrones = service.getDrones();
			/**
			 * add new drones to the panel
			 */
			if (newDrones.size() > drones.size()){
				for (Entry<String, DroneStatus> e1:newDrones.entrySet()){
					for (Entry<String, DroneStatus> e2:drones.entrySet()){
						if (!e1.getValue().getID().equals(e2.getValue().getID())){
							this.addBox(false, e1.getValue().getID(), e1.getValue().getStatus(), e1.getValue().getBatteryLevel(), "green", e1.getValue().getLatitude(), e1.getValue().getLongitude(), e1.getValue().getAltitude(), e1.getValue().getVelocity(), false);
						}	
					}
				}
			}
			/**
			 * delete old drones from the panel
			 */
			if (newDrones.size() < drones.size()){
				for (Entry<String, DroneStatus> old:drones.entrySet()){
					boolean exists = false;
					for (Entry<String, DroneStatus> current:newDrones.entrySet()){
						if (old.getValue().getID().equals(current.getValue().getID()))
							exists = true;
					}
					if (!exists){
						for (int i = 1; i < numUAVs + 1; i++){
							AFInfoBox box = (AFInfoBox) content.getComponent(i);
							if (old.getValue().getID().equals(box.getName()))
									this.removeBox(box.getName());
						}
					}
				}
			}
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/**
		 * update current drones' status
		 */
		try {
			drones = service.getDrones();
			for (Entry<String, DroneStatus> e:drones.entrySet()){
				for (int i = 1; i < numUAVs + 1; i++){
					AFInfoBox box = (AFInfoBox) content.getComponent(i);
					if (e.getValue().getID().equals(box.getName())){
						box.setStatus(e.getValue().getStatus());
						box.setBatteryLife(e.getValue().getBatteryLevel());
						box.setLat(e.getValue().getLatitude());
						box.setLon(e.getValue().getLongitude());
						box.setAlt(e.getValue().getAltitude());
						box.setSpeed(e.getValue().getVelocity());
					}
				}
			}
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}

