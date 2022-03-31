package com.nameless.spin_off.config.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.spin_off.controller.exhandler.ErrorResult;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    private String errorMessage = null;
    private String errorCode = null;
    private final ObjectMapper objectMapper;

    //인증요청
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        log.info("[exceptionHandler] ex", authException);
        writeTokenResponse(response);
    }

    private void writeTokenResponse(HttpServletResponse response) throws IOException {

        errorMessage = ErrorEnum.AUTHENTICATION_ENTRY.getMessage();
        errorCode = ErrorEnum.AUTHENTICATION_ENTRY.getCode();

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(httpStatus.value());
        response.addHeader("isSuccess", String.valueOf(false));
        response.addHeader("code", errorCode);
        response.addHeader("message", errorMessage);
        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(new ErrorResult(errorCode, errorMessage)));
        writer.flush();
    }
}
