package com.sample.na.service;

import java.io.IOException;
import java.rmi.ServerException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sample.na.model.GeoPosition;
import com.sample.na.domain.VehicleData;
import com.sample.na.domain.VehicleDataMessage;
import com.sample.na.helper.CoSeNAHelper;
import com.sample.na.model.DTC;
import com.sample.na.model.DTCTranslationResponse;
import com.sample.na.model.UserContext;
import com.sample.na.model.Vehicle;
import com.sample.na.model.VehicleResponse;
import com.sample.na.repositories.mongo.VehicleDataRepository;
import com.sample.na.repositories.mongo.VehicleDataSearchRepository;
import com.sample.na.util.CoseNAStringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

@Service
public class VehicleDataService {

	private final Logger LOGGER = Logger.getLogger(VehicleDataService.class);

	@Autowired
	VehicleDataRepository vehicleDataRepo;

	@Autowired
	VehicleDataSearchRepository vehicleDataService;

	@Autowired
	private RestTemplate template;

	@Autowired
	CoSeNAHelper coseNAHelper;

	private String tenant;

	@Value("${devicemanager.vehiclelist.endpoint.url}")
	private String deviceManagerVehicleEndpointUrl;

	private GeoPosition currentLocation;

	private String dtcCodesToTranslate;
	
	DecimalFormat FourDecimal = new DecimalFormat("##.####");

	public Object getAllVehicleListFromDMByVin(String vin, String page, String limit, String rwToken,
			UserContext userContext) throws ServerException {
		ResponseEntity<String> content = null;
		JSONArray restResponse = null;
		String jsonNode = "";
		try {
			if (page == null && limit == null) {
				if (vin == null) {
					System.out.println("Things API call to : " + deviceManagerVehicleEndpointUrl + vin);
					content = template.exchange(deviceManagerVehicleEndpointUrl, HttpMethod.GET,
							new HttpEntity<String>(createAuthTokenHeaders(rwToken)), String.class);
					System.out.println("Green Box REST api call Success response : " + content.toString());
					jsonNode = new ObjectMapper().readTree(content.getBody()).get("content").toString();
				} else {
					System.out.println("Things API call to : " + deviceManagerVehicleEndpointUrl);
					content = template.exchange(deviceManagerVehicleEndpointUrl + "?vin=" + vin, HttpMethod.GET,
							new HttpEntity<String>(createAuthTokenHeaders(rwToken)), String.class);
					jsonNode = new ObjectMapper().readTree(content.getBody()).toString();
					// restResponse = new JSONArray(jsonNode);
				}

			} else {
				if (vin == null) {
					System.out.println("Things API call to : " + deviceManagerVehicleEndpointUrl + vin);
					content = template.exchange(
							deviceManagerVehicleEndpointUrl + ((page != null) ? ("?page=" + page) : "?page=" + CoseNAStringUtils.PAGING_DEFAULT_PAGE)
									+ ((limit != null) ? ("&size=" + limit) : "&size=" +  CoseNAStringUtils.PAGING_DEFAULT_LIMIT),
							HttpMethod.GET, new HttpEntity<String>(createAuthTokenHeaders(rwToken)), String.class);
					System.out.println("Green Box REST api call Success response : " + content.toString());
					jsonNode = new ObjectMapper().readTree(content.getBody()).get("content").toString();
				} else {
					System.out.println("Things API call to : " + deviceManagerVehicleEndpointUrl);
					content = template.exchange(
							deviceManagerVehicleEndpointUrl + "?vin=" + vin + ((page != null) ? ("?page=" + page) : "?page=" + CoseNAStringUtils.PAGING_DEFAULT_PAGE)
									+ ((limit != null) ? ("&size=" + limit) : "&size=" +  CoseNAStringUtils.PAGING_DEFAULT_LIMIT),
							HttpMethod.GET, new HttpEntity<String>(createAuthTokenHeaders(rwToken)), String.class);
					jsonNode = new ObjectMapper().readTree(content.getBody()).toString();
				}

			}

		} catch (Exception e) {
			System.out.println("Green Box REST api call Error response :" + e.getMessage());
			throw new ServerException(e.getMessage());
		}
		try {
			restResponse = new JSONArray(jsonNode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Vehicle> responseModelList = new ArrayList<>();
		if (restResponse != null) {
			for (int i = 0; i < restResponse.length(); i++) {
				String searchvehicleIddWhichIsVin = null;
				try {
					searchvehicleIddWhichIsVin = (restResponse.getJSONObject(i).getJSONObject("attributes").get("vin")
							.toString() != null)
									? restResponse.getJSONObject(i).getJSONObject("attributes").get("vin").toString()
									: null;
				} catch (Exception e) {
					System.out.println("Error Parsing DM data from row => " + restResponse.getJSONObject(i).toString());
				}

				Vehicle responseModel = new Vehicle();
				if (responseModel.getOdometer() != null && responseModel.getFuelLevelPercent() != null
						&& responseModel.getBatteryVoltage() != null && responseModel.getLocation() != null) {
					break;
				}

				try {

					if (!userContext.isSuperAdmin()) {
						tenant = restResponse.getJSONObject(i).getJSONObject("attributes").get("tenantId").toString();
					} else {
						tenant = null;
					}

					if (!restResponse.getJSONObject(i).getJSONObject("features").isNull("imsi")) {
						responseModel.setImsi(
								restResponse.getJSONObject(i).getJSONObject("features").get("imsi").toString());
					}
					if (!restResponse.getJSONObject(i).isNull("thingId")) {
						responseModel.setId(restResponse.getJSONObject(i).get("thingId").toString());
					}

					responseModel.setLocation(
							getLatestVehicleData(searchvehicleIddWhichIsVin, "vehicleDataSamples.geoPosition", tenant)
									.getLocation());
					responseModel.setFuelLevelPercent(
							getLatestVehicleData(searchvehicleIddWhichIsVin, "vehicleDataSamples.health", tenant)
									.getFuelLevelPercent());
					responseModel.setOdometer(
							getLatestVehicleData(searchvehicleIddWhichIsVin, "vehicleDataSamples.health", tenant)
									.getOdometer());

					responseModel.setBatteryVoltage(
							getLatestVehicleData(searchvehicleIddWhichIsVin, "vehicleDataSamples.battery", tenant)
									.getBatteryVoltage());

					responseModel.setActiveDtcs((List<DTC>) getAllDtcsInLastTrip(searchvehicleIddWhichIsVin));

					responseModel.setCreatedAt(getCreatedAtDateFromVehicleData(searchvehicleIddWhichIsVin));

					responseModel.setUpdatedAt(getUpdateAtDateFromVehicleData(searchvehicleIddWhichIsVin));

					// From DM attributes
					if (!restResponse.getJSONObject(i).getJSONObject("attributes").isNull("vin")) {
						responseModel.setUrl("/api/v1/vehicles?vin="
								+ restResponse.getJSONObject(i).getJSONObject("attributes").get("vin").toString());
					}

					if (!restResponse.getJSONObject(i).getJSONObject("attributes").isNull("fuelGrade")) {
						responseModel.setFuelGrade(
								restResponse.getJSONObject(i).getJSONObject("attributes").get("fuelGrade").toString());
					}

					if (!restResponse.getJSONObject(i).getJSONObject("attributes").isNull("styleName")) {
						responseModel.setSubModel(
								restResponse.getJSONObject(i).getJSONObject("attributes").get("styleName").toString());
					}
					if (!restResponse.getJSONObject(i).getJSONObject("attributes").isNull("year")) {
						responseModel.setYear( Integer.parseInt(
								restResponse.getJSONObject(i).getJSONObject("attributes").get("year").toString()));
					}
					if (!restResponse.getJSONObject(i).getJSONObject("attributes").isNull("vin")) {
						responseModel.setVin(
								restResponse.getJSONObject(i).getJSONObject("attributes").get("vin").toString());
					}
					if (!restResponse.getJSONObject(i).getJSONObject("attributes").isNull("divisionName")) {
						responseModel.setMake(restResponse.getJSONObject(i).getJSONObject("attributes")
								.get("divisionName").toString());
					}
					if (!restResponse.getJSONObject(i).getJSONObject("attributes").isNull("modelName")) {
						responseModel.setModel(
								restResponse.getJSONObject(i).getJSONObject("attributes").get("modelName").toString());
					}
					if (!restResponse.getJSONObject(i).getJSONObject("attributes").isNull("trimName")) {
						responseModel.setDisplayName(
								restResponse.getJSONObject(i).getJSONObject("attributes").get("trimName").toString());
					}
				} catch (Exception e) {
				//	System.out.println("Error while parsing getAllVehicleListFromDMByVin" + e.getMessage().toString());
					System.out.println("Error while parsing getAllVehicleListFromDMByVin");
					
				}
				responseModelList.add(responseModel);
			}
		}

		if (responseModelList.size() > 1) {
			VehicleResponse response = new VehicleResponse();
			response.setContent(responseModelList);
			JsonNode paginationMapper = null;
			try {
				paginationMapper = new ObjectMapper().readTree(content.getBody());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} // new
				// ObjectMapper().readTree(content.getBody()).get("content").toString();
			response.setFirst(paginationMapper.get("first").toString());
			response.setLast(paginationMapper.get("last").toString());
			response.setNumber(paginationMapper.get("number").intValue());
			response.setNumberOfElements(paginationMapper.get("numberOfElements").intValue());
			response.setSize(paginationMapper.get("size").intValue());
			response.setSort(paginationMapper.get("sort").toString());
			response.setTotalElements(paginationMapper.get("totalElements").intValue());
			response.setTotalPages(paginationMapper.get("totalPages").intValue());
			return response;
		} else {
			return responseModelList;
		}

	}

	public List<VehicleDataMessage> getVehicleByVehicleId(String vehicleId) {
		if (vehicleId != null) {
			// Get last two records from DB as we could have empty Health
			// detailed in last cyclic message.
			// return
			// vehicleDataRepo.findTop2ByDeviceIDOrderByTimestampDesc(deviceId);
			return vehicleDataRepo.findTop2ByVehicleIdOrderByTimestampDesc(vehicleId);
		}
		return null;

	}

	private Vehicle getLatestVehicleData(String searchvehicleIddWhichIsVin, String field, String tenantId) {

		Vehicle responseModel = new Vehicle();
		VehicleDataMessage vehicleDataMessages = vehicleDataService.searchVehicleDataByField(searchvehicleIddWhichIsVin,
				field, tenantId);

		if (vehicleDataMessages != null) {
			System.out.println("All Vehicle Data Samples from Latest Non Null Trip :" + vehicleDataMessages.toString());
			List<VehicleData> vehicleDataList = vehicleDataMessages.getVehicleDataSamples();
			for (VehicleData vehicleData : Lists.reverse(vehicleDataList)) {
				System.out.println("Vehicle Data Sample :" + vehicleData.toString());

				try {
					switch (field) {
					case "vehicleDataSamples.geoPosition":
						if (vehicleData.getGeoPosition() != null && responseModel.getLocation() == null
								&& vehicleData.getGeoPosition().getLatitude() != null
								&& vehicleData.getGeoPosition().getLongitude() != null
								&& Integer.parseInt(vehicleData.getGeoPosition().getLatitude()) != 0
								&& Integer.parseInt(vehicleData.getGeoPosition().getLongitude()) != 0) {
							currentLocation = new GeoPosition();
							currentLocation.setAccuracy(Double.parseDouble(vehicleData.getGeoPosition().getAccuracy()));
							currentLocation.setAltitude(Double.parseDouble(vehicleData.getGeoPosition().getAltitude()));
							currentLocation.setDirection(Double.parseDouble(vehicleData.getGeoPosition().getDirection()));
							currentLocation.setLatitude(Double.parseDouble(FourDecimal.format(Double.parseDouble(coseNAHelper
									.convertGPSMilliArcSecToDegrees(vehicleData.getGeoPosition().getLatitude())))));
							currentLocation.setLongitude(Double.parseDouble(FourDecimal.format(Double.parseDouble(coseNAHelper
									.convertGPSMilliArcSecToDegrees(vehicleData.getGeoPosition().getLongitude())))));
							responseModel.setLocation(currentLocation);
							System.out.println("Latest Location : " + responseModel.getLocation());
						}
						break;
					case "vehicleDataSamples.health":
						if (vehicleData.getHealth() != null && responseModel.getFuelLevelPercent() == null) {
							// Latest Fuel Level
							responseModel.setFuelLevelPercent(Double.parseDouble(vehicleData.getHealth().getFuellevel()));
							System.out.println("Latest Fuel Percentage : " + responseModel.getFuelLevelPercent());
						}
						if (vehicleData.getHealth() != null && responseModel.getOdometer() == null) {
							// Latest Odometer
							responseModel.setOdometer(vehicleData.getHealth().getOdometer());
							System.out.println("Latest Odometer : " + responseModel.getOdometer());
						}
						break;
					case "vehicleDataSamples.battery":
						if (vehicleData.getBattery() != null && responseModel.getBatteryVoltage() == null) {
							responseModel.setBatteryVoltage(vehicleData.getBattery().getVoltage());
						}
						break;
					default:
						break;
					}
				} catch (Exception e) {
					System.out.println("Error in getLatestVehicleData : " + e.getMessage().toString());
				}

			}

		}
		// responseModel.setActiveDtcs(dtcsList);
		return responseModel;
	}

	private String getCreatedAtDateFromVehicleData(String vehicleId) {
		String createdAt = null;
		VehicleDataMessage dataMessage; // vehicleDataRepo.findTop1ByDeviceIDOrderByTimestampDesc(deviceId);
		if (vehicleId != null) {
			dataMessage = vehicleDataRepo.findTop1ByVehicleIdOrderByTimestampAsc(vehicleId);
			if (dataMessage != null && createdAt == null) {
				SimpleDateFormat sampleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				createdAt = sampleDateFormat.format(new Date(dataMessage.getTimestamp())).toString();
			}
		}
		return createdAt;

	}

	private String getUpdateAtDateFromVehicleData(String vehicleId) {
		String updatedAt = null;
		VehicleDataMessage dataMessage; // vehicleDataRepo.findTop1ByDeviceIDOrderByTimestampDesc(deviceId);
		try {
			if (vehicleId != null) {
				dataMessage = vehicleDataRepo.findTop1ByVehicleIdOrderByTimestampDesc(vehicleId);
				if (dataMessage != null && updatedAt == null) {
					SimpleDateFormat sampleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					updatedAt = sampleDateFormat.format(new Date(dataMessage.getTimestamp())).toString();
				}
			}

		} catch (Exception e) {
			System.out.println(
					"Error at getUpdatedAtDateFromVehicleData : for VehicleId " + vehicleId + e.getMessage().toString());
		}
		return updatedAt;

	}

	@SuppressWarnings("unused")
	private List<DTC> getAllDtcsInLastTrip(String searchvehicleIdWhichIsVin) {
		List<DTC> dtcs = new ArrayList<>();
		try {
			HashMap<Object, Object> dtcListHM = new HashMap<>();
			List<VehicleDataMessage> dataMessages = null;
			VehicleDataMessage dataMessage = vehicleDataRepo
					.findTop1ByVehicleIdOrderByTimestampDesc(searchvehicleIdWhichIsVin);
			if (dataMessage != null) {
				dataMessages = vehicleDataRepo.findAllByTripIdOrderByTimestampDesc(dataMessage.getTripId());
				LOGGER.info("DTCs found for this vin = " + searchvehicleIdWhichIsVin
						+ ", total number of vehicle messages = " + dataMessages.size());
				for (VehicleDataMessage vehicleDataMessage : dataMessages) {
					for (VehicleData vehicleData : vehicleDataMessage.getVehicleDataSamples()) {
						if (vehicleData.getDtcs() != null) {
							// dtcs.addAll(getDTCList(vehicleData.getDtcs(),
							// dtcListHM));
							for (com.sample.na.domain.DTC dtcCodes : vehicleData.getDtcs()) {
								DTC dtc = new DTC();
								dtc.setDtcCode(dtcCodes.getDtcCode());
								dtc.setDescription(dtcCodes.getDescription());
								dtc.setSeverity(dtcCodes.getSeverity());
								if (!dtcs.contains(dtc)) {
									dtcs.add(dtc);
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println("DTC parsing error : " + e.getMessage());
		}
		return dtcs;
	}

	@SuppressWarnings("unused")
	private List<DTC> getDTCList(List<com.sample.na.domain.DTC> dtcList, HashMap<Object, Object> checkIfExist) {
		List<DTC> dtcToVehicleList = new ArrayList<>();
		HashMap<Object, Object> hmTempDtcModel = new HashMap<>();
		dtcCodesToTranslate = "";
		dtcList.forEach(dtcDomain -> {
			if (!checkIfExist.containsKey(dtcDomain.getDtcCode())) {
				checkIfExist.put(dtcDomain.getDtcCode(), dtcDomain.getDtcCode());
				DTC dtcModel = DTC.builder().dtcCode(dtcDomain.getDtcCode()).build();
				if (dtcModel.getDescription() == null || dtcModel.getDescription().equals("")
						|| CoseNAStringUtils.DTC_TRANSLATION_API_CALL_ALWAYS) {
					if (dtcCodesToTranslate != null && dtcCodesToTranslate.length() > 0) {
						dtcCodesToTranslate = dtcCodesToTranslate + ",";
					}
					dtcCodesToTranslate = dtcCodesToTranslate + dtcModel.getDtcCode();
				}
				hmTempDtcModel.put(dtcModel.getDtcCode(), dtcModel);
				// dtcToVehicleList.add(dtcModel);
			}
		});

		LOGGER.info("DTCs to translate is " + dtcCodesToTranslate);

		if (dtcCodesToTranslate != null && dtcCodesToTranslate.length() > 0) {
			DTCTranslationResponse dtcTranslation = coseNAHelper.getDTCTranslationForCode(dtcCodesToTranslate);

			LOGGER.info("API is called and got response " + dtcTranslation.toString());

			if (dtcTranslation != null && dtcTranslation.getSuccess() == "true" && dtcTranslation.getDtcCodes() != null
					&& dtcTranslation.getDtcCodes().size() > 0) {
				LOGGER.info("DTC translation details of count  " + dtcTranslation.getDtcCodes().size());
				dtcTranslation.getDtcCodes().forEach(dtcDetailFromAPI -> {
					LOGGER.info("DTC translation details of code  " + dtcDetailFromAPI.getCode() + " is "
							+ dtcDetailFromAPI.getCommonUserLanguageDescription() + ". AND severity is=> "
							+ dtcDetailFromAPI.getSeverity());
					DTC dtcModel = (DTC) hmTempDtcModel.get(dtcDetailFromAPI.getCode());
					LOGGER.info("hmTempDtcModel  " + hmTempDtcModel);
					dtcModel.setDescription(dtcDetailFromAPI.getCommonUserLanguageDescription());
					dtcModel.setSeverity(dtcDetailFromAPI.getSeverity());
					dtcToVehicleList.add(dtcModel);
					hmTempDtcModel.remove(dtcDetailFromAPI.getCode());
					LOGGER.info("hmTempDtcModel After deleting the entry of " + dtcDetailFromAPI.getCode() + " is "
							+ hmTempDtcModel);
				});
			}
		}

		if (hmTempDtcModel.size() > 0) {
			LOGGER.info("hmTempDtcModel has still values remaining that are not there in API-> " + hmTempDtcModel);
			for (Object dtcCodeKey : hmTempDtcModel.keySet()) {
				dtcToVehicleList.add((DTC) hmTempDtcModel.get(dtcCodeKey));
				LOGGER.info(hmTempDtcModel.get(dtcCodeKey) + " is added and to DTCTOVEHICLELIST" + hmTempDtcModel);
			}
		}

		LOGGER.info("Returns the DTC final list -> " + dtcToVehicleList);

		return dtcToVehicleList;
	}

	public void setTemplate(RestTemplate template) {
		this.template = template;
	}

	public RestTemplate getTemplate() {
		return template;
	}

	@SuppressWarnings("unused")
	private ResponseEntity<String> getTenantMappedVinsFromDM(String vin, String authRequest, String page,
			String limit) {
		ResponseEntity<String> content = null;
		// Thing[] restThingResponse = null;
		try {
			UriComponentsBuilder builder = null;
			if (vin != null) {
				builder = UriComponentsBuilder.fromHttpUrl(deviceManagerVehicleEndpointUrl).queryParam("vin", vin);
			} else if (page == null && limit == null) {
				builder = UriComponentsBuilder.fromHttpUrl(deviceManagerVehicleEndpointUrl);
			} else if (page != null && limit != null) {
				builder = UriComponentsBuilder
						.fromHttpUrl(deviceManagerVehicleEndpointUrl + "?page=" + page + "&size=" + limit);
			}

			content = template.exchange(builder.build().encode().toUri(), HttpMethod.GET,
					new HttpEntity<String>(createAuthTokenHeaders(authRequest)), String.class);
			System.out.println("DM response post Authorization for Vin Search : " + vin + " : " + content.toString());

			return content;

		} catch (Exception e) {

			System.out
					.println("Exception during Auth server call in Method 'getTenantMappedToVin' : " + e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("serial")
	HttpHeaders createAuthTokenHeaders(String token) {
		HttpHeaders headers = new HttpHeaders() {
			{
				set(CoseNAStringUtils.AUTHORIZATION_HEADER_KEY, CoseNAStringUtils.AUTHORIZATION_HEADER_VALUE_PREPEND + token);
			}
		};
		return headers;
	}

}
