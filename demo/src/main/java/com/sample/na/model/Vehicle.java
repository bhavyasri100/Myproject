package com.sample.na.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties({ "displayName", "fuelGrade" })
// @JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {

	private String id;
	@ApiModelProperty(value = "Vehicle Identification Number")
	private String vin;
	@ApiModelProperty(value = "The identifier of the SIM card")
	private String imsi;
	@ApiModelProperty(value = "Vehicle Make")
	private String make;
	@ApiModelProperty(value = "Vehicle Model")
	private String model;
	@ApiModelProperty(value = "Vehicle Submodel")
	private String subModel;
	@ApiModelProperty(value = "Vehicle Year")
	private int year;
	@ApiModelProperty(hidden = true)
	private String displayName;
	@ApiModelProperty(value = "A reference to the vehicle data location")
	private String url;
	@ApiModelProperty(value = "Amount of fuel as a percentage of total capacity")
	private Double fuelLevelPercent;
	@ApiModelProperty(value = "Amount of miles shown on the odometer")
	private String odometer;
	@ApiModelProperty(hidden = true)
	private String batteryVoltage;
	@ApiModelProperty(hidden = true)
	private String fuelGrade;
	@ApiModelProperty(value = "Currently active DTC codes, severity, and a common language description.")
	private List<DTC> activeDtcs;
	@ApiModelProperty(value = "Current or last known location")
	private GeoPosition location;
	@ApiModelProperty(value = "The timestamp that the vehicle was created")
	private String createdAt;
	@ApiModelProperty(value = "The timestamp that the vehicle was updated")
	private String updatedAt;
}
