package com.j10d207.tripeer.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j10d207.tripeer.user.db.entity.UserEntity;
import com.j10d207.tripeer.user.db.repository.UserRepository;
import com.j10d207.tripeer.user.dto.res.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        UserEntity user;

        if (registrationId.contains("apple")) {
            String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
            Map<String, Object> attributes = decodeJwtTokenPayload(idToken);
            attributes.put("id_token", idToken);
            oAuth2Response = new AppleResponse(attributes);
            user = loginAndJoin(oAuth2Response);
            return new CustomOAuth2User(oAuth2Response, user.getRole(), user.getUserId());
        }

        OAuth2User oAuth2User = super.loadUser(userRequest);

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            user = loginAndJoin(oAuth2Response);
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            user = loginAndJoin(oAuth2Response);
        } else if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            user = loginAndJoin(oAuth2Response);
        } else {
            return null;
        }


        return new CustomOAuth2User(oAuth2Response, user.getRole(), user.getUserId());

    }

    private UserEntity loginAndJoin(OAuth2Response oAuth2Response) {
        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        UserEntity user = userRepository.findByProviderAndProviderId(provider, providerId);
//        소셜로그인은 되었지만, 우리 사이트에 회원등록이 안된 상태 전달
        if( user == null ) {
            UserEntity newUser = UserEntity.builder()
                    .role("ROLE_VALIDATE")
                    .build();
            return newUser;
        }

        return user;
    }

    public Map<String, Object> decodeJwtTokenPayload(String jwtToken){
        Map<String, Object> jwtClaims = new HashMap<>();
        try {
            String[] parts = jwtToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();

            byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = mapper.readValue(decodedString, Map.class);
            jwtClaims.putAll(map);

        } catch (JsonProcessingException e) {
            log.error("decodeJwtToken: {}-{} / jwtToken : {}", e.getMessage(), e.getCause(), jwtToken);
        }
        return jwtClaims;
    }
}
