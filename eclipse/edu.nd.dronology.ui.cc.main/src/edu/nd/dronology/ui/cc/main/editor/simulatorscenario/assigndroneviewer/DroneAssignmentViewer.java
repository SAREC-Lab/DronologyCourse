package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.services.core.items.AssignedDrone;
import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractUAVEditorPage;
import edu.nd.dronology.ui.cc.main.util.MenuCreationHelper;
import edu.nd.dronology.ui.cc.util.Pair;

public class DroneAssignmentViewer extends LeftToRightDragAndDropViewer<ISimulatorScenario> {

	private final AbstractUAVEditorPage<ISimulatorScenario> page;

	public DroneAssignmentViewer(Composite parent, AbstractUAVEditorPage<ISimulatorScenario> page) {
		super(parent, false);
		this.page = page;
		createContents();
	}

	@Override
	protected String getRightHeader() {
		return "Available Drones";
	}

	@Override
	protected String getLeftHeader() {
		return "Assigned Drones";
	}

	@Override
	protected void createContents() {
		super.createContents();
		hookContextMenu(dndTable.getAssignedElementsViewer());

	}

	private void hookContextMenu(final Viewer viewer) {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (viewer.getSelection() instanceof StructuredSelection) {
					Object elem = ((StructuredSelection) viewer.getSelection()).getFirstElement();
					if (elem instanceof AssignedDrone) {
						MenuCreationHelper.createMenuEntry(manager, "Set Coordinates", ImageProvider.IMG_DRONE_WAYPOINT,
								() -> setCoordinates(dndTable.getAssignedElementsViewer().getSelection()));
					}
				}

			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	private void setCoordinates(ISelection selection) {
		StructuredSelection ss = (StructuredSelection) selection;
		AssignedDrone elem = (AssignedDrone) ss.getFirstElement();

		SetCoordinatesDialog setCordDialog = new SetCoordinatesDialog(Display.getDefault().getActiveShell());
		setCordDialog.open();

		try {
			elem.setStartCoordinate(Double.parseDouble(setCordDialog.getLatitude()), Double.parseDouble(setCordDialog.getLongitude()),
					Double.parseDouble(setCordDialog.getAltitude()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		refeshChildren();

	}

	@Override
	protected void removeItem(String name) {

		if (!name.contains("|")) {
			return;
		}
		String dName = name.substring(0, name.indexOf("|")).trim();

		page.getItem().removeAssignedDrone(dName);
		page.refresh();

		// if (etype != null) {
		// page.getEditor().getItem().unassignEventType(page.getInput(), etype);
		// page.getEditor().setDirty();
		// page.refresh();
		// }

	}

	@Override
	protected void addItems(String[] list) {
		for (String name : list) {
			if (name.contains("|")) {
				continue;
			}

			page.getItem().addAssignedDrone(name);
		}
		page.setDirty();
		page.refresh();
	}

	@Override
	protected void addItems(List<Object> list) {
		for (Object name : list) {
			page.getItem().addAssignedDrone(name.toString());
		}
		page.setDirty();
		page.refresh();
	}

	@Override
	protected Pair<IContentProvider, IBaseLabelProvider> provideLeftProvider() {
		return new Pair<IContentProvider, IBaseLabelProvider>(new AssignedTypesProvider(),
				new AssignedTypesLabelProvider());
	}

	@Override
	protected Pair<IContentProvider, IBaseLabelProvider> provideRightProvider() {
		return new Pair<IContentProvider, IBaseLabelProvider>(new AllTypesProvider(), new AllTypesLabelProvider());

	}

}
