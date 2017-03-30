package com.sample.na.model;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({ "fuelCostUsd", "averageKmpl", "averageFromEpaKmpl", "scoreEvents", "scoreSpeeding",
		"hardBrakes", "hardAccels", "durationOver70s", "durationOver75s", "durationOver80s", "idlingTimeS", "tags",
		"vehicleEvents", "startAddress", "endAddress" })
@ApiModel(description = "Trip details")
public class Trip {

	@Id
	private String id;
	@ApiModelProperty(value = "Time at start of trip.")
	@JsonFormat(pattern = "yyyy-MM-dd HH:MM:SS")
	private String startedAt;
	@ApiModelProperty(value = "Time at end of trip.")
	@JsonFormat(pattern = "yyyy-MM-dd HH:MM:SS")
	private String endedAt;
	@ApiModelProperty(value = "Trip URI.")
	private String url;
	@ApiModelProperty(value = "Distance of trip in miles.")
	private Double distanceM;
	@ApiModelProperty(value = "Distance of trip in kilometers.")
	private Double distanceKM;
	@ApiModelProperty(value = "Duration of trip in seconds.")
	private Long durationS;
	@ApiModelProperty(value = "Vehicle Identification Number")
	private String vin;
	@ApiModelProperty(value = "A vehicle event dictionary")
	private List<Path> path;
	private Double fuelCostUsd;
	@ApiModelProperty(value = "Fuel consumption in percentage.")
	private Double fuelConsumptionP;
	@ApiModelProperty(value = "Fuel consumption in Liter.")
	private Double fuelConsumptionL;
	@ApiModelProperty(value = "Fuel consumption in Gallons.")
	private Double fuelConsumptionG;
	@ApiModelProperty(value = "DistanceM/Fuel Connsumption in Gallons.")
	private Double fuelEconomyUS;
	@ApiModelProperty(value = "Fuel Consumption in Liter / (DistanceKM * 100)")
	private Double fuelEconomyMetric;
	@ApiModelProperty(hidden=true)
	private Double averageKmpl;
	@ApiModelProperty(hidden=true)
	private String averageFromEpaKmpl;
	@ApiModelProperty(hidden=true)
	private String scoreEvents;
	@ApiModelProperty(hidden=true)
	private String scoreSpeeding;
	@ApiModelProperty(hidden=true)
	private String hardBrakes;
	@ApiModelProperty(hidden=true)
	private String hardAccels;
	@ApiModelProperty(hidden=true)
	private Double durationOver70s;
	@ApiModelProperty(hidden=true)
	private Double durationOver75s;
	@ApiModelProperty(hidden=true)
	private Double durationOver80s;
	private String startTimezone;
	private String endTimezone;
	@ApiModelProperty(hidden=true)
	private String idlingTimeS;
	@ApiModelProperty(hidden=true)
	private List<Tag> tags;
	@ApiModelProperty(hidden=true)
	private List<VehicleEvent> vehicleEvents;
	@ApiModelProperty(value = "Lat/Long at start of trip.")
	private TripLocation startLocation;
	@ApiModelProperty(value = "Lat/Long at end of trip.", dataType="Object")
	private TripLocation endLocation;
	@ApiModelProperty(hidden=true)
	private TripAddress startAddress;
	@ApiModelProperty(hidden=true)
	private TripAddress endAddress;
	@ApiModelProperty(value = "Array of dtc code, description and itspriority")
	private List<DTC> dtc;
	@ApiModelProperty(value = "Array of inital fuel level in percentage and last fuel level percentage just before each refueling in the trip")
	private List<FuelLevel> fuelLevels;

}
