package edu.nd.dronology.gstation.python.connector;

import org.json.simple.JSONObject;

import edu.nd.dronology.core.util.Coordinate;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

@Integrate
public class PythonDroneState {

	private static final ILogger LOGGER = LoggerProvider.getLogger(PythonDroneState.class);

	private Coordinate location;
	private Coordinate attitude;
	private Coordinate velocity;
	private Coordinate gimbalRotation;
	private double batteryVoltage;
	private double batteryCurrent;
	private double batteryLevel;
	private Coordinate home;
	private String status;
	private double heading;
	private boolean armable;
	private double airspeed;
	private double groundspeed;
	private boolean armed;
	private String mode;

	public PythonDroneState() {
		location = new Coordinate(0, 0, 0);
		attitude = new Coordinate(0, 0, 0);
		velocity = new Coordinate(0, 0, 0);
		gimbalRotation = new Coordinate(0, 0, 0);
		batteryVoltage = 0;
		batteryCurrent = 0;
		batteryLevel = 0;
		home = new Coordinate(0, 0, 0);
		status = "Initializing";
		heading = 0;
		armable = false;
		airspeed = 0;
		groundspeed = 0;
		armed = false;
		mode = "INIT";
	}

	public Coordinate getLocation() {
		return location;
	}

	public Coordinate getAttitude() {
		return attitude;
	}

	public Coordinate getVelocity() {
		return velocity;
	}

	public Coordinate getGimbalRotation() {
		return gimbalRotation;
	}

	public double getBatteryVoltage() {
		return batteryVoltage;
	}

	public double getBatteryCurrent() {
		return batteryCurrent;
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	public Coordinate getHome() {
		return home;
	}

	public String getStatus() {
		return status;
	}

	public double getHeading() {
		return heading;
	}

	public boolean getArmable() {
		return armable;
	}

	public double getAirspeed() {
		return airspeed;
	}

	public double getGroundspeed() {
		return groundspeed;
	}

	public boolean getArmed() {
		return armed;
	}

	public String getMode() {
		return mode;
	}

	public void setLocation(Coordinate newLocation) {
		location = newLocation;
	}

	public void setAttitude(Coordinate newAttitude) {
		attitude = newAttitude;
	}

	public void setVelocity(Coordinate newVelocity) {
		velocity = newVelocity;
	}

	public void setGimbalRotation(Coordinate newGimbalRotation) {
		gimbalRotation = newGimbalRotation;
	}

	public void setBatteryVoltage(double newBatteryVoltage) {
		batteryVoltage = newBatteryVoltage;
	}

	public void setBatteryCurrent(double newBatteryCurrent) {
		batteryCurrent = newBatteryCurrent;
	}

	public void setBatteryLevel(double newBatteryLevel) {
		batteryLevel = newBatteryLevel;
	}

	public void setHome(Coordinate newHome) {
		home = newHome;
	}

	public void setStatus(String newStatus) {
		status = newStatus;
	}

	public void setHeading(double newHeading) {
		heading = newHeading;
	}

	public void setArmable(boolean newArmable) {
		armable = newArmable;
	}

	public void setAirspeed(double newAirspeed) {
		airspeed = newAirspeed;
	}

	public void setGroundspeed(double newGroundspeed) {
		groundspeed = newGroundspeed;
	}

	public void setArmed(boolean newArmed) {
		armed = newArmed;
	}

	public void setMode(String newMode) {
		mode = newMode;
	}

	// public double coordLongToFloat(long coord) {
	// double floatScaled = coord;
	// return floatScaled/1000000.0;
	// }
	public long floatToCoordLong(double coord) {
		double floatScaled = coord * 1000000.0;
		long longScaled = (long) floatScaled;
		return longScaled;
	}

	public double ensureIsDouble(Object incoming) {
		if (incoming instanceof java.lang.Long) {
			long inLong = (long) incoming;
			return inLong;
		} else {
			return (double) incoming;
		}
	}

	public Coordinate coordFromJSON(JSONObject jsonCoord) {
		double x = ensureIsDouble(jsonCoord.get("x"));
		double y = ensureIsDouble(jsonCoord.get("y"));
		double z = ensureIsDouble(jsonCoord.get("z"));
		long x_l = floatToCoordLong(x);
		long y_l = floatToCoordLong(y);
		int z_i = (int) z;
		// int z = (int) jsonCoord.get("z"); //TODO: this part of the coordinate
		// being an integer might not be ideal
		return new Coordinate(x_l, y_l, z_i);
	}

	// public HashMap<String,Object> JSONfromCoord(Coordinates coord) {
	// HashMap<String,Object> tempData = new HashMap<String,Object>();
	// tempData.put("x", coordLongToFloat(coord.getLatitude()));
	// tempData.put("y", coordLongToFloat(coord.getLongitude()));
	// tempData.put("z", coord.getAltitude());
	// return tempData;
	// }
	//
	public void loadfromJSON(JSONObject droneInfo) {
		for (Object keyObj : droneInfo.keySet()) {
			String key = (String) keyObj;
			Object data = droneInfo.get(keyObj);
			// System.out.println("key: "+key+" data: "+data.toString());
			switch (key) {
			case "location":
				setLocation(coordFromJSON((JSONObject) data));
				break;
			case "attitude":
				setAttitude(coordFromJSON((JSONObject) data));
				break;
			case "velocity":
				setVelocity(coordFromJSON((JSONObject) data));
				break;
			case "gimbalRotation":
				setGimbalRotation(coordFromJSON((JSONObject) data));
				break;
			case "battery":
				JSONObject batteryInfo = (JSONObject) data;
				try {
					setBatteryVoltage((double) batteryInfo.get("voltage"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					setBatteryCurrent((double) batteryInfo.get("current"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Object level = batteryInfo.get("level");
				try {
					System.out.println(level.getClass());
					System.out.print("level: ");
					System.out.println(level);
				} catch (Exception e) {
					LOGGER.error(e);
				}
				if (level instanceof java.lang.Long) {
					long level_l = (long) level;
					setBatteryLevel(level_l);
				} else if (level instanceof java.lang.Double) {
					setBatteryLevel((double) level);
				} else {
					// fallback because of NullPointerException in some cases
					LOGGER.hwFatal("Unknown type for battery level: ");
					/*
					 * try { System.out.println(level.getClass());
					 * System.out.print("level: "); System.out.println(level); }
					 * catch (Exception e) { e.printStackTrace(); }
					 */
					System.out.println("Falling back to setting level to 0.");
					setBatteryLevel(0.0);
				}
				// setBatteryLevel((double) batteryInfo.get("level"));
				break;
			case "home":
				setHome(coordFromJSON((JSONObject) data));
				break;
			case "status":
				setStatus((String) data);
				break;
			case "heading":
				if (data instanceof java.lang.Long) {
					long data_l = (long) data;
					setHeading(data_l);
				} else {
					setHeading((double) data);
				}
				break;
			case "armable":
				setArmable((boolean) data);
				break;
			case "airspeed":
				setAirspeed((double) data);
				break;
			case "groundspeed":
				setGroundspeed((double) data);
				break;
			case "armed":
				setArmed((boolean) data);
				break;
			case "mode":
				setMode((String) data);
				break;
			default:
				LOGGER.hwFatal("Unrecognized drone attribute: " + key);
			}
		}
	}

	@Override
	public String toString() {
		return "armed=" + armed + "| mode " + mode + " | coordinate[" + Long.toString(getLocation().getLatitude()) + ","
				+ Long.toString(getLocation().getLongitude()) + "," + Integer.toString(getLocation().getAltitude())
				+ "]";
	}

}
