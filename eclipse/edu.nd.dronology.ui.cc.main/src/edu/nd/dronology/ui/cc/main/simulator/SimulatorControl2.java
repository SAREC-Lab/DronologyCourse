package edu.nd.dronology.ui.cc.main.simulator;

import java.awt.MouseInfo;
import java.awt.Point;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.util.concurrent.RateLimiter;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.flightzone.ZoneBounds;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.util.DecimalDegreesToXYConverter;
import edu.nd.dronology.core.util.DegreesFormatter;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

@SuppressWarnings("restriction")
public class SimulatorControl2 extends Composite {

	private static final ILogger LOGGER = LoggerProvider.getLogger(SimulatorControl2.class);

	private Composite container;

	private EPartService partService;
	private EModelService modelService;
	private MApplication app;
	private ECommandService comandService;
	private EHandlerService handlerService;

	private HashMap<String, ImageView> allDroneImages;

	private DecimalDegreesToXYConverter coordTransform;

	private Pane root;

	private IDroneSetupRemoteService setupService;

	private IFlightManagerRemoteService flightManagerService;

	private static long xRange = 1100;
	private static long yRange = 600;
	private static int LeftDivider = 0;

	private FXCanvas fxCanvas;

	private org.eclipse.swt.widgets.Label lblBounds;

	RateLimiter LIMITER = RateLimiter.create(1);

	public SimulatorControl2(Composite parent, EPartService partService, EModelService modelService,
			ECommandService commandService, EHandlerService handlerService, MApplication app) {
		super(parent, SWT.NONE);
		this.partService = partService;
		this.modelService = modelService;
		this.comandService = commandService;
		this.handlerService = handlerService;
		this.app = app;
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);

		createPartControl(this);
		// addDnd();
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = layout.marginHeight = 0;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		container.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Button btnRun = new Button(container, SWT.PUSH);
		btnRun.setText("Load Drones");
		btnRun.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setup();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button btnRun2 = new Button(container, SWT.PUSH);
		btnRun2.setText("Load Waypoints");
		btnRun2.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setupFlight();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		lblBounds = new org.eclipse.swt.widgets.Label(container, SWT.FLAT);
		lblBounds.setText("FlightZone: -- not set --");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(container);

		fxCanvas = new FXCanvas(parent, SWT.BORDER);
		GridLayoutFactory.fillDefaults().applyTo(fxCanvas);
		GridDataFactory.fillDefaults().span(3, 1).grab(true, true).applyTo(fxCanvas);

		root = new AnchorPane();
		Scene scene = new Scene(root);

		fxCanvas.setScene(scene);

		try {
			setupService = (IDroneSetupRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSetupRemoteService.class);
		} catch (RemoteException | DronologyServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		start(scene);

		fxCanvas.redraw();
		fxCanvas.layout();
		
		fxCanvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				//point = coordTransform.getPoint(droneStatus.getLatitude(), droneStatus.getLongitude());
				System.out.println(MouseInfo.getPointerInfo().getLocation());
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		

	}

	public void setupFlight() {
		ArrayList<Coordinate> wayPoints = new ArrayList<>();
		wayPoints.add(new Coordinate(42270485, -86200000, 10));
		wayPoints.add(new Coordinate(42500000, -86150000, 10));

		try {
			flightManagerService = (IFlightManagerRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IFlightManagerRemoteService.class);

			flightManagerService.planFlight(wayPoints.get(0), wayPoints);

		} catch (Exception e) {
			LOGGER.error(e);
		}

	}

	// Test loads drones
	public void setup() {
		ArrayList<String[]> newDrones = new ArrayList<>();
		String[] D1 = { "DRN1", "Iris3DR", "41760000", "-86222901", "0" };
		String[] D2 = { "DRN2", "Iris3DR", "41750802", "-86202481", "10" };
		String[] D3 = { "DRN3", "Iris3DR", "41740893", "-86182505", "0" };
		newDrones.add(D1);
		// newDrones.add(D2);
		// newDrones.add(D3);

		try {

			setupService = (IDroneSetupRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSetupRemoteService.class);

			setupService.initializeDrones(newDrones, false);

		} catch (Exception e) {
			LOGGER.error(e);
		}

		// DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
		// setup.initializeDrones(newDrones, false);

	}

	public void start(Scene scene) {
		Display.getDefault().asyncExec(() -> {

			allDroneImages = new HashMap<>();
			coordTransform = DecimalDegreesToXYConverter.getInstance();

			ZoneBounds zb = ZoneBounds.getInstance();
			zb.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
			DecimalDegreesToXYConverter.getInstance().setUp(xRange, yRange, LeftDivider); // Setup happens only once. Must happen after Zonebounds are set.

			String flightArea = "(" + DegreesFormatter.prettyFormatDegrees(zb.getNorthLatitude()) + ","
					+ DegreesFormatter.prettyFormatDegrees(zb.getWestLongitude()) + ") - ("
					+ DegreesFormatter.prettyFormatDegrees(zb.getSouthLatitude()) + ","
					+ DegreesFormatter.prettyFormatDegrees(zb.getEastLongitude()) + ")";

			lblBounds.setText("FlightZone: " + flightArea);
			lblBounds.getParent().layout();

			new AnimationTimerExt(250) {
		
				@Override
				public void handle() {
					loadDroneStatus();
					
				}
			}.start();

		});
		
		
		

	}

	static int run = 0;

	private void loadDroneStatus() {
		// Map<String, DroneStatus> drones = DroneCollectionStatus.getInstance().getDrones();
		try {
			if (setupService == null) {
				LOGGER.info("no connection to server");
				return;
			}
			Map<String, DroneStatus> drones;

			drones = setupService.getDrones();
			// Create temporary Array Map of images to add.
			Map<String, ImageView> imagesToAdd = new HashMap<>();
			for (String droneID : drones.keySet()) { 
				// Get current coordinates for each drone
				// These need to be transformed to x,y coordinates for the screen.
				DroneStatus droneStatus = drones.get(droneID);
				Point point = new Point();

				point = coordTransform.getPoint(droneStatus.getLatitude(), droneStatus.getLongitude());

				ImageView imgView;
				if (!(allDroneImages.containsKey(droneID))) { // This drone is not known and must be added.
					// System.out.println("Adding new Drone");
					// Image drnImage = new Image("images/drone.png", 50, 50, true, true);
					URL url = DronologyMainActivator.getDefault().getLocationOfFile("images/DJI.png");

					Image drnImage = null;
					drnImage = new Image(url.openStream(), 50, 50, true, true);

					imgView = new ImageView(drnImage);
					imgView.setX(point.getX());
					imgView.setY(point.getY());
					imagesToAdd.put(droneID, imgView);
				} else {
					// System.out.println("Drawing existing Drone ->" + point.getX() + ":" + point.getY());
					ImageView image = allDroneImages.get(droneID);
					image.setX(point.getX());
					image.setY(point.getY());

					Circle c0d = new Circle();
					c0d.setCenterX(point.getX() + 25);
					c0d.setCenterY(point.getY() + 25);
					c0d.setRadius(2f);
					root.getChildren().add(c0d);
				}
			}

			// Add the new managed drones.
			for (String droneID : imagesToAdd.keySet()) {
				// System.out.println("ADD");
				allDroneImages.put(droneID, imagesToAdd.get(droneID));
				root.getChildren().add(imagesToAdd.get(droneID)); // add to JavaFX control
			}

		} catch (FlightZoneException | Exception ex) {
			LOGGER.error(ex);
		}

	}

	public void refresh() {

	}

}
