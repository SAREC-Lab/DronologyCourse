package edu.nd.dronology.ui.cc.main.simulator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;

public class DroneViewerContentProvider implements ITreeContentProvider {

	IDroneSetupRemoteService setupService;
	private IFlightManagerRemoteService flightManagerService;

	{
		try {
			setupService = (IDroneSetupRemoteService) ServiceProvider.getBaseServiceProvider().getRemoteManager()
					.getService(IDroneSetupRemoteService.class);

			flightManagerService = (IFlightManagerRemoteService) ServiceProvider.getBaseServiceProvider()
					.getRemoteManager().getService(IFlightManagerRemoteService.class);

		} catch (RemoteException | DronologyServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Map<String, DroneStatus> drones;
		try {
			drones = setupService.getDrones();
			return drones.values().toArray();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Object[0];

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DroneStatus) {
			ArrayList childElements = new ArrayList<>();
			DroneStatus status = (DroneStatus) parentElement;
			childElements.add(status.getCoordinates());

			try {
				FlightInfo flightInfo = flightManagerService.getFlightInfo(status.getID());
				childElements.add(flightInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return childElements.toArray();
		}
		if (parentElement instanceof FlightInfo) {
			List<Object> childElements = new ArrayList<>();
			FlightInfo status = (FlightInfo) parentElement;
			childElements.add(new FlightListWrapper("Current", status.getCurrentFlights()));
			childElements
					.add(new FlightListWrapper("Pending", status.getPendingFlights().toArray(new FlightPlanInfo[0])));
			childElements.add(
					new FlightListWrapper("Completed", status.getCompletedFlights().toArray(new FlightPlanInfo[0])));
			return childElements.toArray();
		}
		if (parentElement instanceof FlightListWrapper) {
			FlightListWrapper status = (FlightListWrapper) parentElement;
			return status.getPlans().toArray();
		}

		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return getChildren(element).length > 0;
	}

	public class FlightListWrapper {

		private String listType;
		private List<FlightPlanInfo> planList;

		public FlightListWrapper(String listType, FlightPlanInfo... plans) {
			this.listType = listType;
			if (plans == null || (plans.length>0 && plans[0] == null)) {
				this.planList = new ArrayList<>();
			} else {
				this.planList = new ArrayList<>(Arrays.asList(plans));
			}
		}

		public List<FlightPlanInfo> getPlans() {
			return Collections.unmodifiableList(planList);
		}

		public String getType() {
			return listType;
		}

	}

}
