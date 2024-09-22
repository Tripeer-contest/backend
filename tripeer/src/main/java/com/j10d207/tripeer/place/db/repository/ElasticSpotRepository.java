package com.j10d207.tripeer.place.db.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.j10d207.tripeer.place.db.entity.ElasticSpotEntity;

@Repository
public interface ElasticSpotRepository extends ElasticsearchRepository<ElasticSpotEntity, String> {

	// 줌레벨 검색
	@Query("{\"bool\": {" +
				"\"must\": {" +
					"\"match\": {" +
						"\"keyword\": {" +
							"\"query\": \"?4\"," +
							"\"operator\": \"and\"" +
						"}" +
					"}" +
				"}," +
				"\"filter\": [" +
					"{\"geo_bounding_box\": {" +
						"\"location\": {" +
							"\"bottom_left\": {\"lat\": ?0, \"lon\": ?2}," +
							"\"top_right\": {\"lat\": ?1, \"lon\": ?3}" +
						"}" +
					"}}," +
					"{\"terms\": {" +
						"\"content_type_id\": ?5" +
					"}}" +
				"]" +
			"}}")
	Page<ElasticSpotEntity> searchSpotsInMap(
		double minLat, double maxLat, double minLon, double maxLon,
		String keyword, List<Integer> contentTypeIds, Pageable pageable);

	// 줌레벨 getAll ( keyword 가 빈문자열일때 지도 안에 모든 관광지 검색 )
	@Query("{\"bool\": {" +
				"\"filter\": [" +
					"{\"geo_bounding_box\": {" +
						"\"location\": {" +
							"\"bottom_left\": {\"lat\": ?0, \"lon\": ?2}," +
							"\"top_right\": {\"lat\": ?1, \"lon\": ?3}" +
						"}" +
					"}}," +
					"{\"terms\": {" +
						"\"content_type_id\": ?4" +
					"}}" +
				"]" +
			"}}")
	Page<ElasticSpotEntity> AllSpotsInMap(
		double minLat, double maxLat, double minLon, double maxLon,
		List<Integer> contentTypeIds, Pageable pageable);

	// 홈화면에서 사용할 검색( 키워드만 가지고 전체 검색 )
	@Query("{\"match\": {\"keyword\": {\"query\": \"?0\", \"operator\": \"and\"}}}")
	Page<ElasticSpotEntity> findByKeywordMatchAll(String keyword, Pageable pageable);

	// @Query("{\"bool\": {" +
	// 			"\"must\": {" +
	// 				"\"match\": {" +
	// 					"\"keyword\": {" +
	// 						"\"query\": \"?0\"," +
	// 						"\"operator\": \"and\"" +
	// 					"}" +
	// 				"}" +
	// 			"}," +
	// 			"\"filter\": [" +
	// 				"{\"terms\": {" +
	// 					"\"content_type_id\": ?1" +
	// 				"}}," +
	// 				"{\"terms\": {" +
	// 					"\"city_id\": ?2" +
	// 				"}}," +
	// 				"{\"terms\": {" +
	// 					"\"town_id\": ?3" +
	// 				"}}" +
	// 			"]" +
	// 		"}}")
	// Page<ElasticSpotEntity> searchSpotsInArea(
	// 	String keyword, List<Integer> contentTypeIds, List<Integer> cityIds, List<Integer> townIds, Pageable pageable);
	//
	// @Query("{\"bool\": {" +
	// 	"\"filter\": [" +
	// 	"{\"terms\": {" +
	// 	"\"content_type_id\": ?0" +
	// 	"}}," +
	// 	"{\"terms\": {" +
	// 	"\"city_id\": ?1" +
	// 	"}}," +
	// 	"{\"terms\": {" +
	// 	"\"town_id\": ?2" +
	// 	"}}" +
	// 	"]" +
	// 	"}}")
	// Page<ElasticSpotEntity> AllSpotsInArea(
	// 	List<Integer> contentTypeIds, List<Integer> cityIds, List<Integer> townIds, Pageable pageable);
	//
	// @Query("{\"bool\": {" +
	// 			"\"must\": {" +
	// 				"\"match\": {" +
	// 					"\"keyword\": {" +
	// 						"\"query\": \"?0\"," +
	// 						"\"operator\": \"and\"" +
	// 					"}" +
	// 				"}" +
	// 			"}," +
	// 			"\"filter\": {" +
	// 				"\"terms\": {" +
	// 				"\"content_type_id\": ?1" +
	// 				"}" +
	// 			"}"+
	// 		"}}")
	// Page<ElasticSpotEntity> searchSpots(String keyword, List<Integer> contentTypeIds, Pageable pageable);


}
