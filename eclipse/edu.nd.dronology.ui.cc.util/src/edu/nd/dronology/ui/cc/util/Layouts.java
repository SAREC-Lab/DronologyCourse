package edu.nd.dronology.ui.cc.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;

public class Layouts {
	public final static int EXPANDED_TREE_HEIGHT = 200;
	public static final int EXPANDED_HEIGHT = 200;
	public static final int NORMAL_HEIGHT = 20;
	public static final GridData gd_horiz = new GridData(GridData.FILL_HORIZONTAL);
	public static final GridData gd_both = new GridData(GridData.FILL_BOTH);
	public static final GridData gd_vert = new GridData(GridData.FILL_VERTICAL);
	public static int STYLE = SWT.BORDER;

	public static Layout getZeroMarginLayout(int num, boolean equal) {
		GridLayout layout = new GridLayout(num, equal);
		layout.marginBottom = layout.marginHeight = layout.marginLeft = layout.marginRight = layout.marginTop = layout.marginWidth = layout.verticalSpacing = 2;
		return layout;
	}
}
