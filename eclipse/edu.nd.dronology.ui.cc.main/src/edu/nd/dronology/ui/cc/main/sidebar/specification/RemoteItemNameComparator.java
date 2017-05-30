package edu.nd.dronology.ui.cc.main.sidebar.specification;

import java.util.Comparator;

import edu.nd.dronology.services.core.info.RemoteInfoObject;

public class RemoteItemNameComparator implements Comparator<RemoteInfoObject> {

	@Override
	public int compare(RemoteInfoObject o1, RemoteInfoObject o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
