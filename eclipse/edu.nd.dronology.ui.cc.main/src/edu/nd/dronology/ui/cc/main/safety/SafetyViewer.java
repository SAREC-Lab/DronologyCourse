package edu.nd.dronology.ui.cc.main.safety;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.nd.dronology.monitoring.monitoring.ConstraintValidationInfo;
import edu.nd.dronology.monitoring.monitoring.UAVValidationInformation;
import edu.nd.dronology.monitoring.service.IDroneSafetyRemoteService;
import edu.nd.dronology.monitoring.service.IMonitoringValidationListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.remote.BaseServiceProvider;
import edu.nd.dronology.ui.cc.main.util.UIRefreshThread;

public class SafetyViewer extends Composite {

	private UIRefreshThread refreshThread;
	private Map<String, ChartItem> chartItems = new HashMap<>();
	private StyledText text;
	private Composite chartComp;
	private Composite consoleComp;

	private static final Color COLOR_INFO = StyleProvider.COLOR_LIGHT_BLUE;
	private static final Color COLOR_WARN = StyleProvider.COLOR_DARK_ORANGE;
	private static final Color COLOR_ERROR = StyleProvider.COLOR_LIGHT2_RED;
	private static final Color COLOR_TRACE = StyleProvider.COLOR_LIGHT2_GREEN;

	public SafetyViewer(Composite parent) {
		super(parent, SWT.FLAT);

		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(10, 10, 10, 10).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		createContents();
	}

	private void createContents() {

		chartComp = new Composite(this, SWT.FLAT);
		consoleComp = new Composite(this, SWT.FLAT);

		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(chartComp);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(chartComp);

		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(consoleComp);
		GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 250).applyTo(consoleComp);

		refreshCharts();
		createConsole(consoleComp);

		refreshThread = new UIRefreshThread(5) {

			@Override
			protected void doRefresh() {
				if (isDisposed()) {
					return;
				}
				refreshCharts();

			}
		};
		refreshThread.start();
	}

	private void createConsole(Composite parent) {
		text = new StyledText(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text.setEditable(false);
		text.setFont(StyleProvider.FONT_MONOSPACED);
		text.setSize(400, 400);
		text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		text.addLineBackgroundListener(new LineBackgroundListener() {

			@Override
			public void lineGetBackground(LineBackgroundEvent event) {
				if (event.lineText.contains("INFO#")) {
					event.lineBackground = COLOR_INFO;
				}
				if (event.lineText.contains("MONITORING_CHECK_ERROR")) {
					event.lineBackground = COLOR_WARN;
				}
				if (event.lineText.contains("MONITORING_CHECK_FAILED")) {
					event.lineBackground = COLOR_ERROR;
				}
				if (event.lineText.contains("MONITORING_CHECK_PASSED")) {
					event.lineBackground = COLOR_TRACE;
				}
			}
		});
		GridDataFactory.fillDefaults().grab(true, true).applyTo(text);
		addLogger();

	}

	private void addLogger() {
		try {
			IDroneSafetyRemoteService service = (IDroneSafetyRemoteService) BaseServiceProvider.getInstance()
					.getRemoteManager().getService(IDroneSafetyRemoteService.class);
			IMonitoringValidationListener listener = new MonitoringValidationListener(this);
			service.addValidationListener(listener);

		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void refreshCharts() {
		IDroneSafetyRemoteService service;
		try {
			service = (IDroneSafetyRemoteService) BaseServiceProvider.getInstance().getRemoteManager()
					.getService(IDroneSafetyRemoteService.class);
			Collection<UAVValidationInformation> info = service.getValidationInfo();
			for (UAVValidationInformation inf : info) {
				creatItems(inf);
			}

		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void creatItems(UAVValidationInformation inf) {
		for (ConstraintValidationInfo constraintInfo : inf.getConstraintValidationInfos()) {
			String uniqueid = inf.getUAVId() + "_" + constraintInfo.getAssumptionid();
			if (chartItems.containsKey(uniqueid)) {
				chartItems.get(uniqueid).refreshData(constraintInfo);
			} else {
				ChartItem chartItem = new ChartItem(chartComp, this, constraintInfo,inf.getUAVId());
				chartItems.put(uniqueid, chartItem);
				chartComp.layout();
			}

		}

	}

	@Override
	public void dispose() {
		super.dispose();
		refreshThread.terminate();
	}

	public void newEvaluationMessage(String message) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					if (text.isDisposed()) {
						dispose();
					}

					text.append(message);
					text.append("\n");
					text.setSelection(text.getText().length());
				} catch (Exception e) {
					// e.printStackTrace();
				}

			}

		});
	}
}
