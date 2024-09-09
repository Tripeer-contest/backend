package com.j10d207.tripeer.place.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.j10d207.tripeer.place.db.entity.SpotCollectionEntity;

public interface SpotCollectionRepository extends MongoRepository<SpotCollectionEntity, String> {
	SpotCollectionEntity findBySpotInfoId(Integer spotInfoId);
}
