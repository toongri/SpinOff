package com.nameless.spin_off.config.auth;

import com.nameless.spin_off.config.auth.dto.OAuth2Attribute;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.security.AlreadyAuthEmailException;
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

import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.ContentsLengthEnum.ACCOUNT_ID_MAX;
import static com.nameless.spin_off.entity.enums.ContentsLengthEnum.NICKNAME_MAX;
import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.KAKAO;
import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.NAVER;

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
                member.getRoles().stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                        .collect(Collectors.toSet()),
                oAuth2Attribute.getAttributes(),
                oAuth2Attribute.getAttributeKey());
    }

//    private Set<GrantedAuthority> getGrantedAuthorities(Member member) {
//        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
//
//        for (AuthorityOfMemberStatus role : member.getRoles()) {
//            authorities.add(new SimpleGrantedAuthority(role.getKey()));
//        }
//        return authorities;
//    }

    private Member getMember(String registrationId, OAuth2Attribute attributes) {

        isAlreadyAuth(attributes.getEmail(), registrationId);

        if (NAVER.getValue().equals(registrationId)) {
            return memberRepository
                    .findByNaverEmailWithRoles(attributes.getEmail())
                    .orElseGet(() ->
                            memberRepository.save(attributes.toNaverEntity(getRandomNickname(), getAccountId())));
        } else if (KAKAO.getValue().equals(registrationId)) {
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
            if (NAVER.getValue().equals(registrationId)) {
                if (optionalMember.get().getNaverEmail() == null) {
                    throw new AlreadyAuthEmailException(ErrorEnum.ALREADY_AUTH_EMAIL);
                }
            } else if (KAKAO.getValue().equals(registrationId)) {
                if (optionalMember.get().getKakaoEmail() == null) {
                    throw new AlreadyAuthEmailException(ErrorEnum.ALREADY_AUTH_EMAIL);
                }
            } else {
                if (optionalMember.get().getGoogleEmail() == null) {
                    throw new AlreadyAuthEmailException(ErrorEnum.ALREADY_AUTH_EMAIL);
                }
            }
        }
    }

    private String getRandomNickname() {
        String randomNickname = RandomStringUtils.randomAlphabetic(NICKNAME_MAX.getLength());

        Optional<Member> member = memberRepository.findByNickname(randomNickname);

        while (member.isPresent()) {
            randomNickname = RandomStringUtils.randomAlphabetic(NICKNAME_MAX.getLength());
            member = memberRepository.findByNickname(randomNickname);
        }
        log.info("randomNickname : {}", randomNickname);
        return randomNickname;
    }

    private String getAccountId() {
        String randomAccountId = RandomStringUtils.randomAlphabetic(ACCOUNT_ID_MAX.getLength());

        Optional<Member> member = memberRepository.findOneByAccountId(randomAccountId);

        while (member.isPresent()) {
            randomAccountId = RandomStringUtils.randomAlphabetic(ACCOUNT_ID_MAX.getLength());
            member = memberRepository.findByNickname(randomAccountId);
        }
        log.info("randomNickname : {}", randomAccountId);
        return randomAccountId;
    }
}
