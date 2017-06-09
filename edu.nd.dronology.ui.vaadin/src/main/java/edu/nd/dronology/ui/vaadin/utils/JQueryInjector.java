package edu.nd.dronology.ui.vaadin.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;

/**
 * This is to be used to execute JS or jQuery code on the client side
 * <p>
 * 1. Write a JS file and save it to Deployed Resources: /webapp/VAADIN/js/. You can use JQuery if you wish.
 * 2. Use the JQueryInjector class like this:
 * 		JQueryInjector.getInstance().injectJSCode("[your js file name.js]");
 * 
 * @author Jinghui Cheng
 */
public class JQueryInjector {
	private static JQueryInjector instance = null;
	protected JQueryInjector() {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		Path p = Paths.get(basepath+"/VAADIN/js/jquery.min.js");
		byte[] b = null;
		try {
			b = Files.readAllBytes(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileString = new String(b, StandardCharsets.UTF_8);
		Page.getCurrent().getJavaScript().execute(fileString);
	}
	
	public static JQueryInjector getInstance() {
		if(instance == null) {
			instance = new JQueryInjector();
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
