package com.sample.na.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "cose_vehicle_data")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class VehicleDataMessage extends DongleDevice {

    @Id
    private String id;
    private String messageType;
    private List<VehicleData> vehicleDataSamples;

}
