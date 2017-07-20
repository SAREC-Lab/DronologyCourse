package edu.nd.dronology.ui.cc.main.safety;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.nd.dronology.monitoring.monitoring.ConstraintValidationInfo;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ChartItem extends Composite {
	private static final ILogger LOGGER = LoggerProvider.getLogger(ChartItem.class);
	private ConstraintValidationInfo info;
	private Label footer;
	private IStatisticsChartControl chart;
	private SafetyViewer viewer;
	private String uavid;

	public ChartItem(Composite parent, SafetyViewer viewer, ConstraintValidationInfo info, String uavid) {
		super(parent, SWT.FLAT);
		this.uavid = uavid;
		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		this.info = info;
		this.viewer = viewer;
		createContents();
	}

	private void createContents() {
		createPieChart(uavid + ":" + info.getAssumptionid(), uavid + ":" + info.getAssumptionid());

	}

	private void createPieChart(final String id, String name) {

		Label showDetails = new Label(this, SWT.FLAT);
		// showDetails.setImage(ImageProvider.IMG_MM_GOTO_VALIDATION_DETAILS);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(showDetails);
		showDetails.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				showEvaluationDetails();

			}
		});

		Label header1 = new Label(this, SWT.FLAT);
		header1.setFont(StyleProvider.FONT_WELCOME);
		header1.setText(name);
		GridDataFactory.fillDefaults().grab(true, false).span(1, 1).align(SWT.BEGINNING, SWT.CENTER).applyTo(header1);

		Label deleteAllViolations = new Label(this, SWT.FLAT);
		// deleteAllViolations.setImage(ImageProvider.IMG_DELTE_ITEM_M);
		GridDataFactory.fillDefaults().grab(false, false).align(SWT.END, SWT.CENTER).applyTo(deleteAllViolations);
		deleteAllViolations.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				removeAllViolations();

			}
		});
		deleteAllViolations.setVisible(false);
		chart = ChartControlFactory.createChart(this, name);

		GridLayoutFactory.fillDefaults().numColumns(3).extendedMargins(5, 5, 5, 5).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		ControlUtil.paintCustomBorder(this, 1, SWT.LINE_DOT);

		footer = new Label(this, SWT.FLAT);
		footer.setFont(StyleProvider.FONT_WELCOME);
		footer.setText(name);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).align(SWT.CENTER, SWT.CENTER).applyTo(footer);
		footer.setText("T:0 | I:0 | W:0 | E:0 | F:0");
		// infoLabels.put(id, footer);

		ControlUtil.setColor(this, footer, header1, showDetails, deleteAllViolations);

	}

	protected void removeAllViolations() {
		// try {
		// LOGGER.info("Deleting violations of constraint '" + constraint.getName() +
		// "'");
		// IValidationRemoteService service;
		// service = (IValidationRemoteService)
		// ServiceProvider.getDistributorProvider().getRemoteManager()
		// .getService(IValidationRemoteService.class);
		// service.removeAllViolations(constraint.getId());
		//
		//
		// viewer.dynamicRemoveOldChartItem(constraint);
		// ElementProvider.updateItemState(true);
		//
		// } catch (RemoteException | DistributionException e) {
		// LOGGER.error(e);
		// }

	}

	protected void showEvaluationDetails() {
		// StatisticsDetailsDialog dialog = new StatisticsDetailsDialog(constraint);
		// dialog.create();
		// dialog.open();
	}

	public boolean refreshData(ConstraintValidationInfo constraintInfo) {
		this.info = constraintInfo;
		// int instances = cst.getInstanceCount();
		int eval = constraintInfo.getTotalEvaluations();
		int passed = constraintInfo.getPassEvaluations();
		chart.refreshData(constraintInfo);
		footer.setText("|T:" + eval + "|P:" + passed + "|");
		footer.getParent().layout();
		boolean visible = eval > 0;

		((GridData) this.getLayoutData()).exclude = !visible;
		this.setVisible(visible);
		return visible;

	}

}
