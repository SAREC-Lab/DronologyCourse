package edu.nd.dronology.ui.cc.main.sidebar.flightrouteplanning;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.services.core.info.FlightRouteCategoryInfo;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.sidebar.base.AbstractSidebarViewer;
import edu.nd.dronology.ui.cc.main.sidebar.specification.RemoteItemNameComparator;
import edu.nd.dronology.ui.cc.main.util.MenuCreationHelper;

public class FlightRoutePlanningnShelfViewer extends AbstractSidebarViewer<FlightRouteCategoryInfo> {

	public FlightRoutePlanningnShelfViewer(Composite parent) {
		super(parent, "Requirements Management");
		shelfItemIcon = ImageProvider.IMG_FLIGHTROUTE_24;
		gradient1 = Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN);
		gradient2 = null;
		selectedGradient1 = StyleProvider.COLOR_ORANGE;
		selectedGradient2 = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		createContents();
	}



	@Override
	protected void doOpen(ISelection selection) {
		StructuredSelection sel = (StructuredSelection) selection;
		if (!(sel.getFirstElement() instanceof FlightRouteInfo)) {
			return;
		}

	
		DronologyMainActivator.getDefault().getEventBroker().post(EventConstants.FLIGHTROUTE_OPEN, sel.getFirstElement());

	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new RequirementsOverviewLabelProvider();
	}

	@Override
	protected IContentProvider getContentProvider() {
		return new ShelfViewerContentProvider();
	}

	@Override
	protected void createToolbar(Composite toolbar) {
		Button btnCreate = new Button(toolbar, SWT.PUSH);
		btnCreate.setImage(ImageProvider.IMG_ADD_24);
		Button btnRefresh = new Button(toolbar, SWT.PUSH);
		btnRefresh.setImage(ImageProvider.IMG_REFRESH_24);

		btnCreate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				createItem();

			}

		});

		btnRefresh.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshFully(true);

			}

		});

	}

	protected void createItem() {
		IFlightRouteplanningRemoteService service;
		try {
			service = (IFlightRouteplanningRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			service.createItem();
			refresh();
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// @Override
	// protected String getShelfItemName(String name) {
	// return name;
	// }

	@Override
	protected Collection<FlightRouteCategoryInfo> getShelfItems() {
		IFlightRouteplanningRemoteService service;
		try {
			service = (IFlightRouteplanningRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			return service.getFlightPathCategories();
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;

	}

	@Override
	protected String getItemCount(FlightRouteCategoryInfo scen) {
		IFlightRouteplanningRemoteService service;
		try {
			service = (IFlightRouteplanningRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IFlightRouteplanningRemoteService.class);
			Collection<FlightRouteInfo> drones = service.getItems();

			List<FlightRouteInfo> toShow = new ArrayList<>(drones);
			Collections.sort(toShow, new RemoteItemNameComparator());

			for (FlightRouteInfo info : drones) {
				if (!info.getCategory().equals(scen.getName())) {
					toShow.remove(info);
				}
			}

			return Integer.toString(toShow.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	@Override
	protected String getShelfItemName(FlightRouteCategoryInfo scen) {
		// TODO Auto-generated method stub
		return scen.getName();
	}

	@Override
	protected void fillContextMenu(RemoteInfoObject remoteItem, IMenuManager manager) {
		MenuCreationHelper.createMenuEntry(manager, "Activate FlightRoute", ImageProvider.IMG_DRONE_ACTIVATE_24,
				() -> activate((FlightRouteInfo) remoteItem));

	}

	private void activate(FlightRouteInfo remoteItem) {
		IFlightManagerRemoteService service;
		try {
			service = (IFlightManagerRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IFlightManagerRemoteService.class);

			List<Coordinate> coordds = new ArrayList<>(remoteItem.getCoordinates());
			Coordinate initPoint = coordds.remove(0);
			service.planFlight(remoteItem.getName(),initPoint, coordds);
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
