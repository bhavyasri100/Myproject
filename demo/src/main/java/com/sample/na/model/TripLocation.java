package com.sample.na.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripLocation {

	private Double lat;
	private Double lon;
	private Double accuracyM;

}
