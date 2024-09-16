package com.j10d207.tripeer.place.db.entity.additional;

import java.util.ArrayList;
import java.util.List;


import com.j10d207.tripeer.place.dto.res.AdditionalDto;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AdditionalBaseEntity {

	// Getters and Setters
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int spotInfoId;

	private int contentTypeId;

	public List<AdditionalDto> toDTO() {
		return new ArrayList<>();
	};
}
