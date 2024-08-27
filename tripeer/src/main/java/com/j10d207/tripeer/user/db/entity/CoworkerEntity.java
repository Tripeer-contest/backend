package com.j10d207.tripeer.user.db.entity;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "coworker")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoworkerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long coworkerId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "PLAN_ID")
    private PlanEntity plan;

    public static CoworkerEntity MakeCoworkerEntity(UserEntity userEntity, PlanEntity planEntity) {
        return CoworkerEntity.builder()
                .user(userEntity)
                .plan(planEntity)
                .build();
    }

}
