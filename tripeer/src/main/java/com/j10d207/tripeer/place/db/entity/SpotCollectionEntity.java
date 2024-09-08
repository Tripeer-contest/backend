package com.j10d207.tripeer.place.db.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "spot_collection")
public class SpotCollectionEntity {

	@Id
	private String id;

	@Field(name = "spot_info_id")
	private Integer spotInfoId;

	@Field(name = "near_spot_id_list")
	private List<Integer> nearSpotIdList;

	@Field(name = "sim_spot_id_list")
	private List<Integer> simSpotIdList;
}
