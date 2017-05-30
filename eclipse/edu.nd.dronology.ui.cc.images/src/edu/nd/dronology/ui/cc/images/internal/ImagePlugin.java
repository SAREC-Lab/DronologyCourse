package edu.nd.dronology.ui.cc.images.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.images.managed.ManagedColor;
import edu.nd.dronology.ui.cc.images.managed.ManagedFont;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * The activator class controls the plug-in life cycle
 */
public class ImagePlugin extends Plugin {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ImagePlugin.class);

	// The plug-in ID
	public static final String PLUGIN_ID = "net.mv.citeomat.images"; //$NON-NLS-1$

	// The shared instance
	private static ImagePlugin plugin;

	/**
	 * The constructor
	 */
	public ImagePlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		LOGGER.info("Image Plug-in started");
		super.start(context);
		plugin = this;
		StyleProvider.getSelectedFont();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		ManagedFont.dispose();
		StyleProvider.disposeAll();
		ManagedColor.dispose();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ImagePlugin getDefault() {
		return plugin;
	}

	/**
	 * 
	 * @param fileName
	 * @return The URL of the file
	 */
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

}
