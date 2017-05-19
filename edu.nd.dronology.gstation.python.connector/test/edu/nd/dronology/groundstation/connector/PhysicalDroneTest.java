package edu.nd.dronology.groundstation.connector;
//package TestCases;
//import java.util.Calendar;
//
//import org.junit.Test;
//
//import edu.nd.dronology.core.exceptions.FlightZoneException;
//import edu.nd.dronology.core.util.Coordinate;
//import edu.nd.dronology.core.vehicle.IDrone;
//import model.drone.groundstation.PythonBase;
//import model.drone.runtime.PhysicalDrone;
//
//public class PhysicalDroneTest {
//
//	@Test
//	public void test() throws FlightZoneException, InterruptedException {
//		PythonBase baseStation = new PythonBase();
//		
//		DroneSet
//		
//		IDrone drone = DroneFleetfacotry.("test_drone", baseStation);
//		long alt_tolerance = 1;
//		long init_lat = drone.getLatitude();
//		long init_lon = drone.getLongitude();
//		long init_alt = drone.getAltitude();
//		System.out.println(init_lat);
//		System.out.println(init_lon);
//		System.out.println(init_alt);
//		
//		assert(init_alt<=alt_tolerance);
//		
//		long dest_lat = init_lat + 1000;
//		long dest_lon = init_lon + 1000;
//		int dest_alt = 20;
//		
//		//TestTakeoff(drone,alt_tolerance,45000,dest_alt);
//		//TestLand(drone,0,45000);
//		
//		//Thread.sleep(1000); //TODO: figure out why land then takeoff doesn't work
//		
//		TestTakeoff(drone,alt_tolerance,45000,dest_alt);
//		TestFlyTo(drone, 60000, dest_lat, dest_lon, dest_alt);
//		TestFlyTo(drone, 60000, init_lat, init_lon, dest_alt);
//		TestLand(drone,alt_tolerance,45000);
//	}
//	
//	private void TestTakeoff(IDrone drone, long alt_tolerance, long timeout, int dest_alt) throws FlightZoneException, InterruptedException {
//		drone.takeOff(dest_alt);
//		long init_time = Calendar.getInstance().getTimeInMillis();
//		//long init_time = System.currentTimeMillis();
//		long current_time = init_time;
//		boolean still_taking_off = true;
//		while(((current_time-init_time)<timeout) && still_taking_off) {
//			Thread.sleep(10);
//			if(drone.getAltitude()>=(dest_alt-alt_tolerance)) {
//				still_taking_off = false;
//			}
//			current_time = Calendar.getInstance().getTimeInMillis();
//		}
//		assert(!still_taking_off);
//	}
//	
//	private void TestLand(IDrone drone, long alt_tolerance, long timeout) throws FlightZoneException, InterruptedException {
//		drone.land();
//		long init_time = Calendar.getInstance().getTimeInMillis();
//		long current_time = init_time;
//		boolean still_landing = true;
//		while(((current_time-init_time)<timeout) && still_landing) {
//			Thread.sleep(10);
//			if(drone.getAltitude()<=alt_tolerance) {
//				still_landing = false;
//			}
//			current_time = Calendar.getInstance().getTimeInMillis();
//		}
//		assert(!still_landing);
//	}
//	
//	private void TestFlyTo(IDrone drone, long timeout, long lat, long lon, long alt) throws FlightZoneException, InterruptedException {
//		drone.flyTo(new Coordinate(lat, lon, (int)alt));
//		long init_time = Calendar.getInstance().getTimeInMillis();
//		long current_time = init_time;
//		boolean still_flying = true;
//		while(((current_time-init_time)<timeout) && still_flying) {
//			Thread.sleep(10);
//			if(drone.isDestinationReached(0)) {
//				still_flying = false;
//			}
//			current_time = Calendar.getInstance().getTimeInMillis();
//		}
//		assert(!still_flying);
//	}
//}
