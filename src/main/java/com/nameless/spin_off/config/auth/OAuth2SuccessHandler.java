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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.KAKAO;
import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.NAVER;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRequestMapper userRequestMapper;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    @Value("${spring.oauth.default}")
    String defaultUrl;
    @Value("${spring.oauth.register}")
    String registerUrl;
    String targetUrl;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        SocialMemberDto socialMemberDto = userRequestMapper.toDto(oAuth2User);

        Member member = getMemberByEmail(socialMemberDto.getEmail());
        if (member.getRefreshToken() == null) {
            targetUrl = registerUrl;
        } else {
            targetUrl = defaultUrl;
        }
        member.updateRefreshToken(jwtTokenProvider.createRefreshToken());

        TokenResponseDto token =
                new TokenResponseDto(jwtTokenProvider.createToken(member.getAccountId()), member.getRefreshToken());
        log.info("{}", token);

        targetUrl = determineTargetUrl(request, response, authentication, token);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication, TokenResponseDto token) {
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", token.getAccessToken())
                .queryParam("refreshToken", token.getRefreshToken())
                .build().toUriString();
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
