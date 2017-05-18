package edu.nd.dronology.ui.cc.main.monitoring;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ServicesViewerLabelProvider extends OwnerDrawLabelProvider {

	private static final ILogger LOGGER = LoggerProvider.getLogger(ServicesViewerLabelProvider.class);

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
				
		//	case 3: return ImageProvider.IMG_MCC_SUPERVISOR_CLEANUP;
			case 5:
				return ImageProvider.IMG_SERVICE_START;
			case 6:
				return ImageProvider.IMG_SERVICE_STOP;
			case 7:
				return ImageProvider.IMG_SERVICE_RESTART;
			case 8:
				return ImageProvider.IMG_SERVICE_INFO;
			default:
				return null;
		}
	}

	private Image getStatusImage(ServiceInfo element) {
		switch (element.getStatus()) {
			case RUNNING:
				return ImageProvider.IMG_SERVICE_STATUS_ONLINE;
			case STARTING:
				return ImageProvider.IMG_SERVICE_STATUS_STARTING;
			case STOPPED:
				return ImageProvider.IMG_SERVICE_STATUS_OFFILE;
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof ServiceInfo)) {
			LOGGER.error("Element of type " + ServiceInfo.class.getName() + " expected, but was"
					+ element.getClass().getName());
			return null;
		}
		ServiceInfo sInfo = (ServiceInfo) element;

		switch (columnIndex) {
			case 1:
				return sInfo.getServiceID();
			case 2:
				return sInfo.getDescription();
			case 3:
				return parseProperties(sInfo);
			default:
				return StringUtils.EMPTY;

		}
	}

	private String parseProperties(ServiceInfo dist) {
		String adr = dist.getProperties().get("address");
		String port = dist.getProperties().get("port-" + dist.getServiceID());
		StringBuilder sb = new StringBuilder();
		// sb.append("address");
		// sb.append(" = ");
		// sb.append(adr);
		// sb.append("\n");
		if (port != null) {
			sb.append("port:");
			sb.append(" : ");
			sb.append(port);
		}
		return sb.toString();
	}

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
	protected void measure(Event event, Object element) {
	}

	@Override
	public void update(ViewerCell cell) {
		cell.setText(getColumnText(cell.getElement(), cell.getColumnIndex()));
		cell.setImage(getColumnImage(cell.getElement(),cell.getColumnIndex()));
	}

	@Override
	protected void paint(Event event, Object element) {
		// TODO Auto-generated method stub

	}

}
