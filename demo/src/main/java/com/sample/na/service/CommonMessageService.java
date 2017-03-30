package com.sample.na.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sample.na.domain.TripEndMessage;
import com.sample.na.domain.TripStartMessage;
import com.sample.na.domain.VehicleDataMessage;
import com.sample.na.helper.CoSeNAHelper;
import com.sample.na.model.CommonMessageRequestParam;
import com.sample.na.repositories.mongo.TripDataSearchRepository;
import com.sample.na.repositories.mongo.TripEndDataRepository;
import com.sample.na.repositories.mongo.TripStartDataRepository;
import com.sample.na.repositories.mongo.VehicleDataRepository;

@Service
public class CommonMessageService {

	private final Logger LOGGER = Logger.getLogger(CommonMessageService.class);

	@Autowired
	TripStartDataRepository tripStartRepo;

	@Autowired
	VehicleDataRepository vehicleDataRepo;

	@Autowired
	TripEndDataRepository tripEndRepo;

	@Autowired
	TripDataSearchRepository tripSearchRepo;
	@Autowired
	CoSeNAHelper coseNAHelper;

	public Object getAllCommonMessage(CommonMessageRequestParam commonMsgReqParam, String tenantId) {

		List<Object> commonMessageList = new ArrayList<>();

		LOGGER.info("Authorized vehicles are " + commonMsgReqParam.getVehicle());

		List<String> vinList = Arrays.asList(commonMsgReqParam.getVehicle().split(","));
		Pageable pageable = new PageRequest(commonMsgReqParam.getPage() - 1, commonMsgReqParam.getLimit());
		List<TripStartMessage> tripStartList = tripStartRepo.findAllByVehicleIdInOrderByTimestampDesc(vinList, pageable);

		List<String> tripIDList = tripStartList.stream().map(trip -> trip.getTripId()).distinct()
				.collect(Collectors.toList());

		List<VehicleDataMessage> vehicleDataMsgList = vehicleDataRepo.findAllByTripIdIn(tripIDList);

		List<TripEndMessage> tripEndMessageList = tripEndRepo.findAllByTripIdIn(tripIDList);

		for (TripStartMessage tripStart : tripStartList) {
			Map<String, Object> retHM = new HashMap<String, Object>();
			retHM.put("tripStart", tripStart);
			List<VehicleDataMessage> vehicleDataMsgListCurrent = vehicleDataMsgList.stream()
					.filter(vDM -> vDM.getTripId().equals(tripStart.getTripId())).collect(Collectors.toList());
			System.out.println(vehicleDataMsgListCurrent);
			retHM.put("vehicleDataMessages", vehicleDataMsgListCurrent);
			System.out.println(retHM.get("vehicleDataMessages"));
			List<TripEndMessage> tripEndMessageListCurrent = tripEndMessageList.stream()
					.filter(trEnd -> trEnd.getTripId().equals(tripStart.getTripId())).collect(Collectors.toList());
			retHM.put("tripEnd", tripEndMessageListCurrent);
			commonMessageList.add(retHM);
			System.out.println(commonMessageList);
		}

		return commonMessageList;
	}
}
