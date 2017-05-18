package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assignpathviewer;

import java.util.List;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractUAVEditorPage;
import edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer.LeftToRightDragAndDropViewer;
import edu.nd.dronology.ui.cc.util.Pair;

public class PathAssignmentViewer extends LeftToRightDragAndDropViewer<ISimulatorScenario> {

	private final AbstractUAVEditorPage<ISimulatorScenario> page;

	public PathAssignmentViewer(Composite parent, AbstractUAVEditorPage<ISimulatorScenario> page) {
		super(parent, false);
		this.page = page;
		createContents();
	}

	@Override
	protected String getRightHeader() {
		return "Available Flight-Paths";
	}

	@Override
	protected String getLeftHeader() {
		return "Assigned  Flight-Paths";
	}

	@Override
	protected void removeItem(String name) {

		page.getItem().removeAssignedPath(name.trim());
		page.refresh();

	}

	@Override
	protected void addItems(String[] list) {
		for (String name : list) {
			page.getItem().addAssignedPath(name.trim());
		}
		page.setDirty();
		page.refresh();
	}

	@Override
	protected void addItems(List<Object> list) {
		for (Object name : list) {
			page.getItem().addAssignedPath(name.toString());
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
