package edu.nd.dronology.ui.cc.main.simulator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import edu.nd.dronology.services.core.info.FlightRouteInfo;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class FlightRouteSelectionDialog extends Dialog {
	private Table table;
	private FlightRouteInfo selectedElement;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public FlightRouteSelectionDialog(Shell parentShell) {
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
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.marginWidth = 0;
		gl_container.marginHeight = 0;
		gl_container.horizontalSpacing = 0;
		container.setLayout(gl_container);

		TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer.setContentProvider(new FlightRouteSelectorContentProvider());
		tableViewer.setLabelProvider(new FlightRouteSelectorLabelProvider());

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (tableViewer.getSelection() instanceof StructuredSelection) {
					StructuredSelection ss = (StructuredSelection) tableViewer.getSelection();
					selectedElement = (FlightRouteInfo) ss.getFirstElement();
				}

			}
		});

		tableViewer.setInput("");
		return container;
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
		return new Point(450, 300);
	}

	public FlightRouteInfo getSelectedElement() {
		return selectedElement;
	}
}
