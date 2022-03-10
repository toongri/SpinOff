package com.nameless.spin_off.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.spin_off.config.auth.dto.UserRequestMapper;
import com.nameless.spin_off.config.jwt.JwtTokenProvider;
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
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        SocialMemberDto socialMemberDto = userRequestMapper.toDto(oAuth2User);

        Member member = getAccountId(socialMemberDto.getEmail());

        // 최초 로그인이라면 회원가입 처리를 한다.
        member.updateRefreshToken(jwtTokenProvider.createRefreshToken());


        TokenResponseDto token =
                new TokenResponseDto(jwtTokenProvider.createToken(member.getAccountId()), member.getRefreshToken());
        log.info("{}", token);

        writeTokenResponse(response, token);
    }

    private Member getAccountId(String email) {
        String provider = email.substring(email.indexOf("@") + 1, email.indexOf("."));

        if ("naver".equals(provider)) {
            return memberRepository
                    .findByNaverEmail(email).get();
        } else if ("kakao".equals(provider)) {
            return memberRepository
                    .findByKakaoEmail(email).get();
        } else {
            return memberRepository
                    .findByGoogleEmail(email).get();
        }
    }

    private void writeTokenResponse(HttpServletResponse response, TokenResponseDto token)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        response.addHeader("accessToken", token.getAccessToken());
        response.addHeader("refreshToken", token.getRefreshToken());
        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(token));
        writer.flush();
    }
}
