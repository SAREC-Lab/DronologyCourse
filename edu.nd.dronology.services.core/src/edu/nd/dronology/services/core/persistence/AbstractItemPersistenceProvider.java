package edu.nd.dronology.services.core.persistence;

import java.io.File
;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import edu.nd.dronology.util.NullUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Abstract base class for all item factories.<br>
 * Provides methods for read/save of items
 * 
 * @author Michael Vierhauser
 * 
 * @param <ITEM>
 *          The interface of the persisted object
 */
public abstract class AbstractItemPersistenceProvider<ITEM> {

	/**
	 * The persistor
	 */
	protected IPersistenceManager<ITEM> PERSISTOR;
	private static final ILogger LOGGER = LoggerProvider.getLogger(AbstractItemPersistenceProvider.class);

	/**
	 * Default constructor
	 */
	public AbstractItemPersistenceProvider() {
		initPersistor();
	}

	public AbstractItemPersistenceProvider(String type) {
		initPersistor(type);
	}

	/**
	 * needs to be implemented in subclasses - initialized the used persistor
	 */
	protected abstract void initPersistor();
	
	protected abstract void initPersistor(String type);

	/**
	 * 
	 * @return a singleton instance of the persistor
	 */
	public static AbstractItemPersistenceProvider<?> getInstance() {
		return null;
	}

	/**
	 * Reads the item represented by resource
	 * 
	 * @param resource
	 * @return either the item represented by resource or an exception.
	 * @throws PersistenceException
	 */
	public ITEM loadItem(URL resource) throws PersistenceException {
		NullUtil.checkNull(resource);
		try {
			return loadItem(resource.openStream());
		} catch (IOException e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Reads the item stored at the given path
	 * 
	 * @param path
	 * @return either the item located at the path or an exception.
	 * @throws PersistenceException
	 */
	public ITEM loadItem(String path) throws PersistenceException {
		NullUtil.checkNull(path);
		File f = new File(path);
		return loadItem(f);
	}
	
	public ITEM loadItem(File file)throws PersistenceException {
		FileInputStream fin;

		try {
			fin = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new PersistenceException(e);
		}
		return loadItem(fin);
	}

	/**
	 * Reads the item represented by the content
	 * 
	 * @param content
	 * @return either the item located by the content or an exception.
	 * @throws PersistenceException
	 */
	public ITEM loadItem(InputStream content) throws PersistenceException {
		NullUtil.checkNull(content);
		ITEM model = null;
		try {
			model = PERSISTOR.open(content);
		} catch (Exception e) {
			throw new PersistenceException(e);
		} finally {
			try {
				if (content != null) {
					content.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
		return model;
	}

	/**
	 * 
	 * @param model
	 *          The model object to save
	 * @param out
	 *          The ountput stream where to write the object.
	 * @return true if saving was successful.
	 * @throws PersistenceException
	 */
	public boolean saveItem(ITEM model, OutputStream out) throws PersistenceException {
		NullUtil.checkNull(model, out);
		boolean success = PERSISTOR.save(model, out);
		return success;
	}

	/**
	 * 
	 * @param item
	 *          The item object to save
	 * @param path
	 *          The path where to save the object.
	 * @return true if saving was successful.
	 * @throws PersistenceException
	 */
	public boolean saveItem(ITEM item, String path) throws PersistenceException {
		NullUtil.checkNull(item, path);
		File file = new File(path);
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(file);
			boolean success = saveItem(item, fout);
			return success;
		} catch (FileNotFoundException e) {
			throw new PersistenceException(e);
		}
	}

}
