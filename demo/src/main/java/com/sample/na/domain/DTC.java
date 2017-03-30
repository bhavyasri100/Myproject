package com.sample.na.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DTC {

	private String dtcCode;
	private String severity;
	private String description;

}
