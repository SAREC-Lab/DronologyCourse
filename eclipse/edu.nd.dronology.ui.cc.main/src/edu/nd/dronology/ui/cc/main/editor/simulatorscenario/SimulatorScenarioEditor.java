package edu.nd.dronology.ui.cc.main.editor.simulatorscenario;

import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.services.core.persistence.AbstractItemPersistenceProvider;
import edu.nd.dronology.services.core.persistence.SimulatorScenarioPersistenceProvider;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractItemEditor;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractUAVEditorPage;
import edu.nd.dronology.ui.cc.main.runnable.RetrieveAndOpenSimScenarioRunnable;
import edu.nd.dronology.ui.cc.main.runnable.TransmitAndCloseSimScenarioRunnable;

public class SimulatorScenarioEditor<T> extends AbstractItemEditor {


	private ISimulatorScenario input;
	private IFile file;
	private UAVSimScenarioPage page;

	public SimulatorScenarioEditor(Composite parent) {
		super(parent);
	}

	@Override
	public void retrieveFile(RemoteInfoObject elem) {
		new RetrieveAndOpenSimScenarioRunnable(elem.getId(), this).runRunnable();

	}

	@Override
	public AbstractItemPersistenceProvider getPersistor() {
		return SimulatorScenarioPersistenceProvider.getInstance();
	}

	@Override
	public AbstractUAVEditorPage createPage() {
		return new UAVSimScenarioPage(this, "", "");
	}

	@Override
	public void doTransmit() {
		new TransmitAndCloseSimScenarioRunnable(this).runRunnable();
		new java.util.Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				DronologyMainActivator.getDefault().getEventBroker().post(EventConstants.REFRESH_SIDEBAR, "");

			}
		}, 2000);

		setVisible(false);
	}

}
