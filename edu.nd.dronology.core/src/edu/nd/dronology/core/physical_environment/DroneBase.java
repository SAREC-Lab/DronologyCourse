package edu.nd.dronology.core.physical_environment;

import java.awt.Point;

import edu.nd.dronology.core.drones_runtime.ManagedDrone;
import edu.nd.dronology.core.drones_runtime.iDrone;
import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.core.utilities.DecimalDegreesToXYConverter;
import edu.nd.dronology.core.zone_manager.FlightZoneException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Defines a drone base at which drones are allowed to takeoff and land
 * @author Jane
 *
 */
public class DroneBase {
	Coordinates basePosition;
	ImageView baseImage;
	String baseName;
	Image img;
	DecimalDegreesToXYConverter coordTransform;
	Circle circle;
	ManagedDrone drone = null;  // Each drone currently has its own landing base.
	
	/** 
	 * Constructor
	 * @param baseName Unique basename
	 * @param latitude of base
	 * @param longitude of base
	 * @param altitude of base
	 * @throws FlightZoneException 
	 */
	public DroneBase(String baseName, long lat, long lon, int alt) throws FlightZoneException{
		basePosition = new Coordinates(lat,lon,alt);
		this.baseName = baseName;	
	}
	
	/**
	 * 
	 * @return if a drone has already been assigned to the base.
	 */
	public boolean hasDroneAssigned(){
		if (drone == null)
			return false;
		else
			return true;
	}
		
	/**
	 * Assign a drone to the base
	 * @param drone2
	 */
	public void assignDroneToBase(ManagedDrone drone2){
		this.drone = drone2;
	}
	
	/**
	 * @return base latitude
	 */
    public long getLatitude(){
    	return basePosition.getLatitude();
    }
    
    /**
     * @return base longitude
     */
    public long getLongitude(){
    	return basePosition.getLongitude();
    }
    
    
    /**
     * @return base altitude (currently not used)
     */
    public int getAltitude(){
    	return basePosition.getAltitude();
    }
    
    /**
     * @return baseCoordinates
     */
    public Coordinates getCoordinates(){
    	return basePosition;
    }
        
    //@TD move this over to center it.
    /**
     * Creates a circle representing the base
     * @return circle
     */
    public Circle getCircle(){
	  return circle;
    }
}
