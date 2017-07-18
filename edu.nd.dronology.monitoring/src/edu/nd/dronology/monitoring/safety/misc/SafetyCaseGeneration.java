package edu.nd.dronology.monitoring.safety.misc;

import edu.nd.dronology.monitoring.safety.IExternalSafetyCase;
import edu.nd.dronology.monitoring.safety.internal.Assumption;
import edu.nd.dronology.monitoring.safety.internal.InfrastructureSafetyCase;
import edu.nd.dronology.monitoring.safety.internal.UAVSaeftyCase;

public class SafetyCaseGeneration {

	public static InfrastructureSafetyCase getSafetyCase() {

		InfrastructureSafetyCase sac = new InfrastructureSafetyCase();
		Assumption ass = new Assumption("A_1_1_1");
		ass.setStatic(true);
		ass.setExpression("$L(#E_MinSpeed,#E_MaxSpeed, #I_MAXSPEED ) : (#E_MinSpeed  <=#I_MAXSPEED  <=#E_MaxSpeed )");
		ass.addParameterValue("#I_MAXSPEED", "15");
		sac.addAssumption(ass);

		Assumption ass2 = new Assumption("A_1_1_2");
		ass2.setStatic(true);
		ass2.setExpression("$L(#E_rotors, #I_MINROTOS ) : ( #E_rotors >= #I_MINROTOS  )");
		ass2.addParameterValue("#I_MINROTOS", "4");
		sac.addAssumption(ass2);

		Assumption ass3 = new Assumption("A_1_1_3");
		ass3.setStatic(false);
		ass3.setMonitorable(true);
		ass3.setExpression(
				"$L(#E_batteryvoltage, #I_BATTERY_THRESHOLD ) : ( #E_batteryvoltage >= #I_BATTERY_THRESHOLD  )");
		ass3.addParameterValue("#I_BATTERY_THRESHOLD", "4");
		sac.addAssumption(ass3);

		Assumption ass4 = new Assumption("A_1_1_4");
		ass4.setStatic(false);
		ass4.setMonitorable(true);
		ass4.setExpression(
				"$L(#E_batterypower, #E_maxbatterypower ) : ( #E_batterypower >= #E_maxbatterypower * 0.1  )");
		sac.addAssumption(ass4);

		// InfrastructureSafetyCase sac = new InfrastructureSafetyCase();
		// Assumption ass = new Assumption("A_1_1_1");
		// ass.setStatic(true);
		// ass.setExpression("$L(#speed, #I_MAXSPEED ) : ( #speed <= #I_MAXSPEED )");
		// ass.addParameterValue("I_MAXSPEED", "15");
		// sac.addAssumption(ass);
		//
		// Assumption ass2 = new Assumption("A_2_1_1");
		// ass2.setStatic(true);
		// ass2.setExpression("$L(#rotors, #I_MINROTOS ) : ( #rotors >= #I_MINROTOS )");
		// ass2.addParameterValue("I_MINROTOS", "6");
		// sac.addAssumption(ass2);
		//
		// Assumption ass3 = new Assumption("A_3_1_1");
		// ass3.setMonitorable(true);
		// ass3.setExpression("$L(#batteryvoltage, #I_BATTERY_THRESHOLD ) : (
		// #batteryvoltage >= #I_BATTERY_THRESHOLD )");
		// ass3.addParameterValue("I_BATTERY_THRESHOLD", "2");
		// sac.addAssumption(ass3);
		//
		// Assumption ass4 = new Assumption("A_4_1_1");
		// ass4.setMonitorable(true);
		// ass4.setExpression("$L(#batterypower, #maxbatterypower ) : ( #batterypower >=
		// #maxbatterypower * 0.1 )");
		// sac.addAssumption(ass4);
		//
		// Assumption ass5 = new Assumption("A_5_1_1");
		// ass5.setMonitorable(true);
		// ass5.setExpression("$L(#batterypower, #maxbatterypower ) : ( #batterypower >=
		// #maxbatterypower * 0.2 )");
		// sac.addAssumption(ass5);
		//
		// Assumption ass6 = new Assumption("A_6_1_1");
		// ass6.setMonitorable(true);
		// ass6.setExpression("$L(#batterypower, #maxbatterypower ) : ( #batterypower >=
		// #maxbatterypower * 0.05 )");
		// sac.addAssumption(ass6);

		return sac;

	}

	public static IExternalSafetyCase getUAVSafetyCase() {

		IExternalSafetyCase sac = new UAVSaeftyCase("SAC1", "Drone1");
		Assumption ass = new Assumption("A_1_1_1");
		ass.setStatic(true);
		ass.addParameterValue("speed", "12");
		sac.addAssumption(ass);

		Assumption ass2 = new Assumption("A_2_1_1");
		ass2.setStatic(true);
		ass2.addParameterValue("rotors", "6");
		sac.addAssumption(ass2);

		Assumption ass3 = new Assumption("A_3_1_1");
		ass3.setMonitorable(true);
		ass3.addParameterMapping("batteryvoltage", "BLEVEL_VOLTAGE");
		sac.addAssumption(ass3);

		Assumption ass4 = new Assumption("A_4_1_1");
		ass4.setMonitorable(true);
		ass4.addParameterMapping("batterypower", "BLEVEL_POWER");
		ass4.addParameterMapping("maxbatterypower", "BLEVEL_POWER_MAX");
		sac.addAssumption(ass4);

		Assumption ass5 = new Assumption("A_5_1_1");
		ass5.setMonitorable(true);
		ass5.addParameterMapping("batterypower", "BLEVEL_POWER");
		ass5.addParameterMapping("maxbatterypower", "BLEVEL_POWER_MAX");
		sac.addAssumption(ass5);

		Assumption ass6 = new Assumption("A_6_1_1");
		ass6.setMonitorable(true);
		ass6.addParameterMapping("batterypower", "BLEVEL_POWER");
		ass6.addParameterMapping("maxbatterypower", "BLEVEL_POWER_MAX");
		sac.addAssumption(ass6);

		return sac;

	}

}
