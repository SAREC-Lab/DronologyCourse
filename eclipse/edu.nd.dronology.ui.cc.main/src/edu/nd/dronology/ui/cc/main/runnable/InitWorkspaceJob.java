package edu.nd.dronology.ui.cc.main.runnable;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class InitWorkspaceJob extends Job {

	public InitWorkspaceJob() {
		super("InitDronologyWorkspaceJob");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		IProject project = root.getProject(DronologyWorkspaceConstants.DRONOLOGY_PROJECT);
		try {
			root.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			if (!project.exists()) {
				project.create(monitor);
				project.open(monitor);
				project.setHidden(true);
			}
			project.open(monitor);
			IFolder specFolder = project.getFolder(DronologyWorkspaceConstants.FOLDER_SPECIFICATION);
			if (!specFolder.exists()) {
				specFolder.create(true, true, monitor);
			}
			
			IFolder frFolder = project.getFolder(DronologyWorkspaceConstants.FOLDER_FLIGHTROUTE);
			if (!frFolder.exists()) {
				frFolder.create(true, true, monitor);
			}
			
			IFolder scFolder = project.getFolder(DronologyWorkspaceConstants.FOLDER_SIMSCENARIO);
			if (!scFolder.exists()) {
				scFolder.create(true, true, monitor);
			}
		

			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Status.OK_STATUS;
	}

}
