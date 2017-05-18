package edu.nd.dronology.ui.cc.main.editor.simulatorscenario.assigndroneviewer;

import java.util.ArrayList;

import java.util.List;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.nd.dronology.ui.cc.util.Pair;

/**
 * 
 * Generic left to right dnd composite
 * 
 * @author Michael Vierhauser
 * 
 */
public abstract class LeftToRightDragAndDropViewer<INPUT> extends AbstractViewerControl {

	protected BasicDndAssignTable dndTable;
	 protected INPUT input;
	private boolean checkViewer = true;

	public LeftToRightDragAndDropViewer(Composite parent) {
		super(parent);
	}

	public LeftToRightDragAndDropViewer(Composite parent, boolean checkViewer) {
		super(parent);
		this.checkViewer = checkViewer;
	}

	@Override
	protected void createContents() {

		dndTable = new BasicDndAssignTable(this, SWT.FLAT, checkViewer);
		dndTable.setHeaders(getLeftHeader(), getRightHeader());

		this.allowDragAndDrop_AssignedRoleAddition(dndTable.getAllElementsViewer().getTable(), dndTable
				.getAssignedElementsViewer().getTable(), true);
		this.allowDragAndDrop_AssignedRoleAddition(dndTable.getAssignedElementsViewer().getTable(), dndTable
				.getAllElementsViewer().getTable(), false);


		dndTable.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		// dndTable.setInput(selection);
		addSelectionListener();
		setProvider();
	}

	protected abstract String getRightHeader();

	protected abstract String getLeftHeader();

	private void allowDragAndDrop_AssignedRoleAddition(final Table source, final Table destination, final boolean addition) {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

		DragSource dragSource = new DragSource(source, operations);
		dragSource.setTransfer(types);

		dragSource.addDragListener(new DragSourceListener() {

			@Override
			public void dragStart(DragSourceEvent event) {
				List<String> data = getDragData();
				if (data.size() > 0) {
					event.doit = true;
				} else {
					event.doit = false;
				}
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				List<String> dragList = getDragData();
				StringBuilder data = new StringBuilder();
				for (String s : dragList) {
					data.append(s + BasicDndAssignTable.SEPARATOR);
				}
				event.data = data.toString();
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
			}

			private List<String> getDragData() {
				TableItem[] selection = source.getSelection();
				List<String> ret = new ArrayList<>();
				if (selection != null && selection.length > 0) {
					for (TableItem item : selection) {
						String txt = item.getText();
						if (txt != null && txt.length() > 0) {
							ret.add(txt);
						}
					}
				}
				return ret;
			}

		});

		DropTarget target = new DropTarget(destination, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {
			@SuppressWarnings("unused")
			TableItem targetItem = null;

			@Override
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				if (event.item != null && event.item instanceof TableItem) {
					this.targetItem = (TableItem) event.item;
				} else {
					this.targetItem = null;
				}
			}

			@Override
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				String[] list = ((String) event.data).split(BasicDndAssignTable.SEPARATOR);
				if (addition) {
					addItems(list);

				} else {
					removeItem(list[0]);
				}
				refresh();
			}
		});
	}

	protected abstract void removeItem(String string);

	protected abstract void addItems(String[] list);

	protected abstract void addItems(List<Object> checked);

	protected void refresh() {
		dndTable.refresh();
	}

	protected abstract Pair<IContentProvider, IBaseLabelProvider> provideLeftProvider();

	protected abstract Pair<IContentProvider, IBaseLabelProvider> provideRightProvider();

	protected void setProvider() {

		Pair<IContentProvider, IBaseLabelProvider> left = provideLeftProvider();
		Pair<IContentProvider, IBaseLabelProvider> right = provideRightProvider();

		dndTable.setAllContentProvider(right.getFirst());
		dndTable.setAllLabelProvider(right.getSecond());

		dndTable.setAssignedContentProvider(left.getFirst());
		dndTable.setAssignedLabelProvider(left.getSecond());

	}

	public void setInput(INPUT input) {
		this.input = input;
		dndTable.setInput(input);
	}

	public void refeshChildren() {
		dndTable.refresh();
	}

	private void addSelectionListener() {
		dndTable.getSelectionButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				List<Object> checked = new ArrayList<>();
				for (TableItem i : dndTable.getAllElementsViewer().getTable().getItems()) {
					if (i.getChecked()) {
						checked.add(i.getData());
					}
				}
				addItems(checked);
			}
		});
	}
}
