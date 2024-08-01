package com.j10d207.tripeer.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.j10d207.tripeer.common.S3Component;
import com.j10d207.tripeer.user.db.vo.InfoVO;
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
    private final S3Component s3Component;

    //회원 가입
    @Override
    public String memberSignup(JoinVO join, HttpServletResponse response) {

        UserEntity user = userRepository.save(UserEntity.JoinVOToEntity(join));

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

        ObjectMetadata metadata = s3Component.MakeMetaData(file, allowedMimeTypes);
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

        s3Component.FileUpload(file, changedName, metadata);

        user.setProfileImage(amazonS3.getUrl(bucketName, changedName).toString());
        userRepository.save(user);
        return "https://tripeer207.s3.ap-northeast-2.amazonaws.com/" + changedName;
    }

    //내 정보 수정
    @Override
    public void modifyMyInfo(long userId, InfoVO infoVO) {
        UserEntity user = userRepository.findByUserId(userId);
        if(!user.getNickname().equals(infoVO.getNickname()) && userRepository.existsByNickname(infoVO.getNickname())){
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }
        userRepository.save(UserEntity.InfoVOToEntity(infoVO, user));
    }

    //소셜정보 불러오기
    @Override
    public UserDTO.Social getSocialInfo() {
        return UserDTO.Social.ContextToDTO();
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
        return userEntityList.stream().map(UserDTO.Search::UserEntityToDTO).toList();
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
        jwtUtil.isExpired(refresh);
        // 기존 access 토크의 만료 확인 과정 제거
        // refresh 미만료 + access 만료 일때만 재발급을 해줬는데 access 만료를 궂이 확인할 필요성이 없다고 판단.
        String newAccess = jwtUtil.createJwt("Authorization", jwtUtil.getName(refresh), jwtUtil.getRole(refresh), jwtUtil.getUserId(refresh), accessTime);
        response.setHeader("Authorization", "Bearer " + newAccess);
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
