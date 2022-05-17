package com.nameless.spin_off.config.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.spin_off.controller.exhandler.ErrorResult;
import com.nameless.spin_off.enums.ErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    HttpStatus httpStatus = HttpStatus.FORBIDDEN;
    private String errorMessage = null;
    private String errorCode = null;
    private final ObjectMapper objectMapper;

    //권한초과
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("[exceptionHandler] ex", accessDeniedException);
        writeTokenResponse(response);
    }

    private void writeTokenResponse(HttpServletResponse response) throws IOException {
        errorMessage = ErrorEnum.ACCESS_DENIED.getMessage();
        errorCode = ErrorEnum.ACCESS_DENIED.getCode();

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
