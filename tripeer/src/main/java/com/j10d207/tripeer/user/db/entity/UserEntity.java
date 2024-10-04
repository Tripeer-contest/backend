package com.j10d207.tripeer.user.db.entity;


import com.j10d207.tripeer.user.db.TripStyleEnum;
import com.j10d207.tripeer.user.dto.req.CustomJoinReq;
import com.j10d207.tripeer.user.dto.req.NotiReq;
import com.j10d207.tripeer.user.dto.res.CustomOAuth2User;
import com.j10d207.tripeer.user.dto.req.InfoReq;
import com.j10d207.tripeer.user.dto.req.JoinReq;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Setter
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
    private boolean allowNotifications;

    public static UserEntity fromJoinReq(JoinReq join) {

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
                .style1(join.getStyle1() == null ? null : TripStyleEnum.getNameOfCode(join.getStyle1()))
                .style2(join.getStyle2() == null ? null : TripStyleEnum.getNameOfCode(join.getStyle2()))
                .style3(join.getStyle3() == null ? null : TripStyleEnum.getNameOfCode(join.getStyle3()))
                .isOnline(false)
                .allowNotifications(true)
                .build();
    }

    public void setModifyInfo(InfoReq infoReq) {
        this.nickname = infoReq.getNickname();
        this.style1 = infoReq.getStyle1Num() == 0 ? null : TripStyleEnum.getNameOfCode(infoReq.getStyle1Num());
        this.style2 = infoReq.getStyle2Num() == 0 ? null : TripStyleEnum.getNameOfCode(infoReq.getStyle2Num());
        this.style3 = infoReq.getStyle3Num() == 0 ? null : TripStyleEnum.getNameOfCode(infoReq.getStyle3Num());
    }

    public void setAllowNotifications(NotiReq notiReq) {
        this.allowNotifications = notiReq.isAllowNotifications();
    }

    public static UserEntity from(CustomJoinReq customJoinReq, String encodedPassword) {
        return UserEntity.builder()
            .provider("tripeer")
            .providerId(encodedPassword)
            .email(customJoinReq.getEmail())
            .nickname(customJoinReq.getNickname())
            .birth(LocalDate.parse(customJoinReq.getYear() + "-" + customJoinReq.getMonth() + "-" + customJoinReq.getDay()))
            .profileImage("https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/profileImg.png")
            .role("ROLE_USER")
            .style1(customJoinReq.getStyle1() == null ? null : TripStyleEnum.getNameOfCode(customJoinReq.getStyle1()))
            .style2(customJoinReq.getStyle2() == null ? null : TripStyleEnum.getNameOfCode(customJoinReq.getStyle2()))
            .style3(customJoinReq.getStyle3() == null ? null : TripStyleEnum.getNameOfCode(customJoinReq.getStyle3()))
            .isOnline(false)
            .allowNotifications(true)
            .build();
    }
}
