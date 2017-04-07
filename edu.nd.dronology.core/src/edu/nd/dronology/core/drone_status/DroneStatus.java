package edu.nd.dronology.core.drone_status;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DroneStatus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3319827887969940655L;
	//public enum flightStatus {Unknown, OnGround, AwaitingTakeOffClearance, TakingOff, Flying, Landing};
	long latitude;
	long longitude;
	int altitude;
	String ID;
	double batteryLevel;
	double velocity;
	Map<String,String> info;
	String status;

	public DroneStatus(String ID, long latitude, long longitude, int altitude, double batteryLevel, double velocity){
		this.ID = ID;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.batteryLevel = batteryLevel;
		this.velocity = velocity;
		info = new HashMap<String,String>();
		status = "UNKNOWN";
	}
	
	public void setInfoItem(String infoID, String infoValue){
		info.put(infoID,infoValue);
	}
	
	public void delInfoItem(String infoID){
		if (info.containsKey(infoID))
			info.remove(infoID);
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public void updateCoordinates(long latitude, long longitude, int altitude){
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public void updateBatteryLevel(double batteryLevel){
		this.batteryLevel = batteryLevel;
	}
	
	public void updateVelocity(double velocity){
		this.velocity = velocity;
	}
	
	@Override
	public String toString(){		
		return "ID: " + ID + " Pos: (" + latitude + "," + longitude + "," + altitude + ") " + " Vel: " + velocity + " Bat: " + batteryLevel + " --- " + this.status;		
	}

	@Override
	public int hashCode() {
        return 17 + ID.hashCode(); 
    }
	
	public long getLongitude(){
		return 0l;
	};

}
