package com.nameless.spin_off.config.auth;

import com.nameless.spin_off.config.auth.dto.OAuth2Attribute;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2Attribute oAuth2Attribute =
                OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        log.info("{}", oAuth2Attribute);

        saveMember(registrationId, oAuth2Attribute);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2Attribute.getAttributes(), oAuth2Attribute.getAttributeKey());
    }

    private Member saveMember(String registrationId, OAuth2Attribute attributes) {

        return memberRepository.save(getMember(registrationId, attributes));
    }
    private Member getMember(String registrationId, OAuth2Attribute attributes) {

        if ("naver".equals(registrationId)) {
            return memberRepository
                    .findByNaverEmail(attributes.getEmail())
                    .orElseGet(() -> attributes.toNaverEntity(getRandomNickname(), getAccountId()));
        } else if ("kakao".equals(registrationId)) {
            return memberRepository
                    .findByKakaoEmail(attributes.getEmail())
                    .orElseGet(() -> attributes.toKakaoEntity(getRandomNickname(), getAccountId()));
        } else {
            return memberRepository
                    .findByGoogleEmail(attributes.getEmail())
                    .orElseGet(() -> attributes.toGoogleEntity(getRandomNickname(), getAccountId()));
        }
    }

    private String getRandomNickname() {
        String randomNickname = RandomStringUtils.randomAlphabetic(8);

        Optional<Member> member = memberRepository.findByNickname(randomNickname);

        while (member.isPresent()) {
            randomNickname = RandomStringUtils.randomAlphabetic(8);
            member = memberRepository.findByNickname(randomNickname);
        }
        log.info("randomNickname : {}", randomNickname);
        return randomNickname;
    }

    private String getAccountId() {
        String randomAccountId = RandomStringUtils.randomAlphabetic(8);

        Optional<Member> member = memberRepository.findByAccountId(randomAccountId);

        while (member.isPresent()) {
            randomAccountId = RandomStringUtils.randomAlphabetic(8);
            member = memberRepository.findByNickname(randomAccountId);
        }
        log.info("randomNickname : {}", randomAccountId);
        return randomAccountId;
    }
}
