package edu.nd.dronology.ui.cc.main.monitoring;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.api.ServiceStatus;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class CenterImageLabelProver extends OwnerDrawLabelProvider {

	private static final ILogger LOGGER = LoggerProvider.getLogger(CenterImageLabelProver.class);

	private final int col;

	public CenterImageLabelProver(int col) {
		super();
		this.col = col;
	}

	@Override
	protected void paint(Event event, Object element) {

		Image img = getColumnImage(element, col);
		if (img != null) {
			Rectangle bounds = ((TableItem) event.item).getBounds(event.index);
			Rectangle imgBounds = img.getBounds();
			bounds.width /= 2;
			bounds.width -= imgBounds.width / 2;
			bounds.height /= 2;
			bounds.height -= imgBounds.height / 2;

			int x = bounds.width > 0 ? bounds.x + bounds.width : bounds.x;
			int y = bounds.height > 0 ? bounds.y + bounds.height : bounds.y;

			event.gc.drawImage(img, x, y);
		}
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ServiceInfo)) {
			LOGGER.error("Element of type " + ServiceInfo.class.getName() + " expected, but was"
					+ element.getClass().getName());
			return null;
		}
		ServiceInfo sInfo = (ServiceInfo) element;
		switch (columnIndex) {
			case 0:
				return getStatusImage(sInfo);
			case 1:
				return null;
				// / return ImageProvider.IMG_SOURCE_REMOTE;

			case 5:
				return !running(sInfo) ? ImageProvider.IMG_SERVICE_START : ImageProvider.IMG_SERVICE_START_DISABLED;
			case 6:
				if (isDistributor(sInfo)) {
					return running(sInfo) ? ImageProvider.IMG_SERVICE_STOP : ImageProvider.IMG_SERVICE_STOP_DISABLED;
				}
				return null;
			case 7:
				// return running(sInfo) ? ImageProvider.IMG_SERVER_RESTART : ImageProvider.IMG_SERVER_RESTART_DISABLED;
				return ImageProvider.IMG_SERVICE_RESTART;
			case 8:
				return running(sInfo) ? ImageProvider.IMG_SERVICE_INFO : ImageProvider.IMG_SERVICE_INFO_DISABLED;
			case 31:
				if (isFile(sInfo)) {
					return ImageProvider.IMG_SERVICE_TYPE_FILE;
				}
				if (isRemote(sInfo)) {
					return ImageProvider.IMG_SERVICE_TYPE_REMOTE;
				}
				if (isSocket(sInfo)) {
					return ImageProvider.IMG_SERVICE_TYPE_SOCKET;
				}
			default:
				return null;
		}
	}

	private boolean isSocket(ServiceInfo sInfo) {
		return sInfo.getDetails().get(ServiceInfo.ATTRIBUTE_TYPE) != null
				&& sInfo.getDetails().get(ServiceInfo.ATTRIBUTE_TYPE).equals(ServiceInfo.ATTRIBUTE_SOCKET);
	}

	private boolean isRemote(ServiceInfo sInfo) {
		return sInfo.getDetails().get(ServiceInfo.ATTRIBUTE_TYPE) != null
				&& sInfo.getDetails().get(ServiceInfo.ATTRIBUTE_TYPE).equals(ServiceInfo.ATTRIBUTE_REMOTE);
	}
	private boolean isFile(ServiceInfo sInfo) {
		return sInfo.getDetails().get(ServiceInfo.ATTRIBUTE_TYPE) != null
				&& sInfo.getDetails().get(ServiceInfo.ATTRIBUTE_TYPE).equals(ServiceInfo.ATTRIBUTE_FILE);
	}
	

	private boolean isDistributor(ServiceInfo element) {
		if (element.getDetails().containsKey(ServiceInfo.ATTRIBUTE_TYPE)) {
			return element.getDetails().get(ServiceInfo.ATTRIBUTE_TYPE).equals(ServiceInfo.ATTRIBUTE_TYPE_DISTRIBUTOR);
		}
		return false;
	}

	private boolean running(ServiceInfo sInfo) {
		return ServiceStatus.RUNNING == sInfo.getStatus();
	}

	private Image getStatusImage(ServiceInfo sInfo) {
		switch (sInfo.getStatus()) {
			case RUNNING:
				return ImageProvider.IMG_SERVICE_STATUS_ONLINE;
			case STARTING:
				return ImageProvider.IMG_SERVICE_STATUS_STARTING;
			case STOPPED:
				return ImageProvider.IMG_SERVICE_STATUS_OFFILE;
			case ERROR:
				return ImageProvider.IMG_SERVICE_STATUS_ERROR;
		}
		return null;
	}

	@Override
	protected void measure(Event event, Object element) {
		// TODO Auto-generated method stub

	}

}
