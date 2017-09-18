package edu.nd.dronology.gstation.python.connector.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Random;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.core.vehicle.commands.ConnectionResponseCommand;
import edu.nd.dronology.gstation.python.connector.messages.ConnectionRequestMessage;
import edu.nd.dronology.gstation.python.connector.messages.UAVMonitoringMessage;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FakePassivPythonGroundstation2 {

	// private static Socket socket;
	private static final ILogger LOGGER = LoggerProvider.getLogger(FakePassivPythonGroundstation2.class);

	final static Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
			.setVersion(1.0).serializeSpecialFloatingPointValues().create();

	public static void main(String[] args) {
		try {
			int port = 1234;
			// ServerSocket serverSocket = new ServerSocket(port);
			// Server is running always. This is done using this while(true) loop

			// socket = serverSocket.accept();
			String hostAddr = "localhost";

			LOGGER.info("Connecting to Python base " + hostAddr + "@" + port);
			Socket pythonSocket = new Socket();
			pythonSocket.connect(new InetSocketAddress(hostAddr, port), 5000);

			System.out.println("Client has connected!");
			InputStream is = pythonSocket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			// UAVHandshakeMessage handshake = new UAVHandshakeMessage("Drone1", "Drone1");
			ConnectionRequestMessage connect = new ConnectionRequestMessage("FAKE_GROUND_2");

			String handshakeString = GSON.toJson(connect);
			Thread.sleep(10000);
			OutputStream os = pythonSocket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(handshakeString);
			bw.write("\n");
			System.out.println("Message sent to the client is " + handshakeString);
			bw.flush();

			String ackMesasge = br.readLine();
			ConnectionResponseCommand response = GSON.fromJson(ackMesasge, ConnectionResponseCommand.class);
			System.out.println("RESPONSE:" + response.toJsonString());
			Thread.sleep(10000);

			int i = 2;
			while (i > 1) {

				UAVMonitoringMessage mm = new UAVMonitoringMessage("Dronexxx", "Dronexxx");
				Random rand = new Random();
				mm.setType(UAVMonitoringMessage.MESSAGE_TYPE);
				// mm.setuavid("DRONE1");
				mm.addPropery("NR_SATELITES", "5");
				mm.addPropery("GPS_BIAS", "3.125");
				mm.addPropery("CURRENT_SPEED", "5.25");
				mm.addPropery("BLEVEL_VOLTAGE", "3");
				mm.addPropery("BATTERY_MAXPOWERX", "50");
				mm.addPropery("BATTERY_VOLTAGE", rand.nextInt(10));
				mm.addPropery("BATTERY_POWER", rand.nextInt(10));
				mm.addPropery("BLEVEL_POWER", rand.nextInt(10));

				// if flying mission mlevel > 20%
				// if retunr home blvel > 10;

				String toSend = GSON.toJson(mm);

				// Reading the message from the client

				// String number = br.readLine();
				// System.out.println("Message received from client is " + number);

				// Multiplying the number by 2 and forming the return message
				// String returnMessage;
				// try {
				// int numberInIntFormat = Integer.parseInt(number);
				// int returnValue = numberInIntFormat * 2;
				// returnMessage = String.valueOf(returnValue) + "\n";
				// } catch (NumberFormatException e) {
				// // Input was not a number. Sending proper message back to client.
				// returnMessage = "Please send a proper number\n";
				// }

				// Sending the response back to the client.
				// OutputStream os = socket.getOutputStream();
				// OutputStreamWriter osw = new OutputStreamWriter(os);
				// BufferedWriter bw = new BufferedWriter(osw);
				bw.write(toSend);
				bw.write("\n");
				System.out.println("Message sent to the client is " + toSend);
				bw.flush();

				Thread.sleep(5000);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}