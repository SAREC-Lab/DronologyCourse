package edu.nd.dronology.ui.cc.main.editor.simulatorscenario;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractMandatoryItemComposite;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractUAVEditorPage;
import edu.nd.dronology.ui.cc.main.editor.base.IUAVEditorPage;
import edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer.DroneAssignmentViewer;
import edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assignpathviewer.PathAssignmentViewer;
import edu.nd.dronology.ui.cc.util.controls.ControlCreationHelper;
import edu.nd.dronology.ui.cc.util.managedcontrol.ManagedText;

public class UAVSimScenarioPage extends AbstractUAVEditorPage<ISimulatorScenario> implements IUAVEditorPage<ISimulatorScenario> {

	private ManagedText txtDesc;
	private DroneAssignmentViewer droneViewer;
	private PathAssignmentViewer pathViewer;

	public UAVSimScenarioPage(SimulatorScenarioEditor editor, String id, String title) {
		super(editor, id, title);

	}

	@Override
	protected void doCreateLabel(Composite parent) {
		headerText.setText("Simulator Scenario Details");
		headerIcon.setImage(ImageProvider.IMG_SIMSCENARIO_24);
	}

	@Override
	protected void doSetValues() {
		txtDesc.setValue(editor.getItem().getDescription());
		droneViewer.setInput(editor.getItem());
		pathViewer.setInput(editor.getItem());
	}

	@Override
	protected AbstractMandatoryItemComposite doCreateMandatoryComposite(Composite parent) {
		return new MandatorySimScenarioDataComposite(parent, this);
	}

	@Override
	protected void doCreateOptionalParts(Composite container) {
		
		txtDesc = ControlCreationHelper.createMultiLineTextWitLabel(container, "Description", 2, SWT.BORDER, null);

		txtDesc.addApplyListener((value) -> {
			getItem().setDescription(value);
		});

	}

	@Override
	protected void doCreateAdditionalParts(Composite container) {
		//createRequirementsText(container);
		
	droneViewer = new DroneAssignmentViewer(container, this);
	pathViewer = new PathAssignmentViewer(container, this);
	}



	@Override
	public void layout() {
		container.layout();

	}

	@Override
	public void refresh() {
	//	viewer.refesh();
		
	}

}
