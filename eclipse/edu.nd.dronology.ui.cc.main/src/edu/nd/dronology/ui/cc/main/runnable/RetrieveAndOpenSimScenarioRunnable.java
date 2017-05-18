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

import edu.nd.dronology.services.core.remote.IDroneSimulatorRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractItemEditor;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class RetrieveAndOpenSimScenarioRunnable extends AbstractUIRunnable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(RetrieveAndOpenSimScenarioRunnable.class);

	private String elemid;

	private IProgressMonitor monitor;

	private AbstractItemEditor editor;


	public RetrieveAndOpenSimScenarioRunnable(String elemid, AbstractItemEditor  editor) {
		super(7);
		this.elemid = elemid;
		this.editor = editor;
	}

	@Override
	protected void doWork() {
		try {
			updateTask("Retrieving Simulator Scenario  from Server...");
			final IFile fileToOpen = retrieveRequirement();
			updateTask("Fetching List...");
			updateTask("Simulator Scenario Editor...");
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
			IFolder folder = project.getFolder(DronologyWorkspaceConstants.FOLDER_SIMSCENARIO);
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			IFile constraintFile = folder.getFile(elemid + ".scen");
			if (constraintFile.exists()) {
				constraintFile.delete(true, monitor);
			}

			IDroneSimulatorRemoteService service = (IDroneSimulatorRemoteService) ServiceProvider.getBaseServiceProvider()
					.getRemoteManager().getService(IDroneSimulatorRemoteService.class);
			byte[] constraint = service.requestFromServer(elemid);

			in = new ByteArrayInputStream(constraint);
			constraintFile.create(in, true, monitor);
			LOGGER.info("Simulator Scenario File received and saved @" + constraintFile.getRawLocationURI().toString());
			return constraintFile;
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
