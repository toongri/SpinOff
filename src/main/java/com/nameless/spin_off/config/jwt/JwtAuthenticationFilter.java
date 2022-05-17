package com.nameless.spin_off.config.jwt;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.sign.AuthenticationEntryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        // 유효한 토큰인지 확인합니다.
        if (token != null) {
            if (!token.equals("")) {
                if (jwtTokenProvider.validateTokenExpiration(token)) {
                    // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    // SecurityContext에 Authentication 객체를 저장합니다.
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    throw new AuthenticationEntryException(ErrorEnum.AUTHENTICATION_ENTRY);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
