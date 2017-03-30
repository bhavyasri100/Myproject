package com.sample.na.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripRequestParams {

	Long startedAtLTE;
	Long startedAtGTE;
	Long endedAtLTE;
	Long endedAtGTE;
	String vehicle;
	String tagsIn;
	Integer page;
	Integer limit;
	String tripId;
}
