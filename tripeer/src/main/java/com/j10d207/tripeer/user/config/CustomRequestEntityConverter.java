package com.j10d207.tripeer.user.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwsHeader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.security.PrivateKey;
import java.util.Date;

@Slf4j
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;

    public CustomRequestEntityConverter() {
        defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
    }

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
        RequestEntity<?> entity = defaultConverter.convert(req);
        String registrationId = req.getClientRegistration().getRegistrationId();
        MultiValueMap<String, String> params = (MultiValueMap<String,String>) entity.getBody();
        //Apple일 경우 secret key를 동적으로 세팅
        if(registrationId.contains("apple")){
            params.set("client_secret", createAppleClientSecret(params.get("client_id").get(0), params.get("client_secret").get(0)));
        }
        return new RequestEntity<>(params, entity.getHeaders(),
                entity.getMethod(), entity.getUrl());
    }

    //Apple Secret Key를 만드는 메서드
    public String createAppleClientSecret(String clientId, String secretKeyResource) {
        String clientSecret = "";
        //application-oauth.yml에 설정해놓은 apple secret Key를 /를 기준으로 split
        String[] secretKeyResourceArr = secretKeyResource.split("/");
        try {
            InputStream inputStream = new ClassPathResource("https://dongtest.s3.ap-northeast-2.amazonaws.com/AuthKey_XKN39QTTDC.p8" + secretKeyResourceArr[0]).getInputStream();
            File file = File.createTempFile("appleKeyFile",".p8");
            try (InputStream in = inputStream) {  // try-with-resources로 InputStream을 자동으로 닫음
                FileUtils.copyInputStreamToFile(in, file);
            } catch (IOException e) {
                // 오류 처리
                e.printStackTrace();
            }

            String appleKeyId = secretKeyResourceArr[1];
            String appleTeamId = secretKeyResourceArr[2];
            String appleClientId = clientId;

            PEMParser pemParser = new PEMParser(new FileReader(file));
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();

            PrivateKey privateKey = converter.getPrivateKey(privateKeyInfo);

            clientSecret = Jwts.builder()
                    .setHeaderParam(JwsHeader.KEY_ID, appleKeyId)
                    .setIssuer(appleTeamId)
                    .setAudience("https://appleid.apple.com")
                    .setSubject(appleClientId)
                    .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5)))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact();

        } catch (IOException e) {
            log.error("Error_createAppleClientSecret : {}-{}", e.getMessage(), e.getCause());
        }
        log.info("createAppleClientSecret : {}", clientSecret);
        return clientSecret;
    }
}