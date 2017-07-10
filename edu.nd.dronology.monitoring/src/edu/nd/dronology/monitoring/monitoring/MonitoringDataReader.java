package edu.nd.dronology.monitoring.monitoring;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class MonitoringDataReader {

	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringDataHandler.class);
	private BlockingQueue<String> queue;
	private AtomicBoolean cont = new AtomicBoolean(true);
	private static String filePath = "D:\\dronemonitoring";
	private static String recordingName = "monitoringlog";
	private static DB db;
	private static BTreeMap eventMap;
	private static int eventcount;
	private int eventcounter;
	private long recordinStart;

	private static final String RECORDING_FILENAME = "record.prec";

	private static final String RECORDING_FILENAME_P = "record.prec.p";
	private static final String RECORDING_FILENAME_T = "record.prec.t";

	public static void main(String[] args) {
		try {
			initFile();

			Set<Entry<Object, Object>> dataset = eventMap.entrySet();

			for (Entry<Object, Object> d : dataset) {
				System.out.println(d.getKey() + " : " + d.getValue());

				BTreeMap<Object, Object> eventmap = db.getTreeMap(d.getValue().toString());
				System.out.println(eventmap.size());

				for (Entry<Object, Object> s : eventmap.entrySet()) {
					System.out.println(s.getKey() + " : " + s.getValue());
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void initFile() throws Exception {
		try {
			LOGGER.info("Recording Root '" + filePath + "'");

			File root = new File(filePath);
			File folder = new File(root, recordingName);
			File dbFile = new File(folder, RECORDING_FILENAME);

			// db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown().make();
			// db =
			// DBMaker.newFileDB(dbFile).transactionDisable().compressionEnable().asyncWriteEnable().executorEnable()
			// .make();
			db = DBMaker.newFileDB(dbFile).transactionDisable().asyncWriteEnable().executorEnable().make(); // TODO
																											// enable
																											// compression!

			eventMap = db.getTreeMap("events");
			eventcount = 0;
			LOGGER.info("EVENTS RECORDED: " + eventMap.values().size());

		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}
