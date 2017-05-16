package edu.nd.dronology.services.supervisor;

import java.util.Collections;
import java.util.Map;

import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractServerService;
import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class SupervisorServiceInstance extends AbstractServiceInstance implements ISupervisorServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(SupervisorServiceInstance.class);

	private static String customWorkspace;

	public SupervisorServiceInstance() {
		super("SUPERVISOR", "Managing the server");
	}

	@Override
	protected Class<?> getServiceClass() {
		return SupervisorService.class;
	}

	@Override
	protected int getOrder() {
		return 1;
	}

	@Override
	protected String getPropertyPath() {
		return null;
	}

	@Override
	protected void doStartService() throws Exception {
		WorkspaceInitializer.getInstance().prepareServerWorkspace(SupervisorService.getWorkspace());
	}

	@Override
	protected void doStopService() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void shutdownServer() {
		LOGGER.info("Shuting Down - Services");
		AbstractServerService.stopAll();
		LOGGER.info("Shutting down application - bye!");
		System.exit(0);
	}

	@Override
	public void restartAllServices() {
		LOGGER.info("Restarting all Services!");

		AbstractServerService.restartAll();

	}

	@Override
	public ServiceInfo getServiceInfo() {
		ServiceInfo sInfo = super.getServiceInfo();
		sInfo.addAttribute(ServiceInfo.ATTRIBUTE_TYPE, ServiceInfo.ATTRIBUTE_REMOTE);
		return sInfo;
	}

	@Override
	public String getFlightPathLocation() {
		return WorkspaceInitializer.getInstance().getFlightPathLocation();
	}



	@Override
	public String getWorkspaceLocation() {
		return WorkspaceInitializer.getInstance().getWorkspaceLocation();
	}

	@Override
	public Map<String, String> getGlobalProperties() {
		//return GlobalConfReader.getGlobalPropertySet();\
		return Collections.emptyMap();
	}

	@Override
	public boolean importItem(String fileName, byte[] byteArray, boolean overwrite) throws DronologyServiceException {
		return WorkspaceInitializer.getInstance().importItem(fileName, byteArray, overwrite);
	}

	@Override
	protected boolean hasProperties() {
		return false;
	}

	@Override
	public String getDroneEquipmentLocation() {
		return WorkspaceInitializer.getInstance().getDroneEquipmentLocation();
	}

	@Override
	public String getSimScenarioLocation() {
		return WorkspaceInitializer.getInstance().getSimScenarioLocation();
	}



}