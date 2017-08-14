package edu.nd.dronology.ui.vaadin.flightroutes.windows;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.ui.vaadin.flightroutes.FRMainLayout;

@SuppressWarnings("serial")
public class FRWayPointPopupView extends PopupView {
	private class FRWayPointPopupViewContent implements PopupView.Content {
		private PopupView popupView;
		private FRMainLayout mainLayout;
		
		VerticalLayout popupContent = new VerticalLayout();
		Label latitudeLabel = new Label();
		Label longitudeLabel = new Label();
		Label altitudeLabel = new Label();
		Label transitSpeedLabel = new Label();
		Button toDelete = new Button("Remove Waypoint");
		
		public FRWayPointPopupViewContent(FRMainLayout mainLayout, PopupView popupView) {
            this.popupView = popupView;
            this.mainLayout = mainLayout;
        }
		@Override
		public String getMinimizedValueAsHTML() {
			return "";
		}
		@Override
		public Component getPopupComponent() {
	    		popupContent.addComponents(latitudeLabel, longitudeLabel, altitudeLabel, transitSpeedLabel);
	    		
	    		toDelete.addClickListener(event -> {
	    			popupView.setPopupVisible(false);
	    			mainLayout.getDeleteWayPointConfirmation().showWindow(event);
	    		});
	    		
	    		toDelete.setId("toDelete");
	    		popupContent.addComponent(toDelete);
	    		
	    		popupContent.addStyleName("fr_waypoint_popup");
	    		return popupContent;
		}
		
		public void setLatitute (String latitute) {
			latitudeLabel.setValue("Latitude: " + latitute);
		}
		public void setLongitude (String longitude) {
			longitudeLabel.setValue("Longitude: " + longitude);
		}
		public void setAltitude (String altitude) {
			altitudeLabel.setValue("Altitude: " + altitude);
		}
		public void setTransitSpeed (String transitSpeed) {
			transitSpeedLabel.setValue("Transit Speed: " + transitSpeed);
		}
		public void setDeleteButtonVisible (boolean visible) {
			toDelete.setVisible(visible);
		}
	}
	
	FRWayPointPopupViewContent content;
	public FRWayPointPopupView(FRMainLayout mainLayout) {
        super(null, null);
        content = new FRWayPointPopupViewContent(mainLayout, this);
        this.setContent(content);
		this.addStyleName("bring_front");
		this.setVisible(false);
		this.setPopupVisible(false);	
	}
	public void setLatitute (String latitute) {
		content.setLatitute(latitute);
	}
	public void setLongitude (String longitude) {
		content.setLongitude(longitude);
	}
	public void setAltitude (String altitude) {
		content.setAltitude(altitude);
	}
	public void setTransitSpeed (String transitSpeed) {
		content.setTransitSpeed(transitSpeed);
	}
	public void setDeleteButtonVisible (boolean visible) {
		content.setDeleteButtonVisible(visible);
	}
}
