package com.j10d207.tripeer.noti.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;


@Configuration
@Slf4j
public class FirebaseJsonProvider {

    private final String type;
    private final String projectId;
    private final String privateKeyId;
    private final String privateKey;
    private final String clientEmail;
    private final String clientId;
    private final String authUri;
    private final String tokenUri;
    private final String authProviderX509CertUrl;
    private final String clientX509CertUrl;
    private final String universeDomain;

    public FirebaseJsonProvider(
        @Value("${firebase.type}") String type,
        @Value("${firebase.project.id}") String projectId,
        @Value("${firebase.private.id}") String privateKeyId,
        @Value("${firebase.private.key}") String privateKey,
        @Value("${firebase.client.email}") String clientEmail,
        @Value("${firebase.client.id}") String clientId,
        @Value("${firebase.auth.uri}") String authUri,
        @Value("${firebase.token.uri}") String tokenUri,
        @Value("${firebase.auth.provider.x509.cert.url}") String authProviderX509CertUrl,
        @Value("${firebase.client.x509.cert.url}") String clientX509CertUrl,
        @Value("${firebase.universe.domain}") String universeDomain
    ) {
        this.type = type;
        this.projectId = projectId;
        this.privateKeyId = privateKeyId;
        this.privateKey = privateKey;
        this.clientEmail = clientEmail;
        this.clientId = clientId;
        this.authUri = authUri;
        this.tokenUri = tokenUri;
        this.authProviderX509CertUrl = authProviderX509CertUrl;
        this.clientX509CertUrl = clientX509CertUrl;
        this.universeDomain = universeDomain;
    }

    @Bean
    @Qualifier("firebaseInputStream")
    public InputStream getInputStream() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JsonGenerator generator = getJsonGenerator(byteArrayOutputStream);
        makeFirebaseJson(generator);
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private JsonGenerator getJsonGenerator(OutputStream outputStream) {
        try {
            return new JsonFactory().createGenerator(outputStream);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void makeFirebaseJson(JsonGenerator generator) {
        try {
            generator.writeStartObject();
            generator.writeStringField("type", type);
            generator.writeStringField("project_id", projectId);
            generator.writeStringField("private_key_id", privateKeyId);
            generator.writeStringField("private_key", privateKey);
            generator.writeStringField("client_email", clientEmail);
            generator.writeStringField("client_id", clientId);
            generator.writeStringField("auth_uri", authUri);
            generator.writeStringField("token_uri", tokenUri);
            generator.writeStringField("auth_provider_x509_cert_url", authProviderX509CertUrl);
            generator.writeStringField("client_x509_cert_url", clientX509CertUrl);
            generator.writeStringField("universe_domain",universeDomain);
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
            new RuntimeException(e.getMessage());
        }
    }

}
