package com.j10d207.tripeer.user.db.entity;


import com.j10d207.tripeer.user.db.TripStyleEnum;
import com.j10d207.tripeer.user.db.dto.CustomOAuth2User;
import com.j10d207.tripeer.user.db.vo.InfoVO;
import com.j10d207.tripeer.user.db.vo.JoinVO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;

@Entity(name = "user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK
    private long userId;
    private String provider;
    private String providerId;
    private String email;
    private String nickname;
    private LocalDate birth;
    @Setter
    private String profileImage;
    private String role;
    private String style1;
    private String style2;
    private String style3;
    private boolean isOnline;

    public static UserEntity JoinVOToEntity(JoinVO join) {

        //소셜정보 가져오기
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String newImg;
        if (customOAuth2User.getProfileImage() != null) {
            String[] splitImg = customOAuth2User.getProfileImage().split(":");
            if(splitImg[0].equals("http")) {
                newImg = splitImg[0] + "s" + ":" + splitImg[1];
            } else {
                newImg = customOAuth2User.getProfileImage();
            }
        } else {
            newImg = "https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/profileImg.png";
        }

        return UserEntity.builder()
                .provider(customOAuth2User.getProvider())
                .providerId(customOAuth2User.getProviderId())
                .email(customOAuth2User.getEmail())
                .nickname(join.getNickname())
                .birth(LocalDate.parse(join.getYear() + "-" + join.getMonth() + "-" + join.getDay()))
                .profileImage(newImg)
                .role("ROLE_USER")
                .style1(join.getStyle1() == null ? null : TripStyleEnum.getNameByCode(join.getStyle1()))
                .style2(join.getStyle2() == null ? null : TripStyleEnum.getNameByCode(join.getStyle2()))
                .style3(join.getStyle3() == null ? null : TripStyleEnum.getNameByCode(join.getStyle3()))
                .isOnline(false)
                .build();
    }

    public static UserEntity InfoVOToEntity(InfoVO infoVO, UserEntity userEntity) {

        return UserEntity.builder()
                .userId(userEntity.getUserId())
                .provider(userEntity.getProvider())
                .providerId(userEntity.getProviderId())
                .email(infoVO.getEmail())
                .nickname(infoVO.getNickname())
                .birth(userEntity.getBirth())
                .profileImage(userEntity.getProfileImage())
                .role(userEntity.getRole())
                .style1(TripStyleEnum.getNameByCode(infoVO.getStyle1Num()))
                .style2(TripStyleEnum.getNameByCode(infoVO.getStyle2Num()))
                .style3(TripStyleEnum.getNameByCode(infoVO.getStyle3Num()))
                .isOnline(userEntity.isOnline())
                .build();
    }

}
