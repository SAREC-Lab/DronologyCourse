package edu.nd.dronology.ui.cc.images;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.e4.ui.css.swt.engine.CSSSWTEngineImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import edu.nd.dronology.ui.cc.images.managed.ManagedColor;
import edu.nd.dronology.ui.cc.images.managed.ManagedFont;

public class StyleProvider {

	private static List<Image> imageList = new ArrayList<>();
	private static Font SELECTED_FONT = null;
	private static Font SELECTED_FONT_BOLD = null;
	private static List<IFontChangeListener> fontChangeListener = new ArrayList<>();

	public static final Font FONT_BUTTON = new ManagedFont("Tahoma", 13, SWT.NORMAL).getFont();
	public static final Font FONT_BUTTON_SELECTED = new ManagedFont("Tahoma", 13, SWT.BOLD).getFont();
	public static final Font FONT_SMALL = new ManagedFont("Tahoma", 9, SWT.BOLD).getFont();
	public static final Font FONT_LUCID = new ManagedFont("Lucida Sans", 18, SWT.BOLD).getFont();
	public static final Font STYLED_TEXT_FONT = new ManagedFont("Tahoma", 14, SWT.NORMAL).getFont();
	public static final Font FONT_MONOSPACED = new ManagedFont("Courier New", 9, SWT.NORMAL).getFont();
	public static final Font FONT_MONOSPACED_BOLD = new ManagedFont("Courier New", 9, SWT.BOLD).getFont();
	public static final Font FONT_MONOSPACED_10 = new ManagedFont("Courier New", 10, SWT.NORMAL).getFont();
	public static final Font FONT_MONOSPACED_11 = new ManagedFont("Courier New", 11, SWT.NORMAL).getFont();
	public static final Font FONT_MONOSPACED_16 = new ManagedFont("Courier New", 16, SWT.NORMAL).getFont();

	public static final Font FONT_BIG = new ManagedFont("Courier New", 40, SWT.BOLD).getFont();

	public static final Font FONT_EDITOR = new ManagedFont("Courier New", 12, SWT.NORMAL).getFont();

	public static final Font FONT_WELCOME = new ManagedFont("Tahoma", 11, SWT.NORMAL).getFont();

	public static final Font FONT_TITLE_BAR = new ManagedFont("Tahoma", 14, SWT.BOLD).getFont();

	public static final Font FONT_SEGOE_14 = new ManagedFont("Segoe", 14, SWT.BOLD).getFont();
	public static final Font FONT_SEGOE_9 = new ManagedFont("Segoe UI", 9, SWT.NORMAL).getFont();
	public static final Font FONT_SEGOE_10 = new ManagedFont("Segoe", 10, SWT.NORMAL).getFont();
	public static final Font FONT_SEGOE_8 = new ManagedFont("Segoe", 8, SWT.NORMAL).getFont();
	public static final Font FONT_SEGOE_11 = new ManagedFont("Segoe", 11, SWT.NORMAL).getFont();
	public static final Font FONT_SEGOE_11_BOLD = new ManagedFont("Segoe", 11, SWT.BOLD).getFont();
	public static final Font FONT_TAHOMA_25 = new ManagedFont("Tahoma", 25, SWT.NORMAL).getFont();

	public static final Font FONT_DOTUM_9 = new ManagedFont("Dotum", 9, SWT.NORMAL).getFont();

	public static final Color COLOR_CLICK = new ManagedColor(173, 216, 230).getColor();
	public static final Color COLOR_ORANGE = new ManagedColor(255, 215, 0).getColor();
	public static final Color COLOR_LIGHT_ORANGE = new ManagedColor(255, 253, 181).getColor();
	public static final Color COLOR_BUTTON_SELECTED = new ManagedColor(100, 98, 105).getColor();
	public static final Color COLOR_LIGHT2_RED = new ManagedColor(244, 101, 66).getColor();

	public static final Color COLOR_SETTINGS = new ManagedColor(102, 194, 102).getColor();
//	public static final Color COLOR_LIVE = new ManagedColor(255, 184, 77).getColor();
	public static final Color COLOR_LIVE = new ManagedColor(254,191,4).getColor();
	public static final Color COLOR_CONSTRAINT = new ManagedColor(128, 153, 230).getColor();
	//public static final Color COLOR_MISC = new ManagedColor(153, 153, 153).getColor();
	public static final Color COLOR_MISC = new ManagedColor(158, 171, 178).getColor(); 
	public static final Color TURQUOISE = new ManagedColor(66, 244, 223).getColor();
	public static final Color COLOR_LIGHT_TURQUOISE = new ManagedColor(183, 237, 188).getColor();
	public static final Color DARK_GRAY = new ManagedColor(49, 79, 79).getColor();
	public static final Color LAUNCHER_BLUE = new ManagedColor(77,136,219).getColor();
	public static final Color LAUNCHER_DARK = new ManagedColor(30, 137, 62).getColor();

	public static final Color COLOR_LIGHTER_BLUE = new ManagedColor(255, 245, 238).getColor();
	public static final Color COLOR_LIGHT_BLUE = new ManagedColor(220, 224, 252).getColor();
	public static final Color DARKER_BLUE = new ManagedColor(57, 141, 237).getColor();
	public static final Color RED_ORANGE = new ManagedColor(255, 69, 0).getColor();
	public static final Color COLOR_LIGHT_RED = new ManagedColor(253, 208, 162).getColor();
	public static final Color COLOR_LIGHTER_RED = new ManagedColor(250, 220, 240).getColor();

	public static final Color COLOR_DARK_ORANGE = new ManagedColor(242, 213, 24).getColor();
	public static final Color COLOR_DARK_GREEN = new ManagedColor(12, 94, 64).getColor();
	public static final Color COLOR_DARK_RED = new ManagedColor(214, 45, 45).getColor();
	public static final Color COLOR_DARK_BLUE = new ManagedColor(107, 67, 217).getColor();

	public static final Color COLOR_NOT1 = new ManagedColor(40, 73, 97).getColor();
	public static final Color COLOR_NOT2 = new ManagedColor(226, 239, 249).getColor();
	public static final Color COLOR_NOT3 = new ManagedColor(177, 211, 243).getColor();

	public static final Color COLOR_PURPLE = new ManagedColor(244, 66, 220).getColor();
	
	
	public static final Color COLOR_LIGHT_GREEN = new ManagedColor(206, 245, 193).getColor();

	public static final Color CONTROL_NOT_MODIFIED = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	public static final Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	public static final Color COLOR_DISABLED = new ManagedColor(245, 245, 245).getColor();
	public static final Color EXTRA_LIGHT_BLUE = new ManagedColor(232, 242, 254).getColor();
	// public static final Color LIGHT_BLUE = new Color(

	// public static final Color COLOR_LIGHT_RED = new
	// Color(224, 97, 80);
	public static final Color COLOR_LIGHT2_ORANGE = new ManagedColor(239, 203, 83).getColor();
	public static final Color COLOR_LIGHT2_GREEN = new ManagedColor(113, 224, 98).getColor();
	public static final Color COLOR_LIGHT_PINK = new ManagedColor(227, 183, 255).getColor();
	public static final Color COLOR_GRAY = new ManagedColor(239, 239, 232).getColor();
	
	public static final Color COLOR_YELLOW_LEMON = new ManagedColor(206, 221, 42).getColor();
	
	
	public static final String CSS_TAG = "org.eclipse.e4.ui.css.id";

	
	private static Set<Control> CONTROL_LIST_BOLD = Collections.newSetFromMap(new WeakHashMap<Control, Boolean>());
	private static final Set<Control> CONTROL_LIST = Collections.newSetFromMap(new WeakHashMap<Control, Boolean>());
	private static CSSSWTEngineImpl THEME_ENGINE;
	
	public static void disposeAll() {
		for (Image i : imageList) {
			i.dispose();
		}

	}

	public static Image imageFromByteArray(byte[] buffer) {
		InputStream stream = new ByteArrayInputStream(buffer);
		Image image = null;
		try {
			image = new Image(Display.getDefault(), stream);
		} catch (SWTException ex) {
			ex.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		imageList.add(image);
		return image;
	}

	public static Image toGreyScale(Image image) {
		Image grey = new Image(Display.getDefault(), image, SWT.IMAGE_GRAY);
		imageList.add(grey);
		return grey;
	}

	public static Font getSelectedFont(Control toSet) {
		if(toSet== null){
			throw new IllegalArgumentException("Control must not be null!");
		}
		CONTROL_LIST.add(toSet);
		return getSelectedFont();
	}

	public static Font getSelectedBoldFont(Control toSet) {
		if(toSet== null){
			throw new IllegalArgumentException("Control must not be null!");
		}
		CONTROL_LIST_BOLD.add(toSet);
		return getSelectedBoldFont();
	}

	public static Font getSelectedFont() {
		return SELECTED_FONT != null ? SELECTED_FONT : FONT_SEGOE_11;
	}

	public static Font getSelectedBoldFont() {
		return SELECTED_FONT_BOLD != null ? SELECTED_FONT_BOLD : FONT_SEGOE_11_BOLD;
	}

	public static void setSelectedFont(FontData data) {
		if (SELECTED_FONT != null && data.equals(SELECTED_FONT.getFontData()[0])) {
			return;
		}

		SELECTED_FONT = new ManagedFont(data.getName(), data.getHeight(), data.getStyle()).getFont();
		SELECTED_FONT_BOLD = new ManagedFont(data.getName(), data.getHeight(), SWT.BOLD).getFont();
		notifyFontChangeListener();
	}

	private static void notifyFontChangeListener() {
		changeFont(SELECTED_FONT);
		changeBoldFont(SELECTED_FONT_BOLD);
		for (IFontChangeListener f : fontChangeListener) {
			f.fontChanged(SELECTED_FONT);
		}

	}

	public static void addFontChangeListener(IFontChangeListener listener) {
		fontChangeListener.add(listener);

	}

	public static void changeFont(Font selectedFont) {
		// Font font = StyleProvider.getSelectedFont();
		List<Control> changeList = new ArrayList<>(CONTROL_LIST);
		for (Control c : changeList) {
			if (c != null && !c.isDisposed()) {
				c.setFont(selectedFont);
			} else {
				CONTROL_LIST.remove(c);
			}
		}
	}

	public static void changeBoldFont(Font selectedFont) {
		// Font font = StyleProvider.getSelectedFont();
		List<Control> changeList = new ArrayList<>(CONTROL_LIST_BOLD);
		for (Control c : changeList) {
			if (!c.isDisposed()) {
				c.setFont(selectedFont);
			} else {
				CONTROL_LIST.remove(c);
			}
		}
	}

	static {
		StyleProvider.addFontChangeListener((Font selectedFont) -> {
			changeFont(selectedFont);
		});
	}

	public static void reapplyTheme() {
		if (THEME_ENGINE == null) {
			THEME_ENGINE = new CSSSWTEngineImpl(Display.getDefault());
		}

		THEME_ENGINE.reapply();
	}

}
