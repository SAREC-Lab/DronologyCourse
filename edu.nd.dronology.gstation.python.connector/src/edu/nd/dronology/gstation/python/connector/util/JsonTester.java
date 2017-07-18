package edu.nd.dronology.gstation.python.connector.util;

import java.text.DateFormat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.vehicle.commands.GoToCommand;
import edu.nd.dronology.core.vehicle.commands.SetModeCommand;
import edu.nd.dronology.core.vehicle.commands.TakeoffCommand;
import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;

public class JsonTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
		// System.out.println(new TakeoffCommand("1", 20).toJsonString());
		// System.out.println(new SetModeCommand("1", "LAND").toJsonString());
		// System.out.println(new GoToCommand("1", new LlaCoordinate(41.732957,
		// -86.180883, 20)).toJsonString());
		// System.out.println(new TakeoffCommand("1", 20).toJsonString());

		final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
				.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
				.setVersion(1.0).serializeSpecialFloatingPointValues().create();

		UAVMonitoringMessage mm = new UAVMonitoringMessage("Drone1", "Drone1");

		mm.setType("MonitoringMessage");
		// mm.setuavid("DRONE1");
		mm.addPropery("NR_SATELITES", "5");
		mm.addPropery("GPS_BIAS", "3.125");
		mm.addPropery("CURRENT_SPEED", "5.25");

		System.out.println(GSON.toJson(mm));

	}

}
