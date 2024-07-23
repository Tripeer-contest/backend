package com.j10d207.tripeer.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.j10d207.tripeer.user.db.vo.JoinVO;
import io.jsonwebtoken.ExpiredJwtException;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.config.JWTUtil;
import com.j10d207.tripeer.user.db.TripStyleEnum;
import com.j10d207.tripeer.user.db.dto.*;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름
    @Value("${spring.jwt.access}")
    private long accessTime;
    @Value("${spring.jwt.refresh}")
    private long refreshTime;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    //회원 가입
    @Override
    public String memberSignup(JoinVO join, HttpServletResponse response) {
        //생일 값 형식변환
        LocalDate birth = LocalDate.parse(join.getYear() + "-" + join.getMonth() + "-" + join.getDay());


        //소셜정보 가져오기
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String newImg;
        //프로필 사진 경로 필터
        if (customUserDetails.getProfileImage() != null) {
            String[] splitImg = customUserDetails.getProfileImage().split(":");
            if(splitImg[0].equals("http")) {
                newImg = splitImg[0] + "s" + ":" + splitImg[1];
            } else {
                newImg = customUserDetails.getProfileImage();
            }
        } else {
            newImg = "https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/profileImg.png";
        }

        UserEntity user = UserEntity.builder()
                .provider(customUserDetails.getProvider())
                .providerId(customUserDetails.getProviderId())
                .email(customUserDetails.getEmail())
                .nickname(join.getNickname())
                .birth(birth)
                .profileImage(newImg)
                .role("ROLE_USER")
                .style1(join.getStyle1() == null ? null : TripStyleEnum.getNameByCode(join.getStyle1()))
                .style2(join.getStyle2() == null ? null : TripStyleEnum.getNameByCode(join.getStyle2()))
                .style3(join.getStyle3() == null ? null : TripStyleEnum.getNameByCode(join.getStyle3()))
                .isOnline(false)
                .build();
        user = userRepository.save(user);

        String access = "Bearer " + jwtUtil.createJwt("Authorization", join.getNickname(), "ROLE_USER", user.getUserId(), accessTime);
        String refresh = jwtUtil.createJwt("Authorization-re", join.getNickname(), "ROLE_USER", user.getUserId(), refreshTime);

        //access 토큰 헤더에 넣기
        response.setHeader("Authorization", access);
        response.addCookie(createCookie("Authorization-re", refresh));
        return access;
    }

    //프로필 사진 변경
    @Override
    public String uploadProfileImage(MultipartFile file, long userId){

        // 허용할 MIME 타입들 설정 (이미지만 허용하는 경우)
        List<String> allowedMimeTypes = List.of("image/jpg", "image/jpeg", "image/png");

        // 허용되지 않는 MIME 타입의 파일은 처리하지 않음
        String fileContentType = file.getContentType();
        if (!allowedMimeTypes.contains(fileContentType)) {
            throw new CustomException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        ObjectMetadata metadata = new ObjectMetadata(); //메타데이터

        metadata.setContentLength(file.getSize()); // 파일 크기 명시
        metadata.setContentType(fileContentType);   // 파일 확장자 명시
        UserEntity user = userRepository.findByUserId(userId);


        String originName = file.getOriginalFilename(); //원본 이미지 이름
        String ext = originName.substring(originName.lastIndexOf(".")); //확장자
        String changedName = "ProfileImage/" + userId + "/" + UUID.randomUUID().toString() + ext;

        String userPreviousUrl = user.getProfileImage();
        if (userPreviousUrl.contains("tripeer207.s3")) {
            String splitStr = ".com/";
            String fileName = userPreviousUrl.substring(userPreviousUrl.lastIndexOf(splitStr) + splitStr.length());
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        }

        try {
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucketName, changedName, file.getInputStream(), metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            log.error("file upload error " + e.getMessage());
            throw new CustomException(ErrorCode.S3_UPLOAD_ERROR);
        }

        user.setProfileImage(amazonS3.getUrl(bucketName, changedName).toString());
        userRepository.save(user);
        String url = "https://tripeer207.s3.ap-northeast-2.amazonaws.com/" + changedName;
        return url;
    }

    //내 정보 수정
    @Override
    public void modifyMyInfo(long userId, UserDTO.Info info) {
        UserEntity user = userRepository.findByUserId(userId);
        if(!user.getNickname().equals(info.getNickname()) && userRepository.existsByNickname(info.getNickname())){
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }
        UserEntity newUser = UserEntity.builder()
                .userId(user.getUserId())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .email(info.getEmail())
                .nickname(info.getNickname())
                .birth(user.getBirth())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .style1(TripStyleEnum.getNameByCode(info.getStyle1Num()))
                .style2(TripStyleEnum.getNameByCode(info.getStyle2Num()))
                .style3(TripStyleEnum.getNameByCode(info.getStyle3Num()))
                .isOnline(user.isOnline())
                .build();
        userRepository.save(newUser);
    }

    //소셜정보 불러오기
    @Override
    public UserDTO.Social getSocialInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        return UserDTO.Social.builder()
                .nickname(customUserDetails.getName())
                .profileImage(customUserDetails.getProfileImage())
                .build();
    }

    //닉네임 중복체크
    @Override
    public boolean nicknameDuplicateCheck(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    //유저 검색
    @Override
    public List<UserDTO.Search> userSearch(String nickname) {
        List<UserEntity> userEntityList = userRepository.findByNicknameContains(nickname);
        List<UserDTO.Search> searchList = new ArrayList<>();
        for(UserEntity user : userEntityList) {
            UserDTO.Search search = UserDTO.Search.UserEntityToDTO(user);
            searchList.add(search);
        }

        return searchList;
    }

    //내 정보 불러오기
    @Override
    public UserDTO.Info getMyInfo(long userId) {
        // 정보 확장시 DTO 새로 만들어야함
        UserEntity user = userRepository.findByUserId(userId);
        return UserDTO.Info.EntityToDTO(user);
    }


    // access 토큰 재발급
    @Override
    public void tokenRefresh(String token, Cookie[] cookies, HttpServletResponse response) {
        // refresh 토큰 가져오기
        String refresh = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization-re")) {
                refresh = cookie.getValue();
            }
        }

        // refresh 만료 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }

        String access = jwtUtil.splitToken(token);
        // access 만료 확인
        try {
            jwtUtil.isExpired(access);
            throw new CustomException(ErrorCode.TOKEN_EXPIRED_ERROR);
        } catch (ExpiredJwtException e) {
            // access 토큰 재발급 후 헤더에 저장
            String newAccess = jwtUtil.createJwt("Authorization", jwtUtil.getName(refresh), jwtUtil.getRole(refresh), jwtUtil.getUserId(refresh), accessTime);
            response.setHeader("Authorization", "Bearer " + newAccess);
        }


    }

    @Override
    public String getSuper(HttpServletResponse response, long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        String result = jwtUtil.createJwt("Authorization", user.getNickname(), user.getRole(), userId, (long) 60*60*24*1000);
        String refresh = jwtUtil.createJwt("Authorization-re", user.getNickname(), user.getRole(), userId, refreshTime);

        response.addCookie(createCookie("Authorization-re", refresh));
        response.setHeader("Authorization", "Bearer " + result);

        return "Bearer " + result;
    }

    @Override
    public String getSuper2(HttpServletResponse response, long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        String result = jwtUtil.createJwt("Authorization", user.getNickname(), user.getRole(), userId, (long) 90*1000);
        String refresh = jwtUtil.createJwt("Authorization-re", user.getNickname(), user.getRole(), userId, (long) 180*1000);

        response.addCookie(createCookie("Authorization-re", refresh));
        response.setHeader("Authorization", "Bearer " + result);

        return "Bearer " + result;
    }


    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(true);
        cookie.setPath("/");
        if(key.equals("Authorization-re")) {
            cookie.setHttpOnly(true);
        }

        return cookie;
    }
}
