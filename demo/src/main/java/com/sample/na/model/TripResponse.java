package com.sample.na.model;

import java.util.List;

import lombok.Data;

@Data
public class TripResponse {
	private List<Trip> content;
	private String last;
	private int totalPages;
	private int totalElements;
	private String first;
	private String sort;
	private int numberOfElements;
	private int size;
	private int number;
}
