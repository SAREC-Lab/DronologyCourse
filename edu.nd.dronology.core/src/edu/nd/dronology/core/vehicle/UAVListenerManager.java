package edu.nd.dronology.core.vehicle;

public class UAVListenerManager {

	private static volatile UAVListenerManager INSTANCE = null;

	public static UAVListenerManager getInstance() {

		if (INSTANCE == null) {
			synchronized (UAVListenerManager.class) {
				if (INSTANCE == null) {
					INSTANCE = new UAVListenerManager();
				}
			}
		}
		return INSTANCE;
	}

	public void notifyUAVFlightModeChanged(String id, String newState){
		//notify listeners...
	
	}

}
