package com.sample.na.domain;

import lombok.Data;

@Data
public class Thing {

	private String thingId;
	private Attribute attributes;
	private Feature features;
}
