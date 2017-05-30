/***********************************************************************************************
 * mv.net - 2016
 ***********************************************************************************************/
package edu.nd.dronology.ui.cc.images.managed;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import edu.nd.dronology.ui.cc.images.internal.ImagePlugin;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Image wrapper<br>
 * Takes care of registering an {@link ImageDescriptor} to the
 * {@link ImageRegistry} When requesting the Image via. {@link #getImage()} the
 * image is retrieved from the registry.
 * 
 * @author Michael
 *
 */
public class ManagedImage {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ManagedImage.class);

	/**
	 * Experimental... breaks several other things...
	 */
	private static final boolean USE_IMAGE_SCALING = false;

	private String path;
	private boolean colored;
	private static final ImageRegistry registry = new ImageRegistry();

	public ManagedImage(String path) {
		this(path, true);
	}

	@SuppressWarnings("unused")
	public ManagedImage(String path, boolean colored) {
		this.path = path;
		this.colored = colored;

		if (registry.get(path) == null) {
			ImageDescriptor desc = ImageDescriptor.createFromURL(ImagePlugin.getDefault().getLocationOfFile(path));
			// not working as intended - turned off - requires thorough testing
			if (USE_IMAGE_SCALING && desc.getImageData().width == 24 && colored
					&& Display.getDefault().getDPI().x <= 100) {
				Image oldImage = desc.createImage();
				Image scaled = new Image(Display.getDefault(), 16, 16);
				GC gc = new GC(scaled);
				gc.setAntialias(SWT.ON);
				gc.setInterpolation(SWT.HIGH);
				gc.drawImage(oldImage, 0, 0, 24, 24, 0, 0, 16, 16);

				ImageData id = scaled.getImageData();

				final Image newImage = new Image(Display.getDefault(), id, id);

				oldImage.dispose();
				gc.dispose();
				scaled.dispose();
				registry.put(path + "_" + Boolean.toString(colored), newImage);
				return;
			}

			if (!colored) {
				desc = ImageDescriptor.createWithFlags(desc, SWT.IMAGE_GRAY);
			}
			registry.put(path + "_" + Boolean.toString(colored), desc);

		} else {
			LOGGER.error("An object with path '" + path + "' has already been registered");
		}

	}

	public Image getImage() {
		return registry.get(path + "_" + Boolean.toString(colored));
	}

	public static Image resize(Image oldImage) {
		Image scaled = new Image(Display.getDefault(), 16, 16);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(oldImage, 0, 0, 24, 24, 0, 0, 16, 16);

		ImageData id = scaled.getImageData();

		final Image newImage = new Image(Display.getDefault(), id, id);

		oldImage.dispose();
		gc.dispose();
		scaled.dispose();
		registry.put(oldImage.getImageData().toString(), newImage);
		return newImage;
	}

}
