package com.j10d207.tripeer.noti.db;

import com.j10d207.tripeer.user.db.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;

@Entity(name = "notification")
public class Notification {

    @GeneratedValue
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String token;

    @Builder.Default
    private Mark checked = Mark.UNCHECKED;
    private enum Mark {
        CHECKED,
        UNCHECKED
    }


}
