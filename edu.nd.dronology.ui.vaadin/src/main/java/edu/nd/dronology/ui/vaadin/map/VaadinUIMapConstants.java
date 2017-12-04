package edu.nd.dronology.ui.vaadin.map;

public class VaadinUIMapConstants {

	
	
	/* SATELLITE MAP */
	public static final String ONLINE_WORLD_IMAGERY_URL = "http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}";
	public static final String ONLINE_WORLD_IMAGERY_NAME = "World Imagery";
	
	
	/* CARTO LIGHT BASE-MAP */
	public static final String ONLINE_CARTO_URL = "https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png";
	public static final String ONLINE_CARTO_NAME = "Carto Maps";
	
	/* SB Satellite Tiles */
	public static final String LOCAL_SB_LAYER_SATELLITE_NAME = "South Bend Satellite";
	public static final String LOCAL_SB_LAYER_SATELLITE_URL = "VAADIN/sateltiles/{z}/{x}/{y}.png";
	
	/* SB Normal Map */
	public static final String LOCAL_SB_LAYER_URL = "VAADIN/sbtiles/{z}/{x}/{y}.png";
	public static final String LOCAL_SB_LAYER_NAME = "South Bend";


}
