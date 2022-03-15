package com.nameless.spin_off.config.auth.dto;

import com.nameless.spin_off.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String name;
    private String picture;

    public static OAuth2Attribute of(String provider, String attributeKey,
                                     Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle(attributeKey, attributes);
            case "kakao":
                return ofKakao("email", attributes);
            case "naver":
                return ofNaver("id", attributes);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofGoogle(String attributeKey, Map<String, Object> attributes) {

        return OAuth2Attribute.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String)attributes.get("picture"))
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

    private static OAuth2Attribute ofKakao(String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Attribute.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String)kakaoProfile.get("profile_image_url"))
                .attributes(kakaoAccount)
                .attributeKey(attributeKey)
                .build();
    }

    private static OAuth2Attribute ofNaver(String attributeKey,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attribute.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .attributeKey(attributeKey)
                .build();
    }

    public Member toGoogleEntity(String nickname, String accountId) {
        return Member.buildMember()
                .setAccountId(accountId)
                .setName(name)
                .setEmail(email)
                .setGoogleEmail(email)
                .setNickname(nickname)
                .setEmailAuth(true)
                .build();
    }
    public Member toKakaoEntity(String nickname, String accountId) {
        return Member.buildMember()
                .setAccountId(accountId)
                .setName(name)
                .setEmail(email)
                .setKakaoEmail(email)
                .setNickname(nickname)
                .setEmailAuth(true)
                .build();
    }
    public Member toNaverEntity(String nickname, String accountId) {
        return Member.buildMember()
                .setAccountId(accountId)
                .setName(name)
                .setEmail(email)
                .setNaverEmail(email)
                .setNickname(nickname)
                .setEmailAuth(true)
                .build();
    }
}