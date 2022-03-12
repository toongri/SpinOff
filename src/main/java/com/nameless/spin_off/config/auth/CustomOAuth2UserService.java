package com.nameless.spin_off.config.auth;

import com.nameless.spin_off.config.auth.dto.OAuth2Attribute;
import com.nameless.spin_off.entity.enums.member.AuthorityOfMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.AlreadyAuthEmailException;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("loadUser start");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        log.debug("registrationId: {}", registrationId);
        log.debug("userNameAttributeName: {}", userNameAttributeName);

        OAuth2Attribute oAuth2Attribute =
                OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        log.info("oAuth2Attribute: {}", oAuth2Attribute.toString());

        Member member = getMember(registrationId, oAuth2Attribute);
        log.debug("loadUser end");

        return new DefaultOAuth2User(
                getGrantedAuthorities(member),
                oAuth2Attribute.getAttributes(),
                oAuth2Attribute.getAttributeKey());
    }

    private Set<GrantedAuthority> getGrantedAuthorities(Member member) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        for (AuthorityOfMemberStatus role : member.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getKey()));
        }
        return authorities;
    }

    private Member getMember(String registrationId, OAuth2Attribute attributes) {

        isAlreadyAuth(attributes.getEmail(), registrationId);

        if ("naver".equals(registrationId)) {
            return memberRepository
                    .findByNaverEmailWithRoles(attributes.getEmail())
                    .orElseGet(() ->
                            memberRepository.save(attributes.toNaverEntity(getRandomNickname(), getAccountId())));
        } else if ("kakao".equals(registrationId)) {
            return memberRepository
                    .findByKakaoEmailWithRoles(attributes.getEmail())
                    .orElseGet(() ->
                            memberRepository.save(attributes.toKakaoEntity(getRandomNickname(), getAccountId())));
        } else {
            return memberRepository
                    .findByGoogleEmailWithRoles(attributes.getEmail())
                    .orElseGet(() ->
                            memberRepository.save(attributes.toGoogleEntity(getRandomNickname(), getAccountId())));
        }
    }

    private void isAlreadyAuth(String email, String registrationId) {

        Optional<Member> optionalMember = memberRepository.findByEmailWithRoles(email);
        if (optionalMember.isPresent()) {
            if ("naver".equals(registrationId)) {
                if (optionalMember.get().getNaverEmail() == null) {
                    throw new AlreadyAuthEmailException(
                            "해당 이메일은 이미 인증된 이메일입니다. 로그인 후 연동하여 사용해주세요");
                }
            } else if ("kakao".equals(registrationId)) {
                if (optionalMember.get().getKakaoEmail() == null) {
                    throw new AlreadyAuthEmailException(
                            "해당 이메일은 이미 인증된 이메일입니다. 로그인 후 연동하여 사용해주세요");
                }
            } else {
                if (optionalMember.get().getGoogleEmail() == null) {
                    throw new AlreadyAuthEmailException(
                            "해당 이메일은 이미 인증된 이메일입니다. 로그인 후 연동하여 사용해주세요");
                }
            }
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

        Optional<Member> member = memberRepository.findOneByAccountId(randomAccountId);

        while (member.isPresent()) {
            randomAccountId = RandomStringUtils.randomAlphabetic(8);
            member = memberRepository.findByNickname(randomAccountId);
        }
        log.info("randomNickname : {}", randomAccountId);
        return randomAccountId;
    }
}
