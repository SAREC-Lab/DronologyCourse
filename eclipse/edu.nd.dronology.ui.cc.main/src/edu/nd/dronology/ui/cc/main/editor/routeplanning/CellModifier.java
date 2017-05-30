package edu.nd.dronology.ui.cc.main.editor.routeplanning;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.TableItem;

import edu.nd.dronology.core.util.Coordinate;

public class CellModifier extends TextCellEditor implements ICellModifier {

	private UAVFlightRoutePage page;

	public CellModifier(UAVFlightRoutePage view) {
		this.page = view;

	}

	@Override
	public boolean canModify(Object arg0, String arg1) {

		return true;
	}

	@Override
	public Object getValue(Object elem, String colName) {
		if (!(elem instanceof Coordinate)) {
			return StringUtils.EMPTY;
		}
		Coordinate entry = (Coordinate) elem;
		int col = Integer.parseInt(colName);
		if (col == 0) {
			return Long.toString(entry.getLatitude());
		}
		if (col == 1) {
			return  Long.toString(entry.getLongitude());
		}
		if (col == 2) {
			return  Long.toString(entry.getAltitude());
		}
		return -1;
	}

	@Override
	public void modify(Object chanedElem, String column, Object newValue) {
		TableItem item = (TableItem) chanedElem;
		Coordinate e = (Coordinate) item.getData();
		if (column.equals("0")) {
			try {
				Long i = Long.parseLong(newValue.toString());
				e.setLatitude(i);
			} catch (NumberFormatException ex) {
				System.out.println("Not a number " + newValue);
			}
		} else if (column.equals("1")) {
			try {
				Long i =Long.parseLong(newValue.toString());
				e.setLongitude(i);
			} catch (NumberFormatException ex) {
				System.out.println("Not a number " + newValue);
			}
		} else if (column.equals("2")) {
			try {
				Integer i = Integer.parseInt(newValue.toString());
				e.setAltitude(i);
			} catch (NumberFormatException ex) {
				System.out.println("Not a number " + newValue);
			}
		} else {
			throw new RuntimeException("Not this should really not happen!");
		}
		page.refresh();
	}

}
