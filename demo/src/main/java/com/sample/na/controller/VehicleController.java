package com.sample.na.controller;

import java.rmi.ServerException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sample.na.helper.CoSeNAHelper;
import com.sample.na.helper.DisallowUndeclaredRequestParams;
import com.sample.na.helper.UndeclaredRequestParamException;
import com.sample.na.model.UserContext;
import com.sample.na.model.VehicleMILHistory;
import com.sample.na.model.VehicleResponse;
import com.sample.na.service.VehicleDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Vehicle API", tags = { "Vehicle Data" }, produces = "application/json", description = "Vehicle list")
@RestController
@RequestMapping("api/v1")
public class VehicleController {

	private static final Logger LOGGER = Logger.getLogger(VehicleController.class);

	@Autowired
	VehicleDataService vehicleDataSvc;

	@Autowired
	CoSeNAHelper coseNAHelper;

	@ApiIgnore
	@ApiOperation(value = "Returns an array of VehicleMILHistory Objects", notes = "Returns an array of VehicleMILHistory Objects", produces = "application/json", response = VehicleMILHistory.class)
	@DisallowUndeclaredRequestParams
	@RequestMapping(value = "/vehicles/{vehicleId}/mil", method = RequestMethod.GET)
	public void getVehicleMilByVehicleId(@PathVariable(value = "vehicleId") String vehicleId,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "limit", required = false) Integer limit) {
	}

	@ApiOperation(value = "Returns Vehicle list", notes = "Provides a list of vehicle objects that match the query parameters.", produces = "application/json", response = VehicleResponse.class)
	@DisallowUndeclaredRequestParams
	@RequestMapping(value = "/vehicles", method = RequestMethod.GET)
	public Object getVehicleListByVin(@ApiIgnore @AuthenticationPrincipal UserContext userContext,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "size", required = false) String size, HttpServletRequest request,
			@RequestParam(value = "vin", required = false) String vin) throws ServerException {
		String rawToken = coseNAHelper.getRawTokenFromHeader(request);
		LOGGER.info("Usertype is admin? " + userContext.isSuperAdmin());
		return vehicleDataSvc.getAllVehicleListFromDMByVin(vin, page, size, rawToken, userContext);
	}
	
	@ExceptionHandler(UndeclaredRequestParamException.class)
	public ResponseEntity<String> handle(UndeclaredRequestParamException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}