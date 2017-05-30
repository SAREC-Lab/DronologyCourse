package edu.nd.dronology.ui.cc.util.controls;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.util.Layouts;
import edu.nd.dronology.ui.cc.util.managedcontrol.IManagedTextInputValidator;
import edu.nd.dronology.ui.cc.util.managedcontrol.ManagedText;

public class ControlCreationHelper {

	private static Color BACKGROUND_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

	private ControlCreationHelper() {
	}

	public static ManagedText createTextWitLabel(Composite parent, String labelName, int hSpan, int attributes,
			IManagedTextInputValidator<String> validator) {
		return createTextWitLabel(parent, labelName, hSpan, attributes, validator, null, null);
	}

	public static ManagedText createTextWitLabel(Composite parent, String labelName, int hSpan, int attributes,
			IManagedTextInputValidator<String> validator, Font lblFont, Font txtFont) {
		Label lbl = new Label(parent, SWT.INHERIT_DEFAULT);
		lbl.setText(labelName);
		if (lblFont != null) {
			lbl.setFont(lblFont);
		}
		Text text = new Text(parent, Layouts.STYLE | attributes);
		if (txtFont != null) {
			text.setFont(txtFont);
		}
		ManagedText ft;
		if (validator != null) {
			ft = new ManagedText(text, validator);
		} else {
			ft = new ManagedText(text);
		}

		GridData gdText = produceGridData(0, hSpan);
		gdText.horizontalIndent = 0;

		ft.setLayoutData(gdText);
		if (Layouts.STYLE == SWT.FLAT) {
			// toolkit.paintBordersFor(text.getParent());
		}
		return ft;
	}

	public static ManagedText createText(Composite parent, int hSpan, int attributes,
			IManagedTextInputValidator<String> validator) {

		Text text = new Text(parent, Layouts.STYLE | attributes);

		ManagedText ft;
		if (validator != null) {
			ft = new ManagedText(text, validator);
		} else {
			ft = new ManagedText(text);
		}

		GridData gdText = produceGridData(0, hSpan);
		gdText.horizontalIndent = 0;

		ft.setLayoutData(gdText);
		if (Layouts.STYLE == SWT.FLAT) {
			// toolkit.paintBordersFor(text.getParent());
		}
		return ft;
	}

	public static GridData produceGridData(int horizontalIndent, int hSpan) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hSpan;
		gd.horizontalIndent = horizontalIndent;
		gd.verticalIndent = 3;
		return gd;
	}

	public static ImageCombo2 createComboWithLabel(Composite parent, String label, boolean editable, int hSpan) {
		Label lbl = new Label(parent, SWT.INHERIT_DEFAULT);
		lbl.setText(label);
		final ImageCombo2 comboField = new ImageCombo2(parent, Layouts.STYLE);
		// comboField.setEditable(editable);
		GridData gdCombo = produceGridData(0, hSpan);
		// gdCombo.horizontalIndent = 10;
		comboField.setLayoutData(gdCombo);
		if (Layouts.STYLE == SWT.FLAT) {
			// toolkit.paintBordersFor(comboField);
		}
		comboField.setBackground(BACKGROUND_COLOR);
		return comboField;
	}

	public static Button createButtonWitLabel(Composite parent, String label, int hSpan, int attributes) {

		Label lblCustomErrorMessage = new Label(parent, SWT.INHERIT_DEFAULT);
		lblCustomErrorMessage.setText(label);
		lblCustomErrorMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		Button btn = new Button(parent, SWT.CHECK);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(hSpan, 1).grab(false, true).hint(SWT.DEFAULT, 1)
				.applyTo(btn);
		return btn;

	}

	public static ManagedText createMultiLineTextWitLabel(Composite parent, String labelName, int hSpan, int attributes,
			IManagedTextInputValidator<String> validator) {

		ManagedText text = createTextWitLabel(parent, labelName, hSpan, attributes, validator);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 80;
		gd.horizontalSpan = hSpan;
		text.getControl().setLayoutData(gd);
		return text;
	}

	public static Label createSeparator(Composite parent) {
		return createSeparator(parent, 1, 1);
	}

	public static Label createSeparator(Composite parent, int hspan, int vspan) {

		for (int i = 0; i < vspan; i++) {
			new Label(parent, SWT.INHERIT_DEFAULT);
		}
		GridDataFactory.fillDefaults().grab(true, false).span(hspan, 1);
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return separator;
	}

	public static Label createLabel(Composite parent, String text) {
		Label lbl = new Label(parent, SWT.INHERIT_DEFAULT);
		lbl.setText(text);
		return lbl;
	}

	public static Button createButton(Composite parent, int style, String text, String ttText, Image icon,
			Runnable runnable) {
		Button button = new Button(parent, style);
		button.setFont(StyleProvider.getSelectedFont(button));
		if (icon != null) {
			button.setImage(icon);
		}
		if (text != null) {
			button.setText(text);
		}
		if (ttText != null) {
			button.setToolTipText(ttText);
		}
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runnable.run();
			}
		});
		return button;
	}

	public static Button createButton(Composite parent, String text, String ttText, Image icon, Runnable runnable) {
		return createButton(parent, SWT.PUSH, text, ttText, icon, runnable);
	}

}
