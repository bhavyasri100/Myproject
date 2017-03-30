package com.sample.na.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sample.na.SecurityConfig;
import com.sample.na.model.DTCTranslationResponse;
import com.sample.na.model.Trip;
import com.sample.na.model.TripResponse;
import com.sample.na.repositories.mongo.TripDataSearchRepository;
import com.sample.na.util.CoseNAStringUtils;

@Component
public class CoSeNAHelper {

	private final Logger LOGGER = Logger.getLogger(CoSeNAHelper.class);
	
	@Autowired
	TripDataSearchRepository tripSearchRepo;
	@Autowired 
	RestTemplate restTemplate;

	public String convertMilliSecondToDateTime(Long milliSeconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(calendar.getTime());
	}

	public String convertGPSMilliArcSecToDegrees(String gpsVal) {
		Double gpsValDbl = Double.parseDouble(gpsVal);
		return String.valueOf(gpsValDbl/3600000);
	}

	public DTCTranslationResponse getDTCTranslationForCode(String dtcCode) {
		String uri = CoseNAStringUtils.DTC_TRANSLATION_API_URI + "?AuthorizationToken="
				+ CoseNAStringUtils.DTC_TRANSLATION_API_TOKEN + "&DTCCode=" + dtcCode;
		
		LOGGER.info("Calling the DTC Transl API " +  uri);
		try{
//			return null;
			return restTemplate.getForObject(uri, DTCTranslationResponse.class);
		}catch(Exception ex){
			LOGGER.error("Error calling the DTC Trans API - " + uri);
			ex.printStackTrace();
			return null;
		}
		
	} 

	public String authorizeUserForVins(String vehicle,String tenantId) {
//		if(vehicle != null){
//			LOGGER.info("Only one vehicle " + vehicle);
//			return vehicle;
//		}
		LOGGER.info("Getting distinct Vins for tenantId " + tenantId + " and Vin : " + vehicle);
		return tripSearchRepo.getDistinctDeviceId(tenantId,vehicle);
	}
	
	public TripResponse PageableTripResponse(List<Trip> trip, int count, int page, int limit) {
		TripResponse tripResponse = new TripResponse();
		tripResponse.setContent(trip);
		tripResponse.setFirst((page == 0)?"true":"false");
		tripResponse.setLast((count==(page*limit) || (count<=limit))?"true":"false");
		tripResponse.setNumber(page);
		tripResponse.setNumberOfElements(count);
		tripResponse.setSize(limit);
		tripResponse.setTotalPages((count<limit)?1:(int)Math.ceil(count/limit));
		return tripResponse;
	}
	public String getRawTokenFromHeader(HttpServletRequest request) {
		String header = request.getHeader(SecurityConfig.HEADER_AUTH_KEY);
		String rwToken = header.substring(SecurityConfig.HEADER_PREFIX.length(), header.length());
		return rwToken;
	}
}