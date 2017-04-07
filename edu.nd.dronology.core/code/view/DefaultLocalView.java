package view;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.nd.dronology.core.drone_status.DroneCollectionStatus;
import edu.nd.dronology.core.drone_status.DroneStatus;
import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.fleet_manager.RuntimeDroneTypes;
import edu.nd.dronology.core.flight_manager.FlightPlan;
import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.flight_manager.Flights;
import edu.nd.dronology.core.physical_environment.BaseManager;
import edu.nd.dronology.core.physical_environment.DroneBase;
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
	
	static int LeftDivider = 180;	
    private FlightZoneStatusPanel leftStatusDisplay;	
    
    private void loadDroneStatus(){
    	Map<String,DroneStatus> drones = DroneCollectionStatus.getInstance().getDrones();
    	
    	// Check if any drones have been removed.
    	//for(String droneID: allDroneImages.keySet()){
    	//	if (!(drones.containsKey(droneID))){
    	//		// Do something to remove it
    	//	}	
    	//}
    	
    	// Check all drones and update their positions
    	for(String droneID: drones.keySet()){
    		if (!(allDroneImages.containsKey(droneID))){  // This drone is not known and must be added.
    			Image droneImage = new Image("images\\drone.png",50,50,true,true);
    			ImageView imgView = new ImageView(droneImage);
    			imgView.setX(drones.get(droneID).);
    			allDroneImages.put(droneID,new ImageView(droneImage));
    			
    			
    			droneImageView = new ImageView(droneImage);
    			droneImageView.setX(pnt.x);
    			droneImageView.setY(pnt.y);	
    		}
    	}
    		
    }

	/**
	 * Initial setup included setting simulation type
	 * @param args
	 */
	public static void DefaultLocalView(String[] args){		
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception { 
		    delImageViews = new ArrayList<ImageView>();
	        root = new AnchorPane();	
	       
	        imageViewQueue = new ArrayList<ImageView>();
			scene = new Scene(root,xRange,yRange); 
			
			stage.setScene(scene);
			stage.show();
			
			Canvas canvas = new Canvas(LeftDivider, yRange);
	        GraphicsContext gc = canvas.getGraphicsContext2D();
			root.getChildren().add(canvas);
			
			
			
			ZoneBounds zb = ZoneBounds.getInstance();
			String flightArea = "(" + DegreesFormatter.prettyFormatDegrees(zb.getNorthLatitude()) + "," + 
					DegreesFormatter.prettyFormatDegrees(zb.getWestLongitude()) + ") - (" + 
					DegreesFormatter.prettyFormatDegrees(zb.getSouthLatitude()) + "," + 
					DegreesFormatter.prettyFormatDegrees(zb.getEastLongitude())+")";
			
			stage.setTitle("Formation Simulator: " + flightArea);

		
			leftStatusDisplay = new FlightZoneStatusPanel(gc, LeftDivider,(int)yRange);
			setZone();

	        displayFlightInfo(gc);

	        new AnimationTimer() { 
	            @Override
	            public void handle(long now) {
	            	
	            	// We cannot directly call loadImage from another thread.  FX thread has to load them itself.
	            	while (!imageViewQueue.isEmpty()){
	            		loadImage(imageViewQueue.get(0));
	            	    imageViewQueue.remove(0);
	            	}
	            	
	            	for(ImageView imgView: delImageViews){
	            		if(root.getChildren().contains(imgView))
	            			root.getChildren().remove(imgView);
	            	}
	            		
	            	for(DroneImage droneImage: allDroneImages){
	            			// The droneImage holds references to both the ImageView and an inner drone.
	            			// Ask the droneImage object to update the ImageView's coordinates from the drone's current position.
	            			//iDrone drone = droneImage.getDroneFromImage();
	            			try {
								droneImage.updateImageCoordinates();
							} catch (FlightZoneException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	            	} 
	            	//displayFlightInfo(gc);  // Side panel
	            }
	        }.start();
	}	
	

	public int getReservedLeftHandSpace(){
		return LeftDivider;
	}
	
//	private void displayFlightInfo(GraphicsContext gc){
//		leftStatusDisplay.displayTopOfPanel();
//		leftStatusDisplay.displayCurrentFlights(flights.getCurrentFlights(), flights.getAwaitingTakeOffFlights()); // delegate
//		leftStatusDisplay.displayPendingFlights(flights.getPendingFlights());
//		leftStatusDisplay.displayCompletedFlights(flights.getCompletedFlights());
//	}
			
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
	 * Add an drone image
	 * @param droneImg
	 */
	public void addDroneImage(DroneImage droneImg){
		allDroneImages.add(droneImg);
	}
	
	/**
	 * Remove a drone image
	 * @param droneImg
	 * @throws FlightZoneException
	 */
	public void removeDroneImage(DroneImage droneImg) throws FlightZoneException{
		if(allDroneImages.contains(droneImg))
			allDroneImages.remove(droneImg);
	    else 
	    	throw new FlightZoneException("DroneFlight image not found in list and therefore not removed.");
	}
	
	/**
	 * Creates a drone image for a drone
	 * @param drone
	 * @throws FlightZoneException 
	 */
	public void createDroneImage(ManagedDrone drone) throws FlightZoneException{
		DroneImage droneImage;
		if(drone.getDroneImage()== null){
			droneImage = new DroneImage(drone,this);
			drone.registerImage(droneImage);
		} else 
			droneImage = drone.getDroneImage();
		imageViewQueue.add(droneImage.getDroneImage());
		allDroneImages.add(droneImage);  
	}
	
	/**
	 * Remove drone image given a drone object
	 * @param drone
	 */
	public void removeDroneImage(ManagedDrone drone){
		if (drone.getDroneImage()!=null){
			DroneImage droneImage = drone.getDroneImage();
			delImageViews.add(droneImage.getDroneImage());
		}
	}
	
	/**
	 * Load an image view
	 * Refactoring needed
	 * @param imgView
	 */
	public void loadImage(ImageView imgView){	
		if(!(root.getChildren().contains(imgView)))
			root.getChildren().add(imgView);
	}
	
	/**
	 * Remove an image view
	 * Refactoring needed
	 * @param imgView
	 */
	public void removeImage(ImageView imgView){
		System.out.println(" NOW REMOVING AN IMAGE ");
		if (root.getChildren().contains(imgView))
			root.getChildren().remove(imgView);
	}
	
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
	
	public void setPlanningMode(boolean planningStatus){
		if (planningStatus == true)
			planningMode = true;
		else
			planningMode = false;
	}
	
}	


