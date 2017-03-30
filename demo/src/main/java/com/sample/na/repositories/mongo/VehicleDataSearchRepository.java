package com.sample.na.repositories.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

import com.sample.na.domain.VehicleDataMessage;

@Repository
public class VehicleDataSearchRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	public VehicleDataMessage searchVehicleDataByField(String vehicleId, String field, String tenantId) {
		BasicQuery basicQuery;
		VehicleDataMessage vehicleDataMessage;
		if (field == "vehicleDataSamples.geoPosition") {

			if (tenantId != null) {
				basicQuery = new BasicQuery(
						"{$and : [{'vehicleDataSamples' : { '$elemMatch':{'geoPosition.latitude':{$nin:['0']}, 'geoPosition.longitude':{$nin:['0']}}}}, {'vehicleDataSamples.geoPosition' : {$exists : true}} ,{'tenant.tenantId' : '"
								+ tenantId + "','vehicleId' : '" + vehicleId + "'}]}");
			} else {
				basicQuery = new BasicQuery(
						"{$and : [{'vehicleDataSamples' : { '$elemMatch':{'geoPosition.latitude':{$nin:['0']}, 'geoPosition.longitude':{$nin:['0']}}}}, {'vehicleDataSamples.geoPosition' : {$exists : true}} ,{'vehicleId' : '"
								+ vehicleId + "'}]}");
			}

		} else {
			if (tenantId != null) {
				basicQuery = new BasicQuery(
						"{" + field.toString() + ":{$not : { $type : 10 },$exists : true}, tenant.tenantId : '"
								+ tenantId + "', vehicleId : '" + vehicleId + "'}");
			} else {
				basicQuery = new BasicQuery("{" + field.toString()
						+ ":{$not : { $type : 10 },$exists : true}, vehicleId : '" + vehicleId + "'}");
			}

		}
		vehicleDataMessage = mongoTemplate.findOne(basicQuery.with(new Sort(new Order(Direction.DESC, "timestamp"))),
				VehicleDataMessage.class);

		return vehicleDataMessage;
	}
}