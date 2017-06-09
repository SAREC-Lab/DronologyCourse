package edu.nd.dronology.ui.vaadin.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;

/**
 * This is to be used to execute JS code on the client side.
 * If you wish you use JQuery, inject jquery.min.js in the main UI.
 * Then...
 * <p>
 * 1. Write a JS file and save it to Deployed Resources: /webapp/VAADIN/js/.
 * 2. Use the JQueryInjector class like this:
 * 		JQueryInjector.getInstance().injectJSCode("[your js file name.js]");
 * 
 * @author Jinghui Cheng
 */
public class JavaScriptInjector {
	private static JavaScriptInjector instance = null;
	protected JavaScriptInjector() {
	}
	
	public static JavaScriptInjector getInstance() {
		if(instance == null) {
			instance = new JavaScriptInjector();
		}
		return instance;
	}
	
	/**
	 * Execute JS code. The .js file need to be put into /VAADIN/js/
	 *
	 * @param  jsFileName .js file name
	 */
	public void injectJSCode(String jsFileName) {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		Path p = Paths.get(basepath + "/VAADIN/js/" + jsFileName);
		byte[] b = null;
		try {
			b = Files.readAllBytes(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileString = new String(b, StandardCharsets.UTF_8);
		Page.getCurrent().getJavaScript().execute(fileString);
	}
}
