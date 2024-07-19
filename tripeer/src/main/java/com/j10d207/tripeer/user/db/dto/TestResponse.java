package com.j10d207.tripeer.user.db.dto;

import java.util.Map;

public class TestResponse implements OAuth2Response {


    @Override
    public String getProvider() {
        return "TestProvider";
    }

    @Override
    public String getProviderId() {
        return "TestProviderId";
    }

    @Override
    public String getEmail() {
        return "Test@Email";
    }

    @Override
    public String getProfileImage() {
        return "TestProfileImage";
    }

    @Override
    public Map<String, Object> getAttribute() {
        return Map.of();
    }
}
