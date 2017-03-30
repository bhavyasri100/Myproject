package com.sample.na.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sample.na.domain.TripEndMessage;
import com.sample.na.domain.TripStartMessage;
import com.sample.na.helper.DisallowUndeclaredRequestParams;
import com.sample.na.model.Trip;
import com.sample.na.model.TripRequestParams;
import com.sample.na.model.TripResponse;
import com.sample.na.model.UserContext;
import com.sample.na.service.TripDataService;
import com.sample.na.util.CoseNAStringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Trip API", tags = { "Trip Data" }, produces = "application/json", description = "Trip Related Data")
@RestController
@RequestMapping("api/v1")
public class TripController {

	@Autowired
	TripDataService tripSvc;

	@CrossOrigin(origins = "*")
	@ApiOperation(value = "Returns List of Trip Objects", notes = "Provides a list of trip objects that match the query parameters.", produces = "application/json", response = TripResponse.class)
	@RequestMapping(value = "/trips", method = RequestMethod.GET)
	@DisallowUndeclaredRequestParams
	public @ResponseBody Object getAllTrip(@RequestParam(value = "vin", required = false) String vin,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "size", required = false) String size,
			@ApiIgnore @AuthenticationPrincipal UserContext userContext) {

		TripRequestParams tripRequestParam = TripRequestParams.builder().startedAtLTE(null).startedAtGTE(null)
				.endedAtLTE(null).endedAtGTE(null).vehicle(vin).tagsIn(null).page((page!=null)?Integer.valueOf(page.toString()):CoseNAStringUtils.PAGING_DEFAULT_PAGE)
				.limit((size!=null)?Integer.valueOf(size.toString()):CoseNAStringUtils.PAGING_DEFAULT_LIMIT).build();

		System.out.println("Trip API call for Tenant ID : " + userContext.getTenant().get("id"));
		return tripSvc.getAllTrip(tripRequestParam, userContext);
	}

	@ApiOperation(value = "Returns a Trip Object", notes = "Provides a single trip object based on the trip id.", produces = "application/json", response = Trip.class, responseContainer = "List")
	@RequestMapping(value = "/trips/{id}", method = RequestMethod.GET)
	@DisallowUndeclaredRequestParams
	public @ResponseBody Object getTripById(@PathVariable(value = "id") String id,
			@ApiIgnore @AuthenticationPrincipal UserContext userContext) {
		TripRequestParams tripRequestParam = TripRequestParams.builder().tripId(id).build();
		System.out.println("Trip API call for Tenant ID : " + userContext.getTenant());
		return tripSvc.getAllTrip(tripRequestParam, userContext);
	}

	@ApiIgnore
	@ApiOperation(value = "Returns a TripTag Object", notes = "Removes a TripTag from a Trip.", produces = "application/json", response = Trip.class)
	@RequestMapping(value = "/trips/{tripId}/tag/{tag}", method = RequestMethod.DELETE)
	@DisallowUndeclaredRequestParams
	public void deleteTripByIdAndTag(@PathVariable(value = "tripId") String tripId,
			@PathVariable(value = "tag") String tag) {

	}

	@ApiIgnore
	@ApiOperation(value = "Returns a TripTagCreate Object", notes = "Create a TripTag to apply a Tag to a Trip.", produces = "application/json", response = Trip.class)
	@RequestMapping(value = "/trip/{tripId}/tag", method = RequestMethod.POST)
	public void createTripByIdAndTag(@PathVariable(value = "tripId") String tripId) {

	}
	



}
