package edu.nd.dronology.ui.cc.main.editor.specification;

import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.items.IDroneSpecification;
import edu.nd.dronology.services.core.persistence.AbstractItemPersistenceProvider;
import edu.nd.dronology.services.core.persistence.DroneSpecificationPersistenceProvider;
import edu.nd.dronology.ui.cc.application.constants.EventConstants;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractItemEditor;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractUAVEditorPage;
import edu.nd.dronology.ui.cc.main.runnable.RetrieveAndOpenUAVSpecificationtRunnable;
import edu.nd.dronology.ui.cc.main.runnable.TransmitAndCloseSpecificationRunnable;

public class SpecificationEditor<T> extends AbstractItemEditor {


	private IDroneSpecification input;
	private IFile file;
	private UAVSpecificationPage page;

	public SpecificationEditor(Composite parent) {
		super(parent);
	}

	@Override
	public void retrieveFile(RemoteInfoObject elem) {
		new RetrieveAndOpenUAVSpecificationtRunnable(elem.getId(), this).runRunnable();

	}

	@Override
	public AbstractItemPersistenceProvider getPersistor() {
		return DroneSpecificationPersistenceProvider.getInstance();
	}

	@Override
	public AbstractUAVEditorPage createPage() {
		return new UAVSpecificationPage(this, "", "");
	}

	@Override
	public void doTransmit() {
		new TransmitAndCloseSpecificationRunnable(this).runRunnable();
		new java.util.Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				DronologyMainActivator.getDefault().getEventBroker().post(EventConstants.REFRESH_SIDEBAR, "");

			}
		}, 2000);

		setVisible(false);
	}

}
