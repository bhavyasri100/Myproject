package com.sample.na.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sample.na.model.DongleDevice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Device API", tags = { "Device Data Controller" }, produces = "application/json", hidden=true)
@RestController
@RequestMapping("api/v1")
public class DongleDeviceController {

	@ApiOperation(value = "Returns an array of devices and their information", notes = "Returns an array of devices and their information", produces = "application/json",hidden=true, response = DongleDevice.class)
	@RequestMapping(value = "/device", method = RequestMethod.GET)
	public void getAllDevice(
			@RequestParam(value = "device_serial_number", required = false) String deviceSerialNumber,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "limit", required = false) Integer limit) {

	}

	@ApiOperation(value = "Returns a single device's information", notes = "Returns a single device's information", produces = "application/json", response = DongleDevice.class, hidden=true)
	@RequestMapping(value = "/device/{vehicleId}", method = RequestMethod.GET)
	public void getDeviceByVehicleId(@PathVariable(value = "vehicleId") String vehicleId,
			@RequestParam(value = "device_serial_number", required = false) String device_serial_number,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "limit", required = false) Integer limit) {
	}

}
