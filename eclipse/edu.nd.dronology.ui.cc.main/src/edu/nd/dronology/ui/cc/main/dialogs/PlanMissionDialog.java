package edu.nd.dronology.ui.cc.main.dialogs;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import edu.nd.dronology.core.mission.IMissionPlan;
import edu.nd.dronology.core.mission.MissionPlan;
import edu.nd.dronology.core.mission.MissionPlan.RouteSet;
import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.remote.BaseServiceProvider;

public class PlanMissionDialog extends Dialog {
	private Text text;
	private Table table;
	private TableViewer tableViewer;
	private MissionPlan missionPlan;
	private List<String> uavList;
	private List<FlightRouteInfo> routeList;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public PlanMissionDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, true));

		missionPlan = new MissionPlan();
		RouteSet routeSet = new RouteSet();
		missionPlan.addRouteSet(routeSet);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Execution Delay (ms)");

		text = new Text(container, SWT.BORDER);
		text.setText("1000");
		text.addModifyListener((e)->{routeSet.setExecutionDelay(Integer.parseInt(text.getText()));});
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnAddRoute = new Button(container, SWT.NONE);
		btnAddRoute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnAddRoute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				routeSet.addPan(uavList.get(0),routeList.get(0).getId());
				tableViewer.refresh();
			}
		});
		btnAddRoute.setText("Add Route");

		Button btnRemoveRoute = new Button(container, SWT.NONE);
		btnRemoveRoute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemoveRoute.setText("Remove Route");

		createViewer(container);
		tableViewer.setInput(routeSet);
		return container;
	}

	private void createViewer(Composite parent) {
		String[] columnNames = new String[] { "0", "1" };

		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setFont(StyleProvider.FONT_SEGOE_11);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(table);

		uavList = new ArrayList<>();
		routeList = new ArrayList<>();

		try {
			IFlightRouteplanningRemoteService routeService = (IFlightRouteplanningRemoteService) BaseServiceProvider
					.getInstance().getRemoteManager().getService(IFlightRouteplanningRemoteService.class);

			routeList = new ArrayList(routeService.getItems());
			
			Collections.sort(routeList, new RouteListNameComparator());
			

			IDroneSetupRemoteService setupService = (IDroneSetupRemoteService) BaseServiceProvider.getInstance()
					.getRemoteManager().getService(IDroneSetupRemoteService.class);

			Collection<DroneStatus> uavItemList = setupService.getDrones().values();
			for (DroneStatus status : uavItemList) {
				uavList.add(status.getID());
			}
		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tableViewer.setColumnProperties(columnNames);
		CellEditor[] editors = new CellEditor[2];
		editors[0] = createUAVEditor(uavList);
		editors[1] = createRouteEditor(routeList);

		tableViewer.setCellEditors(editors);

		TableViewerColumn col0 = new TableViewerColumn(tableViewer, SWT.FLAT);
		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.FLAT);

		col0.getViewer().setCellModifier(new ComboCellRouteModifier(this,uavList,routeList));
		col1.getViewer().setCellModifier(new ComboCellRouteModifier(this, uavList,routeList));

		col0.getColumn().setText("Parameter name");
		col1.getColumn().setText("Mapped Parameter");

		col0.getColumn().setWidth(250);
		col1.getColumn().setWidth(250);

		tableViewer.setLabelProvider(new MissionViewerLabelProvider(routeList));
		tableViewer.setContentProvider(new MissionViewerContentProvider());

	}

	private CellEditor createUAVEditor(List<String> uavList) {
		ComboBoxCellEditor editor = new ComboBoxCellEditor(tableViewer.getTable(), uavList.toArray(new String[0]));
		CCombo combo = (CCombo) editor.getControl();
		combo.setFont(StyleProvider.FONT_MONOSPACED_11);
		combo.setBackground(StyleProvider.COLOR_CONSTRAINT);
		combo.setEditable(false);
		return editor;
	}

	private CellEditor createRouteEditor(Collection<FlightRouteInfo> routeList) {
		ComboBoxCellEditor editor = new MyRouteComboBoxCellEditor(tableViewer.getTable(), routeList);
		CCombo combo = (CCombo) editor.getControl();
		combo.setFont(StyleProvider.FONT_MONOSPACED_11);
		combo.setBackground(StyleProvider.COLOR_CONSTRAINT);
		combo.setEditable(false);
		return editor;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(520, 300);
	}

	public void refresh() {
		tableViewer.refresh();

	}

	public IMissionPlan getMissionPlan() {
		return missionPlan;
	}

}
