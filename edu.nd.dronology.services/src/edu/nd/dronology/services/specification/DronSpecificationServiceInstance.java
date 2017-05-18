package edu.nd.dronology.services.specification;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.nd.dronology.services.core.api.IFileChangeNotifyable;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.DroneSpecificationInfo;
import edu.nd.dronology.services.core.info.TypeSpecificationInfo;
import edu.nd.dronology.services.core.items.IDroneSpecification;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.DroneSpecificationPersistenceProvider;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.util.DronologyConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.instances.DronologyElementFactory;
import edu.nd.dronology.services.instances.flightroute.FlightRouteplanningService;
import edu.nd.dronology.services.supervisor.SupervisorService;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DronSpecificationServiceInstance extends AbstractFileTransmitServiceInstance<DroneSpecificationInfo>
		implements IFileChangeNotifyable, IDroneSpecificationServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DronSpecificationServiceInstance.class);

	private static final int ORDER = 2;

	public static final String EXTENSION = DronologyConstants.EXTENSION_SPECIFICATION;

	private List<TypeSpecificationInfo> typeSpecifications = new ArrayList<>();

	public DronSpecificationServiceInstance() {
		super(ServiceIds.SERVICE_SPECIFICATION, "UAV Specifiation Management", EXTENSION);

		typeSpecifications.add(new TypeSpecificationInfo("Default", "Default"));
		typeSpecifications.add(new TypeSpecificationInfo("DGI-Drone", "DGI-Drone"));
		typeSpecifications.add(new TypeSpecificationInfo("OctoCopter", "OctoCopter"));

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
	public DroneSpecificationInfo createItem() throws DronologyServiceException {
		DroneSpecificationPersistenceProvider persistor = DroneSpecificationPersistenceProvider.getInstance();
		IDroneSpecification specification = DronologyElementFactory.createNewDroneEqiupment();
		specification.setName("New-DroneSpecification");
		String savePath = FileUtil.concat(storagePath, specification.getId(), EXTENSION);

		try {
			persistor.saveItem(specification, savePath);
		} catch (PersistenceException e) {
			throw new DronologyServiceException("Error when creating drone euqipment: " + e.getMessage());
		}
		return new DroneSpecificationInfo(specification.getName(), specification.getId());
	}

	@Override
	protected String getPath() {
		String path = SupervisorService.getInstance().getDroneSpecificationLocation();
		return path;
	}

	@Override
	protected DroneSpecificationInfo fromFile(String id, File file) throws Throwable {
		IDroneSpecification atm = DroneSpecificationPersistenceProvider.getInstance().loadItem(file.toURI().toURL());
		DroneSpecificationInfo info = new DroneSpecificationInfo(atm.getName(), id);
		info.setType(atm.getType());
		return info;
	}

	@Override
	protected boolean hasProperties() {
		return false;
	}

	@Override
	public void notifyFileChange(Set<String> changed) {
		for (String s : changed) {
			updateItem(s);
		}
		super.notifyFileChange(changed);
		for (String s : changed) {
			String id = s.replace("." + extension, "");
			if (!itemmap.containsKey(id)) {

			}
		}
	}

	private void updateItem(String s) {
		System.out.println("UPDATE");

	}

	@Override
	public Collection<TypeSpecificationInfo> getTypeSpecifications() {
		return Collections.unmodifiableCollection(typeSpecifications);
	}

}
