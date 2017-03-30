package com.sample.na.model;

import lombok.Data;

@Data
public class ApiError {

	private int code;
	private String message;
	private String fields;

}
