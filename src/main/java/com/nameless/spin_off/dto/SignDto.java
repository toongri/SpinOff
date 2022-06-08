package com.nameless.spin_off.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SignDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthCodeRequestDto {

        @ApiModelProperty(
                value = "인증 코드",
                example = "abcd232")
        private String authCode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateEmailRequestDto {

        @ApiModelProperty(
                value = "이메일",
                example = "spinoff@naver.com")
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkageEmailRequestDto {

        @ApiModelProperty(
                value = "이메일",
                example = "spinoff@naver.com")
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountIdRequestDto {

        @ApiModelProperty(
                value = "계정 아이디",
                example = "abcd232")
        private String accountId;
    }
}
