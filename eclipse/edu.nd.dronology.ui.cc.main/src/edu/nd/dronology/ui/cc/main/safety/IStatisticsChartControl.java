package edu.nd.dronology.ui.cc.main.safety;

import edu.nd.dronology.monitoring.monitoring.ConstraintValidationInfo;

public interface IStatisticsChartControl {

	void refreshData(ConstraintValidationInfo constraintInfo);

	void dispose();

}
