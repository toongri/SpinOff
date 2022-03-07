package com.nameless.spin_off.config.auth.dto;

import com.nameless.spin_off.entity.member.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String email;
    private String name;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String name, String nameAttributeKey, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Member toGoogleEntity(String nickname) {
        return Member.buildMember()
                .setName(name)
                .setGoogleEmail(email)
                .setNickname(nickname)
                .build();

    }
    public Member toKakaoEntity(String nickname) {
        return Member.buildMember()
                .setName(name)
                .setKakaoEmail(email)
                .setNickname(nickname)
                .build();
    }
    public Member toNaverEntity(String nickname) {
        return Member.buildMember()
                .setName(name)
                .setNaverEmail(email)
                .setNickname(nickname)
                .build();
    }
}
