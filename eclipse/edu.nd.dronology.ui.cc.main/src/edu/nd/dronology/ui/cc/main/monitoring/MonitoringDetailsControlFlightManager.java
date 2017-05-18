package edu.nd.dronology.ui.cc.main.monitoring;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.util.UIRefreshThread;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

@SuppressWarnings("restriction")
public class MonitoringDetailsControlFlightManager extends Composite {

	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringDetailsControlFlightManager.class);

	private Composite container;

	private EPartService partService;
	private EModelService modelService;
	private MApplication app;
	private ECommandService comandService;
	private EHandlerService handlerService;

	private TableViewer tableViewer;

	private UIRefreshThread refreshThread;

	private Composite detailsContainer;

	private TreeViewer viewer;

	public MonitoringDetailsControlFlightManager(Composite parent, EPartService partService, EModelService modelService,
			EHandlerService handlerService, MApplication app) {
		super(parent, SWT.NONE);
		this.partService = partService;
		this.modelService = modelService;
		this.handlerService = handlerService;
		this.app = app;
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
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

		viewer = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);

		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(viewer.getTree());
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getTree());

		viewer.setContentProvider(new FlightManagerContentProvider());
		viewer.setLabelProvider(new FlightManagerLabelProvider());
		viewer.setAutoExpandLevel(5);
		viewer.setInput("");

		refreshThread = new UIRefreshThread(5) {

			@Override
			protected void doRefresh() {
				if (isVisible()) {
					refresh();
				}

			}
		};

		refreshThread.start();

	}

	public void refresh() {

		try {
			IFlightManagerRemoteService service = (IFlightManagerRemoteService) ServiceProvider.getBaseServiceProvider()
					.getRemoteManager().getService(IFlightManagerRemoteService.class);

			FlightInfo flightInfo = service.getFlightDetails();
			viewer.setInput(flightInfo);

		} catch (Exception e) {
			LOGGER.error(e);
			viewer.setInput("");
		}

	}

	public void show(boolean show) {
		this.setVisible(show);
		((GridData) this.getLayoutData()).exclude = !show;

	}

}
