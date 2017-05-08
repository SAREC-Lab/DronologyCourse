package edu.nd.dronology.services.instances.flightpath;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.nd.dronology.core.utilities.Coordinates;
import edu.nd.dronology.services.core.api.IFileChangeNotifyable;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.FlightPathCategoryInfo;
import edu.nd.dronology.services.core.info.FlightPathInfo;
import edu.nd.dronology.services.core.items.IFlightPath;
import edu.nd.dronology.services.core.persistence.FlightPathPersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.util.DronologyConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.instances.DronologyElementFactory;
import edu.nd.dronology.services.supervisor.SupervisorService;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FlightPathServiceInstance extends AbstractFileTransmitServiceInstance<FlightPathInfo> implements
		IFileChangeNotifyable, IFlightPathServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightPathServiceInstance.class);

	private static final int ORDER = 2;

	public static final String EXTENSION = DronologyConstants.EXTENSION_FLIGHTPATH;

	private Map<String, FlightPathInfo> flightPaths = new Hashtable<>();

	private Collection<FlightPathCategoryInfo> categories = new ArrayList<>();

	public FlightPathServiceInstance() {
		super(ServiceIds.SERVICE_FLIGHTPATH, "FlightPath Management", EXTENSION);
		
		categories.add(new FlightPathCategoryInfo("South-Bend Area", "sba"));
		categories.add(new FlightPathCategoryInfo("River", "river"));
		categories.add(new FlightPathCategoryInfo("Default", "Default"));
	}

	@Override
	protected Class<?> getServiceClass() {
		return FlightPathService.class;
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
	public FlightPathInfo createItem() throws DronologyServiceException {
		FlightPathPersistenceProvider persistor = FlightPathPersistenceProvider.getInstance();
		IFlightPath flightPath = DronologyElementFactory.createNewFlightPath();
		flightPath.setName("New-FlightPath");
		String savePath = FileUtil.concat(storagePath, flightPath.getId(), EXTENSION);

		try {
			persistor.saveItem(flightPath, savePath);
		} catch (PersistenceException e) {
			throw new DronologyServiceException("Error when creating flightpath: " + e.getMessage());
		}
		return new FlightPathInfo(flightPath.getName(), flightPath.getId());
	}

	@Override
	protected String getPath() {
		String path = SupervisorService.getInstance().getFlightPathLocation();
		return path;
	}

	@Override
	protected FlightPathInfo fromFile(String id, File file) throws Throwable {
		IFlightPath atm = FlightPathPersistenceProvider.getInstance().loadItem(file.toURI().toURL());
		FlightPathInfo info = new FlightPathInfo(atm.getName(), id);
		info.setCategory(atm.getCategory());
		for(Coordinates c: atm.getCoordinates()){
			info.addCoordinate(c);
		}
		
//		Set<String> elementids = new HashSet<>();
//		for (ISAMArtifactMapping mapping : atm.getArtifactMappings()) {
//			for (IMappedItem itm : mapping.getMappedItems()) {
//				elementids.add(itm.getItemId());
//			}
//
//			MappingInfo mpInfo = new MappingInfo(mapping.getName(), elementids);
//			info.addMappingInfo(mpInfo);
//		}
//		if (atm.getRMMId() != null) {
//			rmmMapping.put(atm.getRMMId(), info);
//		}

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
				HashSet<Entry<String, FlightPathInfo>> allEntries = new HashSet(flightPaths.entrySet());
				for (Entry<String, FlightPathInfo> e : allEntries) {
					if (e.getValue().getId().equals(changed)) {
						flightPaths.remove(e.getKey());
					}
				}
			}
		}
	}

	@Override
	public Collection<FlightPathCategoryInfo> getFlightPathCategories() {
		return Collections.unmodifiableCollection(categories );
	}
}
