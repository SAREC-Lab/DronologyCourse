package edu.nd.dronology.ui.cc.main.sidebar.specification;

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

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.info.DroneInitializationInfo.DroneMode;
import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.info.TypeSpecificationInfo;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IDroneSpecificationRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.runnable.ExtendedErrorDialog;
import edu.nd.dronology.ui.cc.main.sidebar.base.AbstractSidebarViewer;
import edu.nd.dronology.ui.cc.main.util.MenuCreationHelper;

public class SpecificationShelfViewer extends AbstractSidebarViewer<TypeSpecificationInfo> {

	public SpecificationShelfViewer(Composite parent) {
		super(parent, "Requirements Management");
		shelfItemIcon = ImageProvider.IMG_SPECIFICATION_24;
		gradient2 = StyleProvider.COLOR_LIGHT_TURQUOISE;
		gradient1 = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
		// selectedGradient1 = StyleProvider.RED_ORANGE;
		// selectedGradient2 = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		selectedGradient1 = StyleProvider.COLOR_LIGHT_TURQUOISE;
		selectedGradient2 = StyleProvider.COLOR_DARK_GREEN;
		createContents();
	}


	@Override
	protected void doOpen(ISelection selection) {
		StructuredSelection sel = (StructuredSelection) selection;
		if (!(sel.getFirstElement() instanceof DroneSpecificationInfo)) {
			return;
		}

		DronologyMainActivator.getDefault().getEventBroker().post(EventConstants.SPECIFICATION_OPEN, sel.getFirstElement());
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new SpecificationOverviewLabelProvider();
	}

	@Override
	protected IContentProvider getContentProvider() {
		return new SpecificationOverviewContentProvider();
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

	// @Override
	// protected String getShelfItemName(String name) {
	// return name;
	// }

	protected void createItem() {
		IDroneSpecificationRemoteService service;
		try {
			service = (IDroneSpecificationRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSpecificationRemoteService.class);
			service.createItem();
			refresh();
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected Collection<TypeSpecificationInfo> getShelfItems() {
		IDroneSpecificationRemoteService service;
		try {
			service = (IDroneSpecificationRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSpecificationRemoteService.class);
			return service.getTypeSpecifications();
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;

	}

	@Override
	protected String getItemCount(TypeSpecificationInfo type) {

		IDroneSpecificationRemoteService service;
		try {
			service = (IDroneSpecificationRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSpecificationRemoteService.class);
			Collection<DroneSpecificationInfo> drones = service.getItems();

			List<DroneSpecificationInfo> toShow = new ArrayList<>(drones);
			Collections.sort(toShow, new RemoteItemNameComparator());

			for (DroneSpecificationInfo info : drones) {
				if (!info.getType().equals(type.getName())) {
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
	protected String getShelfItemName(TypeSpecificationInfo scen) {
		// TODO Auto-generated method stub
		return scen.getName();
	}

	@Override
	protected void fillContextMenu(RemoteInfoObject remoteItem, IMenuManager manager) {

		MenuCreationHelper.createMenuEntry(manager, "Activate Drone", ImageProvider.IMG_DRONE_ACTIVATE_24,
				() -> activate((DroneSpecificationInfo) remoteItem));
;
	}

	private void activate(DroneSpecificationInfo remoteItem) {
		IDroneSetupRemoteService setupService;
		try {
			setupService = (IDroneSetupRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSetupRemoteService.class);

			DroneInitializationInfo item = new DroneInitializationInfo(remoteItem.getName(), DroneMode.MODE_VIRTUAL, remoteItem.getName(),
					new LlaCoordinate(41.760000, -86.222901, 0));
//			// "41760000", "-86222901", "0"
//			setupService.initializeDrones(item);
		} catch (RemoteException | DronologyServiceException e) {
			new ExtendedErrorDialog(Display.getDefault().getActiveShell(), "Error when adding Drone",
					"Error when adding Drone", e.getMessage()).open();
		}

	}

}
