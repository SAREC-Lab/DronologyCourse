package edu.nd.dronology.ui.cc.main;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.nd.dronology.ui.cc.main.runnable.InitWorkspaceJob;

/**
 * The activator class controls the plug-in life cycle
 */
public class DronologyMainActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.nd.dronology.ui.cc.main"; //$NON-NLS-1$

	// The shared instance
	private static DronologyMainActivator plugin;

	private EventBroker eventBroker;

	private ECommandService commandService;

	private EHandlerService handlerService;

	/**
	 * The constructor
	 */
	public DronologyMainActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		initWorkspace();
	}

	private void initWorkspace() {
		InitWorkspaceJob job = new InitWorkspaceJob();
		job.schedule();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DronologyMainActivator getDefault() {
		return plugin;
	}

	public URL getLocationOfFile(String fileName) {
		try {
			IPath path = new Path(fileName);
			URL url = FileLocator.find(getBundle(), path, null);
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setEventBroker(EventBroker eventBroker) {
		this.eventBroker = eventBroker;

	}

	public void setCommandHandler(EventBroker eventBroker) {
		this.eventBroker = eventBroker;

	}

	public EventBroker getEventBroker() {
		return eventBroker;
	}

	public ECommandService getCommandService() {
		return commandService;

	}

	public EHandlerService getHandlerService() {
		return handlerService;

	}

	public void setCommandService(ECommandService commandService) {
		this.commandService = commandService;

	}

	public void setHandlerService(EHandlerService handlerService) {
		this.handlerService = handlerService;
	}

}
