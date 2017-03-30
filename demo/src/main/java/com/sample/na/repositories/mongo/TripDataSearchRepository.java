package com.sample.na.repositories.mongo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.sample.na.domain.TripEndMessage;
import com.sample.na.domain.TripStartMessage;
import com.sample.na.domain.VehicleDataMessage;
import com.sample.na.util.CoseNAStringUtils;

@Repository
public class TripDataSearchRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	public Collection<TripStartMessage> searchTripStart(Criteria criteria, Integer page, Integer limit) {

		if (page != null || limit != null) {
			page=(page!=null)?page:CoseNAStringUtils.PAGING_DEFAULT_PAGE;
			limit=(limit!=null)?limit:CoseNAStringUtils.PAGING_DEFAULT_LIMIT; 
			PageRequest pageRequest = new PageRequest(page, limit);
			return mongoTemplate.find(
					Query.query(criteria).with(pageRequest).with(new Sort(new Order(Direction.DESC, "timestamp"))),
					TripStartMessage.class);
			// Default pagination will be applied
		} else if (CoseNAStringUtils.PAGING_DEFAULT_VALUES_ACTIVE) {
			PageRequest pageRequest = new PageRequest(CoseNAStringUtils.PAGING_DEFAULT_PAGE,
					CoseNAStringUtils.PAGING_DEFAULT_LIMIT);
			return mongoTemplate.find(
					Query.query(criteria).with(pageRequest).with(new Sort(new Order(Direction.DESC, "timestamp"))),
					TripStartMessage.class);

		}

		return mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC, "timestamp"))),
				TripStartMessage.class);
	}

	public Collection<TripEndMessage> searchTripEnd(Criteria criteria, Integer page, Integer limit) {

		/*
		 * if (page != null && limit != null) { PageRequest pageRequest = new
		 * PageRequest(page, limit); return
		 * mongoTemplate.find(Query.query(criteria).with(pageRequest),
		 * TripEndMessage.class); }
		 */

		return mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC, "timestamp"))),
				TripEndMessage.class);
	}

	public Collection<VehicleDataMessage> searchVehicleData(Criteria criteria, Integer page, Integer limit) {

		/*
		 * if (page != null && limit != null) { PageRequest pageRequest = new
		 * PageRequest(page, limit); return
		 * mongoTemplate.find(Query.query(criteria).with(pageRequest),
		 * TripEndMessage.class); }
		 */

		return mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC, "timestamp"))),
				VehicleDataMessage.class);
	}

	public int searchTripStartCount(Criteria criteria, Integer page, Integer limit) {
		Collection<TripStartMessage> collection;

		collection = mongoTemplate.find(Query.query(criteria).with(new Sort(new Order(Direction.DESC, "timestamp"))),
				TripStartMessage.class);
		return collection.size();
	}

	public String getDistinctDeviceId(String tenantId, String vehicle) {
		// TODO Auto-generated method stub
		return null;
	}

}
