package com.sample.na.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleData {

	private Acceleration vehicleAcceleration;
	private Battery battery;
	private List<DTC> dtcs;
	private GeoPosition geoPosition;
	private Gyro gyro;
	private Health health;
	private Oil oil;
	private Speed vehicleSpeed;
	private Tire tire;
	private long timestamp;

}
