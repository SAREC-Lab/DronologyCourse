package edu.nd.dronology.ui.cc.main.monitoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;

import edu.nd.dronology.core.status.DroneStatus;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.ui.cc.main.remote.ServiceProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class DroneSetupContentProvider implements ITreeContentProvider {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DroneSetupContentProvider.class);

	@Override
	public Object[] getElements(Object inputElement) {

		try {
			IDroneSetupRemoteService service = (IDroneSetupRemoteService) ServiceProvider.getBaseServiceProvider()
					.getRemoteManager().getService(IDroneSetupRemoteService.class);

			Map<String, DroneStatus> drones = service.getDrones();
			
			ArrayList drns = new ArrayList<>(drones.values());
			Collections.sort(drns, new DroneComparator());
			//System.out.println(drns.size());
			return drns.toArray();

		} catch (Exception e) {
			LOGGER.error(e);
		}

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

}
