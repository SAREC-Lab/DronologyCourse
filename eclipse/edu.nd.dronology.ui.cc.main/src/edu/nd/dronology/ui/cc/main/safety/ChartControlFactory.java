package edu.nd.dronology.ui.cc.main.safety;

import org.eclipse.swt.widgets.Composite;

public class ChartControlFactory {

	public static IStatisticsChartControl createChart(Composite parent, String title) {
		return new JFreeChartControl(parent, title);
	}

}
