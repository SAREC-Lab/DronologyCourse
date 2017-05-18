package edu.nd.dronology.core.simulator;

public interface IBatterySimulator {
	
	void startBatteryDrain();

	void stopBatteryDrain();
	
	double getVoltage();


}
