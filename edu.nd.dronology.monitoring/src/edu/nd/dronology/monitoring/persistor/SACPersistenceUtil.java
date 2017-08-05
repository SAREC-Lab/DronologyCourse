package edu.nd.dronology.monitoring.persistor;

import com.thoughtworks.xstream.XStream;

import edu.nd.dronology.monitoring.safety.internal.Assumption;
import edu.nd.dronology.monitoring.safety.internal.InfrastructureSafetyCase;
import edu.nd.dronology.monitoring.safety.internal.UAVSaeftyCase;

public class SACPersistenceUtil {


	
	private static String ESAC_ALIAS = "SAC";
	private static String ASSUMPTION_ALIAS = "Assumption";

	public static void preprocessStream(XStream xstream) {

		xstream.alias(ESAC_ALIAS , InfrastructureSafetyCase.class);
		xstream.alias(ASSUMPTION_ALIAS ,Assumption.class);
	//	xstream.alias(COORDINATE_ALIAS, LlaCoordinate.class);
	//	xstream.alias(WAYPOINT_ALIAS, Waypoint.class);
		
	//	xstream.alias(SPEC_ALIAS, DroneSpecification.class);

	}

}
