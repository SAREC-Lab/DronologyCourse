package edu.nd.dronology.ui.cc.main.editor.routeplanning;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.nd.dronology.core.util.Coordinate;
import edu.nd.dronology.ui.cc.images.ImageProvider;

public class CoordinatesLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if(columnIndex==0){
			return ImageProvider.IMG_DRONE_WAYPOINT;
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Coordinate cord = (Coordinate) element;
		switch (columnIndex) {
			case 0: return Long.toString(cord.getLatitude());
			case 1: return Long.toString(cord.getLongitude());
			case 2: return Long.toString(cord.getAltitude());
		
			default:
				return "-1";
		}
	}




}
