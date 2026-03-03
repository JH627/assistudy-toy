package com.assistudy.userservice.oauth.userInfo;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
}
