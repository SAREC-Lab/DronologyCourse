package view;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.nd.dronology.core.drone_status.DroneCollectionStatus;
import edu.nd.dronology.core.drone_status.DroneStatus;
import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightPlan;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.flight_manager.Flights;
import edu.nd.dronology.core.physical_environment.BaseManager;
import edu.nd.dronology.core.physical_environment.DroneBase;
import edu.nd.dronology.core.start.DronologyRunner;
import edu.nd.dronology.core.utilities.DecimalDegreesToXYConverter;
import edu.nd.dronology.core.utilities.DegreesFormatter;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import edu.nd.dronology.core.zone_manager.ZoneBounds;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.flights.data.FlightPlanWayPointCollection;
import model.flights.data.InteractiveWayPointDot;
import model.flights.xml.SaveXMLFlight;
import javafx.stage.Stage;
import view.DroneImage;

/**
 * Starts up the drone formation simulation extends Application class from JavaFX
 * Needs JavaFX to be installed. 
 * For eclipse:  Help / Add new Software / http://download.eclipse.org/efxclipse/updates-released/2.4.0/site
 * @author Jane Cleland-Huang
 * @version 0.1
 *
 */
public class DefaultLocalView extends Application {
	Scene scene;
	AnchorPane root;
	static long xRange = 1600;
	static long yRange = 960;
	Map<String,DroneStatus> drones;
	Map<String,ImageView> allDroneImages;
	DecimalDegreesToXYConverter coordTransform ;	
	static int LeftDivider = 180;	
    private FlightZoneStatusPanel leftStatusDisplay;	
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private void loadDroneStatus() throws FlightZoneException{
    	Map<String,DroneStatus> drones = DroneCollectionStatus.getInstance().getDrones();
    	
      	// Check if any drones have been removed.
    	// FIX
    	//r(String droneID: allDroneImages.keySet()){
    	//f (!(drones.containsKey(droneID))){
    	//root.getChildren().remove(allDroneImages.get(droneID));
    	//
    	//	
    	//
    	
    	// Check all drones and update their positions   		
    	
    	// Create temporary Array Map of images to add.
    	Map<String,ImageView> imagesToAdd = new HashMap<String,ImageView>();
    	for(String droneID: drones.keySet()){
    		
    		// Get current coordinates for each drone
    		// These need to be transformed to x,y coordinates for the screen.
    		DroneStatus droneStatus = drones.get(droneID);
    		Point point = coordTransform.getPoint(droneStatus.getLatitude(), droneStatus.getLongitude());    		
    		
    		ImageView imgView;
    		if (!(allDroneImages.containsKey(droneID))){  // This drone is not known and must be added.
    			Image drnImage = new Image("images\\drone.png",50,50,true,true);
    			imgView = new ImageView(drnImage);
    			imgView.setX(point.getX());
    			imgView.setY(point.getY());
    			imagesToAdd.put(droneID, imgView);
    		} else {    			
    			allDroneImages.get(droneID).setX(point.getX());
    			allDroneImages.get(droneID).setY(point.getY());   			
    		}
    	}
    	
    	// Add the new managed drones.
    	for (String droneID: imagesToAdd.keySet()){
    		allDroneImages.put(droneID,imagesToAdd.get(droneID));
    		root.getChildren().add(imagesToAdd.get(droneID));  // add to JavaFX control
    	}    		
    }

	/**
	 * Initial setup included setting simulation type
	 * @param args
	 */
	public DefaultLocalView(String[] args){	
		ZoneBounds zoneBounds = ZoneBounds.getInstance();
	    //zoneBounds.setZoneBounds(42722381, -86290828, 41660473, -86140256, 100);
	    zoneBounds.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
		DecimalDegreesToXYConverter.getInstance().setUp(xRange, yRange, LeftDivider);
		//launch(args);
	}
	
	@Override
	public void start(Stage stage) { 
		System.out.println("ARRIVED HERE");
		    coordTransform = DecimalDegreesToXYConverter.getInstance();	
	        root = new AnchorPane();	
	       
			scene = new Scene(root,xRange,yRange); 
			
			stage.setScene(scene);
			stage.show();
			
			Canvas canvas = new Canvas(LeftDivider, yRange);
	        GraphicsContext gc = canvas.getGraphicsContext2D();
			root.getChildren().add(canvas);		
			
			ZoneBounds zb = ZoneBounds.getInstance();
			zb.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
			DecimalDegreesToXYConverter.getInstance().setUp(xRange, yRange, LeftDivider);  //Setup happens only once.  Must happen after Zonebounds are set.
  
			String flightArea = "(" + DegreesFormatter.prettyFormatDegrees(zb.getNorthLatitude()) + "," + 
					DegreesFormatter.prettyFormatDegrees(zb.getWestLongitude()) + ") - (" + 
					DegreesFormatter.prettyFormatDegrees(zb.getSouthLatitude()) + "," + 
					DegreesFormatter.prettyFormatDegrees(zb.getEastLongitude())+")";
			
			stage.setTitle("Formation Simulator: " + flightArea);
	
			//leftStatusDisplay = new FlightZoneStatusPanel(gc, LeftDivider,(int)yRange);
			//try {
			//	setZone();
			//} catch (InterruptedException e1) {
			//	// TODO Auto-generated catch block
			//	e1.printStackTrace();
			//}

			// need to add this back in
	        //displayFlightInfo(gc);

	      /*  new AnimationTimer() { 
	            @Override
	            public void handle(long now) {
	            	
	            	try {
						loadDroneStatus();
					} catch (FlightZoneException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            		
		       }
	        }.start();
	        */
	}	
	

	public int getReservedLeftHandSpace(){
		return LeftDivider;
	}
	

	/**
	 * Set Zone bounds
	 * @throws InterruptedException
	 */
	public void setZone() throws InterruptedException{
		ZoneBounds zoneBounds = ZoneBounds.getInstance();
	    zoneBounds.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
		DecimalDegreesToXYConverter.getInstance().setUp(xRange, yRange, LeftDivider);  //Setup happens only once.  Must happen after Zonebounds are set.
//		try {
//			constructBases(5);
//		} catch (FlightZoneException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
//	public void constructBases(int baseCount) throws FlightZoneException{
//		baseManager = new BaseManager(5);
//		HashMap<String,DroneBase> droneBases = baseManager.getBases();
//		for (DroneBase base: droneBases.values()) 
//		    root.getChildren().add(base.getCircle());	    
//	}
	
	/**
	 * 	
	 * @return X range of screen display
	 */
	public long getXRange(){
		return xRange;
	}
	
	/**
	 * 
	 * @return Y range of screen display
	 */
	public long getYRange(){
		return yRange;
	}
	
	
	
}	


