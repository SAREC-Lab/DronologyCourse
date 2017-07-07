package edu.nd.dronology.services.instances.flightroute;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.nd.dronology.core.util.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.api.IFileChangeNotifyable;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.FlightRouteCategoryInfo;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.util.DronologyConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.instances.DronologyElementFactory;
import edu.nd.dronology.services.supervisor.SupervisorService;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FlightRouteplanningServiceInstance extends AbstractFileTransmitServiceInstance<FlightRouteInfo>
		implements IFileChangeNotifyable, IFlightRouteplanningServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightRouteplanningServiceInstance.class);

	private static final int ORDER = 2;

	public static final String EXTENSION = DronologyConstants.EXTENSION_FLIGHTROUTE;

	private Map<String, FlightRouteInfo> flightPaths = new Hashtable<>();

	private Collection<FlightRouteCategoryInfo> categories = new ArrayList<>();

	public FlightRouteplanningServiceInstance() {
		super(ServiceIds.SERVICE_FLIGHROUTE, "Routeplanning Management", EXTENSION);

		categories.add(new FlightRouteCategoryInfo("South-Bend Area", "sba"));
		categories.add(new FlightRouteCategoryInfo("River", "river"));
		categories.add(new FlightRouteCategoryInfo("Default", "Default"));
	}

	@Override
	protected Class<?> getServiceClass() {
		return FlightRouteplanningService.class;
	}

	@Override
	protected int getOrder() {
		return ORDER;
	}

	@Override
	protected String getPropertyPath() {
		return null;
	}

	@Override
	protected void doStartService() throws Exception {
		reloadItems();
	}

	@Override
	protected void doStopService() throws Exception {
		fileManager.tearDown();
	}

	@Override
	public ServiceInfo getServiceInfo() {
		ServiceInfo sInfo = super.getServiceInfo();
		sInfo.addAttribute(ServiceInfo.ATTRIBUTE_TYPE, ServiceInfo.ATTRIBUTE_FILE);
		return sInfo;
	}

	@Override
	public FlightRouteInfo createItem() throws DronologyServiceException {
		FlightRoutePersistenceProvider persistor = FlightRoutePersistenceProvider.getInstance();
		IFlightRoute flightRoute = DronologyElementFactory.createNewFlightPath();
		flightRoute.setName("New-FlightRoute");
		String savePath = FileUtil.concat(storagePath, flightRoute.getId(), EXTENSION);

		try {
			persistor.saveItem(flightRoute, savePath);
		} catch (PersistenceException e) {
			throw new DronologyServiceException("Error when creating flight route: " + e.getMessage());
		}
		return new FlightRouteInfo(flightRoute.getName(), flightRoute.getId());
	}

	@Override
	protected String getPath() {
		String path = SupervisorService.getInstance().getFlightPathLocation();
		return path;
	}

	@Override
	protected FlightRouteInfo fromFile(String id, File file) throws Throwable {
		IFlightRoute atm = FlightRoutePersistenceProvider.getInstance().loadItem(file.toURI().toURL());
		FlightRouteInfo info = new FlightRouteInfo(atm.getName(), id);
		info.setCategory(atm.getCategory());
		for (Waypoint waypoint : atm.getWaypoints()) {
			info.addWaypoint(waypoint);
		}

		return info;
	}

	@Override
	protected boolean hasProperties() {
		return false;
	}

	@Override
	public void notifyFileChange(Set<String> changed) {
		super.notifyFileChange(changed);
		for (String s : changed) {
			String id = s.replace("." + extension, "");
			if (!itemmap.containsKey(id)) {
				HashSet<Entry<String, FlightRouteInfo>> allEntries = new HashSet(flightPaths.entrySet());
				for (Entry<String, FlightRouteInfo> e : allEntries) {
					if (e.getValue().getId().equals(changed)) {
						flightPaths.remove(e.getKey());
					}
				}
			}
		}
	}

	@Override
	public Collection<FlightRouteCategoryInfo> getFlightPathCategories() {
		return Collections.unmodifiableCollection(categories);
	}

	@Override
	public FlightRouteInfo getItem(String name) throws DronologyServiceException {
		for (FlightRouteInfo item : itemmap.values()) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		throw new DronologyServiceException("Flightpath '" + name + "' not found");
	}
}
