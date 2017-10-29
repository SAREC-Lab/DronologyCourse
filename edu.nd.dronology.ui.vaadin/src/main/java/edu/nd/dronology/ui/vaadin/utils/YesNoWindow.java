package edu.nd.dronology.ui.vaadin.utils;

import java.util.ArrayList;

import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This is a simple Vaadin Alert Window
 * 
 * @author Jinghui Cheng
 */
@SuppressWarnings("serial")
public class YesNoWindow extends Window {
	VerticalLayout totalLayout = new VerticalLayout();
	Label label = new Label("");
	HorizontalLayout buttonLayout = new HorizontalLayout();
	Button yesBtn = new Button("Yes");
	Button noBtn = new Button("No");
	
	ArrayList<Registration> yesBtnListerReg = new ArrayList<>();
	ArrayList<Registration> noBtnListerReg = new ArrayList<>();
	
	protected YesNoWindow() {
		label.setContentMode(ContentMode.HTML);
		
		buttonLayout.addComponents(yesBtn, noBtn);
		totalLayout.addComponents(label, buttonLayout);
		this.setContent(totalLayout);

		this.setStyleName("confirm_window");
		buttonLayout.setStyleName("confirm_button_area");
		yesBtn.setStyleName("btn-danger");

		this.center();
		this.setClosable(false);
		this.setModal(true);
		this.setResizable(false);
		this.setDraggable(false);
	}
	
	protected void showWindow () {
		UI.getCurrent().addWindow(this);
	}
	
	protected void initForNewMessage (String message) {
		label.setValue(message);
		removeAllYesButtonListeners ();
		removeAllNoButtonListeners ();
	}
	
	protected void setYesBtnText (String text) {
		yesBtn.setCaption(text);
	}
	
	protected void setNoBtnText (String text) {
		noBtn.setCaption(text);
	}
	
	protected void addYesButtonClickListener (ClickListener listener) {
		yesBtnListerReg.add(yesBtn.addClickListener(listener));
	}
	
	protected void addNoButtonClickListener (ClickListener listener) {
		noBtnListerReg.add(noBtn.addClickListener(listener));
	}
	
	private void removeAllYesButtonListeners () {
		for (Registration r : yesBtnListerReg) {
			r.remove();
		}
		yesBtnListerReg.clear();
	}
	
	private void removeAllNoButtonListeners () {
		for (Registration r : noBtnListerReg) {
			r.remove();
		}
		noBtnListerReg.clear();
	}
}
