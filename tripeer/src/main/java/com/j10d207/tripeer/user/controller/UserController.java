package com.j10d207.tripeer.user.controller;

import com.j10d207.tripeer.exception.CustomException;
import com.j10d207.tripeer.exception.ErrorCode;
import com.j10d207.tripeer.response.Response;
import com.j10d207.tripeer.user.db.dto.*;
import com.j10d207.tripeer.user.db.vo.InfoVO;
import com.j10d207.tripeer.user.db.vo.JoinVO;
import com.j10d207.tripeer.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public Response<String> memberSignup(@RequestBody JoinVO join, HttpServletResponse response) {
        String jwt = userService.memberSignup(join, response);
        return Response.of(HttpStatus.OK, "회원가입, 토큰발급 완료", jwt);
    }

    //소셜정보 불러오기
    @GetMapping("/social/info")
    public Response<UserDTO.Social> socialInfo() {
        UserDTO.Social social = userService.getSocialInfo();
        return Response.of(HttpStatus.OK, "OAuth 제공 정보", social);
    }

    //닉네임 중복체크
    @GetMapping("/name/duplicatecheck/{nickname}")
    public Response<Boolean> nameDuplicateCheck(@PathVariable("nickname") String nickname) {
            return Response.of(HttpStatus.OK, "닉네임 중복체크", userService.nicknameDuplicateCheck(nickname));
    }

    //유저 검색
    @GetMapping("/search/{nickname}")
    public Response<List<UserDTO.Search>> memberSearch(@PathVariable("nickname") String nickname) {
        return Response.of(HttpStatus.OK, "유저 검색", userService.userSearch(nickname));
    }

    //내 정보 불러오기
    @GetMapping("/myinfo")
    public Response<UserDTO.Info> myInfo(@AuthenticationPrincipal CustomOAuth2User user) {
        return Response.of(HttpStatus.OK, "내 정보 불러오기 완료", userService.getMyInfo(user.getUserId()));
    }

    //내 정보 수정
    @PatchMapping("/myinfo")
    public Response<?> myInfoModify(@AuthenticationPrincipal CustomOAuth2User user, @RequestBody InfoVO infoVO) {
        userService.modifyMyInfo(user.getUserId(), infoVO);
        return Response.of(HttpStatus.OK, "내 정보 수정 완료", null);
    }

    //내 프로필 수정
    @PatchMapping("/myinfo/profileimage")
    public Response<?> myInfoModify(@AuthenticationPrincipal CustomOAuth2User user, @RequestPart(value = "image") MultipartFile multipartFile) {
        String imageUrl =  userService.uploadProfileImage(multipartFile, user.getUserId());
        return Response.of(HttpStatus.OK, "내 프로필 수정 완료", Map.of("imageUrl", imageUrl));
    }


    // access 토큰 재발급
    @PostMapping("/reissue")
    public Response<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        userService.tokenRefresh(request.getHeader("AuthorizationOff"), request.getCookies(), response);
        return Response.of(HttpStatus.OK, "토큰 재발급 완료", null);
    }

    @GetMapping("/test/getsuper/{userId}")
    public Response<String> getSuper(HttpServletResponse response, @PathVariable("userId") long userId) {
        String result = userService.getSuper(response, userId);
        return Response.of(HttpStatus.OK, "getSuper", result);
    }

    @GetMapping("/test/getsuper2/{userId}")
    public Response<String> getSuper2(HttpServletResponse response, @PathVariable("userId") long userId) {
        String result = userService.getSuper2(response, userId);
        return Response.of(HttpStatus.OK, "getSuper", result);
    }


    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    @GetMapping("/authtest")
    public String test2(@AuthenticationPrincipal CustomOAuth2User principal) {
        SecurityContext test = SecurityContextHolder.getContext();
        Authentication principal1 = test.getAuthentication();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal1.getPrincipal();

        System.out.println(customOAuth2User.getUserId());
        System.out.println("principal.getUserId() = " + principal.getUserId());
        return "hi";
    }

    @GetMapping("/error")
    public String testError() {
        throw new CustomException(ErrorCode.REQUEST_AUTHORIZATION);
    }

}
