package edu.nd.dronology.ui.cc.main.wizard.settings;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;

public class ConfigWizardPage extends WizardPage {

	private Button btnRefSelect;

	private CTabItem settingsItem;


	/**
	 * Create the wizard.
	 */
	public ConfigWizardPage() {
		super("Configuration");
		setTitle("Configuration");
		setDescription("Configure File-Path, Colors, Fonts, and Abbreviations");

	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setData(StyleProvider.CSS_TAG, "Wizard");

		setControl(container);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).applyTo(container);
	

		CTabFolder folder = new CTabFolder(container, SWT.FLAT);

		folder.setTabPosition(SWT.BOTTOM);
		settingsItem = new GeneralSettingsTabItem(folder, (ConfigWizard) getWizard());
		
		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(15, 15, 5, 5).applyTo(folder);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(folder);
		ControlUtil.setColor(container, parent);
		container.setBackground(StyleProvider.COLOR_DARK_ORANGE);
	}

	
}
