package com.sample.na.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sample.na.domain.Acceleration;

@Repository
public interface AccelerationRepository extends MongoRepository<Acceleration, String> {
	/* List<Acceleration> findAllBy(TextCriteria criteria); */

	// public List<Acceleration> findTop500ByOrderByIdDesc();
}
