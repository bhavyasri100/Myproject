package com.sample.na.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripAddress {

	private String name;
	private String displayName;
	private String streetNumber;
	private String streetName;
	private String city;
	private String state;
	private String country;

}
