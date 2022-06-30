package com.nameless.spin_off.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.spin_off.controller.exhandler.ErrorResult;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.sign.AuthenticationEntryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    HttpStatus httpStatus;
    private String errorMessage = null;
    private String errorCode = null;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RequestRejectedException e) {
            errorMessage = ErrorEnum.REQUEST_REJECTED.getMessage();
            errorCode = ErrorEnum.REQUEST_REJECTED.getCode();
            httpStatus = HttpStatus.BAD_REQUEST;
            log.error("exception code : {}", errorMessage);
            log.error("exception message : {}", errorCode);
            writeTokenResponse(response);
        } catch (AuthenticationEntryException e) {
            errorMessage = ErrorEnum.AUTHENTICATION_ENTRY.getMessage();
            errorCode = ErrorEnum.AUTHENTICATION_ENTRY.getCode();
            httpStatus = HttpStatus.UNAUTHORIZED;
            log.info("exception code : {}", errorMessage);
            log.info("exception message : {}", errorCode);
            writeTokenResponse(response);
        } catch (UsernameNotFoundException e) {
            errorMessage = ErrorEnum.USERNAME_NOT_FOUND.getMessage();
            errorCode = ErrorEnum.USERNAME_NOT_FOUND.getCode();
            httpStatus = HttpStatus.BAD_REQUEST;
            log.error("exception code : {}", errorMessage);
            log.error("exception message : {}", errorCode);
            writeTokenResponse(response);
        } catch (RuntimeException e) {
            errorMessage = ErrorEnum.RUNTIME.getMessage();
            errorCode = ErrorEnum.RUNTIME.getCode();
            httpStatus = HttpStatus.BAD_REQUEST;
            log.error("[exceptionHandler] ex", e);
            writeTokenResponse(response);
        }
    }

    private void writeTokenResponse(HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(httpStatus.value());
        response.addHeader("code", errorCode);
        response.addHeader("message", errorMessage);
        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(new ErrorResult(errorCode, errorMessage)));
        writer.flush();
    }
}