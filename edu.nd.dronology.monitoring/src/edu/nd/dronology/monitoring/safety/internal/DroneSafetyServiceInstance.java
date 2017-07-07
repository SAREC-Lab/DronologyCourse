package edu.nd.dronology.monitoring.safety.internal;

import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneSafetyServiceInstance extends AbstractServiceInstance implements IDroneSafetyServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSafetyServiceInstance.class);


	public DroneSafetyServiceInstance() {
		super("DRONESETUP");
	}

	@Override
	protected Class<?> getServiceClass() {
		return DroneSafetyService.class;
	}

	@Override
	protected int getOrder() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	protected String getPropertyPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doStartService() throws Exception {
	
	}

	@Override
	protected void doStopService() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerDroneSafetyCase(String uavid, String safetycase) throws DronologyServiceException {
		// TODO Auto-generated method stub
		
	}




}
