/***********************************************************************************************
 * mv.net - 2016
 ***********************************************************************************************/
package edu.nd.dronology.ui.cc.images.managed;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Color wrapper class.<br>
 * Takes care of registering a {@link Color}. When requesting the Color via.
 * {@link #getColor()} the color is retrieved from the registry. To dispose all
 * managed colors {@link #dispose()} must be called on application shutdown.
 * 
 * @author Michael
 *
 */
public class ManagedColor {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ManagedColor.class);
	private static final Map<RGB, Color> COLOR_REGISTRY = new HashMap<>();

	private RGB rgb;

	public ManagedColor(int red, int green, int blue) {
		this(new RGB(red, green, blue));
	}

	public ManagedColor(RGB rgb) {
		this.rgb = rgb;

		if (COLOR_REGISTRY.get(rgb) == null) {
			Color c = new Color(Display.getDefault(), rgb);
			COLOR_REGISTRY.put(rgb, c);
		} else {
			// LOGGER.info("COLOR EXISTS! '"+rgb.toString()+"'");
		}
	}

	public Color getColor() {
		return COLOR_REGISTRY.get(rgb);
	}

	public static void dispose() {
		for (Color e : COLOR_REGISTRY.values()) {
			e.dispose();
		}
		LOGGER.info(COLOR_REGISTRY.size() + " Colors disposed");
	}

}
