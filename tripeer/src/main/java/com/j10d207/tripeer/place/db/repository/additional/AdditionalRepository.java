package com.j10d207.tripeer.place.db.repository.additional;

import java.util.Optional;

public interface AdditionalRepository<T> {
	Optional<T> findBySpotInfoId(int spotInfoId);
}
