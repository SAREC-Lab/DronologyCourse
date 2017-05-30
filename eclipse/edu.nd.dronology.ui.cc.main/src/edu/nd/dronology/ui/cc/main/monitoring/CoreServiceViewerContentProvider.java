package edu.nd.dronology.ui.cc.main.monitoring;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.nd.dronology.services.core.api.IServiceInstance;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;

public class CoreServiceViewerContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<IServiceInstance> server = new ArrayList<>();
	//	if (!RemoteConnector.isConnected()) {
		//	return new Object[0];
	//	}

		try {
			IRemoteManager manager =ServiceProvider.getBaseServiceProvider().getRemoteManager();
			if (manager != null) {
				List<ServiceInfo> servers = manager.getAllServices();
				List<ServiceInfo> res = new ArrayList<>(servers);
				Collections.sort(res);
				return res.toArray();
			}
			return new Object[0];
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Object[0];
		} catch (DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Object[0];
		}
	}
}
