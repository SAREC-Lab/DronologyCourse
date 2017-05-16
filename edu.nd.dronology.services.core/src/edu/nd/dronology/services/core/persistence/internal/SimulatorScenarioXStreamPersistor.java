package edu.nd.dronology.services.core.persistence.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.services.core.persistence.DronologyPersistenceUtil;
import edu.nd.dronology.services.core.persistence.IPersistenceManager;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class SimulatorScenarioXStreamPersistor implements IPersistenceManager<ISimulatorScenario> {

	private static final XStream xstream = new XStream(new XppDriver());
	private static ILogger LOGGER = LoggerProvider.getLogger(SimulatorScenarioXStreamPersistor.class);

	static final boolean useCompression = false;

	@Override
	public ISimulatorScenario open(InputStream fin) throws PersistenceException {
		DronologyPersistenceUtil.preprocessStream(xstream);
	//	 xstream.setMode(XStream.);
		// xstream.addImplicitCollection(ImplMMEArtifactType.class, "children");
		
		if (useCompression) {
			return loadedCompressedStream(fin);
		} else {
			return loadUncompressedStream(fin);
		}
	}

	private ISimulatorScenario loadUncompressedStream(InputStream fin) throws PersistenceException {
		try {
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(fin), Charset.forName("UTF-8"));
			Object model = xstream.fromXML(reader);
			return (ISimulatorScenario) model;
		} catch (Exception e) {
			throw new PersistenceException(e);
		} finally {
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}

	private ISimulatorScenario loadedCompressedStream(InputStream fin) throws PersistenceException {
		InputStream zIn = null;
		try {
			zIn = new GZIPInputStream(fin);
			Object model = xstream.fromXML(new BufferedReader(new InputStreamReader(zIn, "UTF-8")));

			return (ISimulatorScenario) model;
		} catch (IOException e) {
			throw new PersistenceException(e);
		} finally {
			try {
				if (zIn != null) {
					zIn.close();
				}
				if (fin != null) {
					fin.close();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}

		}
	}
 
	@Override
	public boolean save(ISimulatorScenario o, OutputStream fout) throws PersistenceException {
		DronologyPersistenceUtil.preprocessStream(xstream);
		// xstream.setMode(XStream.ID_REFERENCES);
		// xstream.addImplicitCollection(ImplMMEArtifactType.class, "children");
		// xstream.addImplicitMap(ImplMEEvent.class, "relatedEvents", ImplMEEvent.class, "relatedEvents");

		if (useCompression) {
			return saveCompressedStream(o, fout);
		} else {
			return saveUncompressedStream(o, fout);
		}

	}

	private boolean saveUncompressedStream(ISimulatorScenario o, OutputStream fout) throws PersistenceException {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(fout), Charset.forName("UTF-8"));
			xstream.toXML(o, writer);
			return true;
		} catch (Exception e) {
			throw new PersistenceException(e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}

	}

	private boolean saveCompressedStream(ISimulatorScenario o, OutputStream fout) throws PersistenceException {
		try {
			GZIPOutputStream zOut = new GZIPOutputStream(fout);
			xstream.toXML(o, new BufferedWriter(new OutputStreamWriter(zOut, "UTF-8")));

			zOut.close();
			fout.close();

			return true;
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

}
