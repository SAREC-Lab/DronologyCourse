package edu.nd.dronology.ui.cc.main.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.nd.dronology.ui.cc.application.constants.CommandConstants;
import edu.nd.dronology.ui.cc.application.constants.PerspectiveConstants;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

@SuppressWarnings("restriction")
public class LauncherControl extends Composite {

	private static final ILogger LOGGER = LoggerProvider.getLogger(LauncherControl.class);

	private static final int SIZE_X = 1200;
	private static final int SIZE_Y = 800;

	private static final Color BLUE_COLOR = StyleProvider.COLOR_LIVE;
	private static final Color ORANGE_COLOR = StyleProvider.LAUNCHER_BLUE;
	private static final Color GREEN_COLOR = StyleProvider.LAUNCHER_DARK;
	private static final Color GRAY_COLOR = StyleProvider.COLOR_MISC;

	private Composite container;

	private Pushable pushFlightPlan;
	private Pushable pushTakeoff;
	private Pushable pushSettings;
	private Pushable pushSimulator;
	private Pushable pushSpecification;
	private Pushable pushMonitor;
	private Pushable pushSimulatorScenario;
	private Pushable pushSafetyMonitor;


	private List<Pushable> pushables = new ArrayList<>();
	private Label lblBanner;
	
	private EPartService partService;
	private EModelService modelService;
	private MApplication app;
	private ECommandService comandService;
	private EHandlerService handlerService;



	public LauncherControl(Composite parent, EPartService partService, EModelService modelService,
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
		//addDnd();
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		ServiceProvider.getBaseServiceProvider();
		
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		createTitleBar();

		Composite bodyComp = new Composite(container, SWT.NONE);
		bodyComp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridLayout bodyLayout = new GridLayout(4, false);
		layout.marginWidth = layout.marginHeight = 0;
		bodyComp.setLayout(bodyLayout);
		bodyComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		createSpacer(bodyComp);

		Composite pushableContainer = new Composite(bodyComp, SWT.NONE);

		// pushableContainer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		GridLayout layout2 = new GridLayout(3, false);
		layout2.marginWidth = layout.marginHeight = layout.marginTop = 0;
		layout2.verticalSpacing = 0;
		layout2.horizontalSpacing = 3;
		pushableContainer.setLayout(layout2);
		pushableContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		pushableContainer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		// ROW 1
		int indent = -12;

		pushFlightPlan = new Pushable(pushableContainer, "Flight-Route/Planning", ImageProvider.IMG_LAUNCHER_FLIGHTPLAN, //$NON-NLS-1$
				ImageProvider.IMG_LAUNCHER_FLIGHTPLAN_LARGE, ImageProvider.IMG_LAUNCHER_FLIGHTPLAN, indent, BLUE_COLOR,
				checkLoaded(PerspectiveConstants.FLIGHT_ROUTEPLANNING_PERSPECTIVE));

		
		pushTakeoff = new Pushable(pushableContainer, "Take-off", ImageProvider.IMG_LAUNCHER_TAKEOFF, //$NON-NLS-1$
				ImageProvider.IMG_LAUNCHER_TAKEOFF_LARGE, ImageProvider.IMG_LAUNCHER_TAKEOFF, indent, ORANGE_COLOR,
				checkLoaded(PerspectiveConstants.FLIGHT_ROUTEPLANNING_PERSPECTIVE));

		
		pushSpecification = new Pushable(pushableContainer, "Manage /UAV Specifications", ImageProvider.IMG_LAUNCHER_SPECIFICATION, //$NON-NLS-1$
				ImageProvider.IMG_LAUNCHER_SPECIFICATION_LARGE, ImageProvider.IMG_LAUNCHER_SPECIFICATION, indent, GREEN_COLOR,
				checkLoaded(PerspectiveConstants.SPECIFICATION_PERSPECTIVE));
		

		// ROW 2
		indent = -8;
		pushMonitor = new Pushable(pushableContainer, "Monitor Drone Status", ImageProvider.IMG_LAUNCHER_MONITOR, //$NON-NLS-1$
				ImageProvider.IMG_LAUNCHER_MONITOR_LARGE, ImageProvider.IMG_LAUNCHER_MONITOR, indent, BLUE_COLOR,
				checkLoaded(PerspectiveConstants.MONITOR_PERSPECTIVE));

		
		pushSimulator = new Pushable(pushableContainer, "Virtual Drone/Simulator", ImageProvider.IMG_LAUNCHER_SIMULATOR, //$NON-NLS-1$
				ImageProvider.IMG_LAUNCHER_SIMULATOR_LARGE, ImageProvider.IMG_LAUNCHER_SIMULATOR, indent, ORANGE_COLOR,
				checkLoaded(PerspectiveConstants.SIMULATOR_PERSPECTIVE));
		
		pushSettings = new Pushable(pushableContainer, "General/Settings", ImageProvider.IMG_LAUNCHER_SETTINGS, //$NON-NLS-1$
				ImageProvider.IMG_LAUNCHER_SETTINGS_LARGE, ImageProvider.IMG_LAUNCHER_SETTINGS, indent, GRAY_COLOR);
	
		indent = -5;
		pushSafetyMonitor = new Pushable(pushableContainer, "Safety Monitoring", ImageProvider.IMG_LAUNCHER_SAFETYMONITORING, //$NON-NLS-1$
				ImageProvider.IMG_LAUNCHER_SAFETYMONITORING_LARGE, ImageProvider.IMG_LAUNCHER_SAFETYMONITORING, indent, ORANGE_COLOR);
		pushSimulatorScenario = new Pushable(pushableContainer, "Simulator Scenarios", ImageProvider.IMG_LAUNCHER_SIMSCENARIO, //$NON-NLS-1$
				ImageProvider.IMG_LAUNCHER_SIMSCENARIO_LARGE, ImageProvider.IMG_LAUNCHER_SIMSCENARIO, indent, ORANGE_COLOR);

	
		createTile(pushableContainer, indent);
	   // createTile(pushableContainer, indent);
		createSpacer(bodyComp);

	
		pushables.add(pushFlightPlan);
		pushables.add(pushTakeoff);
		pushables.add(pushSimulator);
		pushables.add(pushSettings);
		pushables.add(pushSpecification);
		pushables.add(pushMonitor);
		pushables.add(pushSimulatorScenario);
		pushables.add(pushSafetyMonitor);
		setInfoText();
		addListener();

		pushSettings.setState(true);
		pushFlightPlan.setState(true);
		pushTakeoff.setState(true);
		pushSimulator.setState(true);
		pushSpecification.setState(true);
		pushMonitor.setState(true);
		pushSimulatorScenario.setState(true);
		pushSafetyMonitor.setState(true);
	}

	private void setInfoText() {
	
		
	}

	private boolean checkLoaded(String perspectiveId) {
		return true;
	}

	private void createSpacer(Composite parent) {
		Label spacer = new Label(parent, SWT.FLAT);
		spacer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridDataFactory.fillDefaults().grab(true, true).hint(177, 100).applyTo(spacer);
	}

	private void createTitleBar() {
		Composite logo = new Composite(container, SWT.FLAT);
		logo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridLayoutFactory.fillDefaults().applyTo(logo);
		GridDataFactory.fillDefaults().hint(0, 90).span(2, 1).applyTo(logo);
		lblBanner = new Label(logo, SWT.FLAT);
		lblBanner.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		lblBanner.setImage(ImageProvider.IMG_BANNER);
		lblBanner.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

	}

	private void addListener() {
	
		pushFlightPlan.addListener(SWT.MouseUp, (Event e) -> {
			if (listenerEnabled)
				switchToPerspective(PerspectiveConstants.FLIGHT_ROUTEPLANNING_PERSPECTIVE);
		});
		
		pushTakeoff.addListener(SWT.MouseUp, (Event e) -> {
			if (listenerEnabled)
				switchToPerspective(PerspectiveConstants.TAKEOFF_PERSPECTIVE);
		});
		
		pushSimulator.addListener(SWT.MouseUp, (Event e) -> {
			if (listenerEnabled)
				switchToPerspective(PerspectiveConstants.SIMULATOR_PERSPECTIVE);
		});
		
		pushSpecification.addListener(SWT.MouseUp, (Event e) -> {
			if (listenerEnabled)
				switchToPerspective(PerspectiveConstants.SPECIFICATION_PERSPECTIVE);
		});
		
		pushMonitor.addListener(SWT.MouseUp, (Event e) -> {
			if (listenerEnabled)
				switchToPerspective(PerspectiveConstants.MONITOR_PERSPECTIVE);
		});
		
		pushSimulatorScenario.addListener(SWT.MouseUp, (Event e) -> {
			if (listenerEnabled)
				switchToPerspective(PerspectiveConstants.SIMULATOR_SCENARIO_PERSPECTIVE);
		});
		
		pushSettings.addListener(SWT.MouseUp, (Event e) -> {
			if (listenerEnabled)
				showSettings();
		});
		pushSafetyMonitor.addListener(SWT.MouseUp, (Event e) -> {
			if (listenerEnabled)
				switchToPerspective(PerspectiveConstants.SAFETY_PERSPECTIVE);
		});
	
		

		container.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				try {
					listenerEnabled = false;
					MWindow window = app.getSelectedElement();
					if (window == null) {
						return;
					}

					int width = window.getWidth();
					int height = window.getHeight();
					if (width > SIZE_X || height > SIZE_Y) {
						resize(width, height);
					} else {
						defaultSize();
					}
					Display.getDefault().timerExec(50, () -> {
						listenerEnabled = true;

					});
				} catch (Throwable t) {
					LOGGER.error(t);
				}
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});

		container.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (container.getShell().getMaximized()) {
					MWindow window = app.getSelectedElement();
					if (window == null) {
						return;
					}
					int width = container.getShell().getSize().x;
					int height = container.getShell().getSize().y;

					if (width > SIZE_X || height > SIZE_Y) {
						resize(width, height);
					} else {
						defaultSize();
					}

				}
			}
		});

	}

	boolean listenerEnabled = true;

	protected void showSearch() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ParameterizedCommand cmd = comandService.createCommand(CommandConstants.SEARCH_ONLINE_COMMAND, parameters);
		handlerService.executeHandler(cmd);
	}

	protected void defaultSize() {
		for (Composite p : pushables) {
			if (p instanceof Pushable) {
				((Pushable) p).setDefaultSize();
			} else {
				GridData d = (GridData) p.getLayoutData();
				d.heightHint = Pushable.TILE_HEIGHT;
				d.widthHint = 200;
			}
		}

		container.layout();

		double bheight = 90;
		lblBanner.setImage(ImageProvider.IMG_BANNER);
		lblBanner.getParent().layout();
		GridDataFactory.fillDefaults().hint(0, new Double(bheight).intValue()).span(2, 1)
				.applyTo(lblBanner.getParent());

	}

	protected void resize(int width, int height) {
		for (Pushable p : pushables) {
			GridData d = (GridData) p.getLayoutData();
			Double dheight = (height - 200) / 3d;
			Double dwidth = (width - 500) / 3d;

			p.resize(dwidth.intValue(), dheight.intValue());

		}

		double bheight = (width - 30) * 0.075d;
		lblBanner.setImage(resizeBanner(width - 30, new Double(bheight).intValue()));
		lblBanner.getParent().layout();
		GridDataFactory.fillDefaults().hint(0, new Double(bheight).intValue()).span(2, 1)
				.applyTo(lblBanner.getParent());

		container.layout();
	}

	private Image resizeBanner(int width, int height) {
		// return new Image(getDisplay(), image.getImageData().scaledTo(width,
		// height));

		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		// gc.fillRectangle(0, 0, width, height);
		gc.drawImage(ImageProvider.IMG_BANNER, 0, 0,

				ImageProvider.IMG_BANNER.getBounds().width, ImageProvider.IMG_BANNER.getBounds().height, 0,
				0, width, height);
		gc.dispose();
		// image.dispose(); // don't forget about me!
		return scaled;

	}

	protected void showSettings() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ParameterizedCommand cmd = comandService.createCommand(CommandConstants.SHOW_SETTINGS_COMMAND, parameters);
		handlerService.executeHandler(cmd);

	}

	private void switchToPerspective(String perspectiveId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", perspectiveId);
		ParameterizedCommand cmd = comandService.createCommand(CommandConstants.SWTICH_PERSPECTIVE_COMMAND, parameters);
		handlerService.executeHandler(cmd);
	}

	protected void performImportAction() {
		ParameterizedCommand cmd = comandService.createCommand(CommandConstants.IMPORT_COMMAND, null);
		handlerService.executeHandler(cmd);
	}

	protected void performExportAction() {
		ParameterizedCommand cmd = comandService.createCommand(CommandConstants.EXPORT_COMMAND, null);
		handlerService.executeHandler(cmd);

	}

	protected void performAddAction() {
		ParameterizedCommand cmd = comandService.createCommand(CommandConstants.ADD_ENTRY_COMMAND, null);
		handlerService.executeHandler(cmd);
	}

	private Pushable createTile(Composite parent, int indent) {
		Pushable comp = new Pushable(parent, indent);
		pushables.add(comp);
		return comp;
	}


	
	public void refresh() {

	}

}
