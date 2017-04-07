package edu.nd.dronology.core.start;
import edu.nd.dronology.core.drone_status.DroneCollectionStatus;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import view.DronologyRunner;

/**
 * Starts up the drone formation simulation.
 * @author Jane Cleland-Huang
 * @version 0.1
 *
 */
public class StartFlightZone{	
	public static void main(String[] args) {
		
	try {
			DronologyRunner droneRunner = new DronologyRunner();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlightZoneException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}	


