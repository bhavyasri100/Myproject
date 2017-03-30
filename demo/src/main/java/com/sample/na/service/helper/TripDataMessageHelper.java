package com.sample.na.service.helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.sample.na.domain.TripEndMessage;
import com.sample.na.domain.TripStartMessage;
import com.sample.na.domain.VehicleData;
import com.sample.na.domain.VehicleDataMessage;
import com.sample.na.helper.CoSeNAHelper;
import com.sample.na.model.DTC;
import com.sample.na.model.DTCCode;
import com.sample.na.model.DTCTranslationResponse;
import com.sample.na.model.FuelLevel;
import com.sample.na.model.Path;
import com.sample.na.model.TripAddress;
import com.sample.na.model.TripLocation;
import com.sample.na.model.TripRequestParams;
import com.sample.na.repositories.mongo.VehicleDataRepository;
import com.sample.na.util.CoseNAStringUtils;
import com.google.common.collect.Lists;

@Component
public class TripDataMessageHelper {
	@Autowired
	CoSeNAHelper coseNAHelper;

	@Autowired
	VehicleDataRepository vehicleRepo;

	private final Logger LOGGER = Logger.getLogger(TripDataMessageHelper.class);

	boolean flagDTCTransDetailsNeedToSave;

	String dtcCodesToTranslate;

	private TripLocation tripStartLocation;

	private TripLocation tripEndLocation;
	
	DecimalFormat FourDecimal = new DecimalFormat("##.####");

	public Criteria createCriteriaForTripDataStart(TripRequestParams tripRequestParam, String tenantId) {

		LOGGER.info("Inside the helper function with parameters - " + tripRequestParam.getStartedAtLTE() + ","
				+ tripRequestParam.getStartedAtGTE() + "," + tripRequestParam.getVehicle() + ","
				+ tripRequestParam.getTagsIn() + " Tenant Id :" + tenantId);

		Criteria criteria = new Criteria();

		if (tripRequestParam.getTripId() != null) {
			criteria.and("tripId").is(tripRequestParam.getTripId());
		} else {
			if (tripRequestParam.getStartedAtLTE() != null) {
				criteria.and("timestamp").lte(tripRequestParam.getStartedAtLTE());
			}
			if (tripRequestParam.getStartedAtGTE() != null) {
				criteria.and("timestamp").gte(tripRequestParam.getStartedAtGTE());
			}
			if (tripRequestParam.getVehicle() != null) {
				// Call the API and get deviceId and then pass device id here

				criteria.and("vehicleId").in(Arrays.asList(tripRequestParam.getVehicle().split(",")));
			}
			if (tripRequestParam.getTagsIn() != null) {
				criteria.and("tags__in").is(tripRequestParam.getTagsIn());
			}
			if (tenantId != null) {
				criteria.and("tenantId").is(tenantId);
			} else {
				System.out.println("[Error] ===>>>> Tenant ID recieve in Token is Null!!!!");
			}
		}

		return criteria;
	}

	public List<String> getIdsFromStartMessage(List<TripStartMessage> tripStartMsg) {

		ArrayList<String> tripIds = new ArrayList<>();
		for (TripStartMessage tripstart : tripStartMsg) {
			tripIds.add(tripstart.getTripId());
		}

		return tripIds;
	}

	public Criteria createCriteriaForTripDataEnd(Long endedAtLTE, Long endedAtGTE, List<String> tripIds) {
		Criteria criteria = new Criteria();

		if (endedAtLTE != null) {
			criteria.and("timestamp").lte(endedAtLTE);
		}
		if (endedAtGTE != null) {
			criteria.and("timestamp").gte(endedAtGTE);
		}
		if (!tripIds.isEmpty()) {
			criteria.and("tripId").in(tripIds);
		}

		return criteria;
	}

	public Criteria createCriteriaForVehicleData(List<String> tripIds) {
		Criteria criteria = new Criteria();
		if (!tripIds.isEmpty()) {
			criteria.and("tripId").in(tripIds);
		}

		return criteria;
	}

	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getVehicleDataMes(List<VehicleDataMessage> vehicleDataMsgList, String tripId) {
		HashMap<Object, Object> retHM = new HashMap<>();
		Double firstFuelLevel = null, diffFuelLevel = 0.0, currentFuelLevel = null, preCurrentFuelLevel = null,
				fuelVolumeLitre = null;
		boolean flagFirstFuelSet = false;
		List<DTC> dtcList = new ArrayList<>();
		FuelLevel fuelLvel = new FuelLevel();
		List<FuelLevel> fuelLevels = new ArrayList<>();
		HashMap<Object, Object> dtcListHM = new HashMap<>();

		// Looping from bottom to top : ascending order of time TOP TO BOTTOM
		for (int i = vehicleDataMsgList.size() - 1; i >= 0; i--) {
			if (vehicleDataMsgList.get(i).getTripId().equals(tripId)) {

				// Taking the first Vehicle Data Message out
				if (!retHM.containsKey("firstVehicleData")
						&& vehicleDataMsgList.get(i).getVehicleDataSamples().size() > 0) {
					retHM.put("firstVehicleData", vehicleDataMsgList.get(i).getVehicleDataSamples().get(0));
				}

				if (vehicleDataMsgList.get(i).getVehicleDataSamples() != null
						&& vehicleDataMsgList.get(i).getVehicleDataSamples().size() > 0) {
					// Looping each vehicle data in the message from top to
					// bottom
					List<VehicleData> listVehicleDataSamples = vehicleDataMsgList.get(i).getVehicleDataSamples();
					for (int j = 0; j < listVehicleDataSamples.size(); j++) {

						// Taking valid GEO location
						if (!retHM.containsKey("tripStartLocation")
								&& listVehicleDataSamples.get(j).getGeoPosition() != null
								&& listVehicleDataSamples.get(j).getGeoPosition().getLatitude() != null
								&& listVehicleDataSamples.get(j).getGeoPosition().getLongitude() != null
								&& !(listVehicleDataSamples.get(j).getGeoPosition().getLatitude().equals("0"))
								&& !(listVehicleDataSamples.get(j).getGeoPosition().getLongitude().equals("0"))) {

							tripStartLocation = TripLocation.builder()
									.lat(Double.parseDouble(FourDecimal.format(Double.parseDouble(coseNAHelper.convertGPSMilliArcSecToDegrees
											(listVehicleDataSamples.get(j).getGeoPosition().getLatitude())))))
									.lon(Double.parseDouble(FourDecimal.format(Double.parseDouble(coseNAHelper.convertGPSMilliArcSecToDegrees(
											listVehicleDataSamples.get(j).getGeoPosition().getLongitude())))))
									.accuracyM(Double.parseDouble(listVehicleDataSamples.get(j).getGeoPosition().getAccuracy())).build();
							retHM.put("tripStartLocation", tripStartLocation);
						}

						// Taking valid GEO location
						if (listVehicleDataSamples.get(j).getGeoPosition() != null
								&& listVehicleDataSamples.get(j).getGeoPosition().getLatitude() != null
								&& listVehicleDataSamples.get(j).getGeoPosition().getLongitude() != null
								&& !(listVehicleDataSamples.get(j).getGeoPosition().getLatitude().equals("0"))
								&& !(listVehicleDataSamples.get(j).getGeoPosition().getLongitude().equals("0"))){
							tripEndLocation = TripLocation.builder()
									.lat(Double.parseDouble(FourDecimal.format(Double.parseDouble(coseNAHelper.convertGPSMilliArcSecToDegrees(
											listVehicleDataSamples.get(j).getGeoPosition().getLatitude())))))
									.lon(Double.parseDouble(FourDecimal.format(Double.parseDouble(coseNAHelper.convertGPSMilliArcSecToDegrees(
											listVehicleDataSamples.get(j).getGeoPosition().getLongitude())))))
									.accuracyM(Double.parseDouble(listVehicleDataSamples.get(j).getGeoPosition().getAccuracy())).build();
							retHM.put("tripEndLocation", tripEndLocation);
						}

						// DTC Codes
						if (listVehicleDataSamples.get(j).getDtcs() != null
								&& listVehicleDataSamples.get(j).getDtcs().size() > 0) {

							HashMap<Object, Object> hmDTCDetail = getDTCList(listVehicleDataSamples.get(j).getDtcs(),
									dtcListHM);

							dtcList.addAll((List<DTC>) hmDTCDetail.get("dtcToTripList"));
							List<com.sample.na.domain.DTC> DTCDetailListToSave = (List<com.sample.na.domain.DTC>) hmDTCDetail
									.get("dtcToSaveVM");
							if (DTCDetailListToSave != null && DTCDetailListToSave.size() > 0) {
								listVehicleDataSamples.get(j).setDtcs(DTCDetailListToSave);
								vehicleRepo.save(vehicleDataMsgList.get(i));
							}
						}

						// Health values
						if (listVehicleDataSamples.get(j).getHealth() != null) {

							/****
							 * First and Last available Odometer values
							 *****/
							if (listVehicleDataSamples.get(j).getHealth().getOdometer() != null) {
								if (!retHM.containsKey("startOdometer")) {
									retHM.put("startOdometer", listVehicleDataSamples.get(j).getHealth().getOdometer());
								}
								retHM.put("endOdometer", listVehicleDataSamples.get(j).getHealth().getOdometer());
							}
							/****
							 * End First and Last available Odometer values
							 *****/

							/***
							 * Fuel Level calculates after checking if there any
							 * refueling happened in between
							 ***/
							if (listVehicleDataSamples.get(j).getHealth().getFuellevel() != null) {
								preCurrentFuelLevel = currentFuelLevel;
								currentFuelLevel = Double
										.parseDouble(listVehicleDataSamples.get(j).getHealth().getFuellevel());

								if (!flagFirstFuelSet) {
									LOGGER.info("First fuel level for " + tripId + " is " + currentFuelLevel);
									firstFuelLevel = currentFuelLevel;
									flagFirstFuelSet = true;
								} else if (preCurrentFuelLevel < currentFuelLevel) {
									fuelLvel = FuelLevel.builder().initialP(firstFuelLevel)
											.lastP(preCurrentFuelLevel).build();

									fuelLevels.add(fuelLvel);
									LOGGER.info("Previous and current for " + tripId + " is " + preCurrentFuelLevel
											+ "," + currentFuelLevel);
									diffFuelLevel += firstFuelLevel - preCurrentFuelLevel;
									LOGGER.info("Difference for " + tripId + " is " + diffFuelLevel);
									flagFirstFuelSet = true;
									firstFuelLevel = currentFuelLevel;
									preCurrentFuelLevel = 0.0;
								}
							}

							if (!retHM.containsKey("firstVehicleDataForAllExceptLocation")) {
								retHM.put("firstVehicleDataForAllExceptLocation", listVehicleDataSamples.get(0));
							}
							retHM.put("lastVehicleDataForAllExceptLocation",
									listVehicleDataSamples.get(listVehicleDataSamples.size() - 1));
						}

					}
					retHM.put("lastVehicleData", vehicleDataMsgList.get(i).getVehicleDataSamples()
							.get(vehicleDataMsgList.get(i).getVehicleDataSamples().size() - 1));
				}

				retHM.put("lastVehicleCyclicData", vehicleDataMsgList.get(i));
			}
		}

		if (currentFuelLevel == null && firstFuelLevel != null) {
			currentFuelLevel = firstFuelLevel;
		}

		if (firstFuelLevel != null) {
			diffFuelLevel += firstFuelLevel - currentFuelLevel;
			fuelVolumeLitre = diffFuelLevel * CoseNAStringUtils.FUEL_TANK_CAPACITY_LITRE / 100;
			fuelLvel = FuelLevel.builder().initialP(firstFuelLevel)
					.lastP(currentFuelLevel).build();
		} else {
			diffFuelLevel = null;
		}

		fuelLevels.add(fuelLvel);

		LOGGER.info("Fuel Difference finally for " + tripId + " is " + diffFuelLevel + " Formula = "
				+ firstFuelLevel + " - " + currentFuelLevel + " and volume is in " + fuelVolumeLitre);
		retHM.put("fuelLevelDifference", diffFuelLevel);
		retHM.put("fuelVolumeLitre", fuelVolumeLitre);
		retHM.put("fuelLevels", fuelLevels);
		retHM.put("DTCList", dtcList);
		return retHM;
	}

	public HashMap<Object, Object> getDTCList(List<com.sample.na.domain.DTC> dtcList,
			HashMap<Object, Object> checkIfExist) {
		List<DTC> dtcToTripList = new ArrayList<>();
		List<com.sample.na.domain.DTC> dtcDomainListSave = new ArrayList<>();
		HashMap<Object, Object> hmDTCDetailStored = new HashMap<>();
		flagDTCTransDetailsNeedToSave = false;
		dtcCodesToTranslate = "";
		dtcList.forEach(dtcDomain -> {

			if (!checkIfExist.containsKey(dtcDomain.getDtcCode())) {
				checkIfExist.put(dtcDomain.getDtcCode(), dtcDomain.getDtcCode());
				DTC dtc = DTC.builder().dtcCode(dtcDomain.getDtcCode()).description(dtcDomain.getDescription())
						.severity(dtcDomain.getSeverity()).build();
				if (dtc.getDescription() == null || dtc.getDescription().equals("")
						|| CoseNAStringUtils.DTC_TRANSLATION_API_CALL_ALWAYS) {
					if (dtcCodesToTranslate != null && dtcCodesToTranslate.length() > 0) {
						dtcCodesToTranslate = dtcCodesToTranslate + ",";
					}
					dtcCodesToTranslate = dtcCodesToTranslate + dtc.getDtcCode();
					// To determine whether this should be saved or not.
					flagDTCTransDetailsNeedToSave = true;

				}
				dtcToTripList.add(dtc);
			}
		});

		HashMap<Object, Object> retHM = new HashMap<>();
		retHM.put("dtcToTripList", dtcToTripList);
		if (flagDTCTransDetailsNeedToSave) {
			LOGGER.info("DTC Translation details are null in DB for DTC Codes = " + dtcCodesToTranslate
					+ " and requesting the DTC Translation API");
			DTCTranslationResponse dtcTranslation = coseNAHelper.getDTCTranslationForCode(dtcCodesToTranslate);
			if (dtcTranslation != null && dtcTranslation.getSuccess() == "true") {
				LOGGER.info("DTC Translation API success got response of count " + dtcTranslation.getDtcCodes().size());
				if (dtcTranslation.getDtcCodes().size() > 0) {
					for (int i = 0; i < dtcTranslation.getDtcCodes().size(); i++) {
						hmDTCDetailStored.put(dtcTranslation.getDtcCodes().get(i).getCode(),
								dtcTranslation.getDtcCodes().get(i));
					}
				}
			}

			dtcList.forEach(dtcDomainObj -> {
				DTCCode DTCCodeDetail = (DTCCode) hmDTCDetailStored.get(dtcDomainObj.getDtcCode());
				DTC dtc = DTC.builder().dtcCode(dtcDomainObj.getDtcCode()).description(dtcDomainObj.getDescription())
						.severity(dtcDomainObj.getSeverity()).build();
				int index = dtcToTripList.indexOf(dtc);
				if (index >= 0) {
					if (DTCCodeDetail != null && DTCCodeDetail.getCommonUserLanguageDescription() != null) {
						dtc.setDescription(DTCCodeDetail.getCommonUserLanguageDescription());
					}
					if (DTCCodeDetail != null && DTCCodeDetail.getSeverity() != null) {
						dtc.setSeverity(DTCCodeDetail.getSeverity());
					}
					dtcToTripList.set(index, dtc);
				}

				com.sample.na.domain.DTC dtcDomSave = com.sample.na.domain.DTC.builder()
						.dtcCode(dtcDomainObj.getDtcCode()).description(dtcDomainObj.getDescription())
						.severity(dtcDomainObj.getSeverity()).build();
				if (DTCCodeDetail != null && DTCCodeDetail.getCommonUserLanguageDescription() != null) {
					dtcDomSave.setDescription(DTCCodeDetail.getCommonUserLanguageDescription());
				}
				if (DTCCodeDetail != null && DTCCodeDetail.getSeverity() != null) {
					dtcDomSave.setSeverity(DTCCodeDetail.getSeverity());
				}
				dtcDomainListSave.add(dtcDomSave);
			});

			retHM.put("dtcToSaveVM", dtcDomainListSave);
		} else {
			retHM.put("dtcToSaveVM", null);
		}
		retHM.put("dtcToTripList", dtcToTripList);
		return retHM;
	}

	public TripEndMessage getTripDataMsgForTrip(List<TripEndMessage> tripEndMsgList, String tripId) {

		for (int i = 0; i < tripEndMsgList.size(); i++) {
			if (tripEndMsgList.get(i).getTripId().equals(tripId)) {
				return tripEndMsgList.get(i);
			}
		}
		return null;
	}

	public TripAddress getAddressForLocation(String latitude, String longitude, String accuracy) {
		TripAddress tripAddress = new TripAddress();
		tripAddress.setCity("NOT AVAILABLE");
		tripAddress.setCountry("NOT AVAILABLE");
		tripAddress.setDisplayName("NOT AVAILABLE");
		tripAddress.setName("NOT AVAILABLE");
		tripAddress.setState("NOT AVAILABLE");
		tripAddress.setStreetName("NOT AVAILABLE");
		tripAddress.setStreetNumber("NOT AVAILABLE");
		return tripAddress;
	}

	public List<Path> getPathForTrip(List<VehicleDataMessage> vehicleDataMsgList, String tripId) {
		List<Path> path = new ArrayList<Path>();

		if (vehicleDataMsgList != null) {
			Lists.reverse(vehicleDataMsgList).forEach(vehicleDataMsg -> {
				if (vehicleDataMsg.getTripId().equals(tripId)) {
					vehicleDataMsg.getVehicleDataSamples().forEach(vehicleData -> {
						// System.out.println(vehicleData.getGeoPosition().getLatitude()
						// + "," + vehicleData.getGeoPosition().getLongitude());
						if (vehicleData.getGeoPosition() != null && vehicleData.getGeoPosition().getLatitude() != null
								&& vehicleData.getGeoPosition().getLongitude() != null
								&& !(vehicleData.getGeoPosition().getLatitude().equals("0"))
								&& !(vehicleData.getGeoPosition().getLongitude().equals("0"))) {
							path.add(new Path(
									Double.parseDouble(FourDecimal.format(Double.parseDouble(coseNAHelper
											.convertGPSMilliArcSecToDegrees(vehicleData.getGeoPosition().getLatitude())))),
									Double.parseDouble(FourDecimal.format(Double.parseDouble(coseNAHelper.convertGPSMilliArcSecToDegrees(
											vehicleData.getGeoPosition().getLongitude()))))));
						}
					});
				}
			});
		}

		return path;
	}
}
