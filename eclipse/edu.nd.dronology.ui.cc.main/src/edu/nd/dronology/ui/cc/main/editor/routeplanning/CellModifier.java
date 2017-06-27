package edu.nd.dronology.ui.cc.main.editor.routeplanning;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.TableItem;

import edu.nd.dronology.core.util.LlaCoordinate;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

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
		if (!(elem instanceof LlaCoordinate)) {
			return StringUtils.EMPTY;
		}
		LlaCoordinate entry = (LlaCoordinate) elem;
		int col = Integer.parseInt(colName);
		if (col == 0) {
			return Double.toString(entry.getLatitude());
		}
		if (col == 1) {
			return Double.toString(entry.getLongitude());
		}
		if (col == 2) {
			return Double.toString(entry.getAltitude());
		}
		return -1;
	}

	@Override
	public void modify(Object chanedElem, String column, Object newValue) {
		TableItem item = (TableItem) chanedElem;
		LlaCoordinate e = (LlaCoordinate) item.getData();
		if (column.equals("0")) {
			try {
				Double i = Double.parseDouble(newValue.toString());
				// e.setLatitude(i);
				page.setNewLatitude(e,i);
			} catch (NumberFormatException ex) {
				System.out.println("Not a number " + newValue);
			}
		} else if (column.equals("1")) {
			try {
				Double i = Double.parseDouble(newValue.toString());
				page.setNewLongitude(e,i);
			} catch (NumberFormatException ex) {
				System.out.println("Not a number " + newValue);
			}
		} else if (column.equals("2")) {
			try {
				Double i = Double.parseDouble(newValue.toString());
				page.setNewAltitude(e,i);
			} catch (NumberFormatException ex) {
				System.out.println("Not a number " + newValue);
			}
		} else {
			throw new RuntimeException("Not this should really not happen!");
		}
		page.refresh();
	}

}
