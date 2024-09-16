package com.j10d207.tripeer.user.db.repository;

import com.j10d207.tripeer.user.db.entity.WishListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WishListRepository extends JpaRepository<WishListEntity, Long> {

//    Boolean existsBySpotInfo_SpotInfoId(int spotInfoId);
    Boolean existsByUser_UserIdAndSpotInfo_SpotInfoId(long userId, int spotInfoId);

    Optional<WishListEntity> findBySpotInfo_SpotInfoIdAndUser_UserId(int spotInfoId, long userId);
    List<WishListEntity> findByUser_UserId(long userId);


    @Query("SELECT w.spotInfo.spotInfoId FROM wishlist w WHERE w.user.userId = :userId")
    Set<Integer> findAllSpotInfoIdsByUserId(@Param("userId") long userId);
}
