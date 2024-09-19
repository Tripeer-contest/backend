package com.j10d207.tripeer.user.db.entity;

import com.j10d207.tripeer.plan.db.entity.PlanEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Setter
    private String role;

    @ManyToOne
    @JoinColumn(name = "INVITE_ID")
    private UserEntity invite;

    public static CoworkerEntity createNewEntity(UserEntity userEntity, PlanEntity planEntity) {
        return CoworkerEntity.builder()
                .user(userEntity)
                .plan(planEntity)
                .role("member")
                .invite(userEntity)
                .build();
    }

    public static CoworkerEntity createInviteEntity(long userId, long planId, long inviteId) {
        return CoworkerEntity.builder()
                .user(UserEntity.builder().userId(userId).build())
                .plan(PlanEntity.builder().planId(planId).build())
                .role("pending")
                .invite(UserEntity.builder().userId(inviteId).build())
                .build();
    }

}
