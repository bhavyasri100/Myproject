package com.sample.na.domain;

import lombok.Data;

@Data
public class Feature {

	private String iccId;
	private String macAddress;
	private String swRevision;
	private String pin;
	private String productionNumber;
	private String imei;
	private String imsi;
	private String publicKey;
	private String hwRevision;
	private String flagged;
	private String comment;
}
