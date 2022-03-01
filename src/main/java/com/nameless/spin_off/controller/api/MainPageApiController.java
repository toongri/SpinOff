package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.service.query.MainPageQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/main-page")
public class MainPageApiController {

    private final MainPageQueryService mainPageService;


    @Data
    @AllArgsConstructor
    public static class MainPageResult<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    public static class MainPageDiscoveryResult<T> {
        private T data;
    }
}
