package com.j10d207.tripeer.user.service;

import com.j10d207.tripeer.place.db.repository.SpotReviewRepository;
import com.j10d207.tripeer.s3.dto.S3Option;
import com.j10d207.tripeer.s3.dto.FileInfoDto;
import com.j10d207.tripeer.s3.service.S3Service;
import com.j10d207.tripeer.user.db.entity.WishListEntity;
import com.j10d207.tripeer.user.db.repository.WishListRepository;
import com.j10d207.tripeer.user.dto.req.CustomJoinReq;
import com.j10d207.tripeer.user.dto.req.CustomLoginReq;
import com.j10d207.tripeer.user.dto.req.InfoReq;
import com.j10d207.tripeer.user.dto.req.JoinReq;
import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.user.config.JWTUtil;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import com.j10d207.tripeer.user.dto.req.NotiReq;
import com.j10d207.tripeer.user.dto.req.PasswordChangeReq;
import com.j10d207.tripeer.user.dto.req.WishlistReq;
import com.j10d207.tripeer.user.dto.res.JWTDto;
import com.j10d207.tripeer.user.dto.res.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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
    private final JavaMailSender javaMailSender;
    private final CacheManager cacheManager;
    private final PasswordEncoder passwordEncoder;
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

    @Override
    public String customSignup(CustomJoinReq join, HttpServletResponse response) {
        // 코드 검증(이메일 코드가 일치할때만 회원가입 허용)
        Cache cache = cacheManager.getCache("emailCodes");
        String cachedCode = cache.get(join.getEmail(), String.class);
        if (userRepository.existsByEmail(join.getEmail())) throw new CustomException(ErrorCode.DUPLICATE_USER);
        if (!join.getCode().equals(cachedCode)) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }
        //회원가입 입력 정보를 변환하여 회원 목록에 바로 저장 0802
        String encodedPassword = passwordEncoder.encode(join.getPassword());
        UserEntity user = userRepository.save(UserEntity.from(join, encodedPassword));

        //회원 가입 후 즉시 로그인을 위한 토큰 발급
        String access = jwtUtil.createJWT(new JWTDto("Authorization", join.getNickname(), "ROLE_USER", user.getUserId()), accessTime);
        String refresh = jwtUtil.createJWT(new JWTDto("Authorization-re", join.getNickname(), "ROLE_USER", user.getUserId()), refreshTime);
        //access 토큰 헤더에 넣기
        response.addCookie(createCookie("Authorization", access));
        response.addCookie(createCookie("Authorization-re", refresh));
        return access;
    }

    @Override
    public String customLogin(CustomLoginReq loginReq, HttpServletResponse response) {
        // 이메일 해당 유저가 있나 검사
        UserEntity user = userRepository.findByEmail(loginReq.getEmail()).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 비밀번호가 일치하는지 겁사
        if (!passwordEncoder.matches(loginReq.getPassword(), user.getProviderId())) throw new CustomException(ErrorCode.INVALID_PASSWORD);
        // 일치하면 즉시 로그인을 위한 토큰 발급
        String access = jwtUtil.createJWT(new JWTDto("Authorization", user.getNickname(), "ROLE_USER", user.getUserId()), accessTime);
        String refresh = jwtUtil.createJWT(new JWTDto("Authorization-re", user.getNickname(), "ROLE_USER", user.getUserId()), refreshTime);
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
        user.setModifyInfo(infoReq);
        userRepository.save(user);
    }

    //소셜정보 불러오기
    @Override
    public UserDTO.Social getSocialInfo() {
        // 회원 가입 창에서 일부 정보를 바로 불러오기 위해 사용, 코드 압축 가능성 있으나 프론트와 협업 필요하다 판단하여 간단하게 유지 0802
        return UserDTO.Social.getContext();
    }

    //비밀번호 변경
    @Override
    public void changePassword(PasswordChangeReq passwordChangeReq) {
        UserEntity user = userRepository.findByEmail(passwordChangeReq.getEmail()).orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!(user.getProvider().equals("tripeer"))) throw new CustomException(ErrorCode.NOT_TRIPPER_USER);
        Cache cache = cacheManager.getCache("passwordCodes");
        String cachedCode = cache.get(passwordChangeReq.getEmail(), String.class);
        if (!passwordChangeReq.getCode().equals(cachedCode)) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }
        if (!passwordChangeReq.getConfirmPassword().equals(passwordChangeReq.getPassword())) throw new CustomException(ErrorCode.INVALID_PASSWORD);
        String encodedPassword = passwordEncoder.encode(passwordChangeReq.getPassword());
        user.setProviderId(encodedPassword);
        userRepository.save(user);
    }

    //인증 이메일 발송
    @Override
    public boolean sendValidEmail(String email) {
        if (userRepository.existsByEmail(email)) return false;
        String code = String.format("%06d", new Random().nextInt(999999));
        Cache cache = cacheManager.getCache("emailCodes");
        cache.put(email, code);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(email);
            messageHelper.setSubject("[Tripeer] " + "인증코드 안내");
            messageHelper.setFrom("tripeer@gmail.com");
            String content = buildEmailContent(code);
            messageHelper.setText(content, true); // true는 HTML 메일을 보내겠다는 의미.
        };
        try {
            javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            // 이메일 발송 실패
            return false;
        }
        return true;
    }

    //인증 이메일 발송
    @Override
    public boolean sendValidPassword(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!(user.getProvider().equals("tripeer"))) throw new CustomException(ErrorCode.NOT_TRIPPER_USER);
        String code = String.format("%06d", new Random().nextInt(999999));
        Cache cache = cacheManager.getCache("passwordCodes");
        cache.put(email, code);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(email);
            messageHelper.setSubject("[Tripeer] " + "인증코드 안내");
            messageHelper.setFrom("tripeer@gmail.com");
            String content = buildEmailContent(code);
            messageHelper.setText(content, true); // true는 HTML 메일을 보내겠다는 의미.
        };
        try {
            javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            // 이메일 발송 실패
            return false;
        }
        return true;
    }

    // 인증 이메일 검증
    public boolean emailVerification(String email, String code) {
        Cache cache = cacheManager.getCache("emailCodes");
        if (cache != null) {
            String cachedCode = cache.get(email, String.class);
			return code.equals(cachedCode);
        }
        return false;
    }

    // 인증 이메일 검증 (for 비밀번호 변경)
    public boolean passwordVerification(String email, String code) {
        Cache cache = cacheManager.getCache("passwordCodes");
        if (cache != null) {
            String cachedCode = cache.get(email, String.class);
            return code.equals(cachedCode);
        }
        return false;
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

    // 알람 설정 변경
    @Override
    public String changeNoti(long userId, NotiReq notiReq) {
        UserEntity user = userRepository.findByUserId(userId);
        user.setAllowNotifications(notiReq);
        userRepository.save(user);
        return "allowNotifications 변경 완료";
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

    // 해당 하는 유저의 notification allow 상태 확인
    public Boolean getAllowNotificationById(final Long userId) {
        final Optional<UserEntity> user = userRepository.findById(userId);
		return user.map(UserEntity::isAllowNotifications).orElse(false);
	}

    private String buildEmailContent(String messageContent) {
        return "<html>" +
            "<body style='margin: 0; padding: 0; text-align: center; background-color: #f2f2f2;'>" +
            "<div style='padding-top: 40px;'>" +  // 이미지 위의 패딩만 유지
            "<img src='https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/diaryBanner.png' alt='Tripper Welcome Image' style='width: 100%; height: auto; display: block; margin: 0 auto;'>" +  // 중앙 정렬
            "<div style='margin: 0 auto; background: white; border-top: 5px solid #4FBDB7; border-bottom: 5px solid #04ACB5; padding: 40px 20px; font-family: Arial, sans-serif; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05); min-height: 330px; text-align: center;'>" +  // 기본 높이와 패딩 조정, 텍스트 중앙 정렬 추가
            "<img src='https://tripeer207.s3.ap-northeast-2.amazonaws.com/front/static/title.png' alt='Tripper logo Image' style='max-width: 300px; width: 100%; height: auto; display: block; margin: 0 auto;'>" +
            "<h2 style='color: #04ACB5; margin-top: 50px;'>안녕하세요! Tripper입니다.</h2>" +
            "<h2 style='color: #04ACB5; margin-top: 50px;'>인증코드를 확인해주세요.</h2>" +
            "<p style='font-size: 48px; line-height: 1.5; color: #333333;'>" + messageContent + "</p>" +
            "</div>" +
            "</body>" +
            "</html>";
    }

}
