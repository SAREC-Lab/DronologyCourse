package edu.nd.dronology.core.mission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MissionPlan implements IMissionPlan {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5704550391713503487L;
	private String name;
	private String id;
	private boolean synchronizedRoutes = false;
	private List<RouteSet> routeSets = new ArrayList<>();

	public MissionPlan() {
		id = UUID.randomUUID().toString();
		name = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRouteSet(RouteSet routeSet) {
		routeSets.add(routeSet);
	}

	public static class RouteSet implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1006883164987214757L;
		private List<UavRoutePair> uav2routeMapping = new ArrayList();
		private int executionDelay;

		public int getExecutionDelay() {
			return executionDelay;
		}

		public void addPan(String uavid, String routeid) {
			uav2routeMapping.add(new UavRoutePair(uavid, routeid));
		}

		public void setExecutionDelay(int executionDelay) {
			this.executionDelay = executionDelay;
		}

		public List<UavRoutePair> getUav2routeMappings() {
			return Collections.unmodifiableList(uav2routeMapping);
		}

	}

	public static class UavRoutePair implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2852859691271746076L;
		String uavid;
		String routeid;

		public UavRoutePair(String uavid, String routeid) {
			this.uavid = uavid;
			this.routeid = routeid;
		}

		public String getRouteid() {
			return routeid;
		}

		public void setRouteid(String routeid) {
			this.routeid = routeid;
		}

		public String getUavid() {
			return uavid;
		}

		public void setUavid(String uavid) {
			this.uavid = uavid;
		}

	}

	@Override
	public List<RouteSet> getRouteSets() {
		return Collections.unmodifiableList(routeSets);
	}

}
