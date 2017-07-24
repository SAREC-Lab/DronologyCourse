package edu.nd.dronology.monitoring.monitoring;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DB.BTreeMapMaker;
import org.mapdb.DBMaker;

import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import edu.nd.dronology.monitoring.validation.MonitoringValidator;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class MonitoringDataHandler implements Runnable {

	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringDataHandler.class);
	private BlockingQueue<UAVMonitoringMessage> queue;
	private AtomicBoolean cont = new AtomicBoolean(true);
	private String filePath = "D:\\dronemonitoring";
	private String recordingName = "monitoringlog";
	private DB db;
	private BTreeMap eventMap;
	private int eventcounter;
	private long recordinStart;

	private static final String RECORDING_FILENAME = "record.prec";

	private static final String RECORDING_FILENAME_P = "record.prec.p";
	private static final String RECORDING_FILENAME_T = "record.prec.t";

	public MonitoringDataHandler(final BlockingQueue<UAVMonitoringMessage> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			// initFile();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// TODO Auto-generated method stub

		while (cont.get()) {

			try {
				UAVMonitoringMessage monitoringMesasge = queue.take();
				//LOGGER.info("MONITORING MESSAGE RECEIVED"+ monitoringMesasge.toString());
				MonitoringValidator validator = UAVMonitoringManager.getInstance()
						.getValidator(monitoringMesasge.getUavid());
				if (validator != null) {
					validator.validate(monitoringMesasge);
				}
				else {
					LOGGER.error("No validator found for "+ monitoringMesasge.getUavid());
				}

			} catch (Exception e) {
				LOGGER.error(e);
			}

		}
	}

	private void extractParameters(String monitoringMesasge) {

		JSONObject obj;
		try {
			obj = new JSONObject(monitoringMesasge);
			String id = obj.getString("id");

			String logid = UUID.randomUUID().toString();

			// JSONObject dataArray = obj.getJSONObject("data");
			// System.out.println(monitoringMesasge);
			//
			JSONObject dataArray = obj.optJSONObject("battery");

			// Object elem = dataArray.get(0);
			// if (elem != null) {
			// System.out.println(elem.getClass());
			// }

			BTreeMapMaker map = db.createTreeMap(logid);

			BTreeMap<String, String> m = map.makeOrGet();
			eventMap.put(new Integer(eventcounter++).toString(), logid);
			m.put("DUMMY", "ABC");
			if (dataArray != null) {
				// System.out.println(id + " BATTERY is: " +
				// dataArray.get("level"));
				m.put("BATTERY", dataArray.get("level").toString());
			}

			db.commit(); // persist changes into disk

		} catch (

		JSONException e) {
			LOGGER.error(e);
		}

	}

	private void initFile() throws Exception {
		LOGGER.info("Recording Root '" + filePath + "'");
		File root = new File(filePath);
		File folder = new File(root, recordingName);
		if (folder.exists()) {
			// folder.delete();
			// throw new Exception("Recording '" + recordingName + "' already
			// existing");
		}
		folder.mkdirs();
		File dbFile = new File(folder, RECORDING_FILENAME);
		if (dbFile.exists()) {
			dbFile.delete();
		}

		db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown().make();
		eventMap = db.createTreeMap("events").makeStringMap();
		eventcounter = 0;
		LOGGER.info("New recording scheduled '" + recordingName + "'");
		recordinStart = System.currentTimeMillis();

	}

}
