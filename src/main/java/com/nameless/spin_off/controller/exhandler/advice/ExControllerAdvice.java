package com.nameless.spin_off.controller.exhandler.advice;

import com.nameless.spin_off.controller.exhandler.ErrorResult;
import com.nameless.spin_off.exception.support.CustomRuntimeException;
import com.nameless.spin_off.exception.support.CustomRuntimeForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.nameless.spin_off.entity.enums.ErrorEnum.*;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(CustomRuntimeException e) {
        log.info("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult(e.getErrorEnum().getCode(), e.getErrorEnum().getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(CustomRuntimeForbiddenException e) {
        log.info("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult(e.getErrorEnum().getCode(), e.getErrorEnum().getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(RuntimeException e) {
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult(RUNTIME.getCode(), RUNTIME.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> requestHandler(MissingRequestValueException e) {
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult(MISSING_REQUEST_VALUE.getCode(), MISSING_REQUEST_VALUE.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult(UNKNOWN.getCode(), UNKNOWN.getMessage());
    }
}
