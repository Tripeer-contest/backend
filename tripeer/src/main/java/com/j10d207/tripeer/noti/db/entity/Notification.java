package com.j10d207.tripeer.noti.db.entity;

import com.j10d207.tripeer.user.db.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity(name = "notification")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder

public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String token;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Mark checked = Mark.UNCHECKED;

    public static Notification of(final UserEntity user, final String firebaseToken) {
        return Notification.builder()
            .user(user)
            .token(firebaseToken)
            .build();
    }


    private enum Mark {
        CHECKED,
        UNCHECKED
    }


    public void mark() {
        this.checked = Mark.CHECKED;
    }

    public void unMark() {
        this.checked = Mark.UNCHECKED;
    }


}
