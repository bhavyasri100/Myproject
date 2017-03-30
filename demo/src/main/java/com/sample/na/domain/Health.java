package com.sample.na.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Health {

	private String fuellevel;
	private String odometer;
	private boolean milLightStatus;

}
