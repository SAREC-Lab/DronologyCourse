package edu.nd.dronology.core.gui;

import javafx.application.Application;

public class JavaFXGUILauncher extends Thread{
	private String[] mainArgs;
	
	public JavaFXGUILauncher(String[] args){
		mainArgs = args;
	}

	@Override
	public void run() {
		Application.launch(LocalGUISimpleDisplay.class, mainArgs);		
	}	
}

