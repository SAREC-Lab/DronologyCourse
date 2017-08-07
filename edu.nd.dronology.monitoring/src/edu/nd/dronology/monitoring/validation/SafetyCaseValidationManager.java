package edu.nd.dronology.monitoring.validation;

import java.text.DateFormat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.media.jfxmedia.logging.Logger;

import edu.nd.dronology.gstation.python.connector.IUAVSafetyValidator;
import edu.nd.dronology.monitoring.safety.internal.UAVSaeftyCase;
import edu.nd.dronology.monitoring.trust.TrustManager;

public class SafetyCaseValidationManager implements IUAVSafetyValidator {

	private static volatile SafetyCaseValidationManager INSTANCE;

	public static final transient Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
			.setVersion(1.0).serializeSpecialFloatingPointValues().create();

	/**
	 * 
	 * @return The singleton instance.
	 */
	public static SafetyCaseValidationManager getInstance() {
		if (INSTANCE == null) {
			synchronized (SafetyCaseValidationManager.class) {
				if (INSTANCE == null) {
					INSTANCE = new SafetyCaseValidationManager();
				}
			}
		}
		return INSTANCE;
	}

	public SafetyCaseValidationManager() {
		TrustManager.getInstance().initialize();
	}

	@Override
	public synchronized boolean validate(String uavid, String safetyCase) {
		TrustManager.getInstance().initializeUAV(uavid);
		UAVSaeftyCase sac = GSON.fromJson(safetyCase, UAVSaeftyCase.class);
		sac.setUAVId(uavid);

		ValidationResult result = new SafetyCaseValidator(sac).validate();
		// initialize repuation manager
		return result.validationPassed();
	}

}
