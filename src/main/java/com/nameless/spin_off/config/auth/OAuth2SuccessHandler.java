package com.nameless.spin_off.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.spin_off.config.auth.dto.UserRequestMapper;
import com.nameless.spin_off.config.jwt.JwtTokenProvider;
import com.nameless.spin_off.dto.MemberDto.OauthResponseDto;
import com.nameless.spin_off.dto.MemberDto.SocialMemberDto;
import com.nameless.spin_off.dto.MemberDto.TokenResponseDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.KAKAO;
import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.NAVER;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRequestMapper userRequestMapper;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.debug("onAuthenticationSuccess start");
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        SocialMemberDto socialMemberDto = userRequestMapper.toDto(oAuth2User);

        Member member = getMemberByEmail(socialMemberDto.getEmail());
        member.updateRefreshToken(jwtTokenProvider.createRefreshToken());

        TokenResponseDto token =
                new TokenResponseDto(jwtTokenProvider.createToken(member.getAccountId()), member.getRefreshToken());
        log.info("{}", token);

        log.debug("onAuthenticationSuccess end");
        writeTokenResponse(response, token);
    }

    private Member getMemberByEmail(String email) {
        String provider = email.substring(email.indexOf("@") + 1, email.indexOf("."));

        if (NAVER.getValue().equals(provider)) {
            return memberRepository
                    .findByNaverEmailWithRoles(email).get();
        } else if (KAKAO.getValue().equals(provider)) {
            return memberRepository
                    .findByKakaoEmailWithRoles(email).get();
        } else {
            return memberRepository
                    .findByGoogleEmailWithRoles(email).get();
        }
    }

    private void writeTokenResponse(HttpServletResponse response, TokenResponseDto token)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.addHeader("isSuccess", String.valueOf(true));
        response.addHeader("code", "0");
        response.addHeader("message", "성공");
        response.addHeader("data", String.valueOf(token));
        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(new OauthResponseDto(
                true, "0", "성공", token)));
        writer.flush();
    }
}
