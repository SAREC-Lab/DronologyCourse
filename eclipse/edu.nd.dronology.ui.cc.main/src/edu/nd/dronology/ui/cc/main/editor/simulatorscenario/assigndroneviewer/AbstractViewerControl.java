package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.nd.dronology.ui.cc.util.Layouts;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public abstract class AbstractViewerControl extends Composite {


	public AbstractViewerControl(Composite parent) {
		super(parent, SWT.FLAT);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumHeight = 150;
		 gd.grabExcessVerticalSpace = true;
		 gd.grabExcessHorizontalSpace = true;
		setLayoutData(gd);
		setLayout(Layouts.getZeroMarginLayout(1, false));
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	protected Composite createGroup(Composite parent, int numCols) {
		Composite group = new Composite(parent, SWT.BORDER);
		GridLayout gl = new GridLayout();
		gl.numColumns = numCols;
		group.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		group.setLayoutData(gd);
		return group;
	}

	protected abstract void createContents();

}
