package edu.nd.dronology.ui.cc.main.editor.base;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import edu.nd.dronology.services.core.items.IPersistableItem;

public abstract class AbstractMandatoryItemComposite<ITEM extends IPersistableItem> extends Composite {

	protected Group mainContainer;


	protected IUAVEditorPage<ITEM> page;


	public AbstractMandatoryItemComposite(Composite parent, IUAVEditorPage<ITEM> page) {
		super(parent, SWT.FLAT);
		this.page = page;


		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(this);
		setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		createContents();

	}

	protected void createContents() {
		mainContainer = new Group(this, SWT.FLAT);
		mainContainer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(mainContainer);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(mainContainer);
		mainContainer.setText("Mandatory Item Attributes");
		createFields();

	}

	public void setValues() {
		doSetValuesToFields();
	}

	protected abstract void createFields();

	protected abstract void doSetValuesToFields();

	public abstract void setValuesToBundle();

}
