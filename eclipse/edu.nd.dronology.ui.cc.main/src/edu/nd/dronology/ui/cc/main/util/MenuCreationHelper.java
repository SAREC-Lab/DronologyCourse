package edu.nd.dronology.ui.cc.main.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MenuCreationHelper {

	public static void createMenuEntry(IMenuManager manager, String name, Image icon, Runnable runnable) {

		manager.add(new Action(name, ImageDescriptor.createFromImage(icon)) {
			@Override
			public void run() {
				runnable.run();
			}

		});

	}

	public static void createMenuEntry(Menu menu, String name, Image icon, Runnable runnable) {
		MenuItem searchScholar = new MenuItem(menu, SWT.NONE);
		searchScholar.setText(name);
		searchScholar.setImage(icon);
		searchScholar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runnable.run();
			}
		});
		
	}

}
