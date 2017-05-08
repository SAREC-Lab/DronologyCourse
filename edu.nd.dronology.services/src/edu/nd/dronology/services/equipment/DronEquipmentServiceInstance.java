package edu.nd.dronology.services.equipment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.nd.dronology.services.core.api.IFileChangeNotifyable;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.DroneEquipmentInfo;
import edu.nd.dronology.services.core.info.EquipmentTypeInfo;
import edu.nd.dronology.services.core.items.IDroneEquipment;
import edu.nd.dronology.services.core.items.IFlightPath;
import edu.nd.dronology.services.core.persistence.DroneEquipmentPersistenceProvider;
import edu.nd.dronology.services.core.persistence.FlightPathPersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.util.DronologyConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.instances.DronologyElementFactory;
import edu.nd.dronology.services.instances.flightpath.FlightPathService;
import edu.nd.dronology.services.supervisor.SupervisorService;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DronEquipmentServiceInstance extends AbstractFileTransmitServiceInstance<DroneEquipmentInfo>
		implements IFileChangeNotifyable, IDroneEquipmentServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DronEquipmentServiceInstance.class);

	private static final int ORDER = 2;

	public static final String EXTENSION = DronologyConstants.EXTENSION_EQUIPMENT;

	private List<EquipmentTypeInfo> equipmentTypes = new ArrayList<>();

	public DronEquipmentServiceInstance() {
		super(ServiceIds.SERVICE_EQUIPMENT, "Equipment Management", EXTENSION);

		equipmentTypes.add(new EquipmentTypeInfo("Default", "Default"));
		equipmentTypes.add(new EquipmentTypeInfo("DGI-Drone", "DGI-Drone"));
		equipmentTypes.add(new EquipmentTypeInfo("OctoCopter", "OctoCopter"));

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
	public DroneEquipmentInfo createItem() throws DronologyServiceException {
		DroneEquipmentPersistenceProvider persistor = DroneEquipmentPersistenceProvider.getInstance();
		IDroneEquipment equipment = DronologyElementFactory.createNewDroneEqiupment();
		equipment.setName("New-DroneEquipment");
		String savePath = FileUtil.concat(storagePath, equipment.getId(), EXTENSION);

		try {
			persistor.saveItem(equipment, savePath);
		} catch (PersistenceException e) {
			throw new DronologyServiceException("Error when creating drone euqipment: " + e.getMessage());
		}
		return new DroneEquipmentInfo(equipment.getName(), equipment.getId());
	}

	@Override
	protected String getPath() {
		String path = SupervisorService.getInstance().getDroneEquipmentLocation();
		return path;
	}

	@Override
	protected DroneEquipmentInfo fromFile(String id, File file) throws Throwable {
		IDroneEquipment atm = DroneEquipmentPersistenceProvider.getInstance().loadItem(file.toURI().toURL());
		DroneEquipmentInfo info = new DroneEquipmentInfo(atm.getName(), id);
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
	public Collection<EquipmentTypeInfo> getEquipmentTypes() {
		return Collections.unmodifiableCollection(equipmentTypes);
	}

}
