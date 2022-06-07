package com.nameless.spin_off.dto;

import com.nameless.spin_off.enums.help.ComplainStatus;
import com.nameless.spin_off.enums.help.ContentTypeStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class HelpDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComplainRequestDto {

        @ApiModelProperty(
                value = "컨텐츠 id",
                example = "123")
        Long contentId;

        @ApiModelProperty(
                value = "컨텐츠 타입",
                example = "A")
        ContentTypeStatus contentTypeStatus;

        @ApiModelProperty(
                value = "신고 종류",
                example = "A")
        ComplainStatus complainStatus;
    }

}
