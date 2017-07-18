package edu.nd.dronology.ui.cc.main.safety;

public interface IRuntimeEvaluationInfo {

	String getId();

	int getEvaluationCount();

	int getFailureCount();

	int getPassedCount();

}
