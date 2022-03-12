package com.nameless.spin_off.controller.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResult {
    private Boolean isSuccess;
    private String code;
    private String message;
}
