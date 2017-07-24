package edu.nd.dronology.gstation.python.connector.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.gstation.python.connector.messages.StateMessageTypeAdapter;
import edu.nd.dronology.gstation.python.connector.messages.UAVStateMessage;

public class JsonTester2 {

	static final Gson GSON = new GsonBuilder().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
			.setVersion(1.0).serializeSpecialFloatingPointValues().registerTypeAdapter(Map.class, new StateMessageTypeAdapter()).create();

	public static void main(String[] args) {
		try {
			String string = FileUtils.readFileToString(new File("D:\\state.txt"));

			UAVStateMessage message = GSON.fromJson(string, UAVStateMessage.class);
		
			
			System.out.println(message.getLocation());
			System.out.println(message.getGroundspeed());
			System.out.println(message.getUavid());
			System.out.println(message.getAttitude());
			System.out.println(message.getBatterystatus());
			System.out.println(message.getMode());
			System.out.println(message.getStatus());
			System.out.println(message.getVelocity());
			System.out.println(message.getLocation());
			System.out.println(message.getLocation());
			System.out.println(message.getLocation());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
