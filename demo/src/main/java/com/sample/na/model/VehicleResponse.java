package com.sample.na.model;

import java.util.List;

import lombok.Data;

@Data
public class VehicleResponse {
	private List<Vehicle> content;
	private String last;
	private int totalPages;
	private int totalElements;
	private String sort;
	private String first;
	private int numberOfElements;
	private int size;
	private int number;
	
}
