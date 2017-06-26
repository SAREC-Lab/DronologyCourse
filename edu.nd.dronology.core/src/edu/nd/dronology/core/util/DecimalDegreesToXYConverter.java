package edu.nd.dronology.core.util;

import java.awt.Point;

import edu.nd.dronology.core.exceptions.FlightZoneException;
import edu.nd.dronology.core.flightzone.ZoneBounds;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * Given the window coordinates for the flight simulation, and the area of the map that is to be covered by the simulation, this class computes the scaling factor and transforms GPS coordinates into
 * x,y coordinates.
 * 
 * @author Jane Cleland-Huang
 * @version 0.1 Modification: Changing to singleton as it is called in multiple places.
 */
public class DecimalDegreesToXYConverter {

	private static final ILogger LOGGER = LoggerProvider.getLogger(DecimalDegreesToXYConverter.class);

	ZoneBounds zoneBounds;
	private double xRange = 0; // X coordinates in range of 0 to x
	private double yRange = 0; // Y coordinates in range of 0 to y
	private double xScale = 0.0; // The scale that transforms longitude to x coordinates
	private double yScale = 0.0; // The scale that transforms latitude to y coordinates
	private double latitudeOffset = 0;
	private double longitudeOffset = 0;
	private int reservedLeftHandSpace = 0;

	private double zoneXRange = 0d;
	private double zoneYRange = 0d;

	private static volatile DecimalDegreesToXYConverter INSTANCE = null;

	protected DecimalDegreesToXYConverter() {
	}

	/**
	 * Return an instance of DecimalDegreesToXYConverter
	 * 
	 * @return
	 */
	public static DecimalDegreesToXYConverter getInstance() {
		if (INSTANCE == null) {
			synchronized (DecimalDegreesToXYConverter.class) {
				if (INSTANCE == null) {
					INSTANCE = new DecimalDegreesToXYConverter();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * Setup
	 * 
	 * @param xSize
	 *          Width of window
	 * @param ySize
	 *          Height of window
	 */
	public void setUp(double xSize, double ySize, int reservedLeftHandSpace) {
		this.xRange = xSize;
		this.yRange = ySize;
		zoneBounds = ZoneBounds.getInstance();
		zoneXRange = zoneBounds.getXRange();
		zoneYRange = zoneBounds.getYRange();
		xScale = (xRange - reservedLeftHandSpace) / zoneXRange;
		yScale = xScale; // (double)yRange/zoneYRange; @TD: Don't want to change scales and disproportionately stretch the map.
		this.reservedLeftHandSpace = reservedLeftHandSpace;
	}

	private boolean passSetUpCheck() throws FlightZoneException {
		if (xRange == 0 || yRange == 0)
			throw new FlightZoneException("Flight zone area has not been setup correctly. xRange = " + xRange
					+ " and yRange = " + yRange + ". Both must be positive, non-zero values");
		else
			return true;
	}

	/**
	 * Converts an integer (X) to Longitude decimal degrees. Uses the scale factor originally computed by ZoneBounds according to the area displayed in the screen.
	 * 
	 * @param X
	 * @return X converted to decimal degrees
	 * @throws FlightZoneException
	 */
	public double ConvertXCoordsToDecimalDegrees(int X) throws FlightZoneException {
		LOGGER.info("Start Convert X: ");
		LOGGER.info("West Long: " + ZoneBounds.getInstance().getWestLongitude());
		LOGGER.info("Eash Long: " + ZoneBounds.getInstance().getEastLongitude());

		LOGGER.info("XScale: " + xScale);
		LOGGER.info("X: " + X);
		// System.out.println("XScale: " + ZoneBounds.getInstance().);
		if (passSetUpCheck()) {
			long delta = (long) (((double) X - reservedLeftHandSpace) / xScale);// + longitudeOffset
			LOGGER.info("Delta: " + delta);
			LOGGER.info(zoneBounds.getWestLongitude() + delta);
			return zoneBounds.getWestLongitude() + delta;

		} else
			return 0L; // Unreachable
	}

	/**
	 * Converts an integer (Y) to Latitude decimal degrees. Uses the scale factor originally computed by ZoneBounds according to the area displayed in the screen.
	 * 
	 * @param Y
	 * @return Y converted to decimal degrees
	 * @throws FlightZoneException
	 */
	public double ConvertYCoordsToDecimalDegrees(int Y) throws FlightZoneException {
		LOGGER.info("Start Convert Y: ");
		LOGGER.info("North Latitude: " + ZoneBounds.getInstance().getNorthLatitude());
		LOGGER.info("SOuth lat: " + ZoneBounds.getInstance().getSouthLatitude());

		LOGGER.info("YScale: " + yScale);
		LOGGER.info("Y: " + Y);
		if (passSetUpCheck()) {
			long delta = (long) (Y / yScale); // latitudeOffset + (long)((double)Y/yScale);
			LOGGER.info("Delta Y: " + delta);
			LOGGER.info(zoneBounds.getNorthLatitude() - delta);
			return zoneBounds.getNorthLatitude() - delta;
		}

		return 0L; // unreachable
	}

	/**
	 * Returns a point representing X and Y screen coordinates given latitude and longitude and assuming the previously defined ZoneBounds and y and x scaling factors.
	 * 
	 * @param latitude
	 *          in degrees (with decimal point removed)
	 * @param longitude
	 *          in degrees (with decimal point removed)
	 * @return a point representing X,Y coordinates
	 * @throws FlightZoneException
	 */
	public Point getPoint(double latitude, double longitude) throws FlightZoneException {
		if (passSetUpCheck()) {
			Point point = new Point();
			latitudeOffset = Math.abs(latitude - zoneBounds.getNorthLatitude());
			longitudeOffset = Math.abs(longitude - zoneBounds.getWestLongitude());
			long xLocation = reservedLeftHandSpace + (long) (longitudeOffset * xScale);
			long yLocation = (long) (latitudeOffset * yScale);
			point.setLocation(xLocation, yLocation);
			return point;
		} else
			return null;
	}
}
