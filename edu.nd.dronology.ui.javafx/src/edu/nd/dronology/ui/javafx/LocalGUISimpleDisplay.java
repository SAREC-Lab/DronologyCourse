package edu.nd.dronology.ui.javafx;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.status.DroneCollectionStatus;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.DecimalDegreesToXYConverter;
import edu.nd.dronology.core.util.DegreesFormatter;
import edu.nd.dronology.core.zone_manager.ZoneBounds;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.ui.javafx.start.DronologyFXUIRunner;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LocalGUISimpleDisplay extends Application {
	private Scene scene;
	private AnchorPane root;
	private static long xRange = 1600;
	private static long yRange = 960;
	private Map<String, ImageView> allDroneImages;
	private DecimalDegreesToXYConverter coordTransform;
	private static int LeftDivider = 180;
	private FlightZoneStatusPanel leftStatusDisplay;
	private IDroneSetupRemoteService setupService;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		try {
			setupService = (IDroneSetupRemoteService) DronologyFXUIRunner.provider.getRemoteManager()
					.getService(IDroneSetupRemoteService.class);

		

		} catch (Exception e) {
			e.printStackTrace();
		}

		allDroneImages = new HashMap<String, ImageView>();
		coordTransform = DecimalDegreesToXYConverter.getInstance();
		root = new AnchorPane();

		scene = new Scene(root, xRange, yRange);

		stage.setScene(scene);
		stage.show();

		Canvas canvas = new Canvas(LeftDivider, yRange);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);

		ZoneBounds zb = ZoneBounds.getInstance();
		zb.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
		DecimalDegreesToXYConverter.getInstance().setUp(xRange, yRange, LeftDivider); // Setup happens only once. Must happen after Zonebounds are set.

		String flightArea = "(" + DegreesFormatter.prettyFormatDegrees(zb.getNorthLatitude()) + ","
				+ DegreesFormatter.prettyFormatDegrees(zb.getWestLongitude()) + ") - ("
				+ DegreesFormatter.prettyFormatDegrees(zb.getSouthLatitude()) + ","
				+ DegreesFormatter.prettyFormatDegrees(zb.getEastLongitude()) + ")";

		stage.setTitle("Formation Simulator: " + flightArea);
		leftStatusDisplay = new FlightZoneStatusPanel(gc, LeftDivider, (int) yRange);

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				// System.out.println("Loading");
				loadDroneStatus();

			}
		}.start();
	}

	private void loadDroneStatus() {
		/// Map<String,DroneStatus> drones = DroneCollectionStatus.getInstance().getDrones();
		Map<String, DroneStatus> drones = null;
		try {
			drones = setupService.getDrones();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Create temporary Array Map of images to add.
		Map<String, ImageView> imagesToAdd = new HashMap<String, ImageView>();
		for (String droneID : drones.keySet()) {

			// Get current coordinates for each drone
			// These need to be transformed to x,y coordinates for the screen.
			DroneStatus droneStatus = drones.get(droneID);
			Point point = new Point();
			try {
				point = coordTransform.getPoint(droneStatus.getLatitude(), droneStatus.getLongitude());
			} catch (FlightZoneException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ImageView imgView;
			if (!(allDroneImages.containsKey(droneID))) { // This drone is not known and must be added.
				Image drnImage = new Image("file:images/drone.png", 50, 50, true, true);
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
		for (String droneID : imagesToAdd.keySet()) {
			allDroneImages.put(droneID, imagesToAdd.get(droneID));
			root.getChildren().add(imagesToAdd.get(droneID)); // add to JavaFX control
		}
	}

}