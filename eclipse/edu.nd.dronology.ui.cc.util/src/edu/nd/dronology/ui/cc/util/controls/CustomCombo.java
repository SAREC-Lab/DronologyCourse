package edu.nd.dronology.ui.cc.util.controls;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TypedListener;

import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.util.NullUtil;

public class CustomCombo extends Composite {

	private static final int VISIBLE_ITEM_COUNT = 5;
	private Label lblIcon;
	private Table table;
	private Shell popup;
	private boolean hasFocus;
	private Button arrow;
	private Label lblText;
	private Listener listener;
	private Listener filter;

	public CustomCombo(Composite parent, int style) {
		super(parent, style);
		GridLayoutFactory.fillDefaults().spacing(2, 1).numColumns(3).extendedMargins(5, 0, 0, 0).applyTo(this);
		createContents();
	}

	private void createContents() {
		setBackground(StyleProvider.COLOR_WHITE);

		lblIcon = new Label(this, SWT.FLAT);
		lblText = new Label(this, SWT.FLAT);
		lblText.setBackground(StyleProvider.COLOR_WHITE);
		arrow = new Button(this, SWT.ARROW | SWT.DOWN);

		createListener();
		createPopup(0);

		lblIcon.setData(StyleProvider.CSS_TAG, "white");
		lblText.setData(StyleProvider.CSS_TAG, "white");
		setData(StyleProvider.CSS_TAG, "white");
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.CENTER).applyTo(lblText);
		GridDataFactory.fillDefaults().grab(false, true).align(SWT.BEGINNING, SWT.FILL).applyTo(arrow);

	}

	private void createListener() {
		listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (popup == event.widget) {
					popupEvent(event);
					return;
				}
				if (lblText == event.widget) {
					textEvent(event);
					return;
				}
				if (table == event.widget) {
					listEvent(event);
					return;
				}
				if (arrow == event.widget) {
					arrowEvent(event);
					return;
				}
				if (CustomCombo.this == event.widget) {
					comboEvent(event);
					return;
				}
				if (getShell() == event.widget) {
					handleFocus(SWT.FocusOut);
				}
			}
		};
		this.filter = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Shell shell = ((Control) event.widget).getShell();
				if (shell == CustomCombo.this.getShell()) {
					handleFocus(SWT.FocusOut);
				}
			}
		};

		int[] comboEvents = { SWT.Dispose, SWT.Move, SWT.Resize };
		for (int i = 0; i < comboEvents.length; i++) {
			this.addListener(comboEvents[i], this.listener);
		}

		int[] textEvents = { SWT.KeyDown, SWT.KeyUp, SWT.Modify, SWT.MouseDown, SWT.MouseUp, SWT.Traverse,
				SWT.FocusIn };
		for (int i = 0; i < textEvents.length; i++) {
			this.lblText.addListener(textEvents[i], this.listener);
		}

		int[] arrowEvents = { SWT.Selection, SWT.FocusIn };
		for (int i = 0; i < arrowEvents.length; i++) {
			this.arrow.addListener(arrowEvents[i], this.listener);
		}

	}

	void listEvent(Event event) {
		switch (event.type) {
		case SWT.Dispose:
			if (getShell() != this.popup.getParent()) {
				int selectionIndex = this.table.getSelectionIndex();
				this.popup = null;
				this.table = null;
				createPopup(selectionIndex);
			}
			break;
		case SWT.FocusIn: {
			handleFocus(SWT.FocusIn);
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) {
				return;
			}
			drop(false);
			break;
		}
		case SWT.Selection: {
			int index = this.table.getSelectionIndex();
			if (index == -1) {
				return;
			}
			// this.lblText.setText(this.table.getItem(index).getText());
			// this.lblIcon.setImage(this.table.getItem(index).getImage());
			setCurrentVal();
			this.table.setSelection(index);
			Event e = new Event();
			e.time = event.time;
			e.stateMask = event.stateMask;
			e.doit = event.doit;
			notifyListeners(SWT.Selection, e);
			event.doit = e.doit;
			break;
		}
		case SWT.Traverse: {
			switch (event.detail) {
			case SWT.TRAVERSE_RETURN:
			case SWT.TRAVERSE_ESCAPE:
			case SWT.TRAVERSE_ARROW_PREVIOUS:
			case SWT.TRAVERSE_ARROW_NEXT:
				event.doit = false;
				break;
			}
			Event e = new Event();
			e.time = event.time;
			e.detail = event.detail;
			e.doit = event.doit;
			e.character = event.character;
			e.keyCode = event.keyCode;
			notifyListeners(SWT.Traverse, e);
			event.doit = e.doit;
			event.detail = e.detail;
			break;
		}
		case SWT.KeyUp: {
			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyUp, e);
			break;
		}
		case SWT.KeyDown: {
			if (event.character == SWT.ESC) {
				// Escape key cancels popup list
				drop(false);
			}
			if ((event.stateMask & SWT.ALT) != 0
					&& (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN)) {
				drop(false);
			}
			if (event.character == SWT.CR) {
				// Enter causes default selection
				drop(false);
				Event e = new Event();
				e.time = event.time;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.DefaultSelection, e);
			}
			// At this point the widget may have been disposed.
			// If so, do not continue.
			if (isDisposed()) {
				break;
			}
			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyDown, e);
			break;

		}
		}
	}

	public void handleFocus(int type) {
		if (isDisposed()) {
			return;
		}
		switch (type) {
		case SWT.FocusIn: {
			if (this.hasFocus) {
				return;
			}

			this.hasFocus = true;
			Shell shell = getShell();
			shell.removeListener(SWT.Deactivate, this.listener);
			shell.addListener(SWT.Deactivate, this.listener);
			Display display = getDisplay();
			display.removeFilter(SWT.FocusIn, this.filter);
			display.addFilter(SWT.FocusIn, this.filter);
			Event e = new Event();
			notifyListeners(SWT.FocusIn, e);
			break;
		}
		case SWT.FocusOut: {
			if (!this.hasFocus) {
				return;
			}
			Control focusControl = getDisplay().getFocusControl();
			if (focusControl == this.arrow || focusControl == this.table || focusControl == this) {
				return;
			}
			this.hasFocus = false;
			Shell shell = getShell();
			shell.removeListener(SWT.Deactivate, this.listener);
			Display display = getDisplay();
			display.removeFilter(SWT.FocusIn, this.filter);
			Event e = new Event();
			notifyListeners(SWT.FocusOut, e);
			break;
		}
		}
	}

	void comboEvent(Event event) {
		switch (event.type) {
		case SWT.Dispose:
			if (this.popup != null && !this.popup.isDisposed()) {
				this.table.removeListener(SWT.Dispose, this.listener);
				this.popup.dispose();
			}
			Shell shell = getShell();
			shell.removeListener(SWT.Deactivate, this.listener);
			Display display = getDisplay();
			display.removeFilter(SWT.FocusIn, this.filter);
			this.popup = null;
			this.table = null;
			this.arrow = null;
			break;
		case SWT.Move:
			drop(false);
			break;
		case SWT.Resize:
			internalLayout(false);
			break;
		}
	}

	void arrowEvent(Event event) {
		switch (event.type) {
		case SWT.FocusIn: {
			handleFocus(SWT.FocusIn);
			break;
		}
		case SWT.Selection: {
			drop(!isDropped());
			break;
		}
		}
	}

	void textEvent(Event event) {
		switch (event.type) {
		case SWT.FocusIn: {
			handleFocus(SWT.FocusIn);
			break;
		}
		case SWT.KeyDown: {
			if (event.character == SWT.CR) {
				drop(false);
				Event e = new Event();
				e.time = event.time;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.DefaultSelection, e);
			}
			// At this point the widget may have been disposed.
			// If so, do not continue.
			if (isDisposed()) {
				break;
			}

			if (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN) {
				event.doit = false;
				if ((event.stateMask & SWT.ALT) != 0) {
					boolean dropped = isDropped();
					if (!dropped) {
						setFocus();
					}
					drop(!dropped);
					break;
				}

				 int oldIndex = getSelectionIndex();
				 if (event.keyCode == SWT.ARROW_UP) {
				 select(Math.max(oldIndex - 1, 0));
				 } else {
				 select(Math.min(oldIndex + 1, getItemCount() - 1));
				 }
				 if (oldIndex != getSelectionIndex()) {
				 Event e = new Event();
				 e.time = event.time;
				 e.stateMask = event.stateMask;
				 notifyListeners(SWT.Selection, e);
				 }
				// At this point the widget may have been disposed.
				// If so, do not continue.
				if (isDisposed()) {
					break;
				}
			}

			// Further work : Need to add support for incremental search in
			// pop up list as characters typed in text widget

			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyDown, e);
			break;
		}
		case SWT.KeyUp: {
			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyUp, e);
			break;
		}
		case SWT.Modify: {
			this.table.deselectAll();
			Event e = new Event();
			e.time = event.time;
			notifyListeners(SWT.Modify, e);
			break;
		}
		case SWT.MouseDown: {
			if (event.button != 1) {
				return;
			}
			boolean dropped = isDropped();

			if (!dropped) {
				setFocus();
			}
			drop(!dropped);
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) {
				return;
			}
			arrow.setFocus();
			break;
		}
		case SWT.Traverse: {
			switch (event.detail) {
			case SWT.TRAVERSE_RETURN:
			case SWT.TRAVERSE_ARROW_PREVIOUS:
			case SWT.TRAVERSE_ARROW_NEXT:
				// The enter causes default selection and
				// the arrow keys are used to manipulate the list contents so
				// do not use them for traversal.
				event.doit = false;
				break;
			}

			Event e = new Event();
			e.time = event.time;
			e.detail = event.detail;
			e.doit = event.doit;
			e.character = event.character;
			e.keyCode = event.keyCode;
			notifyListeners(SWT.Traverse, e);
			event.doit = e.doit;
			event.detail = e.detail;
			break;
		}
		}
	}

	public int getItemCount() {
		checkWidget();
		return this.table.getItemCount();
	}

	void popupEvent(Event event) {
		switch (event.type) {
		case SWT.Paint:
			// draw black rectangle around list
			Rectangle listRect = this.table.getBounds();
			Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
			event.gc.setForeground(black);
			event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
			break;
		case SWT.Close:
			event.doit = false;
			drop(false);
			break;
		case SWT.Deactivate:
			drop(false);
			break;
		}
	}


	
	private void setCurrentVal() {
		int index = table.getSelectionIndex();
		TableItem item = table.getItem(index);
		lblText.setText(item.getText());
		lblIcon.setImage(item.getImage());
	}

	void internalLayout(boolean changed) {
		if (isDropped()) {
			drop(false);
		}
		Rectangle rect = getClientArea();
		int width = rect.width + 20;
		int height = rect.height;
		// Point arrowSize = this.arrow.computeSize(SWT.DEFAULT, height,
		// changed);
		// this.comboComposite.setBounds(0, 0, width - arrowSize.x, height);
		// this.arrow.setBounds(width - arrowSize.x, 0, arrowSize.x,
		// arrowSize.y);

		arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
	}

	protected void drop(boolean drop) {
		if (drop == isDropped()) {
			return;
		}

		if (!drop) {
			this.popup.setVisible(false);
			if (!isDisposed() && this.arrow.isFocusControl()) {
				this.setFocus();
			}
			return;
		}

		if (getShell() != this.popup.getParent()) {
			int selectionIndex = this.table.getSelectionIndex();
			// this.table.removeListener(SWT.Dispose, this.listener);
			this.popup.dispose();
			this.popup = null;
			this.table = null;
			createPopup(selectionIndex);
		}

		Point size = getSize();
		int itemCount = this.table.getItemCount();
		itemCount = (itemCount == 0) ? VISIBLE_ITEM_COUNT : Math.min(VISIBLE_ITEM_COUNT, itemCount);
		int itemHeight = this.table.getItemHeight() * itemCount;
		Point listSize = this.table.computeSize(SWT.DEFAULT, itemHeight, false);
		this.table.setBounds(1, 1, Math.max(size.x - 2, listSize.x), listSize.y);
		int index = this.table.getSelectionIndex();
		if (index != -1) {
			this.table.setTopIndex(index);
		}
		Display display = getDisplay();
		Rectangle listRect = this.table.getBounds();
		Rectangle parentRect = display.map(getParent(), null, getBounds());
		Point comboSize = getSize();
		Rectangle displayRect = getMonitor().getClientArea();
		int width = Math.max(comboSize.x, listRect.width + 2);
		int height = listRect.height + 2;
		int x = parentRect.x;
		int y = parentRect.y + comboSize.y;
		if (y + height > displayRect.y + displayRect.height) {
			y = parentRect.y - height;
		}
		this.popup.setBounds(x, y, width, height);
		this.popup.setVisible(true);

		this.table.setFocus();
	}

	boolean isDropped() {
		return this.popup.getVisible();
	}

	void createPopup(int selectionIndex) {
		popup = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(0, 0, 0, 0).applyTo(popup);
		table = new Table(popup, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, true).applyTo(table);
		int[] popupEvents = { SWT.Close, SWT.Paint, SWT.Deactivate };
		for (int i = 0; i < popupEvents.length; i++) {
			popup.addListener(popupEvents[i], this.listener);
		}
		int[] listEvents = { SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn,
				SWT.Dispose };
		for (int i = 0; i < listEvents.length; i++) {
			table.addListener(listEvents[i], this.listener);
		}
		if (selectionIndex != -1) {
			table.setSelection(selectionIndex);
		}
	}

	@Override
	public void setFont(Font font) {
		lblText.setFont(font);
	}

	public void add(Image image, String string) {
		checkWidget();
		NullUtil.checkNull(string);

		TableItem newItem = new TableItem(this.table, SWT.FILL);
		newItem.setFont(StyleProvider.getSelectedFont());
		newItem.setText(string);//
		if (image != null) {
			newItem.setImage(image);
			lblIcon.setImage(image);
		}
	}

	public void select(int index) {
		table.select(index);
		setCurrentVal();

	}

	public void setEditable(boolean editable) {
		// cmb.setEditable(editable);

	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection, listener);
	}


	public int getSelectionIndex() {
		checkWidget();
		return this.table.getSelectionIndex();
	}


	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		int width = 0, height = 0;
		String[] items = getStringsFromTable();
		int textWidth = 0;
		GC gc = new GC(this);
		int spacer = gc.stringExtent(" ").x; //$NON-NLS-1$
		for (int i = 0; i < items.length; i++) {
			textWidth = Math.max(gc.stringExtent(items[i]).x, textWidth);
		}
		gc.dispose();
		Point textSize = lblText.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		Point arrowSize = this.arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		Point listSize = this.table.computeSize(wHint, SWT.DEFAULT, changed);
		int borderWidth = getBorderWidth();

		// height = Math.max(hHint, Math.max(textSize.y, arrowSize.y) + 2 *
		// borderWidth);
		height = Math.max(hHint, arrowSize.y + 2 * borderWidth + 2);
		int sumWidht = Math.max(textWidth + 2 * spacer + arrowSize.x + 2 * borderWidth, listSize.x);
		width = Math.max(wHint, sumWidht);
		Point th = lblText.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return new Point(width + 40, Math.max(height, th.y) + 2);
	}

	String[] getStringsFromTable() {
		String[] items = new String[this.table.getItems().length];
		for (int i = 0, n = items.length; i < n; i++) {
			items[i] = this.table.getItem(i).getText();
		}
		return items;
	}

}
