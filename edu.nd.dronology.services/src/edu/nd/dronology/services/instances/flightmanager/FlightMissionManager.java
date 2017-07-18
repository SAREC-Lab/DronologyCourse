package edu.nd.dronology.services.instances.flightmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.nd.dronology.core.exceptions.DroneException;
import edu.nd.dronology.core.mission.IMissionPlan;
import edu.nd.dronology.core.mission.MissionPlan.RouteSet;
import edu.nd.dronology.core.mission.MissionPlan.UavRoutePair;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.instances.flightroute.FlightRouteplanningService;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FlightMissionManager {

	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightMissionManager.class);
	private Timer timer = new Timer();

	public FlightMissionManager() {
	}

	public void planMission(IMissionPlan missionPlan) {
		LOGGER.info("New missionplan scheduled: " + missionPlan.getId());
		for (RouteSet rs : missionPlan.getRouteSets()) {
			timer.schedule(new DelayedMissionExecutionTask(rs), rs.getExecutionDelay());
		}

	}

	public class DelayedMissionExecutionTask extends TimerTask {

		private RouteSet routeset;

		public DelayedMissionExecutionTask(RouteSet routeset) {
			this.routeset = routeset;
		}

		@Override
		public void run() {
			List<String> assignedRoutes = new ArrayList<>();
			for (UavRoutePair pair : routeset.getUav2routeMappings()) {
				FlightRouteInfo route;
				try {
					if(assignedRoutes.contains(pair.getRouteid())) {
						throw new DroneException("Route '"+pair.getRouteid()+"' already assigned!");
					}
					route = FlightRouteplanningService.getInstance().getItem(pair.getRouteid());
					FlightManagerService.getInstance().planFlight(pair.getUavid(), route.getName(),
							route.getWaypoints());
					assignedRoutes.add(pair.getRouteid());
				} catch (Exception e) {
					LOGGER.error(e);
				}

			}

		}

	}

}
