package com.nameless.spin_off.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
public class ApiResult<T> {
    private T data;
}
