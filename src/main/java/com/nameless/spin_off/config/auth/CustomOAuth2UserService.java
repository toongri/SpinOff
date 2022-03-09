package com.nameless.spin_off.config.auth;

import com.nameless.spin_off.config.auth.dto.OAuthAttributes;
import com.nameless.spin_off.config.auth.dto.SessionMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.NotExistMemberException;
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

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.info("oAuth2User : {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId : {}", registrationId);
        String userNameAttributeName =
                userRequest.getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint().getUserNameAttributeName();
        log.info("userNameAttributeName : {}", userNameAttributeName);

//        SessionMember loginMember = (SessionMember)httpSession.getAttribute("member");
        log.info("oAuth2User.getAttributes() : {}", oAuth2User.getAttributes());

        OAuthAttributes attributes =
                OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        log.info("attributes.getEmail : {}", attributes.getEmail());
        log.info("attributes.getName : {}", attributes.getName());
        Member member = saveMember(registrationId, attributes);;

//        if (loginMember == null) {
//            member = saveMember(registrationId, attributes);
//        } else {
//            log.info("loginMember.getMemberId : {}", loginMember.getMemberId());
//            log.info("loginMember.getNickname : {}", loginMember.getNickname());
//            member = updateMember(registrationId, attributes, loginMember);
//
//            httpSession.removeAttribute("member");
//        }
        httpSession.setAttribute("member", new SessionMember(member));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getAuthorityOfMemberStatus().getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private Member updateMember(String registrationId, OAuthAttributes attributes, SessionMember loginMember) {
        return memberRepository.save(getMember(registrationId, attributes, loginMember));
    }
    private Member saveMember(String registrationId, OAuthAttributes attributes) {

        return memberRepository.save(getMember(registrationId, attributes));
    }

    private Member getMember(String registrationId, OAuthAttributes attributes, SessionMember loginMember) {

        if ("naver".equals(registrationId)) {
            return memberRepository
                    .findById(loginMember.getMemberId())
                    .map(member1 -> member1.updateNaverEmail(attributes.getEmail()))
                    .orElseThrow(NotExistMemberException::new);
        } else if ("kakao".equals(registrationId)) {
            return memberRepository
                    .findById(loginMember.getMemberId())
                    .map(member1 -> member1.updateKakaoEmail(attributes.getEmail()))
                    .orElseThrow(NotExistMemberException::new);
        } else {
            return memberRepository
                    .findById(loginMember.getMemberId())
                    .map(member1 -> member1.updateGoogleEmail(attributes.getEmail()))
                    .orElseThrow(NotExistMemberException::new);
        }
    }

    private Member getMember(String registrationId, OAuthAttributes attributes) {

        if ("naver".equals(registrationId)) {
            return memberRepository
                    .findByNaverEmail(attributes.getEmail())
                    .orElseGet(() -> attributes.toNaverEntity(getRandomNickname()));
        } else if ("kakao".equals(registrationId)) {
            return memberRepository
                    .findByKakaoEmail(attributes.getEmail())
                    .orElseGet(() -> attributes.toKakaoEntity(getRandomNickname()));
        } else {
            return memberRepository
                    .findByGoogleEmail(attributes.getEmail())
                    .orElseGet(() -> attributes.toGoogleEntity(getRandomNickname()));
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
}
