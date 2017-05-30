package edu.nd.dronology.ui.cc.images.definitions;

import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.ui.cc.images.managed.ManagedImage;

public interface Launcher {
	
	
	final String FOLDER ="img/launcher/";

	public static final Image IMG_LAUNCHER_FLIGHTPLAN = new ManagedImage(FOLDER+"launcher_flightplan_96.png").getImage();
	public static final Image IMG_LAUNCHER_FLIGHTPLAN_LARGE = new ManagedImage(FOLDER+"launcher_flightplan_128.png").getImage();
	
	
	public static final Image IMG_LAUNCHER_TAKEOFF = new ManagedImage(FOLDER+"launcher_takeoff_96.png").getImage();
	public static final Image IMG_LAUNCHER_TAKEOFF_LARGE = new ManagedImage(FOLDER+"launcher_takeoff_128.png").getImage();
	
	public static final Image IMG_LAUNCHER_SIMULATOR = new ManagedImage(FOLDER+"launcher_simulator_96.png").getImage();
	public static final Image IMG_LAUNCHER_SIMULATOR_LARGE = new ManagedImage(FOLDER+"launcher_simulator_128.png").getImage();
	
	public static final Image IMG_LAUNCHER_SPECIFICATION = new ManagedImage(FOLDER+"launcher_equipment_96.png").getImage();
	public static final Image IMG_LAUNCHER_SPECIFICATION_LARGE = new ManagedImage(FOLDER+"launcher_equipment_128.png").getImage();
	
	public static final Image IMG_LAUNCHER_MONITOR = new ManagedImage(FOLDER+"launcher_monitor_96.png").getImage();
	public static final Image IMG_LAUNCHER_MONITOR_LARGE = new ManagedImage(FOLDER+"launcher_monitor_128.png").getImage();
	
	
	public static final Image IMG_LAUNCHER_SETTINGS = new ManagedImage(FOLDER+"launcher_settings.png").getImage();
	public static final Image IMG_LAUNCHER_SETTINGS_LARGE = new ManagedImage(FOLDER+"launcher_settings_large.png").getImage();

	public static final Image IMG_LAUNCHER_SIMSCENARIO = new ManagedImage(FOLDER+"launcher_scenario_96.png").getImage();
	public static final Image IMG_LAUNCHER_SIMSCENARIO_LARGE = new ManagedImage(FOLDER+"launcher_scenario_128.png").getImage();

	public static final Image IMG_LAUNCHER_INFO = new ManagedImage(FOLDER+"launcher_info.png").getImage();

}
