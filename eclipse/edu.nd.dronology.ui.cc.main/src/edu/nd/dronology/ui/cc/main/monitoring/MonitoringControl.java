package edu.nd.dronology.ui.cc.main.monitoring;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.api.ServiceStatus;
import edu.nd.dronology.services.core.listener.IDroneStatusChangeListener;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IRemoteServiceListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.util.UIRefreshThread;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

@SuppressWarnings("restriction")
public class MonitoringControl extends Composite {

	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringControl.class);

	private Composite container;

	private EPartService partService;
	private EModelService modelService;
	private MApplication app;
	private ECommandService comandService;
	private EHandlerService handlerService;

	private TableViewer tableViewer;

	private UIRefreshThread refreshThread;

	private UIRefreshListener processListener;

	private Composite detailsContainer;

	private MonitoringDetailsControlDroneSetup droneSetupDetails;

	private MonitoringDetailsControlFlightManager flightManagerDetails;

	public MonitoringControl(Composite parent, EPartService partService, EModelService modelService,
			ECommandService commandService, EHandlerService handlerService, MApplication app) {
		super(parent, SWT.NONE);
		this.partService = partService;
		this.modelService = modelService;
		this.comandService = commandService;
		this.handlerService = handlerService;
		this.app = app;
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this);
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

		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		Table negTable = tableViewer.getTable();

		negTable.setHeaderVisible(false);
		negTable.setLinesVisible(false);
		// addMultiLineSupport(negTable, 3);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, SWT.DEFAULT).applyTo(tableViewer.getTable());
		// gdText.horizontalSpan = 2;
		// gdText.heightHint = containerHeight;
		// tableViewer.getTable().setLayoutData(gdText);

		tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = 23;
			}
		});

		TableViewerColumn tableViewerColumn_0 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnStatus = tableViewerColumn_0.getColumn();
		tblclmnStatus.setAlignment(SWT.CENTER);
		tblclmnStatus.setWidth(40);
		tableViewerColumn_0.setLabelProvider(new CenterImageLabelProver(0));

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnId = tableViewerColumn_1.getColumn();
		tblclmnId.setText("ID");
		tblclmnId.setWidth(140);
		tableViewerColumn_1.setLabelProvider(new ServicesViewerLabelProvider());

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnDesc = tableViewerColumn_2.getColumn();
		tblclmnDesc.setText("ID");
		tblclmnDesc.setWidth(240);
		tableViewerColumn_2.setLabelProvider(new ServicesViewerLabelProvider());

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnProperties = tableViewerColumn_3.getColumn();
		tblclmnProperties.setText("Properties");
		tblclmnProperties.setWidth(140);
		tableViewerColumn_3.setLabelProvider(new ServicesViewerLabelProvider());

		TableViewerColumn tableViewerColumn_31 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnType = tableViewerColumn_31.getColumn();
		tblclmnType.setText("Type");
		tblclmnType.setWidth(20);
		tableViewerColumn_31.setLabelProvider(new CenterImageLabelProver(31));

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnDetails = tableViewerColumn_4.getColumn();
		tblclmnDetails.setText("Properties");
		tblclmnDetails.setWidth(40);
		tableViewerColumn_4.setLabelProvider(new CenterImageLabelProver(6));

		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnRestart = tableViewerColumn_5.getColumn();
		tblclmnRestart.setAlignment(SWT.CENTER);
		tblclmnRestart.setWidth(40);
		tableViewerColumn_5.setLabelProvider(new CenterImageLabelProver(7));

		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnInfo = tableViewerColumn_6.getColumn();
		tblclmnInfo.setAlignment(SWT.CENTER);
		tblclmnInfo.setWidth(40);
		tableViewerColumn_6.setLabelProvider(new CenterImageLabelProver(8));

		tableViewer.setContentProvider(new CoreServiceViewerContentProvider());
		tableViewer.setInput("xxx");
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!tableViewer.getSelection().isEmpty()) {
					showDetails(tableViewer.getSelection());
					tableViewer.setSelection(null);
				}
			}
		});
		try {
			processListener = new UIRefreshListener();
			// RemoteConnector.addProcessListener(processListener);
		} catch (RemoteException e) {
			LOGGER.error(e);
		}

		addListener();

		refreshThread = new UIRefreshThread(5) {

			@Override
			protected void doRefresh() {
				tableViewer.refresh();
			}
		};
		refreshThread.start();

		tableViewer.getTable().pack();

		droneSetupDetails = new MonitoringDetailsControlDroneSetup(parent, partService, modelService, handlerService, app);
		droneSetupDetails.show(false);

		flightManagerDetails = new MonitoringDetailsControlFlightManager(parent, partService, modelService, handlerService,
				app);
		flightManagerDetails.show(false);

		try {
			IDroneSetupRemoteService service = (IDroneSetupRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSetupRemoteService.class);
			IDroneStatusChangeListener list = new DroneStatusChangeListener();
			service.addDroneStatusChangeListener(list );
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void showDetails(ISelection selection) {
		Object selected = ((StructuredSelection) selection).getFirstElement();
		if (selected instanceof ServiceInfo) {
			ServiceInfo inf = (ServiceInfo) selected;
			System.out.println(inf.getServiceID());

			droneSetupDetails.show(false);
			flightManagerDetails.show(false);

			if (ServiceIds.SERVICE_FLIGHTMANAGER.equals(inf.getServiceID())) {
				showFlightManagerDetails();
			} else if (ServiceIds.SERVICE_DRONESETUP.equals(inf.getServiceID())) {
				showDroneSetupDetails();
			}
		}
		this.layout();
	}

	private void showDroneSetupDetails() {
		droneSetupDetails.show(true);

	}

	private void showFlightManagerDetails() {
		flightManagerDetails.show(true);
	}

	private void addMultiLineSupport(Table table, final int index) {
		/*
		 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly. Therefore, it is critical for performance that these methods be as efficient as possible.
		 */
		Listener paintListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				int ind = event.index;
				if (ind == index) {
					switch (event.type) {
						case SWT.MeasureItem: {
							TableItem item = (TableItem) event.item;
							String text = getText(item, event.index);
							Point size = event.gc.textExtent(text);
							event.width = size.x;
							event.height = Math.max(event.height, size.y);
							break;
						}
						case SWT.PaintItem: {
							TableItem item = (TableItem) event.item;
							String text = getText(item, event.index);
							Point size = event.gc.textExtent(text);
							int offset2 = event.index == 0 ? Math.max(0, (event.height - size.y) / 2) : 0;
							event.gc.drawText(text, event.x, event.y + offset2, true);
							break;
						}
						case SWT.EraseItem: {
							event.detail &= ~SWT.FOREGROUND;
							break;
						}
					}
				}
			}

			String getText(TableItem item, int column) {
				return item.getText(column);
			}
		};

		table.addListener(SWT.MeasureItem, paintListener);
		table.addListener(SWT.PaintItem, paintListener);
		table.addListener(SWT.EraseItem, paintListener);
	}

	private void addListener() {
		final Table table = tableViewer.getTable();
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					TableItem item = table.getItem(index);
					for (int i = 0; i < 10; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							callAction(index, i);
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});

	}

	private class UIRefreshListener extends UnicastRemoteObject implements IRemoteServiceListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7448069238335412102L;

		protected UIRefreshListener() throws RemoteException {
			super();
		}

		@Override
		public void statusChanged(ServiceStatus newState) throws RemoteException {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					refresh();
				}
			});

		}
	}

	protected void callAction(int row, int col) {
		Table table = tableViewer.getTable();
		Object data = table.getItem(row).getData();
		if (!(data instanceof ServiceInfo)) {
			LOGGER.error(ServiceInfo.class.getName() + " expected - but was " + data.getClass().getName());
			return;
		}
		ServiceInfo infoItem = (ServiceInfo) data;

		if (col == 5 && infoItem.getStatus() == ServiceStatus.RUNNING) {
			// showDetailsView(infoItem);
		}
		if (col == 6) {
			// serverControl.restartService(infoItem);
		}
		if (col == 7 && infoItem.getStatus() == ServiceStatus.RUNNING) {
			// serverControl.showInfo(infoItem);
		}
	}

	public void refresh() {

	}

}
