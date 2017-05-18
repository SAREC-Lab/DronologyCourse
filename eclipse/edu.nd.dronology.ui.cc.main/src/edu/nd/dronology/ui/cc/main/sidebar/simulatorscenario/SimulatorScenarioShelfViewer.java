package edu.nd.dronology.ui.cc.main.sidebar.simulatorscenario;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
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

import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.info.SimulatorScenarioCategoryInfo;
import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.services.core.remote.IDroneSimulatorRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.application.constants.CommandConstants;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.application.constants.PerspectiveConstants;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import edu.nd.dronology.ui.cc.main.sidebar.base.AbstractSidebarViewer;
import edu.nd.dronology.ui.cc.main.sidebar.specification.RemoteItemNameComparator;
import edu.nd.dronology.ui.cc.main.util.MenuCreationHelper;

public class SimulatorScenarioShelfViewer extends AbstractSidebarViewer<SimulatorScenarioCategoryInfo> {

	public SimulatorScenarioShelfViewer(Composite parent) {
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
		if (!(sel.getFirstElement() instanceof SimulatorScenarioInfo)) {
			return;
		}
		DronologyMainActivator.getDefault().getEventBroker().post(EventConstants.SIMULATORSCENARIO_OPEN,
				sel.getFirstElement());

	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new SimScenarioOverviewLabelProvider();
	}

	@Override
	protected IContentProvider getContentProvider() {
		return new SimScenarioShelfViewerContentProvider();
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
		IDroneSimulatorRemoteService service;
		try {
			service = (IDroneSimulatorRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSimulatorRemoteService.class);
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
	protected Collection<SimulatorScenarioCategoryInfo> getShelfItems() {
		IDroneSimulatorRemoteService service;
		try {
			service = (IDroneSimulatorRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSimulatorRemoteService.class);
			return service.getCategories();
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;

	}

	@Override
	protected String getItemCount(SimulatorScenarioCategoryInfo scen) {
		IDroneSimulatorRemoteService service;
		try {
			service = (IDroneSimulatorRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSimulatorRemoteService.class);
			Collection<SimulatorScenarioInfo> drones = service.getItems();

			List<SimulatorScenarioInfo> toShow = new ArrayList<>(drones);
			Collections.sort(toShow, new RemoteItemNameComparator());

			for (SimulatorScenarioInfo info : drones) {
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
	protected String getShelfItemName(SimulatorScenarioCategoryInfo scen) {
		// TODO Auto-generated method stub
		return scen.getName();
	}

	@Override
	protected void fillContextMenu(RemoteInfoObject remoteItem, IMenuManager manager) {
		MenuCreationHelper.createMenuEntry(manager, "Activate Scenario", ImageProvider.IMG_DRONE_ACTIVATE_24,
				() -> activate((SimulatorScenarioInfo) remoteItem));

	}

	private void activate(SimulatorScenarioInfo remoteItem) {
		IDroneSimulatorRemoteService service;
		try {
			service = (IDroneSimulatorRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSimulatorRemoteService.class);

			service.activateScenario(remoteItem);
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", PerspectiveConstants.SIMULATOR_PERSPECTIVE);
		ParameterizedCommand command = DronologyMainActivator.getDefault().getCommandService()
				.createCommand(CommandConstants.SWTICH_PERSPECTIVE_COMMAND, parameters);
		DronologyMainActivator.getDefault().getHandlerService().executeHandler(command);

	}

}
