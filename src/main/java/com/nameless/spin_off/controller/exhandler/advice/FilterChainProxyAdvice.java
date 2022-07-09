package com.nameless.spin_off.controller.exhandler.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.spin_off.controller.exhandler.ErrorResult;
import com.nameless.spin_off.enums.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Aspect
@RequiredArgsConstructor
@Component
public class FilterChainProxyAdvice {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final String errorMessage = ErrorEnum.REQUEST_REJECTED.getMessage();
    private final String errorCode = ErrorEnum.REQUEST_REJECTED.getCode();
    private final ObjectMapper objectMapper;


    @Around("execution(public void org.springframework.security.web.FilterChainProxy.doFilter(..))")
    public void handleRequestRejectedException (ProceedingJoinPoint pjp) throws Throwable {
        try {
            pjp.proceed();
        } catch (RequestRejectedException exception) {
            HttpServletResponse response = (HttpServletResponse) pjp.getArgs()[1];
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