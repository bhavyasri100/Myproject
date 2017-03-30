package com.sample.na.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sample.na.domain.TripEndMessage;
import com.sample.na.domain.TripStartMessage;
import com.sample.na.domain.VehicleDataMessage;
import com.sample.na.helper.DisallowUndeclaredRequestParams;
import com.sample.na.model.CommonMessageRequestParam;
import com.sample.na.model.UserContext;
import com.sample.na.service.CommonMessageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

//@Api(hidden = true)
@Api(value = "CommonMessageAPI", tags = { "CommonMessage Data" }, produces = "application/json", description = "Message Related Data")
@RestController
@RequestMapping("api/v1")
public class CommonMessageController {

	@Autowired
	CommonMessageService commonMsgSvc;
	
	@ApiIgnore
	@RequestMapping(value = "/message")
	public @ResponseBody Object getAllVehicleMessage(@RequestHeader(required = false) String Cookie,
			HttpServletRequest request, @RequestParam(required = false) String vehicle,
			@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit,

			@AuthenticationPrincipal Object principal) {
		System.out.println(Cookie);
		// while(request.getSession().getAttributeNames().hasMoreElements()){
		System.out.println(request.getSession().getAttribute("SPRING_SECURITY_CONTEXT"));
		// }
		CommonMessageRequestParam commonMsgReqParam = new CommonMessageRequestParam();
		if (vehicle != null) {
			commonMsgReqParam.setVehicle(vehicle);
		}
		if (page != null) {
			commonMsgReqParam.setPage(page);
		} else {
			commonMsgReqParam.setPage(1);
		}
		if (limit != null) {
			commonMsgReqParam.setLimit(limit);
		} else {
			commonMsgReqParam.setLimit(10);
		}
		String tenantId = getTenantDetailsFromAuthToken(principal);
		return commonMsgSvc.getAllCommonMessage(commonMsgReqParam, tenantId);
	}
	
	@ApiOperation(value = "Returns a Trip start message", notes = "Trip start message of CommonMessageAPI.", produces = "application/json", response = TripStartMessage.class)
	@RequestMapping(value = "/message/tripStartMessage", method = RequestMethod.GET)
	@DisallowUndeclaredRequestParams
	public @ResponseBody Object getTripStartMessage(@ApiIgnore @AuthenticationPrincipal UserContext userContext) {
		
		System.out.println("Trip start message....");
		return new Object();
	}
	@ApiOperation(value = "Returns a VehicleDataMessage", notes = "VehicleDataMessage of CommonMessageAPI.", produces = "application/json", response = VehicleDataMessage.class)
	@RequestMapping(value = "/message/vehicleDataMessage", method = RequestMethod.GET)
	@DisallowUndeclaredRequestParams
	public @ResponseBody Object getVehicleDataMessage(@ApiIgnore @AuthenticationPrincipal UserContext userContext) {
		
		System.out.println("Vehicle Data message....");
		return new Object();
	}
	@ApiOperation(value = "Returns a Trip end message", notes = "Trip end message of CommonMessageAPI.", produces = "application/json", response = TripEndMessage.class)
	@RequestMapping(value = "/message/tripEndMessage", method = RequestMethod.GET)
	@DisallowUndeclaredRequestParams
	public @ResponseBody Object getTripEndMessage(@ApiIgnore @AuthenticationPrincipal UserContext userContext) {
		
		System.out.println("Trip end message....");
		return new Object();
	}

	@SuppressWarnings("unused")
	private String getTenantDetailsFromAuthToken(Object user) {
		System.out.println(user);
		Map<String, Object> temp = (Map<String, Object>) user;
		System.out.println(" " + temp.get("tenant"));
		Map<String, Object> tenant = (Map<String, Object>) temp.get("tenant");
		System.out.println(tenant.get("id"));
		return tenant.get("id").toString();
	}
}
