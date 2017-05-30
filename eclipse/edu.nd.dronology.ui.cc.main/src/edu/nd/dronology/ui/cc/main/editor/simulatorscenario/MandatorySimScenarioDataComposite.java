package edu.nd.dronology.ui.cc.main.editor.simulatorscenario;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.nd.dronology.services.core.items.ISimulatorScenario;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractMandatoryItemComposite;
import edu.nd.dronology.ui.cc.util.controls.ControlCreationHelper;
import edu.nd.dronology.ui.cc.util.controls.ImageCombo2;
import edu.nd.dronology.ui.cc.util.managedcontrol.ManagedText;

public class MandatorySimScenarioDataComposite extends AbstractMandatoryItemComposite<ISimulatorScenario> {


	private ManagedText txtName;
	private ManagedText txtId;
	private ImageCombo2 cmbType;

	public MandatorySimScenarioDataComposite(Composite parent, UAVSimScenarioPage page) {
		super(parent, page);
	}

	@Override
	protected void createFields() {

		// parentList = new LinkedList<>();


		Label lblId = new Label(mainContainer, SWT.FLAT);
		lblId.setText("Id");
		lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		//
		txtId = ControlCreationHelper.createText(mainContainer, 1, SWT.BORDER, null);
		txtId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtId.setEditable(false);
		txtId.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		txtName = ControlCreationHelper.createTextWitLabel(mainContainer, "Name", 2, SWT.BORDER, null);

		cmbType = ControlCreationHelper.createComboWithLabel(mainContainer, "Type", false, 2);

		
		cmbType.add(ImageProvider.IMG_DRONE_FLYING, "Default");
		cmbType.add(ImageProvider.IMG_DRONE_FLYING, "DGI-Drone");
		cmbType.add(ImageProvider.IMG_DRONE_FLYING, "OctoCopter");
		
		
		txtName.addApplyListener((value) -> {
			page.getItem().setName(value);
		});

		cmbType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setType();
			}
		});

	}

	protected void setType() {
		page.getItem().setCategory(cmbType.getItem(cmbType.getSelectionIndex()));
System.out.println(page.getItem().getCategory());
	}

	@Override
	public void doSetValuesToFields() {
		cmbType.select(0);
		txtName.setValue(page.getItem().getName());
		txtId.setValue(page.getItem().getId());
		String typ = page.getItem().getCategory();
		
		for (int i = 0; i < cmbType.getItemCount(); i++) {
			System.out.println(cmbType.getItem(i));
			if (cmbType.getItem(i).equals(typ)) {
				cmbType.select(i);
				break;
			}

		}

	}

	@Override
	public void setValuesToBundle() {
		// TODO Auto-generated method stub

	}

}
