package edu.nd.dronology.ui.cc.images.definitions;

import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.ui.cc.images.managed.ManagedImage;

public interface Severity {
	
	public static final Image IMG_STATUS_INFO_24 = new ManagedImage("img/severity/info_icon_24.png").getImage();

	public static final Image IMG_STATUS_WARN_24 = new ManagedImage("img/severity/warn_icon_24.png").getImage();

	public static final Image IMG_STATUS_ERROR_24 = new ManagedImage("img/severity/error_icon_24.png").getImage();


}
