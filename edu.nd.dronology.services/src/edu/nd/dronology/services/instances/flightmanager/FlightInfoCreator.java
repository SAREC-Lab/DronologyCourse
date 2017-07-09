package edu.nd.dronology.services.instances.flightmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.nd.dronology.core.flight.IFlightPlan;
import edu.nd.dronology.core.flight.PlanPoolManager;
import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;

public class FlightInfoCreator {

	public static FlightInfo createInfo(String uavId) {
		FlightInfo info = new FlightInfo(uavId, uavId);

		IFlightPlan curentFlight = PlanPoolManager.getInstance().getCurrentPlan(uavId);
		if (curentFlight != null) {
			FlightPlanInfo currPl = new FlightPlanInfo(curentFlight.getFlightID(), curentFlight.getFlightID());
			currPl.setWaypoints(curentFlight.getWayPoints());
			info.setCurrentFlight(currPl);

		}

		List<IFlightPlan> pendingPlans = PlanPoolManager.getInstance().getPendingPlans(uavId);
		for (IFlightPlan plan : pendingPlans) {
			FlightPlanInfo pinfo = new FlightPlanInfo(plan.getFlightID(), plan.getFlightID());
			pinfo.setWaypoints(plan.getWayPoints());
			info.addPending(pinfo);
		}
		List<IFlightPlan> completedPlans = PlanPoolManager.getInstance().getCompletedPlans(uavId);
		for (IFlightPlan plan : completedPlans) {
			FlightPlanInfo pinfo = new FlightPlanInfo(plan.getFlightID(), plan.getFlightID());
			pinfo.setWaypoints(plan.getWayPoints());
			info.addCompleted(pinfo);
		}

		return info;
	}

	public static Collection<FlightPlanInfo> getCurrenctFlights() {
		List<IFlightPlan> current = new ArrayList<>(PlanPoolManager.getInstance().getCurrentFlights());
		List<FlightPlanInfo> planInfo = new ArrayList<>();
		for (IFlightPlan plan : current) {
			FlightPlanInfo pinfo = new FlightPlanInfo(plan.getFlightID(), plan.getFlightID());
			pinfo.setWaypoints(plan.getWayPoints());
			planInfo.add(pinfo);
		}
		return planInfo;
	}

}
