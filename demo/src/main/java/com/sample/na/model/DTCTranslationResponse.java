package com.sample.na.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTCTranslationResponse {
	@JsonProperty("DTCCodes")
	List<DTCCode> dtcCodes;
	
	@JsonProperty("Messages")
	List<DTCMessage> messages;
	
	@JsonProperty("Success")
	String success;
}
