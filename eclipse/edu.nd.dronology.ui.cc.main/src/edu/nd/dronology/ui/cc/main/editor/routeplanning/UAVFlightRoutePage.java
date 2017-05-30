package edu.nd.dronology.ui.cc.main.editor.routeplanning;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractItemEditor;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractMandatoryItemComposite;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractUAVEditorPage;
import edu.nd.dronology.ui.cc.main.editor.base.IUAVEditorPage;
import edu.nd.dronology.ui.cc.util.controls.ControlCreationHelper;
import edu.nd.dronology.ui.cc.util.managedcontrol.ManagedText;

public class UAVFlightRoutePage extends AbstractUAVEditorPage<IFlightRoute> implements IUAVEditorPage<IFlightRoute> {

	private ManagedText txtDesc;
	private TableViewer coordinates;

	public UAVFlightRoutePage(AbstractItemEditor editor, String id, String title) {
		super(editor, id, title);

	}

	@Override
	protected void doCreateLabel(Composite parent) {
		headerText.setText("FlightRoute Details");
		headerIcon.setImage(ImageProvider.IMG_FLIGHTROUTE_24);
	}

	@Override
	protected void doSetValues() {
		txtDesc.setValue(editor.getItem().getDescription());
		coordinates.setInput(editor.getItem());
	}

	@Override
	protected AbstractMandatoryItemComposite doCreateMandatoryComposite(Composite parent) {
		return new MandatoryFlightRouteDataComposite(parent, this);
	}

	@Override
	protected void doCreateOptionalParts(Composite container) {

		txtDesc = ControlCreationHelper.createMultiLineTextWitLabel(container, "Description", 2, SWT.BORDER, null);

		txtDesc.addApplyListener((value) -> {
			getItem().setDescription(value);
		});

	}

	@Override
	protected void doCreateAdditionalParts(Composite container) {
		// createRequirementsText(container);
		Composite coordinateContainer = new Composite(container, SWT.FLAT);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(coordinateContainer);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(coordinateContainer);
		String[] columnNames = new String[] { "0", "1", "2" };

		Button addCoordinate = new Button(coordinateContainer, SWT.PUSH);
		Button removeCoordinate = new Button(coordinateContainer, SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(addCoordinate);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(removeCoordinate);

		addCoordinate.setImage(ImageProvider.IMG_ADD_24);
		removeCoordinate.setImage(ImageProvider.IMG_CANCEL_24);

		addCoordinate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editor.getItem().addCoordinate(new Coordinate(0, 0, 0));
				coordinates.refresh();
			}

		});

		removeCoordinate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection sel = (StructuredSelection) coordinates.getSelection();
				editor.getItem().removeCoordinate((Coordinate) sel.getFirstElement());
				coordinates.refresh();
			}

		});

	

		coordinates = new TableViewer(coordinateContainer, SWT.BORDER | SWT.FULL_SELECTION);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(coordinates.getTable());

		TableViewerColumn col0 = new TableViewerColumn(coordinates, SWT.FLAT);
		TableViewerColumn col1 = new TableViewerColumn(coordinates, SWT.FLAT);
		TableViewerColumn col2 = new TableViewerColumn(coordinates, SWT.FLAT);

		col0.getColumn().setText("Latitude");
		col1.getColumn().setText("Longitude");
		col2.getColumn().setText("Altitude");

		col0.getColumn().setWidth(150);
		col1.getColumn().setWidth(150);
		col2.getColumn().setWidth(150);

		coordinates.setContentProvider(new CoordinatesContentProvider());
		coordinates.setLabelProvider(new CoordinatesLabelProvider());
		coordinates.setColumnProperties(columnNames);
		coordinates.getTable().setFont(StyleProvider.FONT_MONOSPACED_11);

		CellEditor[] editors = new CellEditor[3];

		editors[0] = createEditor();
		editors[1] = createEditor();
		editors[2] = createEditor();

		coordinates.setCellEditors(editors);
		coordinates.setCellModifier(new CellModifier(this));

		coordinates.getTable().setLinesVisible(true);
		coordinates.getTable().setHeaderVisible(true);

		coordinates.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				removeCoordinate.setEnabled(coordinates.getTable().getSelectionIndex() != 0);
			}
		});
		

	}

	private CellEditor createEditor() {
		TextCellEditor editor = new TextCellEditor(coordinates.getTable());
		Text tx0 = (Text) editor.getControl();
		tx0.setFont(StyleProvider.FONT_MONOSPACED_10);
		tx0.setBackground(StyleProvider.COLOR_CONSTRAINT);

		return editor;
	}


	@Override
	public void layout() {
		container.layout();

	}

	@Override
	public void refresh() {
		coordinates.refresh();

	}

}
