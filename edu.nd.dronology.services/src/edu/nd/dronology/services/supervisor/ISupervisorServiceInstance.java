package edu.nd.dronology.services.supervisor;

import java.util.Map;

import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.util.DronologyServiceException;


/**
 * 
 * 
 * @author Michael Vierhauser
 * 
 */
public interface ISupervisorServiceInstance extends IServiceInstance {

	void shutdownServer();

	void restartAllServices();


	String getWorkspaceLocation();

	Map<String, String> getGlobalProperties();

	boolean importItem(String fileName, byte[] byteArray, boolean overwrite) throws DronologyServiceException;

	

	String getFlightPathLocation();

	String getDroneSpecificationLocation();

	String getSimScenarioLocation();



}
