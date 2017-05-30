package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.services.core.info.SimulatorScenarioCategoryInfo;
import edu.nd.dronology.services.core.info.SimulatorScenarioInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;
/**
 * Meta-Model Service Interface: Handling artifact  models.<br>
 * Extends {@link IFileTransmitRemoteService} and provides methods for retrieving and saving models from the server.
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IDroneSimulatorRemoteService extends IRemoteableService, IFileTransmitRemoteService<SimulatorScenarioInfo> {

	
	public void activateScenario(SimulatorScenarioInfo scenario) throws RemoteException, DronologyServiceException;
	
	public Collection<SimulatorScenarioCategoryInfo> getCategories() throws RemoteException;
	
}
