//package edu.nd.dronology.ui.cc.main.dialogs;
//
//import java.util.List;
//
//import org.eclipse.jface.viewers.ComboBoxCellEditor;
//import org.eclipse.jface.viewers.ICellModifier;
//import org.eclipse.swt.widgets.TableItem;
//
//import edu.nd.dronology.core.mission.MissionPlan.UavRoutePair;
//
//public class ComboCellUAVModifier extends ComboBoxCellEditor implements ICellModifier {
//
//	private PlanMissionDialog dialog;
//	private List<String> status;
//
//	public ComboCellUAVModifier(PlanMissionDialog dialog, List<String> uavList) {
//		this.dialog = dialog;
//		this.status = uavList;
//
//	}
//
//	@Override
//	public boolean canModify(Object arg0, String colName) {
//		return "0".equals(colName);
//	}
//
//	@Override
//	public Object getValue(Object elem, String colName) {
//		if (!(elem instanceof UavRoutePair)) {
//			return 0;
//		}
//		UavRoutePair pair = (UavRoutePair) elem;
//
//		return getName(pair.getRouteid());
//
//	}
//
//	private Object getName(String routeid) {
//		for (int i = 0; i < status.size(); i++) {
//			String info = status.get(i);
//			if (info.equals(routeid)) {
//				return i;
//			}
//		}
//		return 0;
//	}
//
//	@Override
//	public void modify(Object chanedElem, String column, Object newValue) {
//		TableItem item = (TableItem) chanedElem;
//		UavRoutePair pair = (UavRoutePair) item.getData();
//		String uavid = status.get(Integer.parseInt(newValue.toString()));
//		pair.setRouteid(uavid);
//		dialog.refresh();
//	}
//
//}
