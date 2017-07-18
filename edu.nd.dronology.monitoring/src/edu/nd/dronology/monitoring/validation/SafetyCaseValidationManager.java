package edu.nd.dronology.monitoring.validation;

import java.text.DateFormat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.gstation.python.connector.IUAVSafetyValidator;
import edu.nd.dronology.monitoring.safety.internal.UAVSaeftyCase;

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

	@Override
	public boolean validate(String uavid, String safetyCase) {

		UAVSaeftyCase sac = GSON.fromJson(safetyCase, UAVSaeftyCase.class);
		sac.setUAVId(uavid);

		ValidationResult result = new SafetyCaseValidator(sac).validate();

		return result.validationPassed();
	}

}
