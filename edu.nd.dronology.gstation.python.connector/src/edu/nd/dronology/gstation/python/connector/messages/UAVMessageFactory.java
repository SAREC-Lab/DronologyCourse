package edu.nd.dronology.gstation.python.connector.messages;

import java.text.DateFormat;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.core.vehicle.commands.IDroneCommand;
import edu.nd.dronology.gstation.python.connector.GroundStationException;

public class UAVMessageFactory {

	private static final String MESSAGE_TYPE = "type";

	public static final transient Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
			.setVersion(1.0).serializeSpecialFloatingPointValues().create();

	static final Gson TYPE_GSON = new GsonBuilder().serializeNulls().setDateFormat(DateFormat.LONG)
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).setVersion(1.0)
			.serializeSpecialFloatingPointValues().registerTypeAdapter(Map.class, new StateMessageTypeAdapter())
			.create();

	public static AbstractUAVMessage<?> create(String messagestring) throws Exception {

		JSONObject messageObject = new JSONObject(messagestring);
		String messagetype = messageObject.optString(MESSAGE_TYPE);

		if (messagetype == null || StringUtils.isEmpty(messagetype)) {
			throw new GroundStationException("Message Type of received message was null!");
		}
		AbstractUAVMessage<?> message = null;
		switch (messagetype) {
		case UAVStateMessage.MESSAGE_TYPE: {
			message = TYPE_GSON.fromJson(messagestring, UAVStateMessage.class);
			message.timestamp();
			return message;
		}

		case UAVHandshakeMessage.MESSAGE_TYPE: {
			message = GSON.fromJson(messagestring, UAVHandshakeMessage.class);
			message.timestamp();
			return message;
		}
		case UAVMonitoringMessage.MESSAGE_TYPE: {
			message = GSON.fromJson(messagestring, UAVMonitoringMessage.class);
			message.timestamp();
			return message;
		}
		case ConnectionRequestMessage.MESSAGE_TYPE: {
			message = GSON.fromJson(messagestring, ConnectionRequestMessage.class);
			message.timestamp();
			return message;
		}
		default:
			throw new GroundStationException("Unknown Message Type! '" + messagetype + "'");
		}
	}

}
