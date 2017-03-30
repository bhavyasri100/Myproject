package com.sample.na.repositories.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sample.na.domain.TripEndMessage;

@Repository
public interface TripEndDataRepository extends MongoRepository<TripEndMessage, String> {

	List<TripEndMessage> findAllByTripIdOrderByTimestampDesc(String tripId);

	TripEndMessage findTop1ByTripIdOrderByTimestampDesc(String tripId);

	List<TripEndMessage> findAllByTripIdIn(List<String> tripIDList);

}
