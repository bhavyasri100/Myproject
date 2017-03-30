package com.sample.na.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.sample.na.domain.Health;
import com.sample.na.domain.VehicleData;
import com.sample.na.domain.VehicleDataMessage;
import com.sample.na.repositories.mongo.VehicleDataRepository;
import com.sample.na.service.TripDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(hidden = true)
@RestController
public class BaseController {

	@Autowired
	VehicleDataRepository vehicleDataRepo;

	@Autowired
	TripDataService tripDataSvc;

	@ApiOperation(hidden = true, value = "")
	@RequestMapping(value = "/api/v1/testVehicleData")
	public @ResponseBody VehicleDataMessage testVehicleData() {
		VehicleDataMessage vehicleDa = new VehicleDataMessage();
		List<VehicleData> vehicl = new ArrayList<>();
		Health health = new Health();
		health.setFuellevel("35");
		health.setOdometer("456");
		VehicleData vehiclD = new VehicleData();
		vehiclD.setHealth(health);
		vehicl.add(vehiclD);

		vehicleDa.setVehicleDataSamples(vehicl);

		return vehicleDa;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/v1/getall")
	public String testShowAllDataDB(@AuthenticationPrincipal Object user) {
		System.out.println(user);
		Map<String, Object> temp = (Map<String, Object>) user;
		System.out.println(" " + temp.get("tenant"));
		Map<String, Object> tenant = (Map<String, Object>) temp.get("tenant");
		System.out.println(tenant.get("id"));
		String result = "";
		return result;
	}

}
