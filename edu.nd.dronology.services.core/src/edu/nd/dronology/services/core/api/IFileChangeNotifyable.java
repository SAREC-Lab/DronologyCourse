package edu.nd.dronology.services.core.api;

import java.util.Set;

/**
 * Interface for being notified on changing objects on the server.
 * @author Michael Vierhauser
 *
 */
public interface IFileChangeNotifyable {

/**
 * 
 * @param changed The set of changed item names.
 */
	void notifyFileChange(Set<String> changed);

}
