package com.sample.na.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoPosition {

    private String accuracy;
    private String latitude;
    private String altitude;
    private String longitude;
    private String direction;

}
