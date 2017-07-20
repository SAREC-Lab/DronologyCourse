package edu.nd.dronology.ui.cc.main.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Table;

import edu.nd.dronology.services.core.info.FlightRouteInfo;

public class MyRouteComboBoxCellEditor extends ComboBoxCellEditor {

	private Collection<FlightRouteInfo> routeList;

	public MyRouteComboBoxCellEditor(Table table, Collection<FlightRouteInfo> routeList) {
		super(table, getList(routeList));
		this.routeList = routeList;
	}

	private static String[] getList(Collection<FlightRouteInfo> uavList) {
		List<String> routeNames = new ArrayList<>();

		for (FlightRouteInfo info : uavList) {
			routeNames.add(info.getName());
		}
		return routeNames.toArray(new String[0]);

	}

}
