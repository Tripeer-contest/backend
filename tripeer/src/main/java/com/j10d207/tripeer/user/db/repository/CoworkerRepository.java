package com.j10d207.tripeer.user.db.repository;


import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoworkerRepository  extends JpaRepository<CoworkerEntity, Long> {

    Boolean existsByPlan_PlanIdAndUser_UserId(long planId, long userId);
    Boolean existsByPlan_PlanIdAndUser_UserIdAndRole(long planId, long userId, String role);
    List<CoworkerEntity> findByUser_UserId(long userId);
    List<CoworkerEntity> findByPlan_PlanIdAndRole(long planId, String role);
    Optional<CoworkerEntity> findByPlan_PlanIdAndUser_UserId(long planId, long userId);

    List<CoworkerEntity> findByUser_UserIdAndRole(long userId, String role);

    @Query("SELECT u FROM coworker c JOIN user u ON u.userId = c.user.userId WHERE c.plan.planId = :planId AND c.role = :role")
    List<UserEntity> findUserByPlanIdAndRole(@Param("planId") long planId, @Param("role") String role);


    List<CoworkerEntity> findByUser_UserIdAndPlan_EndDateAfter(long user_userId, LocalDate startDate);
    List<CoworkerEntity> findByUser_UserIdAndPlan_EndDateAfterAndRole(long user_userId, LocalDate startDate, String role);

    @Query("SELECT c FROM coworker c " +
        "JOIN c.plan p " +
        "WHERE p.endDate < CURRENT_DATE AND p.isSaved = false")
    List<CoworkerEntity> findExpiredAndUnsavedCoworkers();
}
