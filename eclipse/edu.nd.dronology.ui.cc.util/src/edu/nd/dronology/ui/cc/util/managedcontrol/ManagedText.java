package edu.nd.dronology.ui.cc.util.managedcontrol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import edu.nd.dronology.ui.cc.images.StyleProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ManagedText extends AbstractManagedControl<Text, String> {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ManagedText.class);
	private String emptyInfo;

	public ManagedText(Composite parent) {
		this(parent, SWT.NONE);
	}

	public ManagedText(Composite parent, int swtBit) {
		this(new Text(parent, swtBit), ValidatorFactory.createStringValidator(true));
	}

	public ManagedText(Text control) {
		this(control, ValidatorFactory.createStringValidator(true));
	}

	public ManagedText(Text control, TextStyle behaviour) {
		this(control, ValidatorFactory.createStringValidator(true));
		addControlBehaviour(behaviour);
	}

	public ManagedText(Text control, IManagedTextInputValidator<String> validator) {
		super(control, validator);
		if ((control.getStyle() & SWT.MULTI) != 0) {
			control.setToolTipText("Please use:\nSHIFT + CR to insert a linefeed\nCTRL + TAB to insert a tabulator");
		}
		setValue(control.getText());
	}

	@Override
	public void addApplyListener(IApplyListener<String> listener) {
		super.addApplyListener(listener);
	}

	@Override
	public void removeApplyListener(IApplyListener<String> listener) {
		super.removeApplyListener(listener);
	}

	@Override
	public IManagedTextInputValidator<String> getValidator() {
		return super.getValidator();
	}

	@Override
	protected void addModifyListener(ModifyListener modifyListener) {
		getControl().addModifyListener(modifyListener);
	}

	@Override
	protected void removeModifyListener(ModifyListener modifyListener) {
		getControl().removeModifyListener(modifyListener);
	}

	@Override
	protected boolean controlHandlesTraversalKey(int keyCode, int stateMask) {
		int contStyle = getControl().getStyle();
		// in case we wrap a multiline Text Control:
		if ((SWT.MULTI & contStyle) != 0) {
			// 1. use CTRL + TAB to insert a Tab in the TextBox
			boolean isCtrl = isCtrlActive(stateMask);
			if (isCtrl && keyCode == SWT.TAB) {
				return true;
			}
			// 2. use SHIFT + CR to insert a linefeed in the TextBox
			boolean isShift = isShiftActive(stateMask);
			if (isShift && (keyCode == SWT.CR || keyCode == SWT.KEYPAD_CR)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void setControlText(String value) {
		getControl().setText(value != null ? value : ""); //$NON-NLS-1$
	}

	@Override
	protected String getControlText() {
		return getControl().getText();
	}

	@Override
	protected boolean equals(String oldValue, String newValue) {
		return oldValue.equals(newValue);
	}

	@Override
	protected void handleESC(TraverseEvent e) {
		super.handleESC(e);
		getControl().selectAll();
	}

	public void addControlBehaviour(TextStyle behaviour) {
		switch (behaviour) {
		case FILE:
			getControl().addListener(SWT.MouseDoubleClick, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					FileDialog fileDialog = new FileDialog(getControl().getShell());
					String fileName = fileDialog.open();
					if (fileName != null) {
						applyValue(fileName);
					}
				}
			});
			break;
		case URL:
			getControl().addListener(SWT.MouseDoubleClick, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					// if (getControl().getText().length() > 0) {
					// try {
					// BrowserLauncher.open(new URL(getControl().getText()));
					// return;
					// } catch (MalformedURLException e) {
					// logger.error(e);
					// }
					// }
				}
			});
			break;

		default:
			break;
		}
	}

	public void setEditable(boolean editable) {
		setBackground(editable ? StyleProvider.COLOR_WHITE : StyleProvider.COLOR_DISABLED);
		getControl().setEditable(editable);
	}

	public void setFont(Font selectedFont) {
		getControl().setFont(selectedFont);

	}

	// public void setEmptyInfo(String info) {
	// this.emptyInfo=info;
	// }

	//
	// @Override
	// protected void postChange() {
	// if(emptyInfo==null){
	// return;
	// }
	// if(getControl().getText().isEmpty()){
	// getControl().setForeground(StyleProvider.COLOR_DARK_GREEN);
	// getControl().setText(emptyInfo);
	// }
	// }
	//
	//

}
