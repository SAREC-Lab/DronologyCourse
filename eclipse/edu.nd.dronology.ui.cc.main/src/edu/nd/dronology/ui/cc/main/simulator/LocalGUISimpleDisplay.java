//package edu.nd.dronology.ui.cc.main.simulator;
//
//import java.awt.Point;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import edu.nd.dronology.core.gui_middleware.DroneCollectionStatus;
//import edu.nd.dronology.core.gui_middleware.DroneStatus;
//import edu.nd.dronology.core.utilities.DecimalDegreesToXYConverter;
//import edu.nd.dronology.core.utilities.DegreesFormatter;
//import edu.nd.dronology.core.zone_manager.FlightZoneException;
//import edu.nd.dronology.core.zone_manager.ZoneBounds;
//import javafx.animation.AnimationTimer;
//import javafx.application.Application;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.control.Button;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//import view.FlightZoneStatusPanel;
//
//public class LocalGUISimpleDisplay extends Application {
//	Scene scene;
//	AnchorPane root;
//	static long xRange = 1600;
//	static long yRange = 960;
//	Map<String,DroneStatus> drones;
//	Map<String,ImageView> allDroneImages;
//	DecimalDegreesToXYConverter coordTransform ;	
//	static int LeftDivider = 180;	
//    private FlightZoneStatusPanel leftStatusDisplay;	
//    
//   public static void main(String[] args) {
//       launch(args);
//   }
//    
//     @Override
//   public void start(Stage stage) {
//    	allDroneImages = new HashMap<String,ImageView>();
//	    coordTransform = DecimalDegreesToXYConverter.getInstance();	
//        root = new AnchorPane();	
//       
//		scene = new Scene(root,xRange,yRange); 
//		
//		stage.setScene(scene);
//		stage.show();
//		
//		Canvas canvas = new Canvas(LeftDivider, yRange);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//		root.getChildren().add(canvas);		
//		
//		ZoneBounds zb = ZoneBounds.getInstance();
//		zb.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
//		DecimalDegreesToXYConverter.getInstance().setUp(xRange, yRange, LeftDivider);  //Setup happens only once.  Must happen after Zonebounds are set.
//
//		String flightArea = "(" + DegreesFormatter.prettyFormatDegrees(zb.getNorthLatitude()) + "," + 
//				DegreesFormatter.prettyFormatDegrees(zb.getWestLongitude()) + ") - (" + 
//				DegreesFormatter.prettyFormatDegrees(zb.getSouthLatitude()) + "," + 
//				DegreesFormatter.prettyFormatDegrees(zb.getEastLongitude())+")";
//		
//		stage.setTitle("Formation Simulator: " + flightArea);
//		leftStatusDisplay = new FlightZoneStatusPanel(gc, LeftDivider,(int)yRange);
//		
//		new AnimationTimer() { 
//            @Override
//            public void handle(long now) {
//            	//System.out.println("Loading");
//            	loadDroneStatus();
//            	           		
//	       }
//        }.start();
//   }
//   
//   private void loadDroneStatus(){
//   	Map<String,DroneStatus> drones = DroneCollectionStatus.getInstance().getDrones();
//   	
//    // Create temporary Array Map of images to add.
//   	Map<String,ImageView> imagesToAdd = new HashMap<String,ImageView>();
//   	for(String droneID: drones.keySet()){
//   		
//   		// Get current coordinates for each drone
//   		// These need to be transformed to x,y coordinates for the screen.
//   		DroneStatus droneStatus = drones.get(droneID);
//   		Point point = new Point();
//		try {
//			point = coordTransform.getPoint(droneStatus.getLatitude(), droneStatus.getLongitude());
//		} catch (FlightZoneException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}    		
//   		
//   		ImageView imgView;
//   		if (!(allDroneImages.containsKey(droneID))){  // This drone is not known and must be added.
//   			Image drnImage = new Image("images\\drone.png",50,50,true,true);
//   			imgView = new ImageView(drnImage);
//   			imgView.setX(point.getX());
//   			imgView.setY(point.getY());
//   			imagesToAdd.put(droneID, imgView);
//   		} else {    			
//   			allDroneImages.get(droneID).setX(point.getX());
//   			allDroneImages.get(droneID).setY(point.getY());   			
//   		}
//   	}
//   	
//   	// Add the new managed drones.
//   	for (String droneID: imagesToAdd.keySet()){
//   		allDroneImages.put(droneID,imagesToAdd.get(droneID));
//   		root.getChildren().add(imagesToAdd.get(droneID));  // add to JavaFX control
//   	}    		
//   }
//   
//}