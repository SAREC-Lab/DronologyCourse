package edu.nd.dronology.services.launch;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.info.DroneInitializationInfo.DroneMode;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class FlyingFieldDemoScript {

	private static final String ADDRESS_SCHEME = "rmi://%s:%s/Remote";

	public static void main(String[] args) {
		// try {
		// Flying Field
		// LlaCoordinate cord1 = new LlaCoordinate(41.519400, -86.239127, 0);
		// LlaCoordinate cord2 = new LlaCoordinate(41.519400, -86.239527, 0);
		// LlaCoordinate cord3 = new LlaCoordinate(41.519400, -86.239927, 0);
		//
		// IRemoteManager manager = (IRemoteManager) Naming.lookup(String.format(ADDRESS_SCHEME, "localhost", 9898));
		//
		// IDroneSetupRemoteService service = (IDroneSetupRemoteService) manager.getService(IDroneSetupRemoteService.class);
		//
		// DroneInitializationInfo drone1 = new DroneInitializationInfo("Sim-Drone1", "IRIS+",DroneMode.MODE_VIRTUAL, cord1);
		// DroneInitializationInfo drone2 = new DroneInitializationInfo("Sim-Drone2", "IRIS+", cord2);
		// DroneInitializationInfo drone3 = new DroneInitializationInfo("Sim-Drone3", "IRIS+", cord3);
		//
		// service.initializeDrones(drone1);
		// service.initializeDrones(drone2);
		// service.initializeDrones(drone3);
		// } catch (RemoteException | DronologyServiceException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (NotBoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
