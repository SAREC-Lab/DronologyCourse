package edu.nd.dronology.monitoring.trust;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import edu.nd.dronology.monitoring.service.DroneSafetyService;
import edu.nd.dronology.monitoring.service.IMonitoringValidationListener;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class TrustManager implements IMonitoringValidationListener {
	private static final ILogger LOGGER = LoggerProvider.getLogger(TrustManager.class);
	private static volatile TrustManager INSTANCE = null;

	public TrustManager() {

	}

	/**
	 * The singleton instance.
	 */

	public static TrustManager getInstance() {

		if (INSTANCE == null) {
			synchronized (TrustManager.class) {
				if (INSTANCE == null) {
					INSTANCE = new TrustManager();
				}
			}
		}
		return INSTANCE;
	}

	public void initialize() {
		DroneSafetyService.getInstance().addValidationListener(new InternalMonitoringEvalListener());

	}

	public void initializeUAV(String uavid) {
		LOGGER.info("new uav initialized: " + uavid);
	}

	private static class InternalMonitoringEvalListener extends RemoteObject implements IMonitoringValidationListener {
		/**
			 * 
			 */
		private static final long serialVersionUID = -3045122339587951628L;

		@Override
		public void constraintEvaluated(String uavid, String assumptionid, String message) throws RemoteException {
			LOGGER.info("New Eval result:" + uavid + ": " + assumptionid + " --> " + message);

		}
	}

	@Override
	public void constraintEvaluated(String uavid, String assumptionid, String message) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
