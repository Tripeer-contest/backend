package com.j10d207.tripeer.user.db.dto;

import com.j10d207.tripeer.user.db.entity.CoworkerEntity;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class UserDTO {

    @Getter
    @Builder
    public static class Info {
        private long userId;
        private String email;
        private String nickname;
        private LocalDate birth;
        private String profileImage;
        private String style1;
        private int style1Num;
        private String style2;
        private int style2Num;
        private String style3;
        private int style3Num;

        public static Info EntityToDTO(UserEntity userEntity) {
            return UserDTO.Info.builder()
                    .userId(userEntity.getUserId())
                    .nickname(userEntity.getNickname())
                    .email(userEntity.getEmail())
                    .birth(userEntity.getBirth())
                    .profileImage(userEntity.getProfileImage())
                    .style1(userEntity.getStyle1())
                    .style2(userEntity.getStyle2())
                    .style3(userEntity.getStyle3())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Search {
        private long userId;
        private String nickname;
        private String profileImage;

        public static Search UserEntityToDTO(UserEntity userEntity) {
            return Search.builder()
                    .userId(userEntity.getUserId())
                    .nickname(userEntity.getNickname())
                    .profileImage(userEntity.getProfileImage())
                    .build();
        }

        public static Search CoworkerEntityToDTO(CoworkerEntity coworkerEntity) {
                 return Search.builder()
                        .userId(coworkerEntity.getUser().getUserId())
                        .nickname(coworkerEntity.getUser().getNickname())
                        .profileImage(coworkerEntity.getUser().getProfileImage())
                        .build();
        }
    }

    @Getter
    @Builder
    public static class Social {
        private String nickname;
        private String birth;
        private String profileImage;
    }
}
