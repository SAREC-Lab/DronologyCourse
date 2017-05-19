package edu.nd.dronology.gstation.python.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

// org.json.simple obtained from json-simple-1.1.1.jar downloaded from https://code.google.com/archive/p/json-simple/
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.nd.dronology.core.Discuss;
import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.core.vehicle.DroneAttribute;
import edu.nd.dronology.core.vehicle.IDroneAttribute;
import edu.nd.dronology.core.vehicle.IDroneCommandHandler;
import edu.nd.dronology.core.vehicle.commands.IDroneCommand;
import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

@Integrate
public class PythonBase implements Runnable, IDroneCommandHandler {

	private static final ILogger LOGGER = LoggerProvider.getLogger(PythonBase.class);

	protected static final ExecutorService servicesExecutor = Executors.newFixedThreadPool(5,
			new NamedThreadFactory("Groundstation-Threads"));

	// socket for communication with python ground station
	private Socket pythonSocket;
	private InputStream inStream;
	private String incomingData = "";
	private OutputStream outStream;

	private List<Integer> allocatedIDs;
	private List<Integer> unallocatedIDs;

	private ReentrantLock unallocatedLock;

	private HashMap<Integer, PythonDroneState> droneStates;

	Thread thread;

	@Discuss(discuss = "port+ip should be specified in property file/passed to base when initialized")
	public PythonBase() {
		try {

			String host = InetAddress.getLocalHost().getHostAddress();
			int port = 1234;

			LOGGER.info("Connecting to Python base " + host + "@" + port);
			pythonSocket = new Socket(InetAddress.getLocalHost(), port);

			inStream = pythonSocket.getInputStream();
			outStream = pythonSocket.getOutputStream();
			allocatedIDs = new ArrayList<>();
			unallocatedIDs = new ArrayList<>();
			unallocatedLock = new ReentrantLock();
			droneStates = new HashMap<>();
			LOGGER.info("Connected to " + pythonSocket.getInetAddress().toString() + "@" + pythonSocket.getPort());
			servicesExecutor.submit(this);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	@Discuss(discuss = "this needs refactoring!")
	public void getIncomingData() {
		try {
			byte[] inData = new byte[256];
			int readCount = inStream.read(inData);
			if (readCount > 0) {
				String inString = new String(Arrays.copyOfRange(inData, 0, readCount));
				incomingData = incomingData + inString;
				parseIncomingData();

				// more data might still be available. call this function again. // TODO: modify this to not use recursion (unnecessarily uses up stack space and causes overflows)
				getIncomingData();
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	public void parseIncomingData() {
		int dataLength = incomingData.indexOf('\r');
		if (dataLength >= 0) {
			String dataChunk = incomingData.substring(0, dataLength);
			incomingData = incomingData.substring(dataLength + 1);
			parseData(dataChunk);
			// more data might still be available. call this function again. // TODO: modify this to not use recursion (unnecessarily uses up stack space)
			parseIncomingData();
		}
	}

	public void parseData(String data) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject dataObject = (JSONObject) parser.parse(data);
			String parsedType = (String) dataObject.get("type");
			Object parsedData = dataObject.get("data");
			handleData(parsedType, parsedData);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleData(String type, Object data) {
		/*
		 * System.out.println("Incoming data:"); // temporary placeholder to identify incoming data System.out.println("Type: "+type); System.out.print("Data: "); System.out.println(data); // temporary
		 * placeholder to identify incoming data System.out.print("Data type: "); System.out.println(data.getClass()); // temporary placeholder to identify incoming data
		 */

		switch (type) {
			case "drone_list":
				handleDroneList((JSONObject) data);
				break;
			case "new_drone":
				JSONObject json = (JSONObject) data;
				int ID = (int) json.get("id");
				JSONObject droneData = (JSONObject) json.get("data");
				handleNewDrone(ID, droneData);
				break;
			default:
		}
	}

	public void updateDroneInfo(int ID, JSONObject data) {
		PythonDroneState thisDroneState = droneStates.get(ID);
		thisDroneState.loadfromJSON(data);
	}

	public void handleNewDrone(int ID, JSONObject data) {
		unallocatedLock.lock();
		unallocatedIDs.add(ID);
		droneStates.put(ID, new PythonDroneState());
		unallocatedLock.unlock();
		updateDroneInfo(ID, data);
	}

	public void handleDroneList(JSONObject data) {
		for (Object keyObj : data.keySet()) {
			String key = (String) keyObj;
			Object droneInfo = data.get(keyObj);
			int ID = Integer.parseInt(key);
			if (allocatedIDs.contains(ID) || unallocatedIDs.contains(ID)) {
				updateDroneInfo(ID, (JSONObject) droneInfo);
			} else {
				handleNewDrone(ID, (JSONObject) droneInfo);
			}
		}
	}

	public void sendData(String data) {
		try {
			byte[] outData = data.getBytes();
			outStream.write(outData);
			outStream.write('\r');
			outStream.write('\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public void sendCommand(int id, String command, Map<String, Object> data) {
	//
	// JSONObject rootObject = new JSONObject();
	// rootObject.put("type", "command");
	// JSONObject dataObject = new JSONObject();
	// dataObject.put("id", id);
	// dataObject.put("command", command);
	// JSONObject innerDataObject = new JSONObject();
	// innerDataObject.putAll(data);
	// dataObject.put("data", innerDataObject);
	// rootObject.put("data", dataObject);
	//
	// try {
	// StringWriter out = new StringWriter();
	// rootObject.writeJSONString(out);
	// String jsonText = out.toString();
	//
	// sendData(jsonText);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	@Discuss(discuss = "this needs fixing... - now we have 2 different ids")
	@Override
	public int getNewDroneID() throws Exception {
		// TODO: temporarily avoiding race condition with random wait time
		Random temp = new Random();
		Thread.sleep(temp.nextInt(1000));
		boolean stillLooping = true;
		while (stillLooping) {
			// wait until an unallocated drone is available
			System.out.println("waiting for available drone...");
			Thread.sleep(500);
			if (!unallocatedIDs.isEmpty()) {
				if (!unallocatedLock.isLocked()) {
					if (unallocatedLock.tryLock()) {
						stillLooping = false;
					}
				}
			}
		}
		// TODO: This is not threadsafe - may need to add some threadlocking code here
		int ID = unallocatedIDs.get(0);
		unallocatedIDs.remove(0);
		allocatedIDs.add(ID);
		unallocatedLock.unlock();
		return ID;
	}

	public PythonDroneState getDroneState(int ID) {
		return droneStates.get(ID);
	}

	@Discuss(discuss = "move runnable to its own class")
	@Override
	public void run() {
		while (true) {
			// get incoming data
			getIncomingData();
			try {
				// small delay to prevent using too many resources
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendCommand(IDroneCommand cmd) throws DroneException {
		sendData(cmd.toJsonString());
	}

	@Override
	@Deprecated
	/**
	 * Replace with the getAttribute method later...
	 */
	public Coordinate getLocation(int droneID) {
		return getDroneState(droneID).getLocation();
	}

	@Override
	@Deprecated
	/**
	 * Replace with the getAttribute method later...
	 */
	public double getBatteryVoltage(int droneID) {
		return getDroneState(droneID).getBatteryVoltage();
	}

	@Override
	public IDroneAttribute<?> getAttribute(String droneId, String key) {
		switch (key) {
			case IDroneAttribute.ATTRIBUTE_BATTERY_VOLTAGE:
				return new DroneAttribute<>(key, getDroneState(Integer.parseInt(droneId)).getBatteryVoltage());
			case IDroneAttribute.ATTRIBUTE_LOCATION:
				return new DroneAttribute<>(key, getDroneState(Integer.parseInt(droneId)).getLocation());
			default:
				return null;
		}
	}
}
