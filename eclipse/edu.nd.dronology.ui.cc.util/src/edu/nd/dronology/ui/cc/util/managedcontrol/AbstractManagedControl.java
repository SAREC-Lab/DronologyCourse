package edu.nd.dronology.ui.cc.util.managedcontrol;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import edu.nd.dronology.ui.cc.images.StyleProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;


public abstract class AbstractManagedControl<C extends Control, T> {

	public static final String FORM_CONTROL_KEY = "FORM_CONTROL_KEY";

	private final CopyOnWriteArrayList<DisposeListener> dispListeners = new CopyOnWriteArrayList<DisposeListener>();

	private static final ILogger LOGGER = LoggerProvider.getLogger(AbstractManagedControl.class);


	private C control;
	private Color bgColor;
	private T value;
	private boolean disallowErrorInputs;

	private IManagedTextInputValidator<T> validator;
	private final ListenerList applyListeners = new ListenerList();

	private final ManagedTextVerifier verifier;

	private final ModifyListener modifyListener;

	
	public final void addDisposeListener(DisposeListener listener) {
		if (listener != null) {
			dispListeners.addIfAbsent(listener);
		}
	}

	
	public final void removeDisposeListener(DisposeListener listener) {
		dispListeners.remove(listener);
	}


	public final boolean isDisposed() {
		return control.isDisposed();
	}

	private final void notifyDisposeListener(DisposeEvent evt) {
		if (evt == null) {
			evt = new DisposeEvent(new Event());
		}
		for (DisposeListener l : dispListeners) {
			try {
				l.widgetDisposed(evt);
			} catch (Exception ex) {
				LOGGER.error("Caught Exception while notifying DisposeListener '" + l + "'", ex);
			}
		}
	}

	/** Remove all apply listeners when the control is disposed. */
	private DisposeListener disposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent e) {
			dispose();
			notifyDisposeListener(e);
		}
	};


	
	protected void switchToModifiedColor() {
		control.setBackground(StyleProvider.EXTRA_LIGHT_BLUE);
	}


	public AbstractManagedControl(C control, IManagedTextInputValidator<T> validator) {
		if (control == null || validator == null) {
			throw new IllegalArgumentException("control and validator must not be null"); //$NON-NLS-1$
		}
		disallowErrorInputs = true;
		this.control = control;
		setDefaultBackground();
		this.validator = validator;
		verifier = new ManagedTextVerifier(control, validator);
		modifyListener = createModifyListener(verifier);

		control.addFocusListener(focusListener);
		control.addTraverseListener(traverseListener);
		control.addDisposeListener(disposeListener);
		control.setData(AbstractManagedControl.FORM_CONTROL_KEY, this);
		addModifyListener(modifyListener);
		initializeListeners();
	}

	
	protected ModifyListener createModifyListener(final ManagedTextVerifier verify) {
		return new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				verify.validate(getControlText());
				switchToModifiedColor();
				//postChange();
			}
		};
	}


	//protected abstract void postChange();


	public void setValue(T value) {
		setControlTextWithOutNotify(value);
		applyValue(value);
		verifier.validate(getControlText());
	}


	public void allowErrorInput(boolean allowErrorInput) {
		disallowErrorInputs = !allowErrorInput;
	}

	protected void setControlTextWithOutNotify(T value) {
		synchronized (getControl()) {
			removeModifyListener(modifyListener);
			setControlText(value);
			addModifyListener(modifyListener);
		}
	}

	
	protected void initializeListeners() {
		// does nothing by default
	}


	public T getValue() {
		return value;
	}


	public C getControl() {
		return control;
	}


	public String getToolTipText() {
		return control.getToolTipText();
	}


	public void setToolTipText(String string) {
		control.setToolTipText(string);
	}

	public void setLayoutData(Object layoutData) {
		control.setLayoutData(layoutData);
	}


	public Color getBackground() {
		return bgColor;
	}

	public void setBackground(Color color) {
		this.bgColor = color;
		control.setBackground(bgColor);
	}

	public void setDefaultBackground() {
		setBackground(StyleProvider.CONTROL_NOT_MODIFIED);
	}

	public void addApplyListener(IApplyListener<T> applyListener) {
		applyListeners.add(applyListener);
	}

	public void removeApplyListener(IApplyListener<T> applyListener) {
		applyListeners.remove(applyListener);
	}


	public boolean isOk() {
		return verifier.isOk();
	}

	public void validate() {
		verifier.validate(getControlText());
	}


	protected IManagedTextInputValidator<T> getValidator() {
		return validator;
	}

	protected ManagedTextVerifier getVerifier() {
		return verifier;
	}

	protected boolean controlHandlesTraversalKey(int keyCode, int stateMask) {
		return false;
	}

	protected void handleCR(TraverseEvent e) {
		e.detail = SWT.TRAVERSE_TAB_NEXT;
		T value = validator.getValue();
		applyValue(value);
	}


	protected void handleTAB(TraverseEvent e) {
		applyValue(validator.getValue());
	}

	protected void handleESC(TraverseEvent e) {
		setControlText(value);
		control.setBackground(bgColor);
	}


	@SuppressWarnings("unchecked")
	protected void applyValue(T newValue) {
		// if activated: if there is validation error disallow adding a new input
		// not use the null value (replace with "old" correct value)
		if (disallowErrorInputs && newValue == null) {
			setControlText(getInternalValue());
			control.setBackground(bgColor);
		} else {
			control.setBackground(bgColor);
			if (getInternalValue() == null || !equals(getInternalValue(), newValue)) {
				setInternalValue(newValue);
				for (Object listener : applyListeners.getListeners()) {
					try {
						((IApplyListener<T>) listener).applyValue(newValue);
					} catch (Exception e) {
						LOGGER.error("Apply Listener: " + listener + " has thrown an exception!", e);
					}
				}
			}
		}
	}

	protected void setInternalValue(T value) {
		this.value = value;
	}

	protected T getInternalValue() {
		return this.value;
	}

	/**
	 * Called when the wrapped control is disposed.
	 */
	protected void dispose() {
		applyListeners.clear();
	}

	/**
	 * Helper method to add the modifyListener to the concrete SWT control.
	 */
	protected abstract void addModifyListener(ModifyListener modifyListener);

	/**
	 * Helper method to add the modifyListener to the concrete SWT control.
	 */
	protected abstract void removeModifyListener(ModifyListener modifyListener);

	/**
	 * Helper method to set the value to the concrete SWT control.
	 */
	protected abstract void setControlText(T value);

	/**
	 * Helper method to retrieve the current string value from the concrete SWT control.
	 */
	protected abstract String getControlText();

	/**
	 * @return <code>True</code> if oldValue and newValue are equal.
	 */
	protected abstract boolean equals(T oldValue, T newValue);

	/**
	 * For a given state mask determines if the SHIFT key is active
	 * 
	 * @param stateMask
	 *          state mask for some key event
	 * @return True in case SHIFT was pressed
	 */
	protected static boolean isShiftActive(int stateMask) {
		if ((SWT.SHIFT & stateMask) != 0) {
			return true;
		}
		return false;
	}

	/**
	 * For a given state mask determines if the CTRL key is active
	 * 
	 * @param stateMask
	 *          state mask for some key event
	 * @return True in case CTRL was pressed
	 */
	protected static boolean isCtrlActive(int stateMask) {
		if ((SWT.CTRL & stateMask) != 0) {
			return true;
		}
		return false;
	}

	/**
	 * For a given state mask determines if the ALT key is active
	 * 
	 * @param stateMask
	 *          state mask for some key event
	 * @return True in case ALT was pressed
	 */
	protected static boolean isAltActive(int stateMask) {
		if ((SWT.ALT & stateMask) != 0) {
			return true;
		}
		return false;
	}

	/**
	 * Focus listener that accepts the user input when the control loses the focus.
	 */
	private final FocusListener focusListener = new FocusAdapter() {

		@Override
		public void focusLost(FocusEvent e) {
			applyValue(validator.getValue());
		}
	};

	/**
	 * Traverse listener that accepts the user input when CR or TAB is pressed. If the user presses ESC, the current input text is reverted to the last accepted value.
	 */
	private final TraverseListener traverseListener = new TraverseListener() {

		@Override
		public void keyTraversed(TraverseEvent e) {
			if (controlHandlesTraversalKey(e.keyCode, e.stateMask)) {
				e.doit = false;
			} else {
				e.doit = true;
				switch (e.keyCode) {
					case SWT.ESC: {
						handleESC(e);
					}
						break;
					case SWT.CR:
					case SWT.KEYPAD_CR: {
						handleCR(e);
					}
						break;
					case SWT.TAB: {
						handleTAB(e);
					}
						break;
				}
			}
		}
	};


}
