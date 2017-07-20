package edu.nd.dronology.ui.cc.main.safety;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.util.Rotation;

import edu.nd.dronology.monitoring.monitoring.ConstraintValidationInfo;

public class JFreeChartControl implements IStatisticsChartControl {

	static final Color COLOR_INFO = new Color(9, 200, 240);

	private DefaultPieDataset dataset;

	private JFreeChart chart;

	private String title;

	private Composite parent;

	private JFreeChart chart2;

	private ChartComposite frame;

	public JFreeChartControl(Composite parent, String title) {
		this.parent = parent;
		this.title = title;
		createContents();
	}

	void createContents() {
		dataset = createDataset();
		chart = createChart();

		frame = new ChartComposite(parent, SWT.NONE, chart, true) {
			@Override
			protected Menu createPopupMenu(boolean properties, boolean save, boolean print, boolean zoom) {
				Menu menu = new Menu(parent);
				MenuItem trending = new MenuItem(menu, SWT.NONE);
				// trending.setImage(ImageProvider.IMG_MM_MENU_TRENDING);
				trending.setText("Show Total Trending");
				// trending.addSelectionListener(new SelectionAdapter() {
				// @Override
				// public void widgetSelected(SelectionEvent e) {
				// ViewUtil.showViewAndTab(StatisticsView.ID, "Trending", title);
				// }
				// });
				MenuItem interval = new MenuItem(menu, SWT.NONE);
				// interval.setImage(ImageProvider.IMG_MM_MENU_STATUS_INTERVAL);
				interval.setText("Show Status/Interval");
				// interval.addSelectionListener(new SelectionAdapter() {
				// @Override
				// public void widgetSelected(SelectionEvent e) {
				// ViewUtil.showViewAndTab(StatisticsView.ID, "Status/Interval", title);
				// }
				// });
				return menu;
			}
		};

		GridData gd = new GridData(GridData.FILL_BOTH);

		gd.heightHint = 150;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.minimumHeight = 150;
		gd.horizontalSpan = 3;
		frame.setSize(100, 250);
		frame.setLayoutData(gd);
		frame.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		chart.setBackgroundPaint(Color.white);
		chart.removeLegend();
		frame.setChart(chart);
		frame.forceRedraw();
		// frame.pack();
		frame.setVisible(true);
		frame.setSize(500, 500);

	}

	private JFreeChart createChart() {
		chart2 = ChartFactory.createPieChart(title, dataset, false, true, false);
		chart2.setBorderPaint(null);
		final PiePlot plot = (PiePlot) chart2.getPlot();
		plot.setOutlineVisible(false);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setLabelGenerator(null);
		chart2.setTitle((String) null);
		plot.setSectionPaint("Passed", Color.GREEN);
		plot.setSectionPaint("Failed", Color.RED);
		plot.setSectionPaint("Error", Color.ORANGE);
		// plot.setForegroundAlpha(0.5f);
		return chart2;
	}

	@Override
	public void refreshData(ConstraintValidationInfo cst) {
		// int instances = cst.getInstanceCount();
		// int eval = cst.getEvaluationCount();

		int failure = cst.getFailedEvaluations();
		int passed = cst.getPassEvaluations();

		int errors = cst.getErrors();

		// dataset.setValue("NotEvaluated", (instances - eval));
		// dataset.setValue("NotEvaluated", 0);
		dataset.setValue("Passed", passed);
		dataset.setValue("Failed", failure);
		dataset.setValue("Error", errors);
		chart.fireChartChanged();
	}

	private DefaultPieDataset createDataset() {
		DefaultPieDataset result = new DefaultPieDataset();
		result.setValue("Passed", 1);
		result.setValue("Failed", 1);
		// result.setValue("NotEvaluated", 1);
		return result;
	}

	@Override
	public void dispose() {
		frame.dispose();
	}

}
