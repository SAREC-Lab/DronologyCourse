/***********************************************************************************************
 * mv.net - 2016
 ***********************************************************************************************/
package edu.nd.dronology.ui.cc.images.managed;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Font wrapper class.<br>
 * Takes care of registering a {@link Font}. When requesting the Font via.
 * {@link #getFont()} the font is retrieved from the registry. To dispose all
 * managed fonts {@link #dispose()} must be called on application shutdown.
 * 
 * @author Michael
 *
 */
public class ManagedFont {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ManagedFont.class);
	private static final Map<String, Font> FONT_REGISTRY = new HashMap<>();

	private String name;

	public ManagedFont(String fontName, int fontsize, int style) {
		this.name = fontName + fontsize + style;

		if (FONT_REGISTRY.get(name) == null) {
			Font f = new Font(Display.getDefault(), fontName, fontsize, style);
			FONT_REGISTRY.put(name, f);
		}
	}

	public Font getFont() {
		return FONT_REGISTRY.get(name);
	}

	public static void dispose() {
		for (Font e : FONT_REGISTRY.values()) {
			e.dispose();
		}
		LOGGER.info(FONT_REGISTRY.size() + " Fonts disposed");
	}

}
