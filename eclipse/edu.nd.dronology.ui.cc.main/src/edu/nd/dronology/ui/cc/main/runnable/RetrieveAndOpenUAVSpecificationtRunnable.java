package edu.nd.dronology.ui.cc.main.runnable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import edu.nd.dronology.services.core.persistence.DroneSpecificationPersistenceProvider;
import edu.nd.dronology.services.core.remote.IDroneSpecificationRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractItemEditor;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class RetrieveAndOpenUAVSpecificationtRunnable extends AbstractUIRunnable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(RetrieveAndOpenUAVSpecificationtRunnable.class);

	private String elemid;

	private IProgressMonitor monitor;

	private AbstractItemEditor editor;
	private static final DroneSpecificationPersistenceProvider PERSISTOR = DroneSpecificationPersistenceProvider.getInstance();

	public RetrieveAndOpenUAVSpecificationtRunnable(String elemid, AbstractItemEditor editor) {
		super(7);
		this.elemid = elemid;
		this.editor = editor;
	}

	@Override
	protected void doWork() {
		try {
			updateTask("Retrieving UAV Specification from Server...");
			final IFile fileToOpen = retrieveRequirement();
			updateTask("Fetching List...");
			updateTask("Opening Specification Editor...");
			editor.setInputFile(fileToOpen);
			// Display.getDefault().asyncExec(new Runnable() {
			// @Override
			// public void run() {
			//
			// openEditor(fileToOpen);
			// }
			// });
		} catch (DronologyServiceException | InterruptedException | IOException | CoreException e) {
			ConnectionErrorDialog.showError(e);
			LOGGER.error(e);
		}
	}

	protected IFile retrieveRequirement() throws DronologyServiceException, IOException, CoreException {
		ByteArrayInputStream in = null;
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(DronologyWorkspaceConstants.DRONOLOGY_PROJECT);
			IFolder folder = project.getFolder(DronologyWorkspaceConstants.FOLDER_SPECIFICATION);
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			IFile specFile = folder.getFile(elemid + "." + DronologyWorkspaceConstants.EXTENSION_SPECIFICATION);
			if (specFile.exists()) {
				specFile.delete(true, monitor);
			}

			IDroneSpecificationRemoteService service = (IDroneSpecificationRemoteService) ServiceProvider.getBaseServiceProvider()
					.getRemoteManager().getService(IDroneSpecificationRemoteService.class);
			byte[] constraint = service.requestFromServer(elemid);

			in = new ByteArrayInputStream(constraint);
			specFile.create(in, true, monitor);
			LOGGER.info("Specification File received and saved @" + specFile.getRawLocationURI().toString());
			return specFile;
		} finally {
			if (in != null) {
				in.close();
			}

		}
	}

	private void closeOldEditors() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
		page.closeAllEditors(true);

	}

}
