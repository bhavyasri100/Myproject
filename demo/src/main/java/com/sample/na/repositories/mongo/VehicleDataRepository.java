package com.sample.na.repositories.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sample.na.domain.VehicleDataMessage;

@Repository
public interface VehicleDataRepository extends MongoRepository<VehicleDataMessage, String> {

	List<VehicleDataMessage> findByTimestampLessThanEqual(Long created_at__lte);

	List<VehicleDataMessage> findAllByTripIdOrderByTimestampDesc(String tripId);

	VehicleDataMessage findTop1ByVehicleIdOrderByTimestampDesc(String vehicleId);

	List<VehicleDataMessage> findByTimestampGreaterThanEqual(Long start_at__gte);
	
	List<VehicleDataMessage> findTop2ByVehicleIdOrderByTimestampDesc(String vehicleId);
	
	List<VehicleDataMessage> findTop5ByVehicleIdOrderByTimestampDesc(String vehicleId);
	
	List<VehicleDataMessage> findTop5tripIdByVehicleIdOrderByTimestampDesc(String vehicleId);
	
	VehicleDataMessage findTop1ByVehicleIdOrderByTimestampAsc(String vehicleId);

	List<VehicleDataMessage> findAllByTripIdIn(List<String> tripIDList);

}
