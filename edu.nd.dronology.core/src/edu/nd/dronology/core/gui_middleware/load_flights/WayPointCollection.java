package edu.nd.dronology.core.gui_middleware.load_flights;

import java.util.ArrayList;

import edu.nd.dronology.core.flight_manager.FlightZoneManager;
import edu.nd.dronology.core.utilities.Coordinates;

public class WayPointCollection {
	private ArrayList<ArrayList<Coordinates>> wayPointCollection = null;

	public void addNewSetOfWayPoints(ArrayList<Coordinates> wayPoints){
		wayPointCollection.add(wayPoints);
	}
	
	public void loadFlightPlans(FlightZoneManager fzm){
		for(ArrayList<Coordinates> waypoints: wayPointCollection){
			fzm.planFlight(waypoints.get(0), waypoints);
		}
	}
	
	public ArrayList<ArrayList<Coordinates>> getAllWayPoints(){
		return wayPointCollection;
	}

}
