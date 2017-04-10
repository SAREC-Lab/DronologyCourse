
package edu.nd.dronology.services.core.persistence.apache;

import java.io.InputStream;
import java.io.OutputStream;

public interface IPersistenceManager<T> {
	
	public T open(InputStream fin) throws PersistenceException;

	public boolean save(T o, OutputStream fout) throws PersistenceException;
}
