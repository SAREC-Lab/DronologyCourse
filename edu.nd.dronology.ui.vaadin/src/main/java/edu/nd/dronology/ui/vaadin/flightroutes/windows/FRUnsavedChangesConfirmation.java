package edu.nd.dronology.ui.vaadin.flightroutes.windows;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

import edu.nd.dronology.ui.vaadin.flightroutes.FRInfoBox;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMainLayout;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMetaInfo;
import edu.nd.dronology.ui.vaadin.utils.YesNoWindow;

@SuppressWarnings("serial")
public class FRUnsavedChangesConfirmation extends YesNoWindow {
	private FRMainLayout mainLayout = null;
	
	public enum ChangeType {
		EDIT_ANOTHER	,		//User attempts to edit another route with unsaved changes
		SWITCH_ROUTE	,		//User attempts to switch route with unsaved changes
		NEW_ROUTE,			//User attempts to create a new route
		DELETE_ROUTE			//User attempts to delete a route
	}
	
	public FRUnsavedChangesConfirmation(FRMainLayout mainLayout) {
		this.mainLayout = mainLayout;
	}
	
	public void showWindow (String currentRouteName, ChangeType changeType, Event externalEvent) {
		this.initForNewMessage(
				"You have unsaved changes on <b>" + currentRouteName + "</b>.<br>"
				+ "Are you sure you want to discard all unsaved changes?");
		
		this.addYesButtonClickListener(e -> {
			mainLayout.getMapComponent().getEditModeController().exitEditMode();
			
			if (changeType == ChangeType.EDIT_ANOTHER) {
				Button editBtn = (Button)externalEvent.getComponent();
				if (editBtn.findAncestor(FRInfoBox.class) != null) {
					FRInfoBox infoBox = editBtn.findAncestor(FRInfoBox.class);
					mainLayout.switchRoute(infoBox);
					mainLayout.getMapComponent().getEditModeController().enterEditMode();
				}
			} else if (changeType == ChangeType.SWITCH_ROUTE) {
				if (externalEvent.getClass().equals(LayoutClickEvent.class)) {
					Component childComponent = ((LayoutClickEvent)externalEvent).getChildComponent();
					if (childComponent != null && childComponent.getClass().equals(FRInfoBox.class)) {
						mainLayout.switchRoute((FRInfoBox)childComponent);
					}
				}
			} else if (changeType == ChangeType.DELETE_ROUTE) {
				Button deleteBtn = (Button)externalEvent.getComponent();
				if (deleteBtn.findAncestor(FRInfoBox.class) != null) {
					FRInfoBox infoBox = deleteBtn.findAncestor(FRInfoBox.class);
					mainLayout.getDeleteRouteConfirmation().showWindow(infoBox.getFlightRouteInfo(), externalEvent);
				} else if (deleteBtn.findAncestor(FRMetaInfo.class) != null) {
					mainLayout.getDeleteRouteConfirmation().showWindow(
							mainLayout.getControls().getInfoPanel().getHighlightedFRInfoBox().getFlightRouteInfo(), externalEvent);
				}
			}
			
			this.close();
		});
		
		this.addNoButtonClickListener(e -> {
			if (changeType == ChangeType.NEW_ROUTE) {
				mainLayout.getControls().getInfoPanel().removeNewRouteWindow();
			}
			this.close();
		});
		
		this.showWindow();
	}
}
