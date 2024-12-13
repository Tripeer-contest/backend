package com.j10d207.tripeer.place.db.entity;

import com.j10d207.tripeer.place.dto.req.SpotAddReq;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Table(name = "spot_info", indexes = {
	@Index(name = "idx_content_type_city", columnList = "contentTypeId, cityId"),
	@Index(name = "idx_content_type_city_town", columnList = "contentTypeId, cityId, townId")
})
@Entity(name = "spot_info")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpotInfoEntity {

	@Id
	@Setter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// PK
	private int spotInfoId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({@JoinColumn(name = "CITY_ID"), @JoinColumn(name = "TOWN_ID")})
	private TownEntity town;
	private int contentTypeId;
	private String title;
	private String addr1;
	private String addr2;
	private String zipcode;
	private String tel;
	private String firstImage;
	private String firstImage2;
	private int readcount;
	private double latitude;
	private double longitude;
	//받은 DB에 있었는데 뭔지 모르겠습니다.
	private String mlevel;
	// 평균 별점을 들고오기위해 spotReviewList를 받는 것은 비효율적인 것 같아서 추가
	// 리뷰 작성및 삭제때 마다 업데이트해주는 식으로 구현해놓음
	@Setter
	private double starSum;
	@Setter
	private Integer starCount;

	// 별점 평균은 소수점 첫째자리까지만
	public double getStarPointAvg() {
		if (starSum == 0) {
			return 0.0; // starSum가 0이면 기본값 반환
		}
		double starPoint = starSum / starCount;
		return Math.round(starPoint * 10) / 10.0;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumns({@JoinColumn(name = "SPOT_INFO_ID")})
	private List<SpotReviewEntity> spotReviewList;

	public List<String> createImageList() {
		return Stream.of(firstImage, firstImage2)
				.filter(image -> image != null && !image.isEmpty())
				.collect(Collectors.toList());
	}

	public static SpotInfoEntity ofReq(SpotAddReq spotAddReq, TownEntity town, String newAddr) {
		return SpotInfoEntity.builder()
			.town(town)
			.contentTypeId(spotAddReq.getContentTypeId())
			.title(spotAddReq.getTitle())
			.addr1(newAddr)
				//default image URL
			.firstImage("https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/default1.png")
			.latitude(spotAddReq.getLatitude())
			.longitude(spotAddReq.getLongitude())
			.build();
	}

}
