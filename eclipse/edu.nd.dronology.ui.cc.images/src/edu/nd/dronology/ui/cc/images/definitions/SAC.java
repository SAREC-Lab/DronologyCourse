package edu.nd.dronology.ui.cc.images.definitions;

import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.ui.cc.images.managed.ManagedImage;

public interface SAC {
	
	
	final String FOLDER ="img/sac/";

	public static final Image IMG_STATIC_24 = new ManagedImage(FOLDER+"static_24.png").getImage();
	public static final Image IMG_PLUG_24 = new ManagedImage(FOLDER+"plug_24.png").getImage();
	public static final Image IMG_MONITOR_24 = new ManagedImage(FOLDER+"monitor_24.png").getImage();
	

}
