package edu.nd.dronology.ui.cc.main.unused;
//package edu.nd.dronology.ui.cc.main.flightplan;
//
//import org.eclipse.e4.core.commands.ECommandService;
//import org.eclipse.e4.core.commands.EHandlerService;
//import org.eclipse.e4.ui.model.application.MApplication;
//import org.eclipse.e4.ui.workbench.modeling.EModelService;
//import org.eclipse.e4.ui.workbench.modeling.EPartService;
//import org.eclipse.jface.layout.GridDataFactory;
//import org.eclipse.jface.layout.GridLayoutFactory;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//
//import edu.nd.dronology.ui.cc.images.StyleProvider;
//import net.mv.logging.ILogger;
//import net.mv.logging.LoggerProvider;
//
//@SuppressWarnings("restriction")
//public class FlightPlanControl extends Composite {
//
//	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightPlanControl.class);
//
//	private static final int SIZE_X = 1290;
//	private static final int SIZE_Y = 970;
//
//	private static final Color BLUE_COLOR = StyleProvider.COLOR_LIVE;
//	private static final Color ORANGE_COLOR = StyleProvider.LAUNCHER_BLUE;
//	private static final Color GREEN_COLOR = StyleProvider.LAUNCHER_DARK;
//	private static final Color GRAY_COLOR = StyleProvider.COLOR_MISC;
//
//	private Composite container;
//
//	private EPartService partService;
//	private EModelService modelService;
//	private MApplication app;
//	private ECommandService comandService;
//	private EHandlerService handlerService;
//
//	private BrowserViewer browser;
//
//	private FlightPathDetails fligthPathDetails;
//
//	public FlightPlanControl(Composite parent, EPartService partService, EModelService modelService,
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
//		container.setLayoutData(new GridData(GridData.FILL_BOTH));
//		container.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//
//		Composite bodyComp = new Composite(container, SWT.NONE);
//		bodyComp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//		GridLayout bodyLayout = new GridLayout(4, false);
//		layout.marginWidth = layout.marginHeight = 0;
//		bodyComp.setLayout(bodyLayout);
//		bodyComp.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//		Composite pushableContainer = new Composite(bodyComp, SWT.NONE);
//
//		// pushableContainer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
//		GridLayout layout2 = new GridLayout(3, false);
//		layout2.marginWidth = layout.marginHeight = layout.marginTop = 0;
//		layout2.verticalSpacing = 0;
//		layout2.horizontalSpacing = 3;
//		pushableContainer.setLayout(layout2);
//		pushableContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
//		pushableContainer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//		
//		
//		
//
////		
////		Button push = new Button(pushableContainer, SWT.PUSH);
////		push.setText("Push");
////		
////		push.addSelectionListener(new SelectionAdapter() {
////			
////			@Override
////			public void widgetSelected(SelectionEvent e) {
////				testService();
////			}
////
////		});
////		String imageUrl = "https://www.google.com/maps/embed/v1/place?key=AIzaSyAEx1MgNuJoRNbd5MSM6CfbBXKb2KsNW3Y&q=Notre+Dame,Notre+Dame+IN";
////
////		browser = new BrowserViewer(pushableContainer);
////		browser.setInput(imageUrl);
//	
////		
//		fligthPathDetails = new FlightPathDetails(pushableContainer);
//
//	}
//
////	protected void testService() {
////		try {
////			IFlightRouteplanningRemoteService service= (IFlightRouteplanningRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager().getService(IFlightPathRemoteService.class);
////			service.createItem();
////			Collection<FlightPathInfo> allPlans = service.getItems();
////			for(FlightPathInfo p: allPlans){
////				LOGGER.info(p.getName());
////			}
////		} catch (RemoteException | DronologyServiceException e) {
////			LOGGER.error(e);
////		} 
////		
////	}
//
//	public void refresh() {
//
//	}
//
//}
