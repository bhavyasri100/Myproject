package com.sample.na.domain;

import lombok.Data;

@Data
public class Tire {

    private boolean pressureLightStatus;
    private String pressureLF;
    private String pressureRF;
    private String pressureLR;
    private String pressureRR;
    private String pressureSpare;

}
