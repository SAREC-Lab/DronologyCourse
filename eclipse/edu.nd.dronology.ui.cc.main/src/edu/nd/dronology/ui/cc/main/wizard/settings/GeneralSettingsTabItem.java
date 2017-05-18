package edu.nd.dronology.ui.cc.main.wizard.settings;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.nd.dronology.ui.cc.application.constants.PerspectiveConstants;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.handler.ThemeSwitchHandler;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;
import edu.nd.dronology.ui.cc.util.controls.CustomCombo;

public class GeneralSettingsTabItem extends CTabItem {

	private Text txtPort;
	private Button btnSelectFont;
	private ConfigWizard wizard;
	private CustomCombo cmbTheme;
	private CustomCombo cmbPerspective;

	public GeneralSettingsTabItem(CTabFolder parent, ConfigWizard wizard) {
		super(parent, SWT.FLAT);
		this.wizard = wizard;
		createContents();
		setText("Settings");
		setFont(StyleProvider.getSelectedBoldFont());
		setData(StyleProvider.CSS_TAG, "Wizard");
	}

	void createContents() {
		Composite container = new Composite(getParent(), SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 5, 5).applyTo(container);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);

		setControl(container);
		createFileFields(container);
		createColorSelector(container);
		addListener();
		setValues();

	}

	private void createFileFields(Composite parent) {

		Composite selectComp = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 25, 20).numColumns(3).applyTo(selectComp);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(selectComp);

		Label lblPath = new Label(selectComp, SWT.NONE | SWT.INHERIT_DEFAULT);
		lblPath.setText("RMI Port");
		lblPath.setFont(StyleProvider.getSelectedFont(lblPath));

		txtPort = new Text(selectComp, SWT.BORDER);
		txtPort.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtPort.setFont(StyleProvider.getSelectedFont(txtPort));


		txtPort.setData(StyleProvider.CSS_TAG, "noskin");
		ControlUtil.paintCustomBorder(selectComp);
		ControlUtil.setColor(lblPath, selectComp);
	}

	private void createColorSelector(Composite parent) {
		Composite colorComp = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 25, 20).numColumns(2).applyTo(colorComp);
		GridDataFactory.fillDefaults().span(3, 1).grab(true, false).applyTo(colorComp);

		Label lblFont = new Label(colorComp, SWT.NONE);
		lblFont.setText("Font");
		lblFont.setFont(StyleProvider.getSelectedFont(lblFont));

		btnSelectFont = new Button(colorComp, SWT.PUSH);
		btnSelectFont.setText("Select Font");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(btnSelectFont);
		btnSelectFont.setFont(StyleProvider.getSelectedFont(btnSelectFont));

		

		Label lblTheme = new Label(colorComp, SWT.NONE);
		lblTheme.setText("Color-Theme");
		lblTheme.setFont(StyleProvider.getSelectedFont(parent));
		cmbTheme = new CustomCombo(colorComp, SWT.BORDER);
		cmbTheme.add(ImageProvider.IMG_MENU_THEME_24, "Yellow-Theme");
		cmbTheme.add(ImageProvider.IMG_MENU_THEME_24, "Red-Theme");
		cmbTheme.select(0);
		cmbTheme.setData(StyleProvider.CSS_TAG, "noskin");
		cmbTheme.setFont(StyleProvider.getSelectedFont(cmbTheme));
		cmbTheme.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = cmbTheme.getSelectionIndex();
				selectTheme(index);
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(cmbTheme);

		Label lblPerspective = new Label(colorComp, SWT.NONE);
		lblPerspective.setText("Startup Perspective");
		lblPerspective.setFont(StyleProvider.getSelectedFont(parent));
		cmbPerspective = new CustomCombo(colorComp, SWT.BORDER);
		cmbPerspective.add(ImageProvider.IMG_ARROW_RIGHT_24, "Default Start Screen");
		cmbPerspective.add(ImageProvider.IMG_ARROW_RIGHT_24, "Reference List");
		cmbPerspective.select(0);
		cmbPerspective.setData(StyleProvider.CSS_TAG, "noskin");
		cmbPerspective.setFont(StyleProvider.getSelectedFont(cmbTheme));
		cmbPerspective.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = cmbPerspective.getSelectionIndex();
				selectPerspective(index);
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(cmbPerspective);

		ControlUtil.paintCustomBorder(colorComp);
		ControlUtil.setColor(colorComp, lblFont);
	}

	protected void selectPerspective(int index) {
		if (index == 0) {
			wizard.setPerspective(PerspectiveConstants.LAUNCHER_PERSPECTIVE);
		} else {
			wizard.setPerspective(PerspectiveConstants.TAKEOFF_PERSPECTIVE);
		}

	}

	protected void selectTheme(int index) {
		if (index == 0) {
			wizard.setTheme(ThemeSwitchHandler.THEME_DEFAULT);
		} else {
			wizard.setTheme(ThemeSwitchHandler.THEME_RED);
		}

	}

	private void addListener() {
		txtPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				wizard.setPort(txtPort.getText());
			}
		});

		

	

		btnSelectFont.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectFont();
			}

		});

		
		

	}


	protected void selectFont() {
		FontDialog dialog = new FontDialog(Display.getDefault().getActiveShell());
		ConfigWizard wiz = wizard;
		FontData currentFont = new FontData(wiz.getFontName(), wiz.getFontHeight(), wiz.getFontStyle());

		dialog.setFontList(new FontData[] { currentFont });
		FontData result = dialog.open();
		if (result == null) {
			return;
		}
		StyleProvider.setSelectedFont(result);

		wiz.setFontHeight(result.getHeight());
		wiz.setFontStyle(result.getStyle());
		wiz.setFontName(result.getName());

	}

	
	private void setValues() {
		txtPort.setText(wizard.getPort());

		if (ThemeSwitchHandler.THEME_DEFAULT.equals(wizard.getTheme())) {
			cmbTheme.select(0);
		} else {
			cmbTheme.select(1);
		}
		
		if (PerspectiveConstants.LAUNCHER_PERSPECTIVE.equals(wizard.getPerspective())) {
			cmbPerspective.select(0);
		} else {
			cmbPerspective.select(1);
		}
	}

}
