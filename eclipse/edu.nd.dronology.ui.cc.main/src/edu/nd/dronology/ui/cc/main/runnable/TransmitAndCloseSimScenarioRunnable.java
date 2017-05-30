package edu.nd.dronology.ui.cc.main.runnable;

import java.io.IOException;
import java.rmi.RemoteException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import edu.nd.dronology.services.core.remote.IDroneSimulatorRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractItemEditor;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class TransmitAndCloseSimScenarioRunnable extends AbstractUIRunnable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(TransmitAndCloseSimScenarioRunnable.class);

	// private IEditorPart editor;
	AbstractItemEditor editor;

	private String id;

	private byte[] cPackages;

	public TransmitAndCloseSimScenarioRunnable(AbstractItemEditor editor) {
		super(5);
		this.editor = editor;
	}

	@Override
	protected void doWork() throws InterruptedException {
		try {
			// editor = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().getActiveEditor();
			// if (!(editor instanceof RequirementsEditor)) {
			// throw new RuntimeException("Error when retrieving editor - is of type: " + editor.getClass());
			// }
			// reqEditor = (RequirementsEditor) editor;

			updateTask("Transmitting Simulator Scenario to Server...");
			transmitRequirement();
			updateTask("Deleting Temporary Files...");
			cleanup();
			updateTask("Refreshing Scenario List...");
			refresh();
		} catch (DronologyServiceException | IOException | CoreException e) {
			ConnectionErrorDialog.showError(e);
			LOGGER.error(e);
		}
	}

	private void refresh() {
		// reqEditor.close(false);
		// ViewUtil.refreshView(RequirementsOverviewView.ID);
	}

	private void cleanup() {
		IFile scenarioFile = editor.getInputFile();
		try {
			scenarioFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			scenarioFile.delete(true, monitor);
			scenarioFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			LOGGER.error(e);
		}

	}

	protected void transmitRequirement() throws DronologyServiceException, IOException, CoreException {
		Display.getDefault().syncExec(() -> {

			cPackages = editor.getItemAsByteArray();
			id = editor.getItem().getId();

		});
		send(id, cPackages);
	}

	private void send(String id, byte[] cPackage) throws RemoteException, DronologyServiceException {
		LOGGER.info("Transmitting Simulator Scenarioe: '" + id + "'");
		IDroneSimulatorRemoteService service = (IDroneSimulatorRemoteService) ServiceProvider.getBaseServiceProvider()
				.getRemoteManager().getService(IDroneSimulatorRemoteService.class);
		service.transmitToServer(id, cPackage);
	}
}
