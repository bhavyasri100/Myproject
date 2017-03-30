package com.sample.na.repositories.mongo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sample.na.domain.TripStartMessage;

@Repository
public interface TripStartDataRepository extends MongoRepository<TripStartMessage, String> {

	List<TripStartMessage> findAllBy(Criteria criteria);

	TripStartMessage findTop1ByVehicleIdOrderByTimestampDesc(String vehicleId);

	List<TripStartMessage> findAllByVehicleId(String vehicleId);

	TripStartMessage findTop1ByTripIdOrderByTimestampDesc(String tripId);

	List<TripStartMessage> findAllByTimestampGreaterThanAndVehicleId(Long timestamp, String vehicleId);

	List<TripStartMessage> findAllByTimestampLessThanAndVehicleId(Long timestamp, String vehicleId);

	List<TripStartMessage> findAllByVehicleIdInOrderByTimestampDesc(List<String> vinList, Pageable pageable);

}
