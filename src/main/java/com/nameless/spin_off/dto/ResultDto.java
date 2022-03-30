package com.nameless.spin_off.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ResultDto {

    @Data
    @AllArgsConstructor
    public static class SingleApiResult<T> {
        private T data;
        String code;
        String message;
    }

    public static <T> SingleApiResult<T> getResult(T data) {
        return new SingleApiResult<>(data, "0", "성공");
    }
}
