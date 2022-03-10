package com.nameless.spin_off.config.auth.dto;

import com.nameless.spin_off.dto.MemberDto.SocialMemberDto;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserRequestMapper {

    public SocialMemberDto toDto(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();
        return SocialMemberDto.builder()
                .email((String)attributes.get("email"))
                .name((String)attributes.get("name"))
                .picture((String)attributes.get("picture"))
                .build();
    }
}
