package edu.nd.dronology.ui.cc.main.simulator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

import com.sun.media.jfxmedia.events.NewFrameEvent;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.util.MenuCreationHelper;
import edu.nd.dronology.ui.cc.main.util.UIRefreshThread;

public class DroneStatusViewer extends Composite {

	private TreeViewer droneViewer;

	public DroneStatusViewer(Composite parent) {
		super(parent, SWT.FLAT);
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().hint(250, SWT.DEFAULT).grab(false, true).applyTo(this);
		createContents();
	}

	private void createContents() {
		Label l = new Label(this, SWT.FLAT);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(l);
		l.setText("Active UAVs");
		l.setFont(StyleProvider.FONT_MONOSPACED_11);
		droneViewer = new TreeViewer(this);
		droneViewer.setAutoExpandLevel(10);
		droneViewer.expandAll();
		droneViewer.setContentProvider(new DroneViewerContentProvider());
		droneViewer.setLabelProvider(new DroneViewerLabelProvider());
		droneViewer.setInput("");
		hookContextMenu(droneViewer);
		GridLayoutFactory.fillDefaults().applyTo(droneViewer.getTree());
		GridDataFactory.fillDefaults().grab(true, true).applyTo(droneViewer.getTree());

		UIRefreshThread thread = new UIRefreshThread(1) {

			@Override
			protected void doRefresh() {
				refreshViewer();

			}
		};
		thread.start();

	}

	private void hookContextMenu(final TreeViewer viewer) {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (viewer.getSelection() instanceof StructuredSelection) {
					Object elem = ((StructuredSelection) viewer.getSelection()).getFirstElement();
					if (elem instanceof DroneStatus) {
						fillContextMenu((DroneStatus) elem, manager);
					}
				}

			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	protected void fillContextMenu(DroneStatus remoteItem, IMenuManager manager) {
		MenuCreationHelper.createMenuEntry(manager, "Activate FlightRoute", ImageProvider.IMG_DRONE_ACTIVATE_24,
				() -> selectFlightRoute((DroneStatus) remoteItem));
		MenuCreationHelper.createMenuEntry(manager, "Hover", ImageProvider.IMG_DRONE_ACTIVATE_24,
				() -> hover((DroneStatus) remoteItem));
		MenuCreationHelper.createMenuEntry(manager, "Return to Home", ImageProvider.IMG_DRONE_ACTIVATE_24,
				() -> returnHome((DroneStatus) remoteItem));

	}

	private void returnHome(DroneStatus remoteItem) {
		// TODO Auto-generated method stub
		try {
			IFlightManagerRemoteService service = (IFlightManagerRemoteService) ServiceProvider.getBaseServiceProvider()
					.getRemoteManager().getService(IFlightManagerRemoteService.class);

			service.returnToHome(remoteItem.getID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void hover(DroneStatus remoteItem) {
		// TODO Auto-generated method stub

	}

	private void selectFlightRoute(DroneStatus droneStatus) {
		FlightRouteSelectionDialog dlg = new FlightRouteSelectionDialog(Display.getDefault().getActiveShell());
		int result = dlg.open();
		if (result == Window.OK) {
			FlightRouteInfo toAssign = dlg.getSelectedElement();
			if (toAssign != null) {
				assignRoute(droneStatus, toAssign);
			}

		}

	}

	private void assignRoute(DroneStatus droneStatus, FlightRouteInfo toAssign) {
		try {
			IFlightManagerRemoteService service = (IFlightManagerRemoteService) ServiceProvider.getBaseServiceProvider()
					.getRemoteManager().getService(IFlightManagerRemoteService.class);

			List<Waypoint> wayPoints = new ArrayList<>();
			for (Waypoint crd : toAssign.getWaypoints()) {
				wayPoints.add((crd));
			}

			service.planFlight(droneStatus.getID(), toAssign.getName(), wayPoints);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void refreshViewer() {
		droneViewer.refresh();
		droneViewer.expandAll();
	}

}
