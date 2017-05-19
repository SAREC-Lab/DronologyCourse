package edu.nd.dronology.core.vehicle.commands;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AbstractDroneCommand implements IDroneCommand {

	static final transient Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).setVersion(1.0)
			.serializeSpecialFloatingPointValues().create();

	private final Map<String, Object> data = new HashMap<>();
	private final String type = "command";
	protected final transient Map<String, Object> innerdata = new HashMap<>();

	protected AbstractDroneCommand(String droneId, String commandId) {
		data.put("id", droneId);
		data.put("command", commandId);
		data.put("data", innerdata);

	}

	@Override
	public String toJsonString() {

		return GSON.toJson(this);

		// JSONObject rootObject = new JSONObject();
		//// rootObject.put("type", "command");
		// JSONObject dataObject = new JSONObject();
		// dataObject.put("id", id);
		// dataObject.put("command", command);
		// JSONObject innerDataObject = new JSONObject();
		// innerDataObject.putAll(data);
		// dataObject.put("data", innerDataObject);
		// rootObject.put("data", dataObject);

		// try {
		// StringWriter out = new StringWriter();
		// rootObject.writeJSONString(out);
		// String jsonText = out.toString();
		//
		// sendData(jsonText);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
	}
	// }

}
