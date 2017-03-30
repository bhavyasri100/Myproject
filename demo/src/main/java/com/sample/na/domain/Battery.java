package com.sample.na.domain;

import lombok.Data;

@Data
public class Battery {

	private String coldCrankingAmperes;
	private String chargingSystemLightStatus;
	private String voltage;
	private String temperature;

}
