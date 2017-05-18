package edu.nd.dronology.ui.cc.main.editor.routeplanning;

import java.util.TimerTask;

import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.AbstractItemPersistenceProvider;
import edu.nd.dronology.services.core.persistence.FlightRoutePersistenceProvider;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractUAVEditorPage;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractItemEditor;
import edu.nd.dronology.ui.cc.main.runnable.RetrieveAndOpenFlightRouteRunnable;
import edu.nd.dronology.ui.cc.main.runnable.TransmitAndCloseFlightRouteRunnable;

public class FlightRouteEditor extends AbstractItemEditor<IFlightRoute> {

	public FlightRouteEditor(Composite parent) {
		super(parent);
	}

	@Override
	public void retrieveFile(RemoteInfoObject elem) {
		new RetrieveAndOpenFlightRouteRunnable(elem.getId(), this).runRunnable();

	}

	@Override
	public AbstractItemPersistenceProvider<IFlightRoute> getPersistor() {
		return FlightRoutePersistenceProvider.getInstance();
	}

	@Override
	public AbstractUAVEditorPage<IFlightRoute> createPage() {
		return new UAVFlightRoutePage(this, "", "");
	}

	@Override
	public void doTransmit() {
		new TransmitAndCloseFlightRouteRunnable(this).runRunnable();

		new java.util.Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				DronologyMainActivator.getDefault().getEventBroker().post(EventConstants.REFRESH_SIDEBAR, "");

			}
		}, 2000);

		setVisible(false);
	}

}
