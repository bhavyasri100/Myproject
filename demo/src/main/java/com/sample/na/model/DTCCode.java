package com.sample.na.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTCCode {
	@JsonProperty("Causes")
	String cause;
	
	@JsonProperty("Code")
	String code;

	@JsonProperty("CommonUserLanguageDescription")
	String commonUserLanguageDescription;

	@JsonProperty("ComponentDescription")
	String componentDescription;

	@JsonProperty("CreatedBy")
	String createdBy;

	@JsonProperty("CreatedOn")
	String createdOn;

	@JsonProperty("Culture")
	String culture;

	@JsonProperty("DTCCodeID")
	String dtcCodeID;

	@JsonProperty("Description")
	String description;

	@JsonProperty("OeSpecific")
	String oeSpecific;

	@JsonProperty("Resource")
	String resource;

	@JsonProperty("Severity")
	String severity;

	@JsonProperty("Symptoms")
	String symptoms;

	@JsonProperty("UpdatedBy")
	String updatedBy;

	@JsonProperty("UpdatedOn")
	String updatedOn;
}
