package com.nameless.spin_off.config.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.spin_off.controller.exhandler.ErrorResult;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.security.AlreadyAuthEmailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private String errorMessage = null;
    private String errorCode = null;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("[exceptionHandler] ex", exception);

        if (exception instanceof BadCredentialsException) {
            errorMessage = ErrorEnum.BAD_CREDENTIALS.getMessage();
            errorCode = ErrorEnum.BAD_CREDENTIALS.getCode();
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = ErrorEnum.INTERNAL_AUTHENTICATION_SERVICE.getMessage();
            errorCode = ErrorEnum.INTERNAL_AUTHENTICATION_SERVICE.getCode();
        } else if (exception instanceof DisabledException) {
            errorMessage = ErrorEnum.DISABLED.getMessage();
            errorCode = ErrorEnum.DISABLED.getCode();
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = ErrorEnum.CREDENTIALS_EXPIRED.getMessage();
            errorCode = ErrorEnum.CREDENTIALS_EXPIRED.getCode();
        } else if (exception instanceof AlreadyAuthEmailException) {
            errorMessage = ErrorEnum.ALREADY_AUTH_EMAIL.getMessage();
            errorCode = ErrorEnum.ALREADY_AUTH_EMAIL.getCode();
        } else {
            errorMessage = ErrorEnum.UNKNOWN.getMessage();
            errorCode = ErrorEnum.UNKNOWN.getCode();
        }
        writeTokenResponse(response);
    }

    private void writeTokenResponse(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(httpStatus.value());
        response.addHeader("isSuccess", String.valueOf(false));
        response.addHeader("code", errorCode);
        response.addHeader("message", errorMessage);
        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(new ErrorResult(false, errorCode, errorMessage)));
        writer.flush();
    }
}
