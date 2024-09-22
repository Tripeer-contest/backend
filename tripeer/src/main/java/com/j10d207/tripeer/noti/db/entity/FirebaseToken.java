package com.j10d207.tripeer.noti.db.entity;

import com.j10d207.tripeer.user.db.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "firebase_token")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FirebaseToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String token;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Mark checked = Mark.UNCHECKED;

    public static FirebaseToken of(final UserEntity user, final String firebaseToken) {
        return FirebaseToken.builder()
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
