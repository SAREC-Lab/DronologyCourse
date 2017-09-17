package edu.nd.dronology.gstation.python.connector.service;

import java.text.DateFormat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.gstation.python.connector.connect.IncommingGroundstationConnectionServer;
import edu.nd.dronology.services.core.base.AbstractServiceInstance;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneConnectorServiceInstance extends AbstractServiceInstance implements IDroneConnectorServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneConnectorServiceInstance.class);

	static final transient Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
			.setVersion(1.0).serializeSpecialFloatingPointValues().create();

	public DroneConnectorServiceInstance() {
		super("DRONECONNECTOR");
	}

	@Override
	protected Class<?> getServiceClass() {
		return DroneConnectorService.class;
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
		servicesExecutor.submit(new IncommingGroundstationConnectionServer());
	}

	@Override
	protected void doStopService() throws Exception {
		// TODO Auto-generated method stub

	}

}
