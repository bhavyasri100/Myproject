package com.sample.na.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoPosition {

    private Double accuracy;
    private Double latitude;
    private Double altitude;
    private Double longitude;
    private Double direction;

}
