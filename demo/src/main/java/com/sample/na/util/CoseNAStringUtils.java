package com.sample.na.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CoseNAStringUtils {

	// API URLs
	public static final String API_ENDPOINT_VEHICLE_V1 = "/api/v1/vehicles/";
	public static final String API_ENDPOINT_TRIP_V1 = "/api/v1/trips/";
	
	// PAGING VARIABLES - PAGING_DEFAULT_VALUES_ACTIVE is for determining
	// whether paging should be applied by default. By Default paging will be by
	// default to any api.
	public static final boolean PAGING_DEFAULT_VALUES_ACTIVE = true;
	public static final int PAGING_DEFAULT_PAGE = 0;
	public static final int PAGING_DEFAULT_LIMIT = 10;

	// To decide In progress trip
	public static final int DIFF_IN_SECONDS_FOR_IN_PROGRESS_TRIP = 600;

	// Maximum tank Capacity for fuelVolume Calculation in litre
	public static final double FUEL_TANK_CAPACITY_LITRE = 50;

	// DTC Translation Details
	public static final boolean DTC_TRANSLATION_API_CALL_ALWAYS = false;
	public static String DTC_TRANSLATION_API_URI;
	public static String DTC_TRANSLATION_API_TOKEN;
	public static String DTC_TRANSLATION_API_COUNTRY;

	@Value("${dtc.translation.service.uri}")
	public void setDTC_TRANSLATION_API_URI(String DTCTranslationAPIURI) {
		DTC_TRANSLATION_API_URI = DTCTranslationAPIURI;
	}

	@Value("${dtc.translation.service.securitytoken}")
	public void setDTC_TRANSLATION_API_TOKEN(String DTCTranslationAPIToken) {
		DTC_TRANSLATION_API_TOKEN = DTCTranslationAPIToken;
	}

	@Value("${dtc.translation.service.country}")
	public void setDTC_TRANSLATION_API_COUNTRY(String DTCTranslationAPICountry) {
		DTC_TRANSLATION_API_COUNTRY = DTCTranslationAPICountry;
	}
	
	public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	public static final String AUTHORIZATION_HEADER_VALUE_PREPEND = "Bearer ";
}
