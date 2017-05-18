package edu.nd.dronology.ui.cc.images;

import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.ui.cc.images.definitions.DefaultUI;
import edu.nd.dronology.ui.cc.images.definitions.Launcher;
import edu.nd.dronology.ui.cc.images.definitions.Severity;
import edu.nd.dronology.ui.cc.images.managed.ManagedImage;

public abstract class ImageProvider implements Launcher, DefaultUI,Severity {



	public static final Image IMG_BANNER = new ManagedImage("img/launcher/drone_banner.png").getImage();
	
	
	public static final Image IMG_FLIGHTROUTE_24 = new ManagedImage("img/elements/item_flightplan_24.png").getImage();
	public static final Image IMG_SPECIFICATION_24 = new ManagedImage("img/elements/item_equipment_24.png").getImage();
	public static final Image IMG_SIMSCENARIO_24 = new ManagedImage("img/elements/item_simscenario_24.png").getImage();
	
	public static final Image IMG_SERVICE_START = new ManagedImage("img/services/start.png").getImage();
	public static final Image IMG_SERVICE_STOP = new ManagedImage("img/services/stop.png").getImage();
	public static final Image IMG_SERVICE_RESTART = new ManagedImage("img/services/restart.png").getImage();
	public static final Image IMG_SERVICE_INFO = new ManagedImage("img/services/info.png").getImage();
	
	public static final Image IMG_SERVICE_TYPE_REMOTE = new ManagedImage("img/services/type_remote.png").getImage();

	public static final Image IMG_SERVICE_TYPE_SOCKET = new ManagedImage("img/services/type_socket.png").getImage();
	public static final Image IMG_SERVICE_TYPE_FILE = new ManagedImage("img/services/type_file.png").getImage();

	
	public static final Image IMG_SERVICE_START_DISABLED = new ManagedImage("img/services/start_disabled.png").getImage();
	public static final Image IMG_SERVICE_STOP_DISABLED = new ManagedImage("img/services/stop_disabled.png").getImage();
	public static final Image IMG_SERVICE_RESTART_DISABLED = new ManagedImage("img/services/restart_disabled.png").getImage();
	public static final Image IMG_SERVICE_INFO_DISABLED = new ManagedImage("img/services/info_disabled.png").getImage();

	
	public static final Image IMG_SERVICE_STATUS_ONLINE = new ManagedImage("img/services/online.png").getImage();
	public static final Image IMG_SERVICE_STATUS_ERROR = new ManagedImage("img/services/error.png").getImage();
	public static final Image IMG_SERVICE_STATUS_OFFILE = new ManagedImage("img/services/offline.png").getImage();
	public static final Image IMG_MCC_SERVICE_STATUS_PAUSED = new ManagedImage("img/services/paused.png").getImage();
	public static final Image IMG_SERVICE_STATUS_STARTING = new ManagedImage("img/services/starting.png").getImage();


	public static final Image IMG_DRONE_FLYING = new ManagedImage("img/drone/drone_flying_24.png").getImage();
	public static final Image IMG_DRONE_ONGROUND = new ManagedImage("img/drone/drone_ground_24.png").getImage();
	public static final Image IMG_DRONE_TAKINGOFF = new ManagedImage("img/drone/drone_takingoff_24.png").getImage();
	
	
	public static final Image IMG_DRONE_FLIGHTPLAN = new ManagedImage("img/drone/flightplan_24.png").getImage();
	public static final Image IMG_DRONE_WAYPOINT_START = new ManagedImage("img/drone/waypoint_start_24.png").getImage();
	public static final Image IMG_DRONE_WAYPOINT = new ManagedImage("img/drone/waypoint_24.png").getImage();
	
	
	
	public static final Image IMG_TRANSMIT_32 = new ManagedImage("img/general/transmit_32.png").getImage();
	public static final Image IMG_DRONE_ACTIVATE_24 = new ManagedImage("img/drone/drone_activate_24.png").getImage();
	
} 
 