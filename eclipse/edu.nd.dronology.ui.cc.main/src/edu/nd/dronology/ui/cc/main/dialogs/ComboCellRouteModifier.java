package edu.nd.dronology.ui.cc.main.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import edu.nd.dronology.core.mission.MissionPlan.UavRoutePair;
import edu.nd.dronology.services.core.info.FlightRouteInfo;

public class ComboCellRouteModifier extends ComboBoxCellEditor implements ICellModifier {

	private PlanMissionDialog dialog;
	private List<FlightRouteInfo> routeInfo;
	private List<String> uavList;

	public ComboCellRouteModifier(PlanMissionDialog dialog, List<String> uavList,
			Collection<FlightRouteInfo> routeInfo) {
		this.dialog = dialog;
		this.uavList = uavList;
		this.routeInfo = new ArrayList(routeInfo);

	}

	@Override
	public boolean canModify(Object arg0, String colName) {
		return "0".equals(colName) || "1".equals(colName);
	}

	@Override
	public Object getValue(Object elem, String colName) {
		if (!(elem instanceof UavRoutePair)) {
			return 0;
		}
		UavRoutePair pair = (UavRoutePair) elem;

		if ("0".equals(colName)) {
			getUavName(pair.getUavid());
		}
		return getRouteName(pair.getRouteid());
	}

	private Object getUavName(String routeid) {
		for (int i = 0; i < uavList.size(); i++) {
			String info = uavList.get(i);
			if (info.equals(routeid)) {
				return i;
			}
		}
		return 0;
	}

	private Object getRouteName(String routeid) {
		for (int i = 0; i < routeInfo.size(); i++) {
			FlightRouteInfo info = routeInfo.get(i);
			if (info.getId().equals(routeid)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public void modify(Object chanedElem, String column, Object newValue) {
		TableItem item = (TableItem) chanedElem;
		UavRoutePair pair = (UavRoutePair) item.getData();
		if ("0".equals(column)) {
			String uavid = uavList.get(Integer.parseInt(newValue.toString()));
			pair.setUavid(uavid);
		} else {
			FlightRouteInfo info = routeInfo.get(Integer.parseInt(newValue.toString()));
			pair.setRouteid(info.getId());
		}

		dialog.refresh();
	}

}
