package edu.nd.dronology.ui.vaadin.flightroutes.confirmation;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component.Event;

import edu.nd.dronology.ui.vaadin.flightroutes.FRInfoBox;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMainLayout;
import edu.nd.dronology.ui.vaadin.flightroutes.FRMetaInfo;
import edu.nd.dronology.ui.vaadin.start.MyUI;

public class FRUnsavedChangesConfirmation {
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
		MyUI.getYesNoWindow().initForNewMessage(
				"You have unsaved changes on <b>" + currentRouteName + "</b>.<br>"
				+ "Are you sure you want to discard all unsaved changes?");
		
		MyUI.getYesNoWindow().addYesButtonClickListener(e -> {
			mainLayout.getMap().exitEditMode();
			MyUI.getYesNoWindow().close();
			
			if (changeType == ChangeType.EDIT_ANOTHER) {
				Button editBtn = (Button)externalEvent.getComponent();
				if (editBtn.findAncestor(FRInfoBox.class) != null) {
					FRInfoBox infoBox = editBtn.findAncestor(FRInfoBox.class);
					mainLayout.enableMapEdit();
					mainLayout.editClick(infoBox);
				}
			} else if (changeType == ChangeType.SWITCH_ROUTE) {
				mainLayout.switchWindows((LayoutClickEvent)externalEvent, mainLayout.getMap(), null);
			} else if (changeType == ChangeType.DELETE_ROUTE) {
				Button deleteBtn = (Button)externalEvent.getComponent();
				if (deleteBtn.findAncestor(FRInfoBox.class) != null) {
					FRInfoBox infoBox = deleteBtn.findAncestor(FRInfoBox.class);
					mainLayout.getDeleteRouteConfirmation().showWindow(
							infoBox.getFlightRouteInfo());
				} else if (deleteBtn.findAncestor(FRMetaInfo.class) != null) {
					mainLayout.getDeleteRouteConfirmation().showWindow(mainLayout.getMap().getSelectedRoute());
				}
			}
		});
		
		MyUI.getYesNoWindow().addNoButtonClickListener(e -> {
			if (changeType == ChangeType.NEW_ROUTE) {
				mainLayout.getControls().getInfoPanel().removeNewRouteWindow();
			}
			MyUI.getYesNoWindow().close();
		});
		
		MyUI.getYesNoWindow().showWindow();
	}
}
