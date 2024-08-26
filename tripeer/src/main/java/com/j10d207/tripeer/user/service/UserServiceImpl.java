package com.j10d207.tripeer.user.service;

import com.j10d207.tripeer.place.db.repository.SpotReviewRepository;
import com.j10d207.tripeer.s3.dto.S3Option;
import com.j10d207.tripeer.s3.dto.FileInfoDto;
import com.j10d207.tripeer.s3.service.S3Service;
import com.j10d207.tripeer.user.db.entity.WishListEntity;
import com.j10d207.tripeer.user.db.repository.WishListRepository;
import com.j10d207.tripeer.user.dto.req.InfoReq;
import com.j10d207.tripeer.user.dto.req.JoinReq;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.config.JWTUtil;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import com.j10d207.tripeer.user.dto.req.WishlistReq;
import com.j10d207.tripeer.user.dto.res.JWTDto;
import com.j10d207.tripeer.user.dto.res.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름
    @Value("${spring.jwt.access}")
    private long accessTime;
    @Value("${spring.jwt.refresh}")
    private long refreshTime;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final WishListRepository wishListRepository;
    private final S3Service s3Service;
    private final SpotReviewRepository spotReviewRepository;

    //회원 가입
    @Override
    public String memberSignup(JoinReq join, HttpServletResponse response) {

        //회원가입 입력 정보를 변환하여 회원 목록에 바로 저장 0802
        UserEntity user = userRepository.save(UserEntity.fromJoinReq(join));

        //회원 가입 후 즉시 로그인을 위한 토큰 발급
        String access = jwtUtil.createJWT(new JWTDto("Authorization", join.getNickname(), "ROLE_USER", user.getUserId()), accessTime);
        String refresh = jwtUtil.createJWT(new JWTDto("Authorization-re", join.getNickname(), "ROLE_USER", user.getUserId()), refreshTime);

        //access 토큰 헤더에 넣기
        response.addCookie(createCookie("Authorization", access));
        response.addCookie(createCookie("Authorization-re", refresh));
        return access;
    }

    //프로필 사진 변경
    @Override
    public String uploadProfileImage(MultipartFile file, long userId){
        UserEntity user = userRepository.findByUserId(userId);

        String userPreviousUrl = user.getProfileImage();
        String uploadURL;
        FileInfoDto fileInfoDto = FileInfoDto.ofProfileImage(file, userId, S3Option.profileUpload);
        if (userPreviousUrl.contains("tripeer207.s3")) {
            uploadURL = s3Service.changeFile(userPreviousUrl, fileInfoDto);
        } else {
            uploadURL = s3Service.fileUpload(fileInfoDto);
        }
        user.setProfileImage(uploadURL);
        userRepository.save(user);
        return uploadURL;
    }

    //내 정보 수정
    @Override
    public void modifyMyInfo(long userId, InfoReq infoReq) {
        UserEntity user = userRepository.findByUserId(userId);
        //닉네임 중복체크 2중검증
        if(!user.getNickname().equals(infoReq.getNickname()) && userRepository.existsByNickname(infoReq.getNickname())){
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }
        // 변경 사항 반영 부분 Entity 메소드 화 0802
        userRepository.save(UserEntity.ofInfoReqUserEntity(infoReq, user));
    }

    //소셜정보 불러오기
    @Override
    public UserDTO.Social getSocialInfo() {
        // 회원 가입 창에서 일부 정보를 바로 불러오기 위해 사용, 코드 압축 가능성 있으나 프론트와 협업 필요하다 판단하여 간단하게 유지 0802
        return UserDTO.Social.getContext();
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
        //유저 검색 리스트 DTO 메소드로 변경 0802
        return userEntityList.stream().map(UserDTO.Search::fromUserEntity).toList();
    }

    //내 정보 불러오기 0802
    @Override
    public UserDTO.Info getMyInfo(long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        return UserDTO.Info.fromUserEntity(user);
    }

    //마이 페이지에서 찜목록 불러오기
    @Override
    public List<UserDTO.Wishlist> getMyWishlist(long userId) {
        List<WishListEntity> wishListEntityList = wishListRepository.findByUser_UserId(userId);
        List<UserDTO.Wishlist> wishlistList = wishListEntityList.stream().map(UserDTO.Wishlist::ofEntity).toList();
        for ( UserDTO.Wishlist wishlist : wishlistList) {
            Optional<Double> starPoint = spotReviewRepository.findAverageStarPointBySpotInfoId(wishlist.getSpotInfoId());
            if(starPoint.isPresent()) {
                wishlist.setStarPointAvg(Math.round(starPoint.get()*10)/10.0);
            } else {
                wishlist.setStarPointAvg(0);
            }
        }
        return wishlistList;
    }

    //찜목록 추가 or 삭제
    public void addWishList(WishlistReq wishlistReq, long userId) {
        Optional<WishListEntity> optionalWishList = wishListRepository.findBySpotInfo_SpotInfoIdAndUser_UserId(wishlistReq.getSpotInfoId(), userId);
        if (optionalWishList.isPresent() && wishlistReq.isLike() ) {
            wishListRepository.delete(optionalWishList.get());
        } else if (optionalWishList.isPresent()) {
            throw new CustomException(ErrorCode.HAS_WISHLIST);

        } else if (wishlistReq.isLike()) {
            throw new CustomException(ErrorCode.NONE_WISHLIST);
        } else {
            wishListRepository.save(WishListEntity.CreateWishListEntity(wishlistReq.getSpotInfoId(), userId));
        }
    }

    // access 토큰 재발급
    @Override
    public void tokenRefresh(String token, Cookie[] cookies, HttpServletResponse response) {
        // refresh 토큰 가져오기
        String refresh = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("AuthorizationRe")) {
                refresh = cookie.getValue();
            }
        }
        // refresh 만료 확인 만료확인의 try/catch를 isExpired 메소드 안으로 넣어버림 0802
        jwtUtil.getPayload(refresh).getExpiration().before(new Date());
        // 기존 access 토크의 만료 확인 과정 제거
        // refresh 미만료 + access 만료 일때만 재발급을 해줬는데 access 만료를 궂이 확인할 필요성이 없다고 판단.0802
        String newAccess = jwtUtil.createJWT(new JWTDto("Authorization",
                        jwtUtil.getPayload(refresh).get("name", String.class),
                        jwtUtil.getPayload(refresh).get("role", String.class),
                        jwtUtil.getPayload(refresh).get("userId", Long.class)),
                accessTime);
        response.addCookie(createCookie("Authorization", newAccess));
    }


    /*
    아래 메소드 들은 개발자용 편의성 메소드
    아래 메소드 들은 개발자용 편의성 메소드
    아래 메소드 들은 개발자용 편의성 메소드
    아래 메소드 들은 개발자용 편의성 메소드
    아래 메소드 들은 개발자용 편의성 메소드
    아래 메소드 들은 개발자용 편의성 메소드
    아래 메소드 들은 개발자용 편의성 메소드
     */

    @Override
    public String getSuper(HttpServletResponse response, long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        String result = jwtUtil.createJWT(new JWTDto("Authorization", user.getNickname(), user.getRole(), userId), (long) 60*60*24*1000);
        String refresh = jwtUtil.createJWT(new JWTDto("Authorization-re", user.getNickname(), user.getRole(), userId), refreshTime);

        response.addCookie(createCookie("AuthorizationRe", refresh));
        response.setHeader("Authorization", "Bearer " + result);

        return "Bearer " + result;
    }

    @Override
    public String getSuper2(HttpServletResponse response, long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        String result = jwtUtil.createJWT(new JWTDto("Authorization", user.getNickname(), user.getRole(), userId), (long) 90*1000);
        String refresh = jwtUtil.createJWT(new JWTDto("Authorization-re", user.getNickname(), user.getRole(), userId), (long) 180*1000);

        response.addCookie(createCookie("AuthorizationRe", refresh));
        response.setHeader("Authorization", "Bearer " + result);

        return "Bearer " + result;
    }


    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(true);
        cookie.setPath("/");
        if(key.equals("AuthorizationRe")) {
            cookie.setHttpOnly(true);
        }

        return cookie;
    }
}
