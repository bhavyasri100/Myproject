package com.sample.na.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.sample.na.model.DTC;
import com.sample.na.domain.TripEndMessage;
import com.sample.na.domain.TripStartMessage;
import com.sample.na.domain.VehicleDataMessage;
import com.sample.na.helper.CoSeNAHelper;
import com.sample.na.model.FuelLevel;
import com.sample.na.model.Trip;
import com.sample.na.model.TripLocation;
import com.sample.na.model.TripRequestParams;
import com.sample.na.model.UserContext;
import com.sample.na.repositories.mongo.TripDataSearchRepository;
import com.sample.na.repositories.mongo.TripEndDataRepository;
import com.sample.na.repositories.mongo.TripStartDataRepository;
import com.sample.na.repositories.mongo.VehicleDataRepository;
import com.sample.na.service.helper.TripDataMessageHelper;
import com.sample.na.util.CoseNAStringUtils;


@Service
public class TripDataService {

	private final Logger LOGGER = Logger.getLogger(TripDataService.class);

	@Autowired
	TripStartDataRepository tripStartRepo;
	@Autowired
	TripEndDataRepository tripEndRepo;
	@Autowired
	VehicleDataRepository vehicleDataRepo;
	@Autowired
	TripDataSearchRepository tripDataSearchRepo;

	@Autowired
	TripDataMessageHelper tripDataMsgHelper;
	@Autowired
	CoSeNAHelper coseNAHelper;

	List<String> tripIds;

	Long tripStartedAt, tripEndedAt = 0L;

	private Double endOdometer = null, startOdometer = null, fuelLevelDiff, fuelVolumeLitre;
	private TripLocation tripStartLocation;
	private TripEndMessage tripEndMsgForCurrTrip;
	private VehicleDataMessage lastVehicleMsgPerCycle;
	private boolean flagInProgress = false;
	private TripLocation tripEndLocation;
	private List<DTC> dtcList;
	private List<FuelLevel> fuelLevels;

	DecimalFormat SingleDecimal = new DecimalFormat("##.#");
	DecimalFormat TwoDecimal = new DecimalFormat("##.##");

	@SuppressWarnings("unchecked")
	public Object getAllTrip(TripRequestParams tripRequestParam, UserContext userContext) {

		LOGGER.info("In service Get All trip");

		LOGGER.info("Authorizing");
		String tenantId = null;

		if (!userContext.isSuperAdmin()) {
			tenantId = (String) userContext.getTenant().get("id");
		}

		Criteria criteriaStartMessage = tripDataMsgHelper.createCriteriaForTripDataStart(tripRequestParam, tenantId);

		List<TripStartMessage> tripStartMsg = (List<TripStartMessage>) tripDataSearchRepo
				.searchTripStart(criteriaStartMessage, tripRequestParam.getPage(), tripRequestParam.getLimit());

		int totalCount = tripDataSearchRepo.searchTripStartCount(criteriaStartMessage, tripRequestParam.getPage(),
				tripRequestParam.getLimit());

		LOGGER.info("The start event message query returned for all trips " + tripStartMsg.size() + " records");

		tripIds = tripStartMsg.stream().map(tripStart -> tripStart.getTripId()).collect(Collectors.toList());

		// tripIds =
		// tripDataMsgHelper.getTripIdsFromStartMessage(tripStartMsg);

		Criteria criteriaEndMessage = tripDataMsgHelper.createCriteriaForTripDataEnd(tripRequestParam.getEndedAtLTE(),
				tripRequestParam.getEndedAtGTE(), tripIds);

		List<TripEndMessage> tripEndMsgList = (List<TripEndMessage>) tripDataSearchRepo
				.searchTripEnd(criteriaEndMessage, tripRequestParam.getPage(), tripRequestParam.getLimit());

		LOGGER.info("The end event message query returned for all trips " + tripEndMsgList.size() + " records");
		System.out.println(tripIds);

		Criteria criteriaVehicleDataMessage = tripDataMsgHelper.createCriteriaForVehicleData(tripIds);

		List<VehicleDataMessage> vehicleDataMsgList = (List<VehicleDataMessage>) tripDataSearchRepo
				.searchVehicleData(criteriaVehicleDataMessage, tripRequestParam.getPage(), tripRequestParam.getLimit());

		LOGGER.info("The vehicle data message query returned for all trips " + vehicleDataMsgList.size() + " records");

		List<Trip> tripList = new ArrayList<Trip>();

		LOGGER.info("Starting iteration over the TripStartMessage ");

		for (TripStartMessage tripstart : tripStartMsg) {
			lastVehicleMsgPerCycle = null;
			tripStartedAt = null;
			startOdometer = null;
			endOdometer = null;
			tripEndedAt = null;
			fuelLevelDiff = null;
			fuelVolumeLitre = null;
			fuelLevels = null;
			flagInProgress = false;
			dtcList = null;
			tripStartLocation = null;
			tripEndLocation = null;

			Trip tripDetail = new Trip();
			tripDetail.setId(tripstart.getTripId());
			tripDetail.setStartedAt(coseNAHelper.convertMilliSecondToDateTime(tripstart.getTimestamp()));
			tripStartedAt = tripstart.getTimestamp();
			LOGGER.debug("Start time for this trip id " + tripstart.getTripId() + " is " + tripstart.getTimestamp()
					+ " and DateTime is " + coseNAHelper.convertMilliSecondToDateTime(tripstart.getTimestamp()));

			tripDetail.setUrl(CoseNAStringUtils.API_ENDPOINT_TRIP_V1 + tripstart.getTripId());
			tripDetail.setVin(tripstart.getVehicleId());

			HashMap<Object, Object> vehicleDataMsgMapTemp = tripDataMsgHelper.getVehicleDataMes(vehicleDataMsgList,
					tripstart.getTripId());

			if (vehicleDataMsgMapTemp.containsKey("lastVehicleCyclicData")) {
				// These are last vehicle data inside the vehicle message
				lastVehicleMsgPerCycle = (VehicleDataMessage) vehicleDataMsgMapTemp.get("lastVehicleCyclicData");

				if (vehicleDataMsgMapTemp != null) {
					if (vehicleDataMsgMapTemp.get("fuelLevelDifference") != null) {
						fuelLevelDiff = (double) vehicleDataMsgMapTemp.get("fuelLevelDifference");
					}
					if (vehicleDataMsgMapTemp.get("fuelVolumeLitre") != null) {
						fuelVolumeLitre = (double) vehicleDataMsgMapTemp.get("fuelVolumeLitre");
					}
					if (vehicleDataMsgMapTemp.get("fuelLevels") != null) {
						fuelLevels = (List<FuelLevel>) vehicleDataMsgMapTemp.get("fuelLevels");
					}
					if (vehicleDataMsgMapTemp.containsKey("endOdometer")
							&& vehicleDataMsgMapTemp.get("endOdometer") != null) {
						endOdometer = Double.parseDouble((String) vehicleDataMsgMapTemp.get("endOdometer"));
					}
					if (vehicleDataMsgMapTemp.containsKey("startOdometer")
							&& vehicleDataMsgMapTemp.get("startOdometer") != null) {
						startOdometer = Double.parseDouble((String) vehicleDataMsgMapTemp.get("startOdometer"));
					}
					if (vehicleDataMsgMapTemp.containsKey("tripStartLocation")
							&& vehicleDataMsgMapTemp.get("tripStartLocation") != null) {
						tripStartLocation = (TripLocation) vehicleDataMsgMapTemp.get("tripStartLocation");
					}
					if (vehicleDataMsgMapTemp.containsKey("tripEndLocation")
							&& vehicleDataMsgMapTemp.get("tripEndLocation") != null) {
						tripEndLocation = (TripLocation) vehicleDataMsgMapTemp.get("tripEndLocation");
					}
				}

				if (vehicleDataMsgMapTemp != null && vehicleDataMsgMapTemp.get("DTCList") != null) {
					dtcList = (List<DTC>) vehicleDataMsgMapTemp.get("DTCList");
				}

				tripDetail.setStartLocation(tripStartLocation);
			}
			tripEndMsgForCurrTrip = tripDataMsgHelper.getTripDataMsgForTrip(tripEndMsgList, tripstart.getTripId());
			if (tripEndMsgForCurrTrip != null) {
				tripDetail.setEndedAt(coseNAHelper.convertMilliSecondToDateTime(tripEndMsgForCurrTrip.getTimestamp()));
				LOGGER.debug("The end time is from trip End message - " + tripEndMsgForCurrTrip.getTimestamp()
						+ " for the tripId=" + tripEndMsgForCurrTrip.getTripId() + " and date time is "
						+ coseNAHelper.convertMilliSecondToDateTime(tripEndMsgForCurrTrip.getTimestamp()));
				tripEndedAt = tripEndMsgForCurrTrip.getTimestamp();
			} else {
				if (lastVehicleMsgPerCycle != null) {
					LOGGER.debug("The end time is from last vehicle data message - "
							+ lastVehicleMsgPerCycle.getTimestamp() + " for the tripid="
							+ lastVehicleMsgPerCycle.getTripId() + " and date time is "
							+ coseNAHelper.convertMilliSecondToDateTime(lastVehicleMsgPerCycle.getTimestamp()));

					if ((Calendar.getInstance().getTimeInMillis() - lastVehicleMsgPerCycle.getTimestamp())
							/ 1000 < CoseNAStringUtils.DIFF_IN_SECONDS_FOR_IN_PROGRESS_TRIP) {
						List<TripStartMessage> tripStartMsgSameVinAfterThisList = tripStartRepo
								.findAllByTimestampGreaterThanAndVehicleId(lastVehicleMsgPerCycle.getTimestamp(),
										tripstart.getVehicleId());
						LOGGER.info("Trips start fro the vin " + tripstart.getVehicleId() + " and the timestamp "
								+ lastVehicleMsgPerCycle.getTimestamp() + " Are " + tripStartMsgSameVinAfterThisList);

						if (tripStartMsgSameVinAfterThisList.size() == 0) {
							flagInProgress = true;
						}
					}

					if (!flagInProgress) {
						tripDetail.setEndedAt(
								coseNAHelper.convertMilliSecondToDateTime(lastVehicleMsgPerCycle.getTimestamp()));
						tripEndedAt = lastVehicleMsgPerCycle.getTimestamp();
					}
				}
			}

			LOGGER.info("Odometer values(start, end) are " + startOdometer + "," + endOdometer + " for the tripId="
					+ tripstart.getTripId());

			if (!flagInProgress && endOdometer != null && startOdometer != null) {

				tripDetail.setDistanceM(Double.parseDouble(TwoDecimal.format(endOdometer - startOdometer)));

			}
			if (!flagInProgress) {
				if (fuelLevelDiff != null) {
					tripDetail
							.setFuelConsumptionP(Double.parseDouble(TwoDecimal.format(fuelLevelDiff)));
					tripDetail.setFuelConsumptionL(Double.parseDouble(TwoDecimal.format(fuelVolumeLitre)));
					tripDetail.setFuelConsumptionG(Double.parseDouble(TwoDecimal.format((fuelLevelDiff * 13.2) / 100)));
				}
				tripDetail.setEndLocation(tripEndLocation);
			}
			if (tripDetail.getDistanceM() != null) {

				tripDetail.setDistanceKM(Double.parseDouble(TwoDecimal.format(tripDetail.getDistanceM() * 1.60934)));
			}
			if (tripDetail.getDistanceM() != null && fuelVolumeLitre != null && fuelVolumeLitre != 0) {

				tripDetail.setFuelEconomyUS(
						(double) Math.round(tripDetail.getDistanceM() / tripDetail.getFuelConsumptionG()));
			}
			if (tripDetail.getDistanceKM() != null && tripDetail.getDistanceKM() != 0 && fuelVolumeLitre != null) {

				tripDetail.setFuelEconomyMetric(Double.parseDouble(SingleDecimal.format(
						(tripDetail.getFuelConsumptionL()) / (tripDetail.getDistanceKM() / 100))));

			}
			if (tripDetail.getEndedAt() != null && tripDetail.getStartedAt() != null && !flagInProgress) {
				tripEndedAt = tripEndedAt / 1000;
				tripEndedAt = tripEndedAt * 1000;
				tripStartedAt = tripStartedAt / 1000;
				tripStartedAt = tripStartedAt * 1000;
				tripDetail.setDurationS((tripEndedAt - tripStartedAt) / 1000);
			} else {
				tripDetail.setDurationS(null);
			}

			tripDetail.setPath(tripDataMsgHelper.getPathForTrip(vehicleDataMsgList, tripstart.getTripId()));

			tripDetail.setDurationOver70s(0.00);
			tripDetail.setDurationOver75s(0.0);
			tripDetail.setDurationOver80s(0.0);

			tripDetail.setEndTimezone(TimeZone.getDefault().getID());
			tripDetail.setFuelCostUsd(0.0);
			tripDetail.setHardAccels(null);
			tripDetail.setHardBrakes(null);
			tripDetail.setIdlingTimeS(null);

			tripDetail.setScoreEvents(null);
			tripDetail.setScoreSpeeding(null);

			tripDetail.setStartTimezone(TimeZone.getDefault().getID());
			tripDetail.setTags(null);
			tripDetail.setVehicleEvents(null);

			tripDetail.setDtc(dtcList);
			tripDetail.setFuelLevels(fuelLevels);

			tripList.add(tripDetail);
			vehicleDataMsgMapTemp = null;
		}

		LOGGER.debug("Total trip count is for all trips for trip Id" + tripList.size() + " records");

//		if (totalCount > 1) {

			return coseNAHelper.PageableTripResponse(tripList, totalCount,
					(tripRequestParam.getPage() == null) ? CoseNAStringUtils.PAGING_DEFAULT_PAGE
							: tripRequestParam.getPage(),
					(tripRequestParam.getLimit() == null) ? CoseNAStringUtils.PAGING_DEFAULT_LIMIT
							: tripRequestParam.getLimit());
//		} else {
//			return tripList;
//		}

	}

}
