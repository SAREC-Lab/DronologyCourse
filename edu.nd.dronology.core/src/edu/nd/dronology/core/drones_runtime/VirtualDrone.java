package edu.nd.dronology.core.drones_runtime;
import edu.nd.dronology.core.drone_status.DroneCollectionStatus;
import edu.nd.dronology.core.drone_status.DroneStatus;
import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import virtual_drone_simulator.DroneVoltageSimulator;
import virtual_drone_simulator.FlightSimulator;

/**
 * Creates a virtual drone.
 * iDrone interface needs refactoring badly!!!!
 * @author Jane Cleland-Huang
 * @version 0.01
 */
public class VirtualDrone implements iDrone{

	// Drone characteristics
	Coordinates currentPosition;
	String droneName;
	DroneStatus droneStatus;  //PHYS
	
	// Virtual drone requires simulators
	DroneVoltageSimulator voltageSimulator;
	FlightSimulator sim; 

	/**
	 * Constructs drone without specifying its current position.  This will be used by the physical drone (later)
	 * where positioning status will be acquired from the drone.
	 * @param drnName 
	 */
	public VirtualDrone(String drnName) {
		voltageSimulator = new DroneVoltageSimulator();
		currentPosition = null; 
		sim = new FlightSimulator(this);
		droneName = drnName;
		droneStatus = new DroneStatus(drnName,0,0,0,0.0,0.0);  // Not initialized yet  //PHYS
		DroneCollectionStatus.getInstance().addDrone(droneStatus); //PHYS	
	}

	
	@Override
	public void setCoordinates(long lat, long lon, int alt) {  // For physical drone this must be set by reading position
		currentPosition = new Coordinates(lat,lon,alt);	
		droneStatus.updateCoordinates(lat,lon,alt);
	}
	
	@Override
	public long getLatitude() {
		return currentPosition.getLatitude();
	}

	@Override
	public long getLongitude() {
		return currentPosition.getLongitude();
	}

	@Override
	public int getAltitude() {
		return currentPosition.getAltitude();
	}

	@Override
	public void takeOff(int targetAltitude) throws FlightZoneException {
		voltageSimulator.startBatteryDrain(); 
		droneStatus.updateBatteryLevel(voltageSimulator.getVoltage()); // Need more incremental drain!!
		try {			
			Thread.sleep(targetAltitude*100);  // Simulates attaining height.  Later move to simulator.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void flyTo(Coordinates targetCoordinates) {
		//targetPosition = targetCoordinates;
		sim.setFlightPath(currentPosition, targetCoordinates);
	}

	@Override
	public Coordinates getCoordinates() {
		return currentPosition;
	}


	public String getDroneName() {
		return droneName;
	}

	@Override
	public void land() throws FlightZoneException {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		voltageSimulator.checkPoint();
		voltageSimulator.stopBatteryDrain();		
	}
	
	@Override
	public double getBatteryStatus(){
		droneStatus.updateBatteryLevel(voltageSimulator.getVoltage());
		return voltageSimulator.getVoltage();
	}

	@Override
	public boolean move(int i) {  // ALSO NEEDS THINKING ABOUT FOR non-VIRTUAL
	//	System.out.println("Trying to move: " + droneName);
		getBatteryStatus();
		boolean moveStatus = sim.move(10);
		droneStatus.updateCoordinates(getLatitude(), getLongitude(), getAltitude());
		//DroneCollectionStatus.getInstance().testStatus();
		return moveStatus;
	}

	@Override
	public void setVoltageCheckPoint() {
		voltageSimulator.checkPoint();
		
	}

	@Override
	public boolean isDestinationReached(int distanceMovedPerTimeStep) {
		if (sim.isDestinationReached(distanceMovedPerTimeStep))
			return true;
		else
			return false;
	}


	@Override
	public DroneStatus getDroneStatus() {
		return droneStatus;
	}

	
}
