package edu.nd.dronology.ui.cc.main.monitoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.nd.dronology.services.core.info.FlightInfo;
import edu.nd.dronology.services.core.info.FlightPlanInfo;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class FlightManagerContentProvider implements ITreeContentProvider {

	private static final ILogger LOGGER = LoggerProvider.getLogger(FlightManagerContentProvider.class);

	public static final String FLYING = "Currently-Flying";
	public static final String PENDING = "Pending-Flights";
	public static final String AWAITING = "Awaiting-Flights";
	public static final String COMPLETED = "Completed-Flights";

	private FlightInfo input;

	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof String || input == null) {
			return new Object[0];
		}
		if (inputElement instanceof FlightInfo) {
			return new String[] { FLYING, PENDING, AWAITING, COMPLETED };
		}

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement == FLYING) {
			return input.getCurrentFlights().toArray();
		}

		if (parentElement == PENDING) {
			return input.getPendingFlights().toArray();
		}

		if (parentElement == AWAITING) {
			return input.getAwaitingFlights().toArray();
		}

		if (parentElement == COMPLETED) {
			return input.getCompletedFlights().toArray();
		}

		if (parentElement instanceof FlightPlanInfo) {

			FlightPlanInfo fpInfo = (FlightPlanInfo) parentElement;

			List elems = new ArrayList<>();
			elems.add(new WrappedCoordinate(fpInfo.getStartLocation()));
			elems.addAll(fpInfo.getWaypoints());
			return elems.toArray();
		}

		return new Object[0];

	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof FlightInfo) {
			this.input = (FlightInfo) newInput;
		} else {
			input = null;
		}
	}

}
