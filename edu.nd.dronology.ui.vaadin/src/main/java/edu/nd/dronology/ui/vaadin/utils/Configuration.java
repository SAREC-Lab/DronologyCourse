package edu.nd.dronology.ui.vaadin.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.VaadinService;


/**
 * Configuration singleton class that stores all UI parameters
 * 
 * @author Patrick Falvey
 *
 */

public class Configuration {

	private static Configuration instance = null;
	
	public static Configuration getInstance(){
		if (Configuration.instance == null) {
			Configuration.instance = new Configuration();
		}
		return Configuration.instance;
	}
	
	public static final String FILENAME = 
			VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + 
			"/VAADIN/config/configuration.json";
	
	private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());
	
	private JSONObject config = null;
	
	private String projectName;
	
	private double mapCenterLat;
	
	private double mapCenterLon;
	
	private double mapDefaultZoom;
	
	//configuration file needs to be put into /src/main/webapp/VAADIN/config/
	private Configuration(){
		boolean result = this.loadConfig(FILENAME);
		if (!result){
			if (!this.createConfig(FILENAME)) {
				System.exit(0);
			}
		}
	}
	
	private boolean loadConfig(String fileName){
		BufferedReader reader;
		String line;
		String content = "";
		
		try{
			reader = new BufferedReader(new FileReader(fileName));
			while ((line = reader.readLine()) != null) {
				content += line;
			}
			reader.close();
		} catch (IOException exc){
			LOGGER.log(Level.SEVERE, null, exc);
			return false;
		}
		
		if (content.length() <= 0){
			return false;
		}
		
		try {
			this.config = new JSONObject(content);
			
			this.projectName = this.config.getString("projectName");
			this.mapCenterLat = this.config.getDouble("mapCenterLat");
			this.mapCenterLon = this.config.getDouble("mapCenterLon");
			this.mapDefaultZoom = this.config.getDouble("mapDefaultZoom");
		} catch (JSONException exc) {
			LOGGER.log(Level.SEVERE, null, exc);
			return false;
		}
	
		return true;
	}
	
	private boolean createConfig(String fileName){
		/**
		 * set default values for variables
		 */
		this.projectName = "";
		this.mapCenterLat = 0;
		this.mapCenterLon = 0;
		this.mapDefaultZoom = 0;
		
		return this.saveConfig(fileName);
	}
	
	private boolean saveConfig(String fileName){
		try {
			PrintWriter configFile = new PrintWriter(fileName, "UTF-8");
		
      JSONObject json = new JSONObject();
      json.put("projectName", this.projectName);
      json.put("mapCenterLat", this.mapCenterLat);
      json.put("mapCenterLon", this.mapCenterLon);
      json.put("mapDefaultZoom", this.mapDefaultZoom);
		
      LOGGER.log(Level.INFO, null, json.toString());
			
      configFile.write(json.toString());        

      configFile.close();
      
		} catch (FileNotFoundException | UnsupportedEncodingException exc) {
			LOGGER.log(Level.SEVERE, null, exc);
      return false;
    } catch (JSONException exc) {
    	LOGGER.log(Level.SEVERE, null, exc);
			return false;
		}
		return true;	
	}
	
	public String getProjectName(){
		return projectName;
	}
	
	public void setProjectName(String projectName){
		this.projectName = projectName;
		this.saveConfig(FILENAME);
	}
	
	public double getMapCenterLat(){
		return mapCenterLat;
	}
	
	public void setMapCenterLat(double mapCenterLat){
		this.mapCenterLat = mapCenterLat;
		this.saveConfig(FILENAME);
	}
	
	public double getMapCenterLon(){
		return mapCenterLon;
	}
	
	public void setMapCenterLon(double mapCenterLon){
		this.mapCenterLon = mapCenterLon;
		this.saveConfig(FILENAME);
	}
	
	public double getMapDefaultZoom(){
		return mapDefaultZoom;
	}
	
	public void setMapDefaultZoom(double mapDefaultZoom){
		this.mapDefaultZoom = mapDefaultZoom;
		this.saveConfig(FILENAME);
	}
}