package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.util.Layouts;

/**
 * Basic Drag&Drop assignment composite
 * 
 * @author Michael Vierhauser
 * 
 */
public class BasicDndAssignTable extends Composite {

	public static final String SEPARATOR = "&"; //$NON-NLS-1$

	protected Object input;

	private boolean checkBoxes;

	public BasicDndAssignTable(Composite parent, int style) {
		this(parent, style, true);
	}

	public BasicDndAssignTable(Composite parent, int style, boolean checkBoxes) {
		super(parent, style);
		this.checkBoxes = checkBoxes;
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		createControls();
	}

	private TableViewer leftViewer;
	private TableViewer rightViewer;
	private TableViewerColumn assignedCol;
	private TableViewerColumn allCol;

	private Label selectControl;

	private int[] formWeigth = new int[] { 13, 1, 13 };

	private SashForm tableForm;

	private Composite rightComposite;

	private Composite leftComposite;

	public TableViewer getAssignedElementsViewer() {
		return leftViewer;
	}

	public TableViewer getAllElementsViewer() {
		return rightViewer;
	}

	private void createControls() {

		setLayout();
		createLeftViewer();
		createSeparator();
		createRightViewer();


		tableForm.setWeights(formWeigth);
		tableForm.SASH_WIDTH = 3;
 
 
 
 
	}

	private void createSeparator() {

		Composite iconComposite = new Composite(tableForm, SWT.FLAT);
		iconComposite.setLayout(Layouts.getZeroMarginLayout(1, false));
		selectControl = new Label(iconComposite, SWT.NONE);
		selectControl.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		GridData d = (GridData) selectControl.getLayoutData();
		d.horizontalIndent = 0;
		selectControl.setImage(ImageProvider.IMG_SWITCH);
		iconComposite.setLayoutData(new GridData());
		
		selectControl.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		iconComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	private void createRightViewer() {

		
		rightComposite = new Composite(tableForm, SWT.FLAT);
		rightComposite.setLayout(Layouts.getZeroMarginLayout(1, false));
		rightComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		rightViewer = new TableViewer(rightComposite, SWT.MULTI | Layouts.STYLE | (checkBoxes ? SWT.CHECK : 0));

		Table rightTable = rightViewer.getTable();
		rightTable.setToolTipText("Available Elements");
		rightTable.setLinesVisible(false);
		rightTable.setBackground(StyleProvider.COLOR_DISABLED);
		rightTable.setLayout(new GridLayout());
		rightTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		// allCol = new TableViewerColumn(rightViewer, SWT.FULL_SELECTION);
		rightTable.setHeaderVisible(false);
		// allCol.getColumn().setText("Select");
		// allCol.getColumn().setWidth(250);
		// allLayout.setColumnData(allCol.getColumn(), new ColumnWeightData(1, 50, false));
		// allTableComposite.setLayout(allLayout);
		tableForm.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		rightComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		rightViewer.getTable().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	private void createLeftViewer() {
		TableColumnLayout assignedLayout = new TableColumnLayout();
		leftComposite = new Composite(tableForm, SWT.NONE);
		leftComposite.setLayout(Layouts.getZeroMarginLayout(1, false));
		leftComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create first viewer
		leftViewer = new TableViewer(leftComposite, SWT.BORDER);
		final Table leftTable = leftViewer.getTable();
		leftTable.setLinesVisible(true);
		leftTable.setLayout(new GridLayout());
		leftTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		assignedCol = new TableViewerColumn(leftViewer, SWT.FULL_SELECTION);
		assignedCol.getColumn().setText("Selected");
		assignedCol.getColumn().setWidth(250);
		leftTable.setHeaderVisible(true);
		leftTable.setToolTipText("Selected");

		assignedLayout.setColumnData(assignedCol.getColumn(), new ColumnWeightData(1, 50, false));
		// assignedTableComposite.setLayout(assignedLayout);

		leftTable.addListener(SWT.Resize, new Listener()
    {
        @Override
				public void handleEvent(Event event)
        {
        	assignedCol.getColumn().setWidth(leftTable.getClientArea().width);
        }
    });
		leftComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		leftViewer.getTable().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	private void setLayout() {
		GridData gd_contailer = new GridData(GridData.FILL_BOTH);
		setLayout(Layouts.getZeroMarginLayout(1, false));
		gd_contailer.grabExcessHorizontalSpace = true;
		gd_contailer.grabExcessVerticalSpace = true;
		setLayoutData(gd_contailer);
		setBackground(new Color(null, new RGB(255, 255, 255)));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		tableForm = new SashForm(this, SWT.HORIZONTAL | SWT.FLAT);
		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.heightHint = 150;
		tableForm.setLayout((Layouts.getZeroMarginLayout(1, false)));
		tableForm.setLayoutData(gd);

	}

	public void setInput(Object element) {
		if (element == null) {
			return;
		} else {
			this.input = element;

			setUserDetails(element);
		}
	}

	private void setUserDetails(Object element) {
		rightViewer.setInput(element);
		leftViewer.setInput(element);
	}

	public void refresh() {
		rightViewer.refresh();
		leftViewer.refresh();

	}

	public void setAllContentProvider(IContentProvider provider) {
		rightViewer.setContentProvider(provider);
	}

	public void setAssignedContentProvider(IContentProvider provider) {
		leftViewer.setContentProvider(provider);
	}

	public void setAllLabelProvider(IBaseLabelProvider provider) {
		rightViewer.setLabelProvider(provider);
	}

	public void setAssignedLabelProvider(IBaseLabelProvider provider) {
		leftViewer.setLabelProvider(provider);
	}

	public void setHeaders(String assignedText, String allText) {
		assignedCol.getColumn().setText(assignedText);
		// allCol.getColumn().setText(allText);
	}

	public Control getSelectionButton() {
		return selectControl;
	}

}