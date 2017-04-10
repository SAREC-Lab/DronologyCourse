package edu.nd.dronology.services.instances.flightpath;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.nd.dronology.services.core.api.IFileChangeNotifyable;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServiceInstance;
import edu.nd.dronology.services.core.persistence.apache.PersistenceException;
import edu.nd.dronology.services.core.util.DistributorConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.info.FlightPathInfo;
import edu.nd.dronology.services.instances.DronologyElementFactory;
import edu.nd.dronology.services.persistence.FlightPlanPersistenceProvider;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FlightPathServiceInstance extends AbstractFileTransmitServiceInstance<FlightPathInfo> implements
		IFileChangeNotifyable, IFlightPathServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightPathServiceInstance.class);

	private static final int ORDER = 2;

	public static final String EXTENSION = DistributorConstants.EXTENSION_FLIGHTPATH;

	private Map<String, FlightPathInfo> flightPaths = new Hashtable<>();

	public FlightPathServiceInstance() {
		super(ServiceIds.SERVICE_ARTIFACTS, "FlightPath Management", EXTENSION);

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
		FlightPlanPersistenceProvider persistor = FlightPlanPersistenceProvider.getInstance();
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
		//String path = SupervisorService.getInstance().getArtifactModelsLocation();
		String path ="flightpath";
		return path;
	}

	@Override
	protected FlightPathInfo fromFile(String id, File file) throws Throwable {
		IFlightPath atm = FlightPlanPersistenceProvider.getInstance().loadItem(file.toURI().toURL());
		FlightPathInfo info = new FlightPathInfo(atm.getName(), id);

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
}
