package com.sample.na.domain;

import lombok.Data;

@Data
public class DongleDevice {
	
	private String tripId;
    private String messageId;
    private String deviceId;
    private String vehicleId;
    private String messageVersion;
    private String firmwareVersion;
    private Long timestamp;

}
