package edu.nd.dronology.monitoring.trust;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import edu.nd.dronology.monitoring.service.DroneSafetyService;
import edu.nd.dronology.monitoring.service.IMonitoringValidationListener;
import edu.nd.dronology.monitoring.util.BenchmarkLogger;
import edu.nd.dronology.monitoring.validation.ValidationResult.Result;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * An entity used to determine the monitoring frequency of vehicles within the
 * infrastructure.
 * 
 * @author seanbayley
 *
 */
public class TrustManager {

	private static final ILogger LOGGER = LoggerProvider.getLogger(TrustManager.class);
	private static volatile TrustManager INSTANCE = null;

	private static final double FORGETTING_FACTOR = 0.9;

	private Map<String, VehicleReputation> history;
	private String pathToHistory;
	private double forgettingFactor;

	public TrustManager(String pathToHistory) {
		this(pathToHistory, FORGETTING_FACTOR);
	}

	public TrustManager(String pathToHistory, double forgettingFactor) {
		this.pathToHistory = pathToHistory;
		this.forgettingFactor = forgettingFactor;
		try {
			Gson gson = new Gson();
			JsonReader json = new JsonReader(new FileReader(pathToHistory));
			this.history = gson.fromJson(json, new TypeToken<Map<String, VehicleReputation>>() {
			}.getType());

		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			this.history = new HashMap<String, VehicleReputation>();
		}
		if (this.history == null)
			this.history = new HashMap<String, VehicleReputation>();
	}

	/**
	 * The singleton instance.
	 */
	public static TrustManager getInstance() {

		if (INSTANCE == null) {
			synchronized (TrustManager.class) {
				if (INSTANCE == null) {
					String cwd = System.getProperty("user.dir");
					cwd = cwd.replace("\\", "/");
					String[] toks = cwd.split("/");
					toks[toks.length - 1] = "edu.nd.dronology.monitoring";
					String path = Stream.of(toks).collect(Collectors.joining(System.getProperty("file.separator")));
					path = Paths.get(path, "src", "edu", "nd", "dronology", "monitoring", "trust", "history.json").toString();

					INSTANCE = new TrustManager(path);
				}
			}
		}
		return INSTANCE;
	}

	public void initialize() {
		DroneSafetyService.getInstance().addValidationListener(new InternalMonitoringEvalListener());
	}
	
	public void shutDown() {
		LOGGER.info("shutting down trust manager.");
		try {
			Gson gson = new Gson();
			String json = gson.toJson(history);
			FileWriter fw = new FileWriter(this.pathToHistory);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(json);
			bw.close();
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public void initializeUAV(String uavid) {
		if (!history.containsKey(uavid)) {
			LOGGER.info("new uav initialized: " + uavid);
			history.put(uavid, new VehicleReputation(uavid));
		}
		else {
			LOGGER.info(String.format("UAV %s (%f) re-entered the airspace", uavid, getReputationRating(uavid))); 
		}
	}

	/**
	 * Called by InternalMonitoringEvalListener when a constraint is evaluated
	 * 
	 * @param vid: the vehicle id
	 * @param assumptionid: the assumption id
	 * @param r: positive feedback
	 * @param s: negative feedback  
	 * @throws IllegalArgumentException
	 */
	public void constraintEvaluated(String vid, String assumptionid, double r, double s) throws IllegalArgumentException {
		if (!history.containsKey(vid))
			throw new IllegalArgumentException(String.format("vehicle %s not recognized", vid));
		history.get(vid).addFeedback(assumptionid, r, s);
	}

	/**
	 * Determine the reputation rating of a vehicle.
	 * 
	 * @param vid the vehicle id
	 * @return the reputation rating r (0 < r < 1)
	 */
	public double getReputationRating(String vid) {
		if (!history.containsKey(vid))
			throw new IllegalArgumentException(String.format("vehicle %s not recognized", vid));
		return history.get(vid).getReputation();
	}

	@Override
	public String toString() {
		return history.entrySet()
					  .stream()
					  .map(entry -> 
					  	String.format("%s (%f): %s", 
					  			entry.getKey(), entry.getValue().getReputation(), entry.getValue().toString()))
					  .collect(Collectors.joining(System.getProperty("line.separator")));
	}

	private static class InternalMonitoringEvalListener extends RemoteObject implements IMonitoringValidationListener {

		private static final long serialVersionUID = -3045122339587951628L;

		@Override
		public void constraintEvaluated(String vid, String assumptionid, double weight, String message, Result result)
				throws RemoteException {
			// TODO: figure out what whether or not this was a success from the result
			double r;
			double s;
			if (result == Result.MONITORING_CHECK_PASSED) {
				r = weight;
				s = 0.0;
			}
			else {
				r = 0.0;
				s = weight;
			}
			
			try {
				TrustManager.getInstance().constraintEvaluated(vid, assumptionid, r, s);
			} catch (IllegalArgumentException e) {
				LOGGER.warn(e.getMessage());
			}

		}

	}

	public static void main(String[] args) {
		TrustManager mger = TrustManager.getInstance();
		mger.initializeUAV("UAV1");
		mger.constraintEvaluated("UAV1", "A1", 2.0, 0.0);
		mger.constraintEvaluated("UAV1", "A2", 0.0, 1.0);
		mger.constraintEvaluated("UAV1", "A2", 2.0, 0.0);
		System.out.println(mger);
		mger.shutDown();
	}

}
