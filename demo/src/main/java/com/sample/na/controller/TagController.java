package com.sample.na.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sample.na.model.Tag;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Tag API", tags = { "Tag Data Controller" }, produces = "application/json", hidden=true)
@RestController
@RequestMapping("api/v1")
public class TagController {

	@ApiOperation(value = "Returns an array of Tag Objects", notes = "Tags are classifiers for users' trips.", produces = "application/json", response = Tag.class, hidden=true)
	@RequestMapping(value = "/tag", method = RequestMethod.GET)
	public void getAllTags(@RequestParam(value = "tagStartsWith", required = false) String tagStartsWith,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "limit", required = false) Integer limit) {

	}

	@ApiOperation(value = "Returns a Tag Object", notes = "Tags are classifiers for users' trips", produces = "application/json", response = Tag.class, hidden=true)
	@RequestMapping(value = "/tag/{slug}", method = RequestMethod.GET)
	public void getDeviceByVehicleId(@PathVariable(value = "slug") String slug) {

	}

}
