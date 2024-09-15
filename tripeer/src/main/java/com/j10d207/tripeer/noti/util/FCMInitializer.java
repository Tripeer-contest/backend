package com.j10d207.tripeer.noti.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j
public class FCMInitializer {

    InputStream firebaseInputStream;

    public FCMInitializer(@Qualifier("firebaseInputStream") InputStream firebaseInputStream) {
        this.firebaseInputStream = firebaseInputStream;
    }

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseInputStream))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("연결 오류");
        }
    }

}
