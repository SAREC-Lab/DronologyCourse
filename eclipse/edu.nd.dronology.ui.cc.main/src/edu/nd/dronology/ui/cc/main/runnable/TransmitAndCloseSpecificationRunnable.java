package edu.nd.dronology.ui.cc.main.runnable;

import java.io.IOException;
import java.rmi.RemoteException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import edu.nd.dronology.services.core.remote.IDroneSpecificationRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractItemEditor;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class TransmitAndCloseSpecificationRunnable extends AbstractUIRunnable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(TransmitAndCloseSpecificationRunnable.class);

	// private IEditorPart editor;
	AbstractItemEditor editor;

	private String id;

	private byte[] cPackages;

	public TransmitAndCloseSpecificationRunnable(AbstractItemEditor editor) {
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

			updateTask("Transmitting Specification to Server...");
			transmitRequirement();
			updateTask("Deleting Temporary Files...");
			cleanup();
			updateTask("Refreshing Requirements List...");
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
		IFile specFile = editor.getInputFile();
		try {
			specFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			specFile.delete(true, monitor);
			specFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
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
		LOGGER.info("Transmitting specification package: '" + id + "'");
		IDroneSpecificationRemoteService service = (IDroneSpecificationRemoteService) ServiceProvider.getBaseServiceProvider()
				.getRemoteManager().getService(IDroneSpecificationRemoteService.class);
		service.transmitToServer(id, cPackage);
	}
}
