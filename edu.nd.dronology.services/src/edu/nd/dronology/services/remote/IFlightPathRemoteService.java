package edu.nd.dronology.services.remote;

import edu.nd.dronology.services.info.FlightPathInfo;
/**
 * Meta-Model Service Interface: Handling artifact  models.<br>
 * Extends {@link IFileTransmitRemoteService} and provides methods for retrieving and saving models from the server.
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IFlightPathRemoteService extends IRemoteableService, IFileTransmitRemoteService<FlightPathInfo> {

	

}
