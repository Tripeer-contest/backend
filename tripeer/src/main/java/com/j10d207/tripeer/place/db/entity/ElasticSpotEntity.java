package com.j10d207.tripeer.place.db.entity;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(indexName = "elastic_spot")
public class ElasticSpotEntity {
	@Id
	private Integer id;

	private String keyword;

	private Integer contentTypeId;

	private GeoPoint location;
}
