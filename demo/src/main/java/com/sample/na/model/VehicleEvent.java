package com.sample.na.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleEvent {

    private String type;
    private String lat;
    private String lon;
    private String createdAt;
    private String gForce;

}
