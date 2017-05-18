//package edu.nd.dronology.ui.cc.main.simulator;
//
//import java.awt.Point;
//import java.rmi.RemoteException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.Map;
//
//import org.eclipse.e4.core.commands.ECommandService;
//import org.eclipse.e4.core.commands.EHandlerService;
//import org.eclipse.e4.ui.model.application.MApplication;
//import org.eclipse.e4.ui.workbench.modeling.EModelService;
//import org.eclipse.e4.ui.workbench.modeling.EPartService;
//import org.eclipse.jface.layout.GridDataFactory;
//import org.eclipse.jface.layout.GridLayoutFactory;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//
//import edu.nd.dronology.core.gui_middleware.DroneStatus;
//import edu.nd.dronology.core.utilities.Coordinates;
//import edu.nd.dronology.core.utilities.DecimalDegreesToXYConverter;
//import edu.nd.dronology.core.utilities.DegreesFormatter;
//import edu.nd.dronology.core.zone_manager.FlightZoneException;
//import edu.nd.dronology.core.zone_manager.ZoneBounds;
//import edu.nd.dronology.services.core.info.DroneInitializationInfo;
//import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
//import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
//import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
//import javafx.animation.AnimationTimer;
//import javafx.embed.swt.FXCanvas;
//import javafx.scene.Scene;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.paint.Color;
//import net.mv.logging.ILogger;
//import net.mv.logging.LoggerProvider;
//
//@SuppressWarnings("restriction")
//public class SimulatorControl extends Composite {
//
//	private static final ILogger LOGGER = LoggerProvider.getLogger(SimulatorControl.class);
//
//	private Composite container;
//
//	private EPartService partService;
//	private EModelService modelService;
//	private MApplication app;
//	private ECommandService comandService;
//	private EHandlerService handlerService;
//
//	private HashMap<String, ImageView> allDroneImages;
//
//	private DecimalDegreesToXYConverter coordTransform;
//
//	private AnchorPane root;
//
//	private IDroneSetupRemoteService setupService;
//
//	private IFlightManagerRemoteService flightManagerService;
//
//	public SimulatorControl(Composite parent, EPartService partService, EModelService modelService,
//			ECommandService commandService, EHandlerService handlerService, MApplication app) {
//		super(parent, SWT.NONE);
//		this.partService = partService;
//		this.modelService = modelService;
//		this.comandService = commandService;
//		this.handlerService = handlerService;
//		this.app = app;
//		GridLayoutFactory.fillDefaults().applyTo(this);
//		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
//
//		createPartControl(this);
//		// addDnd();
//	}
//
//	/**
//	 * Create contents of the view part.
//	 * 
//	 * @param parent
//	 */
//	public void createPartControl(Composite parent) {
//		container = new Composite(parent, SWT.NONE);
//		GridLayout layout = new GridLayout(1, false);
//		layout.marginWidth = layout.marginHeight = 0;
//		container.setLayout(layout);
//		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		container.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//
//		Button btnRun = new Button(container, SWT.PUSH);
//		btnRun.setText("Load Drones");
//		btnRun.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				setup();
//
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//
//		Button btnRun2 = new Button(container, SWT.PUSH);
//		btnRun2.setText("Load Waypoints");
//		btnRun2.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				setupFlight();
//
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//
//		final FXCanvas fxCanvas = new FXCanvas(parent, SWT.NONE) {
//			@Override
//			public org.eclipse.swt.graphics.Point computeSize(int wHint, int hHint, boolean changed) {
//				getScene().getWindow().sizeToScene();
//				int width = (int) getScene().getWidth();
//				int height = (int) getScene().getHeight();
//				return new org.eclipse.swt.graphics.Point(width, height);
//			}
//		};
//
//		GridDataFactory.fillDefaults().grab(true, true).applyTo(fxCanvas);
//
//		// create the root layout pane
//		BorderPane layout2 = new BorderPane();
//
//		// create a Scene instance
//		// set the layout container as root
//		// set the background fill to the background color of the shell
//		Scene scene = new Scene(layout2, Color.rgb(parent.getShell().getBackground().getRed(),
//				parent.getShell().getBackground().getGreen(), parent.getShell().getBackground().getBlue()));
//
//		// set the Scene to the FXCanvas
//		fxCanvas.setScene(scene);
//		fxCanvas.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_CYAN));
//		start(scene);
//
//	}
//
//	public void setupFlight() {
//		ArrayList<Coordinates> wayPoints = new ArrayList<>();
//		wayPoints.add(new Coordinates(42270485, -86200000, 10));
//		wayPoints.add(new Coordinates(42500000, -86150000, 10));
//
//		try {
//			flightManagerService = (IFlightManagerRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
//					.getService(IFlightManagerRemoteService.class);
//
//			flightManagerService.planFlight(wayPoints.get(0), wayPoints);
//
//		} catch (Exception e) {
//			LOGGER.error(e);
//		}
//
//	}
//
//	// Test loads drones
//	public void setup() {
//		ArrayList<String[]> newDrones = new ArrayList<>();
//		String[] D1 = { "DRN1", "Iris3DR", "41760000", "-86222901", "0" };
//		String[] D2 = { "DRN2", "Iris3DR", "41750802", "-86202481", "10" };
//		String[] D3 = { "DRN3", "Iris3DR", "41740893", "-86182505", "0" };
//		newDrones.add(D1);
//		newDrones.add(D2);
//		newDrones.add(D3);
//
//		DroneInitializationInfo droneInfo = new DroneInitializationInfo("Iris3DR-Drone001", "DRN1",new Coordinates(41760000, -86222901, 0));
//
//		try {
//
//			setupService = (IDroneSetupRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
//					.getService(IDroneSetupRemoteService.class);
//
//			// setupService.initializeDrones(newDrones, false);
//
//			setupService.initializeDrones(droneInfo);
//
//		} catch (Exception e) {
//			LOGGER.error(e);
//		}
//
//		// DronologySetupDronesAccessPoint setup = DronologySetupDronesAccessPoint.getInstance();
//		// setup.initializeDrones(newDrones, false);
//
//	}
//
//	public void start(Scene scene) {
//		allDroneImages = new HashMap<String, ImageView>();
//		coordTransform = DecimalDegreesToXYConverter.getInstance();
//		root = new AnchorPane();
//
//		// Canvas canvas = new Canvas(scene, 100);
//		// GraphicsContext gc = canvas.getGraphicsContext2D();
//		// root.getChildren().add(canvas);
//
//		ZoneBounds zb = ZoneBounds.getInstance();
//		zb.setZoneBounds(41761022, -86243311, 41734699, -86168252, 100);
//		DecimalDegreesToXYConverter.getInstance().setUp(10, 10, 0); // Setup happens only once. Must happen after Zonebounds are set.
//
//		String flightArea = "(" + DegreesFormatter.prettyFormatDegrees(zb.getNorthLatitude()) + ","
//				+ DegreesFormatter.prettyFormatDegrees(zb.getWestLongitude()) + ") - ("
//				+ DegreesFormatter.prettyFormatDegrees(zb.getSouthLatitude()) + ","
//				+ DegreesFormatter.prettyFormatDegrees(zb.getEastLongitude()) + ")";
//
//		// stage.setTitle("Formation Simulator: " + flightArea);
//
//		new AnimationTimer() {
//			@Override
//			public void handle(long now) {
//				// System.out.println("Loading");
//				loadDroneStatus();
//
//			}
//		}.start();
//	}
//
//	private void loadDroneStatus() {
//		// Map<String, DroneStatus> drones = DroneCollectionStatus.getInstance().getDrones();
//		System.out.println("DRAW");
//		Map<String, DroneStatus> drones = new Hashtable<>();
//		try {
//			drones = setupService.getDrones();
//			// System.out.println(drones.size());
//		} catch (RemoteException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		// Create temporary Array Map of images to add.
//		Map<String, ImageView> imagesToAdd = new HashMap<String, ImageView>();
//		for (String droneID : drones.keySet()) {
//			// Get current coordinates for each drone
//			// These need to be transformed to x,y coordinates for the screen.
//			DroneStatus droneStatus = drones.get(droneID);
//			Point point = new Point();
//			try {
//				point = coordTransform.getPoint(droneStatus.getLatitude(), droneStatus.getLongitude());
//			} catch (FlightZoneException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			ImageView imgView;
//			if (!(allDroneImages.containsKey(droneID))) { // This drone is not known and must be added.
//				// System.out.println("DRAW");
//				// Image drnImage = new Image("images/drone.png", 50, 50, true, true);
//				Image drnImage = new Image(getClass().getResourceAsStream("images/drone.png"), 50, 50, true, true);
//				imgView = new ImageView(drnImage);
//				imgView.setX(point.getX());
//				imgView.setY(point.getY());
//				imagesToAdd.put(droneID, imgView);
//			} else {
//				// System.out.println(point);
//
//				allDroneImages.get(droneID).setX(point.getX());
//				allDroneImages.get(droneID).setY(point.getY());
//			}
//		}
//
//		// Add the new managed drones.
//		for (String droneID : imagesToAdd.keySet()) {
//			allDroneImages.put(droneID, imagesToAdd.get(droneID));
//			root.getChildren().add(imagesToAdd.get(droneID)); // add to JavaFX control
//		}
//	}
//
//	public void refresh() {
//
//	}
//
//}
