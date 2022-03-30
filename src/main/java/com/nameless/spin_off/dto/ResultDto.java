package com.nameless.spin_off.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ResultDto {

    @Data
    @AllArgsConstructor
    public static class SingleApiResult<T> {

        @ApiModelProperty(
                value = "결과값",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
        private T data;

        @ApiModelProperty(
                value = "코드",
                example = "ERR000",
                dataType = "String")
        String code;

        @ApiModelProperty(
                value = "메세지",
                example = "오류",
                dataType = "String")
        String message;

        public static <T> SingleApiResult<T> getResult(T data) {
            return new SingleApiResult<>(data, "", "");
        }
    }

}
