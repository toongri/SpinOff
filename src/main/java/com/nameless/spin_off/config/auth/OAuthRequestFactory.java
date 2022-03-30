package com.nameless.spin_off.config.auth;

import com.nameless.spin_off.config.auth.dto.OAuthRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.google;
import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.kakao;

@Component
@RequiredArgsConstructor
public class OAuthRequestFactory {

    private final KakaoInfo kakaoInfo;
    private final GoogleInfo googleInfo;
    private final NaverInfo naverInfo;

    public OAuthRequest getRequest(String code, String provider) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (kakao.getName().equals(provider)) {
            map.add("grant_type", "authorization_code");
            map.add("client_id", kakaoInfo.getKakaoClientId());
            map.add("redirect_uri", kakaoInfo.getKakaoRedirect());
            map.add("code", code);

            return new OAuthRequest(kakaoInfo.getKakaoTokenUrl(), map);

        } else if(google.getName().equals(provider)) {
            map.add("grant_type", "authorization_code");
            map.add("client_id", googleInfo.getGoogleClientId());
            map.add("client_secret", googleInfo.getGoogleClientSecret());
            map.add("redirect_uri", googleInfo.getGoogleRedirect());
            map.add("code", code);

            return new OAuthRequest(googleInfo.getGoogleTokenUrl(), map);
        } else {
            map.add("grant_type", "authorization_code");
            map.add("client_id", naverInfo.getNaverClientId());
            map.add("client_secret", naverInfo.getNaverClientSecret());
            map.add("redirect_uri", naverInfo.getNaverRedirect());
            map.add("state", "project");
            map.add("code", code);

            return new OAuthRequest(naverInfo.getNaverTokenUrl(), map);
        }
    }

    public String getProfileUrl(String provider) {
        if (kakao.getName().equals(provider)) {
            return kakaoInfo.getKakaoProfileUrl();
        } else if(google.getName().equals(provider)) {
            return googleInfo.getGoogleProfileUrl();
        } else {
            return naverInfo.getNaverProfileUrl();
        }
    }

    @Getter
    @Component
    static class KakaoInfo {
        @Value("${spring.social.kakao.client_id}")
        String kakaoClientId;
        @Value("${spring.social.kakao.redirect}")
        String kakaoRedirect;
        @Value("${spring.social.kakao.url.token}")
        private String kakaoTokenUrl;
        @Value("${spring.social.kakao.url.profile}")
        private String kakaoProfileUrl;
    }

    @Getter
    @Component
    static class GoogleInfo {
        @Value("${spring.social.google.client_id}")
        String googleClientId;
        @Value("${spring.social.google.redirect}")
        String googleRedirect;
        @Value("${spring.social.google.client_secret}")
        String googleClientSecret;
        @Value("${spring.social.google.url.token}")
        private String googleTokenUrl;
        @Value("${spring.social.google.url.profile}")
        private String googleProfileUrl;
    }

    @Getter
    @Component
    static class NaverInfo {
        @Value("${spring.social.naver.client_id}")
        String naverClientId;
        @Value("${spring.social.naver.redirect}")
        String naverRedirect;
        @Value("${spring.social.naver.client_secret}")
        String naverClientSecret;
        @Value("${spring.social.naver.url.token}")
        private String naverTokenUrl;
        @Value("${spring.social.naver.url.profile}")
        private String naverProfileUrl;
    }
}