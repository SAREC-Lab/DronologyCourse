package edu.nd.dronology.ui.vaadin.utils;

import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;

import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.shared.Registration;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class WaitingWindow extends Window {
	private Registration previousCloseListener = null;
	public interface ICondition {
		public boolean isConditionMet();
	}
	
	HorizontalLayout totalLayout = new HorizontalLayout();
	Label label = new Label("");

	public WaitingWindow() {
		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		
	    totalLayout.addComponents(progressBar, label);
		this.setContent(totalLayout);

		this.center();
		this.setClosable(false);
		this.setModal(true);
		this.setResizable(false);
		this.setDraggable(false);
	}
	
	public void showWindow (String message, ICondition condition, CloseListener listener) {
		label.setValue(message);
		UI.getCurrent().addWindow(this);
		
		if (previousCloseListener != null)
			previousCloseListener.remove();
		previousCloseListener = this.addCloseListener(listener);
		
		Timer t = new Timer( );
		UI ui = UI.getCurrent();
		t.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
			    	if (ui.getSession() != null){
			    		ui.access(() -> {
				    		if (condition.isConditionMet()) {
				    			hideWindow();
				    			t.cancel();
				    		}
			    		});
			    } else {
			    		t.cancel();
			    }
		    	}
	    }, 200, 200);
	}
	
	public void hideWindow() {
		UI.getCurrent().removeWindow(this);
	}
}
