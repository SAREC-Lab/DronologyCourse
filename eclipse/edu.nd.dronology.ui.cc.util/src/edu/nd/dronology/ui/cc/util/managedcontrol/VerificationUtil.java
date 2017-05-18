package edu.nd.dronology.ui.cc.util.managedcontrol;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;


public class VerificationUtil {



	/** The error overlay image used in control decorations. */
	private static Image decoErrorImage = FieldDecorationRegistry.getDefault()
			.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();



	/**
	 * Creates an error decoration for the given control. Clients must handle the visibility of the decoration with {@link ControlDecoration#setShowHover(boolean)} and {@link ControlDecoration#hide()}.
	 * 
	 * @param c
	 *          The control decoration is created for this control.
	 * @return The create control decoration.
	 */
	public static ControlDecoration createDecoration(Control c) {
		ControlDecoration deco = new ControlDecoration(c, SWT.RIGHT | SWT.BOTTOM);
		deco.setMarginWidth(2);
		deco.setShowOnlyOnFocus(true);
		deco.setImage(decoErrorImage);
		deco.hide();
		return deco;
	}

	/**
	 * Suppresses default constructor, ensuring non-instantiability.
	 */
	private VerificationUtil() {
	}
}
