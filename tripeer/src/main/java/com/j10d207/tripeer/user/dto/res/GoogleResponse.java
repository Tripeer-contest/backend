package com.j10d207.tripeer.user.dto.res;

import java.util.Map;

public class GoogleResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public String getProvider() {

        return "google";
    }

    @Override
    public String getProviderId() {

        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getProfileImage() {
        return attribute.get("picture").toString();
    }


    @Override
    public Map<String, Object> getAttribute() {
        return attribute;
    }
}
